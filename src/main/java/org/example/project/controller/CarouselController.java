package org.example.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project.dto.CarouselCreateDto;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.service.CarouselService;
import org.example.project.valid.RequirePermission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CarouselController {

    private final CarouselService carouselService;


    @GetMapping("/api/v1/open/carousel")
    public ResponseEntity<?> getActive() {
        return ResponseEntity.ok(carouselService.getActive());
    }


    @RequirePermission(Perms.MANAGE_CAROUSEL)
    @GetMapping("/api/v1/secure/carousel")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(carouselService.getAll());
    }

    @RequirePermission(Perms.MANAGE_CAROUSEL)
    @PostMapping("/api/v1/secure/carousel")
    public ResponseEntity<?> create(@RequestBody @Valid CarouselCreateDto dto) {
        ApiResponse response = carouselService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequirePermission(Perms.MANAGE_CAROUSEL)
    @PutMapping("/api/v1/secure/carousel/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid CarouselCreateDto dto) {
        return ResponseEntity.ok(carouselService.update(id, dto));
    }

    @RequirePermission(Perms.MANAGE_CAROUSEL)
    @DeleteMapping("/api/v1/secure/carousel/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(carouselService.delete(id));
    }
}