package org.example.project.dto;

import lombok.Data;

@Data
public class CategoryReorderDto {

    private Integer categoryId;

    private Integer oldOrderId;

    private Integer newOrderId;
}