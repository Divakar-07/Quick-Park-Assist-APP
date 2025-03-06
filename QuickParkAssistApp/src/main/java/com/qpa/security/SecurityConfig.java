package com.qpa.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

      @Bean 
      public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
          http
                  .csrf(csrf -> csrf.disable())
                  .authorizeHttpRequests(auth -> auth
                                  .anyRequest().permitAll() // ✅ Allow all requests without authentication
                  )
                  .formLogin(login -> login.disable())  // ❌ Disable default login form
                  .httpBasic(basic -> basic.disable()); // ❌ Disable basic authentication popup
  
          return http.build();
      }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
