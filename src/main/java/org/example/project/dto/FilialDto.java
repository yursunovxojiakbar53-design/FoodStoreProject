package org.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilialDto {
    @NotBlank(message = "Filial nomi bo'sh bo'lmasligi kerak")
    private String title;
    private String description;
    private String workHours;
    private String phoneNumber;
    @NotNull(message = "Latitude berilishi shart")
    private Double latitude;
    @NotNull(message = "Longitude berilishi shart")
    private Double longitude;
}
