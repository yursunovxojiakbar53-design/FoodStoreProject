package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.Attachment;
import org.example.project.exception.NotFoundException;
import org.example.project.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository repository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public Attachment upload(MultipartFile file) throws IOException, IOException {

        // Papkani yarat
        Path uploadPath = Path.of(uploadDir).toAbsolutePath();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Faylni saqlash
        Files.copy(file.getInputStream(), filePath);

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setSize(file.getSize());
        attachment.setContentType(file.getContentType());
        attachment.setPath(filePath.toString());  // absolute path

        return repository.save(attachment);
    }

    public File getFile(Integer id) {
        Attachment attachment = repository.findById(id).orElseThrow(() -> new NotFoundException("Attachment not found"));
        return new File(attachment.getPath());
    }


    public Attachment getAttachment(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attachment not found"));
    }
}