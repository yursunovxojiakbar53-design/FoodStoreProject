package org.example.project.telegram.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.project.enums.DeliverType;
import org.example.project.enums.PaymentType;
import org.example.project.telegram.enums.BotState;

import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "telegram_user_id", nullable = false, unique = true)
    private TelegramUser telegramUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BotState state = BotState.MAIN_MENU;

    private Integer page;
    private Integer categoryId;
    private Integer productId;
    private Integer orderId;
    private Integer addressId;
    private Integer cartItemId;

    private String phoneNumber;
    private Double latitude;
    private Double longitude;
    private String addressTitle;
    private String couponCode;

    @Enumerated(EnumType.STRING)
    private DeliverType deliverType;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private Integer filialId;

    @Column(length = 1000)
    private String draftMessage;

    @Builder.Default
    private Integer draftQuantity = 1;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void resetCheckout() {
        phoneNumber = null;
        latitude = null;
        longitude = null;
        addressTitle = null;
        couponCode = null;
        deliverType = null;
        paymentType = null;
        filialId = null;
        draftMessage = null;
        addressId = null;
    }
}
