package com.book.network.service;

import com.book.network.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String userId, Notification notification) {
        log.info("Sending notification to user {} with payload {}", userId, notification);
        messagingTemplate.convertAndSendToUser(userId, "/notifications", notification);
    }
}
