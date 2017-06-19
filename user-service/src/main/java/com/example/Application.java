package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@SpringBootApplication
@EnableEurekaClient
@EnableResourceServer
@RestController
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @RequestMapping(path = "/me")
  public LoginUser me(@AuthenticationPrincipal LoginUser user) {
    return user;
  }

  @Bean
  public PrincipalExtractor principalExtractor() {
    return githubMap -> {
      LoginUser loginUser = new LoginUser();
      loginUser.setId((Integer) githubMap.get("id"));
      loginUser.setName((String) githubMap.get("login"));
      return loginUser;
    };
  }

  @Data
  public static class LoginUser {
    private Integer id;
    private String name;
  }

}
