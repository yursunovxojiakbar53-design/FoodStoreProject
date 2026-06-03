package org.example.project.entity;
import jakarta.persistence.*;
import lombok.*;
import org.example.project.extra.AbstractEntity;
import org.springframework.format.Printer;

import java.util.LinkedHashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product  extends AbstractEntity {
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

    @Column(nullable = false)
    private double price;

    private double currentPrice;

    @Column(nullable = false)
    private double weight;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private double discountPrice;

    private String description;

    @Column(nullable = false)
    private boolean isAvailable;

    private Integer orderCount;
}
