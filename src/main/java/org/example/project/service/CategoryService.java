package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.CategoryDto;
import org.example.project.dto.CategoryReorderDto;
import org.example.project.entity.Attachment;
import org.example.project.entity.Category;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.AttachmentRepository;
import org.example.project.repository.CategoryRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepo categoryRepo;
    private final AttachmentRepository attachmentRepository;

    public ApiResponse add(CategoryDto dto) {

        Attachment attachment = getAttachment(dto.getAttachmentId());

        Integer maxOrder = categoryRepo.findMaxOrderId();
        if (maxOrder == null) {
            maxOrder = 0;
        }
        Integer orderId = dto.getOrderId();

        if (orderId == null) {
            orderId = maxOrder + 1;
        }

        if (orderId < 1)
            return new ApiResponse("Order id 1 dan kichik bo'lishi mumkin emas", false, null);


        if (orderId > maxOrder + 1) {
            orderId = maxOrder + 1;
        }

        Category category = Category.builder()
                .nameUz(dto.getNameUz())
                .nameRu(dto.getNameRu())
                .nameEng(dto.getNameEng())
                .descriptionUz(dto.getDescriptionUz())
                .descriptionRu(dto.getDescriptionRu())
                .descriptionEn(dto.getDescriptionEn())
                .telegramSticker(defaultText(dto.getTelegramSticker()))
                .orderId(orderId)
                .status(dto.isActive())
                .attachment(attachment)
                .build();

        categoryRepo.save(category);

        return new ApiResponse("Added", true, category);
    }

    public ApiResponse update(Integer id, CategoryDto dto) {

        Category category = categoryRepo.findById(id).orElseThrow(() ->
                                new NotFoundException("Not found category with id " + id));

        Attachment attachment = getAttachment(dto.getAttachmentId());

        category.setNameUz(dto.getNameUz());
        category.setNameRu(dto.getNameRu());
        category.setNameEng(dto.getNameEng());
        category.setDescriptionUz(dto.getDescriptionUz());
        category.setDescriptionRu(dto.getDescriptionRu());
        category.setDescriptionEn(dto.getDescriptionEn());
        category.setTelegramSticker(defaultText(dto.getTelegramSticker()));
        category.setStatus(dto.isActive());
        category.setAttachment(attachment);

        categoryRepo.save(category);

        return new ApiResponse("Updated", true, category);
    }

    public ApiResponse delete(Integer id) {

        Category category = categoryRepo.findById(id)
                        .orElseThrow(() ->
                                        new NotFoundException("Category not found with id " + id));

        Integer deletedOrder = category.getOrderId();

        categoryRepo.delete(category);

        List<Category> categories =
                categoryRepo.findByOrderIdGreaterThanOrderByOrderId(deletedOrder);

        for (Category item : categories) {
            item.setOrderId(item.getOrderId() - 1);
        }

        categoryRepo.saveAll(categories);

        return new ApiResponse("Deleted", true, null);
    }

    public ApiResponse reorder(CategoryReorderDto dto) {

        Category category =
                categoryRepo.findById(dto.getCategoryId())
                        .orElseThrow(() ->
                                        new NotFoundException("Category not found with id " + dto.getCategoryId()));

        category.setOrderId(dto.getNewOrderId());

        categoryRepo.save(category);

        return new ApiResponse(
                category.getNameUz() +
                        " endi foydalanuvchilarga " +
                        dto.getNewOrderId() +
                        "-o'rinda ko'rsatiladi",
                true,
                null
        );
    }


    public Page<Category> getAll(int page, int size) {
        return categoryRepo.findAll(
                PageRequest.of(page, size, Sort.by("orderId"))
        );
    }

    public Category getOne(Integer id) {
        return categoryRepo.findById(id).orElse(null);
    }
    public ApiResponse getAllOpen() {
        List<Category> categories = categoryRepo.findAll();
        return new ApiResponse("OK", true, categories);
    }

    private Attachment getAttachment(Integer attachmentId) {
        if (attachmentId == null) {
            return null;
        }
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NotFoundException("Not found attachment with id " + attachmentId));
    }

    private String defaultText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
