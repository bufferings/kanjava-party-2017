package com.example;

import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@SpringBootApplication
@EnableEurekaClient
@EnableResourceServer
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public RequestDumperFilter requestDumperFilter() {
    return new RequestDumperFilter();
  }

  @RestController
  public static class UserController {

    @RequestMapping(path = "/me")
    public ResponseEntity<User> me(OAuth2Authentication oAuth2Authentication) {
      return new ResponseEntity<>((User) oAuth2Authentication.getPrincipal(), HttpStatus.OK);
    }

  }

  @Bean
  public PrincipalExtractor principalExtractor() {
    return map -> {
      User user = new User();
      user.setId((Integer) map.get("id"));
      user.setName((String) map.get("login"));
      return user;
    };
  }

  @Data
  public static class User {
    private Integer id;
    private String name;
  }

}
