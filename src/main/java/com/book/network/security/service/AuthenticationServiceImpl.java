package com.book.network.security.service;

import com.book.network.dto.LoginRequest;
import com.book.network.dto.LoginResponse;
import com.book.network.dto.RegistrationRequest;
import com.book.network.entity.Role;
import com.book.network.entity.Token;
import com.book.network.entity.User;
import com.book.network.enums.EmailTemplateName;
import com.book.network.repository.RoleRepo;
import com.book.network.repository.TokenRepo;
import com.book.network.security.util.JwtTokenUtil;
import com.book.network.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final RoleRepo roleRepo;
    private final TokenRepo tokenRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Override
    public void register(RegistrationRequest registrationRequest) throws MessagingException {
        Role userRole = roleRepo.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));
        if (userService.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new DuplicateKeyException("Email already used. Please choose another one.");
        }
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        User savedUser = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(encodedPassword)
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userService.addUser(savedUser);
        sendEmailValidation(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new
                UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = (User) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("fullName", user.fullName());
        String jwtToken = jwtTokenUtil.generateToken(claims, user);
        return LoginResponse.builder().token(jwtToken).build();
    }

    @Override
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepo.findByToken(token).orElseThrow(() -> new
                RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendEmailValidation(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to your account.");
        }
        User user = userService.findByEmail(savedToken.getUser().getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userService.updateUser(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepo.save(savedToken);
    }

    private void sendEmailValidation(User user) throws MessagingException {
        String newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(user.getEmail(), user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT, activationUrl, newToken, "Account activation");
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationToken(6); // generate a token
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepo.save(token);
        return generatedToken;
    }

    private String generateActivationToken(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

}
