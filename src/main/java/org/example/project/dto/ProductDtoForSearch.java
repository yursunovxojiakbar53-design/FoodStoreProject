package org.example.project.dto;

import lombok.Data;
import org.example.project.entity.Filial;
import org.example.project.entity.Product;

@Data

public class ProductDtoForSearch {
    private Integer filial;
    private Integer product;
}
