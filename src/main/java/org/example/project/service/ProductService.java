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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final AttachmentRepository attachmentRepository;
    private final CategoryRepo categoryRepo;

    public ApiResponse addProduct(ProductDto dto) {
        Category category = categoryRepo.findById(dto.getCategoryId()).orElseThrow(() -> new NotFoundException("Category not found with id " + dto.getCategoryId()));
        Attachment attachment = getAttachment(dto.getAttachmentId());

        Product product = Product.builder()
                        .nameUz(dto.getNameUz())
                        .nameRu(dto.getNameRu())
                        .nameEng(dto.getNameEng())
                        .descriptionUz(dto.getDescriptionUz())
                        .descriptionRu(dto.getDescriptionRu())
                        .descriptionEng(dto.getDescriptionEn())
                        .price(dto.getPrice())
                        .discountPrice(defaultNumber(dto.getDiscountPrice()))
                        .currentPrice(dto.getCurrentPrice() > 0 ? dto.getCurrentPrice() : dto.getPrice())
                        .stockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0)
                        .weight(dto.getWeight())
                        .category(category)
                        .attachment(attachment)
                        .isAvailable(true)
                        .build();
        productRepo.save(product);
        return new ApiResponse("Added ",true,product);
    }

    public ApiResponse updateProduct(ProductDto dto, Integer id) {
        Category category = categoryRepo.findById(dto.getCategoryId()).orElseThrow(() -> new NotFoundException("Category not found with id " + dto.getCategoryId()));
        Attachment attachment = getAttachment(dto.getAttachmentId());
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
        product.setCurrentPrice(dto.getCurrentPrice() > 0 ? dto.getCurrentPrice() : dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
        product.setAvailable(dto.isAvailable());
        product.setDiscountPrice(defaultNumber(dto.getDiscountPrice()));
        productRepo.save(product);
        return new ApiResponse("Updated ",true,product);
    }

    public ApiResponse deleteProduct(Integer id) {
        Product product = productRepo.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id " + id));
        product.setAvailable(false);
        productRepo.save(product);
        return new ApiResponse("Deleted ",true,product);
    }

    // ProductService ga qo'shing:

    public ApiResponse getAll(Integer categoryId, int page, int size) {
        if (categoryId != null) {
            List<Product> products = productRepo.findByCategoryIdAndIsAvailableTrue(categoryId);
            return new ApiResponse("OK", true, products);
        }
        Page<Product> products = productRepo.findByIsAvailableTrue(PageRequest.of(page, size));
        return new ApiResponse("OK", true, products);
    }


    public ApiResponse getOne(Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Mahsulot topilmadi: " + id));
        return new ApiResponse("OK", true, product);
    }

    private Attachment getAttachment(Integer attachmentId) {
        if (attachmentId == null) {
            return null;
        }
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NotFoundException("Attachment not found with id " + attachmentId));
    }

    private double defaultNumber(Double value) {
        return value == null ? 0 : value;
    }
}
