package org.example.project.telegram.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.project.telegram.enums.BotLanguage;
import org.example.project.telegram.enums.BotState;

import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_users", indexes = {
        @Index(name = "idx_tg_chat_id", columnList = "chatId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long chatId;

    private String username;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BotLanguage language = BotLanguage.UZ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BotState currentState = BotState.MAIN_MENU;

    private Integer backendUserId;

    @Column(length = 2000)
    private String jwtToken;

    private String phoneNumber;

    private Integer currentPage;
    private Integer contextId;
    private Integer secondaryContextId;

    @Column(length = 2000)
    private String contextData;

    @Builder.Default
    private boolean admin = false;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
