package org.example.project.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@EqualsAndHashCode
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    private double longitude;
    private double latitude;
}
