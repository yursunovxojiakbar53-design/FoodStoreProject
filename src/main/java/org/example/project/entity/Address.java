package org.example.project.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Embedded;
import lombok.*;
import org.example.project.extra.AbstractEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends AbstractEntity {
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users user;
    @Embedded
    private Location location;
    private String title;
    private Integer house;
    private Integer floor;
    private Integer extrance;
    private Integer apartment;
    private String noteToCourier;

}
