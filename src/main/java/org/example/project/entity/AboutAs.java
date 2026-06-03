package org.example.project.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.example.project.extra.AbstractEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AboutAs extends AbstractEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

    private String telegramBotUrl;
}
