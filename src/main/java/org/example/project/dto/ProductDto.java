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
public class ProductDto {

    @NotBlank
    private String nameUz;
    private String nameRu;
    private String nameEng;

    private Integer attachmentId;

    @NotNull
    private Double price;

    private Double discountPrice;

    @NotNull
    private Double weight;

    private double currentPrice;
    @NotNull
    private Integer categoryId;

    private String descriptionUz;
    private String descriptionRu;
    private String descriptionEn;

    private Integer stockQuantity;

    @Builder.Default
    private boolean isAvailable = true;
}