package com.book.network.repository;

import com.book.network.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo /*extends JpaRepository<Role, Integer>*/ {

    Optional<Role> findByName(String role);

}
