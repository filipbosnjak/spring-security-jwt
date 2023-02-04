package com.security.jwt.controller;

import com.security.jwt.security.CustomUserDetails;
import com.security.jwt.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        CustomUserDetails loggedInUser = UserService.getLoggedInUser();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Hello " + loggedInUser.getUsername() + " !");
    }
}
