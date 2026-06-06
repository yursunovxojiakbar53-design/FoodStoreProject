package org.example.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.project.entity.Attachment;
import org.example.project.extra.Perms;
import org.example.project.service.AttachmentService;
import org.example.project.valid.RequirePermission;
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


    @Operation(summary = "Fayl yuklash")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequirePermission(Perms.MANAGE_ATTACHMENTS)
    public ResponseEntity<Attachment> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(service.upload(file));
    }

    @RequirePermission(Perms.MANAGE_ATTACHMENTS)
    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable Integer id) throws IOException {
        Attachment attachment = service.getAttachment(id);
        File file = new File(attachment.getPath());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .body(resource);
    }

    @RequirePermission(Perms.MANAGE_ATTACHMENTS)
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(@PathVariable Integer id) throws IOException {
        Attachment attachment = service.getAttachment(id);
        File file = new File(attachment.getPath());

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(attachment.getContentType()))
                .body(new InputStreamResource(new FileInputStream(file)));
    }

}