package com.group13.fleet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "customer-login-page", "admin-login-page", "user-login-page", "admin-login", "customer-login", "driver-login")
                        .permitAll()
                        .requestMatchers("/").hasAnyRole("CUSTOMER", "ADMIN","DRIVER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login").defaultSuccessUrl("/dashboard", true)
                                .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .sessionManagement(session -> session
                        .invalidSessionUrl("/login")
                );
        return http.build();
    }
}
