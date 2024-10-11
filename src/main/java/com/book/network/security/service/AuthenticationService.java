package com.book.network.security.service;

import com.book.network.dto.LoginRequest;
import com.book.network.dto.LoginResponse;
import com.book.network.dto.RegistrationRequest;
import jakarta.mail.MessagingException;

public interface AuthenticationService {

    void register(RegistrationRequest registrationRequest) throws MessagingException;

    LoginResponse login(LoginRequest request);

    void  activateAccount(String token) throws MessagingException;

}
