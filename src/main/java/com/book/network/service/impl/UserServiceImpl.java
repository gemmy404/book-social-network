package com.book.network.service.impl;

import com.book.network.entity.User;
import com.book.network.repository.UserRepo;
import com.book.network.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl /*implements UserService*/ {

//    private final UserRepo userRepo;

//    @Override
//    @Transactional
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return userRepo.findByEmail(username).orElseThrow(() -> new
//                UsernameNotFoundException("User not found with email: " + username));
//    }

//    @Override
//    public void addUser(User user) {
//        userRepo.save(user);
//    }
//
//    @Override
//    public void updateUser(User user) {
//        userRepo.save(user);
//    }
//
//    @Override
//    public Optional<User> findByEmail(String email) {
//        return userRepo.findByEmail(email);
//    }

}
