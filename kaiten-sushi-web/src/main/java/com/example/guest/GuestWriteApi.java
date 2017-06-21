package com.example.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.LoginUser;

@RestController
@RequestMapping("guest/api")
public class GuestWriteApi {

  private static final String ORDER_SERVICE_URL = "http://order-service/";

  private OAuth2RestTemplate oAuth2RestTemplate;

  @Autowired
  public GuestWriteApi(OAuth2RestTemplate oAuth2RestTemplate) {
    this.oAuth2RestTemplate = oAuth2RestTemplate;
  }

  @PostMapping("order-items/add")
  public void addOrder(@AuthenticationPrincipal LoginUser loginUser, @RequestParam("productId") String productId,
      @RequestParam("quantity") int quantity) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("orderGuestId", String.valueOf(loginUser.getId()));
    map.add("orderGuestName", loginUser.getName());
    map.add("productId", String.valueOf(productId));
    map.add("quantity", String.valueOf(quantity));
    oAuth2RestTemplate.postForObject(ORDER_SERVICE_URL + "order-items/add", map, Void.class);
  }

  @PostMapping("checkout")
  public void checkout(@AuthenticationPrincipal LoginUser loginUser) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("orderGuestId", String.valueOf(loginUser.getId()));
    oAuth2RestTemplate.postForObject(ORDER_SERVICE_URL + "checkout", map, Void.class);
  }

}
