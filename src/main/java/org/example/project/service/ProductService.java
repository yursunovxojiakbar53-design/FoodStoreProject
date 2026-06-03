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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final AttachmentRepository attachmentRepository;
    private final CategoryRepo categoryRepo;

    public ApiResponse addProduct(ProductDto dto) {
        Category category = categoryRepo.findById(dto.getCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Attachment attachment = attachmentRepository.findById(dto.getAttachmentId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Product product = Product.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .price(dto.getPrice())
                        .discountPrice(dto.getDiscountPrice())
                        .currentPrice(dto.getCurrentPrice())
                        .weight(dto.getWeight())
                .category(category)
                .attachment(attachment)
                .isAvailable(dto.isAvailable())

                        .build();
        productRepo.save(product);
        return new ApiResponse("Added ",true,product);
    }

    public ApiResponse updateProduct(ProductDto dto, Integer id) {
        Category category = categoryRepo.findById(dto.getCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Attachment attachment = attachmentRepository.findById(dto.getAttachmentId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Product product = productRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setAttachment(attachment);
        product.setCategory(category);
        product.setWeight(dto.getWeight());
        product.setCurrentPrice(dto.getCurrentPrice());
        product.setAvailable(dto.isAvailable());
        product.setDiscountPrice(dto.getDiscountPrice());
        productRepo.save(product);
        return new ApiResponse("Updated ",true,product);
    }

    public ApiResponse deleteProduct(Integer id) {
        Product product = productRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        product.setAvailable(false);
        return new ApiResponse("Deleted ",true,product);
    }
}
