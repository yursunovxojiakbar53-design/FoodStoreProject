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
public class Filial extends AbstractEntity {

    private String title;
    private String description;
    private String workHours;
    private String phoneNumber;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

}
