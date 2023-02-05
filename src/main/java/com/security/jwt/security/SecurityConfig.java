package com.security.jwt.security;


import com.security.jwt.security.filter.AuthenticationTokenFilter;
import com.security.jwt.security.jwt.JwtUtils;
import com.security.jwt.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserService userService;

    private final JwtUtils jwtUtils;

    public SecurityConfig(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .addFilterBefore(
                        authenticationJwtTokenFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .httpBasic().disable()
                .authorizeHttpRequests( requests ->
                        requests
                                .requestMatchers("/signin", "/hello").permitAll()
                                .anyRequest().authenticated()
                )
                .cors().disable()
                .csrf().disable()
                .build();
    }

    @Bean
    public AuthenticationTokenFilter authenticationJwtTokenFilter() {
        return new AuthenticationTokenFilter(jwtUtils,userService);
    }
}
