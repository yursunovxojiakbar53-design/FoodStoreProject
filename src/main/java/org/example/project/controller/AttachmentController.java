package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.Attachment;
import org.example.project.service.AttachmentService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService service;


    @PostMapping("/upload")
    public ResponseEntity<Attachment> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(service.upload(file));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable Integer id) throws IOException {

        File file = service.getFile(id);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(@PathVariable Integer id) throws IOException {

        File file = service.getFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(new FileInputStream(file)));
    }

}