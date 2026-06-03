package org.example.project.entity;

import jakarta.persistence.Entity;
import lombok.*;
import org.example.project.extra.AbstractEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends AbstractEntity {
    private double longitude;
    private double latitude;
}
