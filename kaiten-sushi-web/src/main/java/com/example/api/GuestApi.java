package com.example.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.view.OrderGuestView;
import com.example.api.view.OrderGuestViewDao;
import com.example.order.domain.model.product.Product;
import com.example.order.usecase.OrderUsecase;
import com.example.order.usecase.ProductUsecase;

@RestController
@RequestMapping("guest/api")
public class GuestApi {

  private static final int DUMMY_TABLE_NUMBER = 21;

  private ProductUsecase productUsecase;

  private OrderUsecase orderUsecase;

  private OrderGuestViewDao orderGuestViewDao;

  @Autowired
  public GuestApi(ProductUsecase productUsecase, OrderUsecase orderUsecase, OrderGuestViewDao orderGuestViewDao) {
    this.productUsecase = productUsecase;
    this.orderUsecase = orderUsecase;
    this.orderGuestViewDao = orderGuestViewDao;
  }

  @RequestMapping(path = "products", method = RequestMethod.GET)
  public List<Product> getProducts() {
    return productUsecase.getProducts();
  }

  @RequestMapping(path = "products/{productId}", method = RequestMethod.GET)
  public Product getProduct(@PathVariable("productId") String productId) {
    return productUsecase.getProduct(productId);
  }

  @RequestMapping(path = "orders", method = RequestMethod.GET)
  public List<OrderGuestView> getOrders() {
    int tableNumber = DUMMY_TABLE_NUMBER;
    return orderGuestViewDao.selectByTableNumber(tableNumber);
  }

  @RequestMapping(path = "orders/add", method = RequestMethod.POST)
  public void addOrder(@RequestParam("productId") String productId, @RequestParam("quantity") int quantity) {
    int tableNumber = DUMMY_TABLE_NUMBER;
    orderUsecase.addOrder(tableNumber, productId, quantity);
  }

  @RequestMapping(path = "checkout", method = RequestMethod.POST)
  public void checkout() {
    int tableNumber = DUMMY_TABLE_NUMBER;
    orderUsecase.checkout(tableNumber);
  }

}
