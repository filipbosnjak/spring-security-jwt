package com.security.jwt.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
public class JwtResponse {
  private String token;
  private UUID id;
  private String username;
  private List<String> userSpecificData;
}
