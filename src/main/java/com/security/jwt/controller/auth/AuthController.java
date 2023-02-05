package com.security.jwt.controller.auth;

import com.security.jwt.entity.Role;
import com.security.jwt.entity.User;
import com.security.jwt.entity.helper.RoleName;
import com.security.jwt.security.CustomPasswordEncoder;
import com.security.jwt.security.UserContext;
import com.security.jwt.security.dto.LoginRequest;
import com.security.jwt.security.dto.RegisterRequest;
import com.security.jwt.security.jwt.JwtResponse;
import com.security.jwt.security.jwt.JwtUtils;
import com.security.jwt.service.UserService;
import com.security.jwt.service.repository.RoleRepository;
import com.security.jwt.service.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;

    private final CustomPasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtUtils jwtUtils,
            CustomPasswordEncoder passwordEncoder,
            RoleRepository roleRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest
    ) throws Exception {

        Authentication authentication;
        //TODO: TEST THIS
        User user = userRepository.findByUserName(loginRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Username not found in the Database"));

        //Check password
        if(!passwordEncoder.getPasswordEncoder().matches(loginRequest.getPassword(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body("Wrong Password");
        };

        //		**UN/COMMENT**
        user.setPassword(user.getPassword());

        //		user.setPassword(user.getPassword());

        try {
            authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    userService.loadUserByUsername(loginRequest.getUsername()), loginRequest.getPassword()));
        } catch (UsernameNotFoundException | DisabledException ex) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Exception: " + ex.getMessage());
        } catch (BadCredentialsException ex) {
            throw new HttpClientErrorException(
                    HttpStatus.BAD_REQUEST,
                    "Korisnicko ime ili lozinka su krivo uneseni." + "Exception: " + ex.getMessage());
        }

        return getJwtResponseResponseEntity(authentication);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest registerRequest
    ) throws Exception {

        if(userRepository.existsByUserName(registerRequest.getUsername())) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }

        if(!RoleName.roleExists(registerRequest.getRole())) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role does not exist");
        }

        Optional<Role> role = roleRepository.findByRoleName(RoleName.valueOf(registerRequest.getRole()));

        if(role.isEmpty()) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role not found");
        }

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setUserName(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.getPasswordEncoder().encode(registerRequest.getPassword()));
        newUser.setRoles(Set.of(role.get()));
        newUser.setEmail(registerRequest.getEmail());

        userRepository.save(newUser);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                userService.loadUserByUsername(
                                        newUser.getUserName()), newUser.getPassword()));

        //TODO: At this point user registered and authenticated -> send email

        return getJwtResponseResponseEntity(authentication);
    }

    private ResponseEntity<JwtResponse> getJwtResponseResponseEntity(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserContext userDetails = (UserContext) authentication.getPrincipal();
        ResponseCookie jwt = jwtUtils.generateJwtCookie(((UserContext) authentication.getPrincipal()).getUsername());


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwt.toString())
                .body(
                        new JwtResponse(
                                jwt.toString(),
                                userDetails.getUserId(),
                                userDetails.getUsername(),
                                userDetails.getUserSpecificData()
                                ));
    }
}
