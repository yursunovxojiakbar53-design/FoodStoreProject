package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.CarouselCreateDto;
import org.example.project.dto.CarouselDto;
import org.example.project.entity.Attachment;
import org.example.project.entity.Carousel;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.AttachmentRepository;
import org.example.project.repository.CarouselRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarouselService {

    private final CarouselRepo carouselRepo;
    private final AttachmentRepository attachmentRepository;

    public ApiResponse getActive() {
        List<CarouselDto> result = carouselRepo
                .findByIsActiveTrueOrderByOrderIndexAsc()
                .stream()
                .map((Object carousel) -> toDto((Carousel) carousel))
                .toList();
        return new ApiResponse("OK", true, result);
    }

    public ApiResponse getAll() {
        List<CarouselDto> result = new ArrayList<>();
        for (Carousel carousel : carouselRepo.findAll()) {
            CarouselDto dto = toDto(carousel);
            result.add(dto);
        }
        return new ApiResponse("OK", true, result);
    }

    public ApiResponse create(CarouselCreateDto dto) {
        Carousel carousel = new Carousel();
        carousel.setTitle(dto.getTitle());
        carousel.setDescription(dto.getDescription());
        carousel.setOrderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : 0);
        carousel.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        if (dto.getAttachmentId() != null) {
            Attachment attachment = attachmentRepository.findById(dto.getAttachmentId()).orElseThrow(() -> new NotFoundException("Rasm topilmadi"));
            carousel.setAttachment(attachment);
        }

        carouselRepo.save(carousel);
        return new ApiResponse("Carousel yaratildi", true, toDto(carousel));
    }

    public ApiResponse update(Integer id, CarouselCreateDto dto) {
        Carousel carousel = carouselRepo.findById(id).orElseThrow(() -> new NotFoundException("Carousel topilmadi: " + id));

        carousel.setTitle(dto.getTitle());
        carousel.setDescription(dto.getDescription());

        if (dto.getOrderIndex() != null)
            carousel.setOrderIndex(dto.getOrderIndex());

        if (dto.getIsActive() != null)
            carousel.setIsActive(dto.getIsActive());

        if (dto.getAttachmentId() != null) {
            Attachment attachment = attachmentRepository.findById(dto.getAttachmentId()).orElseThrow(() -> new NotFoundException("Rasm topilmadi"));
            carousel.setAttachment(attachment);
        }

        carouselRepo.save(carousel);
        return new ApiResponse("Yangilandi", true, toDto(carousel));
    }

    public ApiResponse delete(Integer id) {
        Carousel carousel = carouselRepo.findById(id).orElseThrow(() -> new NotFoundException("Carousel topilmadi: " + id));
        carouselRepo.delete(carousel);
        return new ApiResponse("O'chirildi", true, null);
    }

    private CarouselDto toDto(Carousel carousel) {
        String imageUrl = carousel.getAttachment() != null ? "/api/v1/files/view/" + carousel.getAttachment().getId() : null;

        return CarouselDto.builder()
                .id(carousel.getId())
                .title(carousel.getTitle())
                .description(carousel.getDescription())
                .imageUrl(imageUrl)
                .orderIndex(carousel.getOrderIndex())
                .build();
    }
}