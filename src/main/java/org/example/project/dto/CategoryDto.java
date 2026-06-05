package org.example.project.dto;

import lombok.Data;

@Data
public class CategoryDto {

    private String nameUz;
    private String nameRu;
    private String nameEng;

    private String descriptionUz;
    private String descriptionRu;
    private String descriptionEn;

    private String telegramSticker;

    private String telegramDescription;

    private Integer orderId;

    private boolean active;

    private Integer attachmentId;
}