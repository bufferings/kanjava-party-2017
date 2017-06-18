package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableEurekaClient
@EnableOAuth2Sso
public class SushiApplication extends WebSecurityConfigurerAdapter {

  public static void main(String[] args) {
    SpringApplication.run(SushiApplication.class, args);
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
      .antMatcher("/**").authorizeRequests()
      .antMatchers("/", "/login**").permitAll()
      .anyRequest().authenticated();
    
    http
      .logout()
      .logoutSuccessUrl("/")
      .permitAll();

    http.csrf().disable();
    // @formatter:on
  }

  @Bean
  public WebMvcConfigurerAdapter forwardToIndex() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/staff/").setViewName("forward:/staff/index.html");
        registry.addViewController("/guest/").setViewName("forward:/guest/index.html");
      }
    };
  }

}
