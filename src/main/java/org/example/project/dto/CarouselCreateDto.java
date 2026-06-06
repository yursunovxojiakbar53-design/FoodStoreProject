package org.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CarouselCreateDto {

    @NotBlank(message = "Sarlavha bo'sh bo'lmasligi kerak")
    private String title;

    private String description;

    private Integer attachmentId;

    private Integer orderIndex;

    private Boolean isActive;
}