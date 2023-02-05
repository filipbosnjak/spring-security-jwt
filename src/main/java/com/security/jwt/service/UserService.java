package com.security.jwt.service;

import com.security.jwt.entity.User;
import com.security.jwt.security.UserContext;
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


    /**
     * Sets up user context - loads it from the database
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        Optional<User> dbUser = userRepository.findByUserName(userName);

        if(dbUser.isEmpty()) {
            throw new UsernameNotFoundException("User: " + userName + " not found");
        }

        //Check if user enabled etc.

        //Attach data to the DB user object (e.g. personalized posts, menu items etc.)

        return new UserContext(dbUser.get(), getUserSpecificData());

    }

    public static UserContext getLoggedInUser() {
        return (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private List<String> getUserSpecificData() {
        return Arrays.asList("user", "specific", "data");
    }
}

