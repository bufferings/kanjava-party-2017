package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableEurekaClient
@EnableKafka
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public WebMvcConfigurerAdapter forwardToIndex() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/staff").setViewName("redirect:/staff/");
        registry.addViewController("/staff/").setViewName("forward:/staff/index.html");

        registry.addViewController("/guest").setViewName("redirect:/guest/");
        registry.addViewController("/guest/").setViewName("forward:/guest/index.html");
      }
    };
  }

  @Bean
  @LoadBalanced
  public OAuth2RestTemplate oAuth2RestTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
    return new OAuth2RestTemplate(resource, context);
  }
}
