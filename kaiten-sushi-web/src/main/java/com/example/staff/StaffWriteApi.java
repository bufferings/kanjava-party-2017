package com.example.staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.LoginUser;

@RestController
@RequestMapping("staff/api")
public class StaffWriteApi {

  private static final String ORDER_SERVICE_URL = "http://order-service/";

  private OAuth2RestTemplate oAuth2RestTemplate;

  @Autowired
  public StaffWriteApi(OAuth2RestTemplate oAuth2RestTemplate) {
    this.oAuth2RestTemplate = oAuth2RestTemplate;
  }

  @PostMapping("order-items/{orderItemId}/deliver")
  public void deliverOrder(@AuthenticationPrincipal LoginUser loginUser,
      @PathVariable("orderItemId") String orderItemId) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("deiveryPersonId", String.valueOf(loginUser.getId()));
    map.add("deliveryPersonName", loginUser.getName());
    oAuth2RestTemplate.postForObject(ORDER_SERVICE_URL + "order-items/{orderItemId}/deliver", map, Void.class,
        orderItemId);
  }

}
