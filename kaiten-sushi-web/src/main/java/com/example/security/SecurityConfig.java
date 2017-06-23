package com.example.security;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
      .antMatcher("/**").authorizeRequests()
      .antMatchers("/", "/login**", "/endpoint", "/stomp.js").permitAll()
      .anyRequest().authenticated();
    
    http
      .logout()
      .logoutSuccessUrl("/")
      .permitAll();

    http.csrf().disable();
    // @formatter:on
  }

  @Bean
  public PrincipalExtractor principalExtractor() {
    return map -> {
      LoginUser loginUser = new LoginUser();
      loginUser.setId((Integer) map.get("id"));
      loginUser.setName((String) map.get("name"));
      return loginUser;
    };
  }

}
