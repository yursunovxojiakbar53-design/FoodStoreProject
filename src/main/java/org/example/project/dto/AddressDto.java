package org.example.project.dto;

import lombok.Data;

@Data
public class AddressDto {
    private Double latitude;
    private Double longitude;
    private String title;
    private Integer house;
    private Integer floor;
    private Integer extrance;
    private Integer apartment;
    private String noteToCourier;
}
