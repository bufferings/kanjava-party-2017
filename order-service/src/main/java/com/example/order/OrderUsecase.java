package com.example.order;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.order.domain.model.OrderAddService;
import com.example.order.domain.model.order.DeliveryDateTime;
import com.example.order.domain.model.order.DeliveryPersonId;
import com.example.order.domain.model.order.DeliveryPersonName;
import com.example.order.domain.model.order.OrderGroup;
import com.example.order.domain.model.order.OrderGuestId;
import com.example.order.domain.model.order.OrderGuestName;
import com.example.order.domain.model.order.OrderItemId;
import com.example.order.domain.model.order.OrderQuantity;
import com.example.order.domain.model.order.OrderRepository;
import com.example.order.domain.model.product.ProductId;
import com.example.order.util.IdUtil;

@Service
@Transactional
@RestController
public class OrderUsecase {

  private static final Logger logger = LoggerFactory.getLogger(OrderUsecase.class);

  private enum InventoryResult {
    WAITING, SUCCEEDED, FAILED
  }

  private static final ConcurrentHashMap<String, CountDownLatch> countDownLatches = new ConcurrentHashMap<>();

  private static final ConcurrentHashMap<String, InventoryResult> inventoryResults = new ConcurrentHashMap<>();

  private OrderRepository orderRepository;

  private OrderAddService orderAddService;

  private KafkaTemplate<String, GenericRecord> kafkaTemplate;

  private Schema orderItemDeliveredEventSchema;

  private Schema orderCheckedOutEventSchema;

  private RestTemplate restTemplate;

  @Autowired
  public OrderUsecase(OrderRepository orderRepository, OrderAddService orderAddService,
      KafkaTemplate<String, GenericRecord> kafkaTemplate, RestTemplate restTemplate,
      @Value(value = "classpath:avro/OrderItemDeliveredEvent.avsc") Resource orderItemDeliveredEventSchemaFile,
      @Value(value = "classpath:avro/OrderCheckedOutEvent.avsc") Resource orderCheckedOutEventSchemaFile)
      throws IOException {
    this.orderRepository = orderRepository;
    this.orderAddService = orderAddService;
    this.restTemplate = restTemplate;
    this.kafkaTemplate = kafkaTemplate;
    try (InputStream is = orderItemDeliveredEventSchemaFile.getInputStream()) {
      orderItemDeliveredEventSchema = new Schema.Parser().parse(is);
    }
    try (InputStream is = orderCheckedOutEventSchemaFile.getInputStream()) {
      orderCheckedOutEventSchema = new Schema.Parser().parse(is);
    }
  }

  @PostMapping("order-items/add")
  public void addOrder(@RequestParam("orderGuestId") Integer orderGuestId,
      @RequestParam("orderGuestName") String orderGuestName, @RequestParam("productId") String productId,
      @RequestParam("quantity") Integer quantity) {

    String eventId = IdUtil.generateId();

    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("eventId", eventId);
    map.add("productId", productId);
    map.add("reservationCount", String.valueOf(quantity));
    restTemplate.postForObject("http://inventory-service/request", map, Void.class);

    waitForResult(eventId);

    InventoryResult result = inventoryResults.get(eventId);
    if (result == InventoryResult.FAILED) {
      throw new RuntimeException("Failed to get inventory");
    }

    orderAddService.addOrder(new OrderGuestId(orderGuestId), new OrderGuestName(orderGuestName),
        new ProductId(productId), new OrderQuantity(quantity));
  }

  private void waitForResult(String eventId) {
    logger.info("IN: waitForResult");

    inventoryResults.put(eventId, InventoryResult.WAITING);
    CountDownLatch countDownLatch = new CountDownLatch(1);
    logger.info("eventId=" + eventId + ", " + countDownLatch);
    countDownLatches.put(eventId, countDownLatch);
    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    logger.info("OUT: waitForResult");
  }

  @PostMapping("order-items/{orderItemId}/deliver")
  public void deliverOrderItem(@PathVariable("orderItemId") String orderItemId,
      @RequestParam("deiveryPersonId") Integer deliveryPersonId,
      @RequestParam("deliveryPersonName") String deliveryPersonName) {
    OrderGroup orderGroup = orderRepository.orderGroupOfOrderItemId(new OrderItemId(orderItemId));
    DeliveryDateTime deliveredOn = new DeliveryDateTime(LocalDateTime.now());
    orderGroup.deliverOrderItem(new OrderItemId(orderItemId), new DeliveryPersonId(deliveryPersonId),
        new DeliveryPersonName(deliveryPersonName), deliveredOn);
    orderRepository.save(orderGroup);

    GenericRecord orderItemDeliveredEvent = new GenericData.Record(orderItemDeliveredEventSchema);
    orderItemDeliveredEvent.put("orderItemId", orderItemId);
    orderItemDeliveredEvent.put("deliveryPersonId", deliveryPersonId);
    orderItemDeliveredEvent.put("deliveryPersonName", deliveryPersonName);
    orderItemDeliveredEvent.put("deliveredOn", deliveredOn.getValue().toString());
    kafkaTemplate.send("order-topic", orderItemDeliveredEvent);
  }

  @PostMapping("checkout")
  public void checkout(@RequestParam("orderGuestId") Integer orderGuestId) {
    OrderGroup orderGroup = orderRepository.activeOrderGroupOf(new OrderGuestId(orderGuestId));
    Assert.notNull(orderGroup, "orderGroup not found");
    orderGroup.checkout();
    orderRepository.save(orderGroup);

    GenericRecord orderCheckedOutEvent = new GenericData.Record(orderCheckedOutEventSchema);
    orderCheckedOutEvent.put("orderGuestId", orderGuestId);
    kafkaTemplate.send("order-topic", orderCheckedOutEvent);
  }

  @KafkaListener(topics = { "inventory-result-topic" })
  public void listen(GenericRecord record) throws Exception {
    String eventId = ((Utf8) record.get("requestEventId")).toString();
    logger.info("eventId=" + eventId);
    String schemaName = record.getSchema().getFullName();
    if ("InventoryReservedEvent".equals(schemaName)) {
      logger.info("eventId=" + eventId + ", " + countDownLatches.get(eventId));
      logger.info("Received InventoryReservedEvent");
      inventoryResults.put(eventId, InventoryResult.SUCCEEDED);
      countDownLatches.get(eventId).countDown();
    } else if ("InventoryReservationFailedEvent".equals(schemaName)) {
      logger.info("eventId=" + eventId + ", " + countDownLatches.get(eventId));
      logger.info("Received InventoryReservationFailedEvent");
      inventoryResults.put(eventId, InventoryResult.FAILED);
      countDownLatches.get(eventId).countDown();
    }
  }
}
