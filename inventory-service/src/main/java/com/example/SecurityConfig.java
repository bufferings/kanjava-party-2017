package com.example;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class SecurityConfig extends ResourceServerConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http.anonymous()
     .and().authorizeRequests()
     .antMatchers("/inventories/").permitAll()
     .antMatchers("/request/").authenticated();
    // @formatter:on
  }

}
