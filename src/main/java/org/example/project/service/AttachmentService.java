package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.Attachment;
import org.example.project.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository repository;

    private final String uploadDir = "uploads/";

    public Attachment upload(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        File folder = new File(uploadDir);
        if (!folder.exists()) folder.mkdirs();

        File newFile = new File(uploadDir + fileName);
        file.transferTo(newFile);

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setSize(file.getSize());
        attachment.setContentType(file.getContentType());
        attachment.setPath(newFile.getAbsolutePath());

        return repository.save(attachment);
    }

    public File getFile(Integer id) {
        Attachment attachment = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));

        return new File(attachment.getPath());
    }
}