package com.book.network.service;

import com.book.network.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService /*extends UserDetailsService*/ {

    void addUser(User user);

    void updateUser(User user);

    Optional<User> findByEmail(String email);

}
