package org.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressDto {
    @NotNull(message = "Latitude berilishi shart")
    private Double latitude;
    @NotNull(message = "Longitude berilishi shart")
    private Double longitude;
    @NotBlank(message = "Address sarlavhasi bo'sh bo'lmasligi kerak")
    private String title;
    private Integer house;
    private Integer floor;
    private Integer extrance;
    private Integer apartment;
    private String noteToCourier;
}
