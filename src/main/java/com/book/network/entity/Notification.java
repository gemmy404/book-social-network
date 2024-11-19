package com.book.network.entity;

import com.book.network.enums.NotificationStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Notification {

    private NotificationStatus status;
    private String message;
    private String bookTitle;

}
