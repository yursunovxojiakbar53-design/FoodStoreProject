package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.AboutAsDto;
import org.example.project.entity.AboutAs;
import org.example.project.entity.Attachment;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.AboutAsRepo;
import org.example.project.repository.AttachmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AboutAsService {
    private final AboutAsRepo aboutAsRepo;
    private final AttachmentRepository attachmentRepository;

    public ApiResponse create(AboutAsDto dto){
        Attachment attachment = null;
        if (dto.getAttachmentId() != null) attachment = attachmentRepository.findById(dto.getAttachmentId()).orElseThrow(() -> new NotFoundException("Attachment not found"));
        AboutAs about = AboutAs.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .phoneNumber(dto.getPhoneNumber())
                .telegramBotUrl(dto.getTelegramBotUrl())
                .attachment(attachment)
                .build();
        aboutAsRepo.save(about);
        return ApiResponse.builder().message("AboutAs created").status(true).data(about).build();
    }

    public ApiResponse update(Integer id, AboutAsDto dto){
        AboutAs about = aboutAsRepo.findById(id).orElseThrow(() -> new NotFoundException("About not found"));
        Attachment attachment = null;
        if (dto.getAttachmentId() != null) attachment = attachmentRepository.findById(dto.getAttachmentId()).orElseThrow(() -> new NotFoundException("Attachment not found"));
        about.setTitle(dto.getTitle());
        about.setDescription(dto.getDescription());
        about.setPhoneNumber(dto.getPhoneNumber());
        about.setTelegramBotUrl(dto.getTelegramBotUrl());
        about.setAttachment(attachment);
        aboutAsRepo.save(about);
        return ApiResponse.builder().message("AboutAs updated").status(true).data(about).build();
    }

    public ApiResponse delete(Integer id){
        AboutAs about = aboutAsRepo.findById(id).orElseThrow(() -> new NotFoundException("About not found"));
        aboutAsRepo.delete(about);
        return ApiResponse.builder().message("AboutAs deleted").status(true).build();
    }

    public ApiResponse list(){
        List<AboutAs> list = aboutAsRepo.findAll();
        return ApiResponse.builder().message("About retrieved").status(true).data(list).build();
    }

    public ApiResponse get(Integer id){
        AboutAs about = aboutAsRepo.findById(id).orElseThrow(() -> new NotFoundException("About not found"));
        return ApiResponse.builder().message("About retrieved").status(true).data(about).build();
    }
}

