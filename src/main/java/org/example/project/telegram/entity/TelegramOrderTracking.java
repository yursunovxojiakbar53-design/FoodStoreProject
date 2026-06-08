package org.example.project.telegram.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.project.enums.OrderStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_order_tracking", indexes = {
        @Index(name = "idx_tg_order_id", columnList = "orderId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramOrderTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer orderId;

    @Column(nullable = false)
    private Long chatId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus lastStatus;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
