package org.example.project.entity;
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
public class Cart extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "users_id")
    private Users users;

}
