package com.gigfinder.config;

import com.gigfinder.security.JwtAuthenticationFilter;
import com.gigfinder.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable())
          .userDetailsService(userDetailsService) // ➤ this tells Spring to use your UserDetailsService
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/api/auth/**","/api/categories/**").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/jobs").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/jobs/me").authenticated()
              .requestMatchers(HttpMethod.GET, "/api/jobs/search").authenticated()
              .requestMatchers(HttpMethod.POST, "/api/jobs").authenticated()
              .requestMatchers(HttpMethod.PUT, "/api/jobs/*/accept").authenticated()
              .requestMatchers(HttpMethod.PUT, "/api/jobs/*/start").authenticated()
              .requestMatchers(HttpMethod.PUT, "/api/jobs/*/complete").authenticated()
                                        .requestMatchers(HttpMethod.POST, "/api/jobs/*/rating").authenticated()
                          .requestMatchers(HttpMethod.POST, "/api/jobs/reports").authenticated()
                          .requestMatchers(HttpMethod.GET, "/api/notifications/**").authenticated()
                          .requestMatchers(HttpMethod.PUT, "/api/notifications/**").authenticated()
                          .requestMatchers(HttpMethod.GET, "/api/jobs/ratings").authenticated()
                          .requestMatchers(HttpMethod.POST, "/api/payments").authenticated()
                          .requestMatchers(HttpMethod.PUT, "/api/payments/**").authenticated()
                          .requestMatchers(HttpMethod.GET, "/api/payments/**").authenticated()
                          // Scheduling endpoints
                          .requestMatchers(HttpMethod.POST, "/api/scheduling/**").authenticated()
                          .requestMatchers(HttpMethod.PUT, "/api/scheduling/**").authenticated()
                          .requestMatchers(HttpMethod.GET, "/api/scheduling/**").authenticated()
                          // Messaging endpoints
                          .requestMatchers(HttpMethod.POST, "/api/messages").authenticated()
                          .requestMatchers(HttpMethod.PUT, "/api/messages/**").authenticated()
                          .requestMatchers(HttpMethod.GET, "/api/messages/**").authenticated()
                          // Address management endpoints
                          .requestMatchers(HttpMethod.POST, "/api/addresses").authenticated()
                          .requestMatchers(HttpMethod.PUT, "/api/addresses/**").authenticated()
                          .requestMatchers(HttpMethod.GET, "/api/addresses/**").authenticated()
                          .requestMatchers(HttpMethod.DELETE, "/api/addresses/**").authenticated()
                          // Service package endpoints
                          .requestMatchers(HttpMethod.POST, "/api/service-packages").authenticated()
                          .requestMatchers(HttpMethod.PUT, "/api/service-packages/**").authenticated()
                          .requestMatchers(HttpMethod.GET, "/api/service-packages/**").authenticated()
                          .requestMatchers(HttpMethod.DELETE, "/api/service-packages/**").authenticated()
                          // Worker endpoints
                          .requestMatchers(HttpMethod.GET, "/api/workers").permitAll()
                          .requestMatchers(HttpMethod.GET, "/api/workers/me").authenticated()
                          .requestMatchers(HttpMethod.PUT, "/api/workers/verification").authenticated()
                          .requestMatchers(HttpMethod.PUT, "/api/workers/availability").authenticated()
                          .requestMatchers(HttpMethod.GET, "/api/workers/available").permitAll()
                          // Wallet endpoints
                          .requestMatchers(HttpMethod.GET, "/api/wallet/balance").authenticated()
                          .requestMatchers(HttpMethod.POST, "/api/wallet/add-money").authenticated()
                          .requestMatchers(HttpMethod.POST, "/api/wallet/withdraw").authenticated()
                          .requestMatchers(HttpMethod.GET, "/api/wallet/transactions").authenticated()
                          // Admin endpoints (restricted to admin users)
                          .requestMatchers("/api/admin/**").hasRole("ADMIN")
                          .anyRequest().authenticated()
          )
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
