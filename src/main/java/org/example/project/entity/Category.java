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
public class Category extends AbstractEntity {
    private String nameUz;
    private String nameRu;
    private String nameEng;

    private String descriptionUz;
    private String descriptionRu;
    private String descriptionEn;

    @Column(nullable = false)
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

    @Column(nullable = false)
    private String telegramSticker;

    @Column(nullable = false)
    private boolean status;
}
