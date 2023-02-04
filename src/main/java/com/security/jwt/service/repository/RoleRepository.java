package com.security.jwt.service.repository;

import com.security.jwt.entity.Role;
import com.security.jwt.entity.helper.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByRoleName(RoleName roleName);
}
