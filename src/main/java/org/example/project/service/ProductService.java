package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.ProductDto;
import org.example.project.entity.Attachment;
import org.example.project.entity.Category;
import org.example.project.entity.Product;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.AttachmentRepository;
import org.example.project.repository.CategoryRepo;
import org.example.project.repository.ProductRepo;
import org.example.project.exception.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final AttachmentRepository attachmentRepository;
    private final CategoryRepo categoryRepo;

    public ApiResponse addProduct(ProductDto dto) {
        Category category = categoryRepo.findById(dto.getCategoryId()).orElseThrow(() -> new NotFoundException("Category not found with id " + dto.getCategoryId()));
        Attachment attachment = attachmentRepository.findById(dto.getAttachmentId()).orElseThrow(() -> new NotFoundException("Attachment not found with id " + dto.getAttachmentId()));

        Product product = Product.builder()
                        .nameUz(dto.getNameUz())
                        .nameRu(dto.getNameRu())
                        .nameEng(dto.getNameEng())
                        .descriptionUz(dto.getDescriptionUz())
                        .descriptionRu(dto.getDescriptionRu())
                        .descriptionEng(dto.getDescriptionEn())
                        .price(dto.getPrice())
                        .discountPrice(dto.getDiscountPrice())
                        .currentPrice(dto.getCurrentPrice())
                        .stockQuantity(dto.getStockQuantity())
                        .weight(dto.getWeight())
                .category(category)
                .attachment(attachment)
                .isAvailable(dto.isAvailable())
                        .build();
        productRepo.save(product);
        return new ApiResponse("Added ",true,product);
    }

    public ApiResponse updateProduct(ProductDto dto, Integer id) {
        Category category = categoryRepo.findById(dto.getCategoryId()).orElseThrow(() -> new NotFoundException("Category not found with id " + dto.getCategoryId()));
        Attachment attachment = attachmentRepository.findById(dto.getAttachmentId()).orElseThrow(() -> new NotFoundException("Attachment not found with id " + dto.getAttachmentId()));
        Product product = productRepo.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id " + id));
        product.setNameUz(dto.getNameUz());
        product.setNameRu(dto.getNameRu());
        product.setNameEng(dto.getNameEng());
        product.setDescriptionUz(dto.getDescriptionUz());
        product.setDescriptionRu(dto.getDescriptionRu());
        product.setDescriptionEng(dto.getDescriptionEn());
        product.setPrice(dto.getPrice());
        product.setAttachment(attachment);
        product.setCategory(category);
        product.setWeight(dto.getWeight());
        product.setCurrentPrice(dto.getCurrentPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setAvailable(dto.isAvailable());
        product.setDiscountPrice(dto.getDiscountPrice());
        productRepo.save(product);
        return new ApiResponse("Updated ",true,product);
    }

    public ApiResponse deleteProduct(Integer id) {
        Product product = productRepo.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id " + id));
        product.setAvailable(false);
        productRepo.save(product);
        return new ApiResponse("Deleted ",true,product);
    }
}
