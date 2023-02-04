package com.security.jwt.security;

import com.security.jwt.entity.Role;
import com.security.jwt.entity.User;
import com.security.jwt.entity.helper.RoleName;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private UUID userId;
    private String userName;
    private String password;
    private List<GrantedAuthority> roles;

    private List<String> userSpecificData;

    public CustomUserDetails(User user, List<String> userSpecificData) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.roles = user.getRoles().stream()
                .map(Role::getRoleName)
                .map(RoleName::getHumanReadable)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // Simulating getting something from the DB
        this.userSpecificData = userSpecificData;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
