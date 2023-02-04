package com.security.jwt.service;

import com.security.jwt.entity.User;
import com.security.jwt.security.CustomUserDetails;
import com.security.jwt.service.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        Optional<User> dbUser = userRepository.findByUserName(userName);

        if(dbUser.isEmpty()) {
            throw new UsernameNotFoundException("User: " + userName + " not found");
        }

        //Check if user enabled etc.

        //Attach data to the DB user object (e.g. personalized posts, menu items etc.)

        return new CustomUserDetails(dbUser.get(), getData());

    }

    public static CustomUserDetails getLoggedInUser() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private List<String> getData() {
        return Arrays.asList("user", "specific", "data");
    }
}

