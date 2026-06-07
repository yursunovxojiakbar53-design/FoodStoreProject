package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.AboutAsDto;
import jakarta.validation.Valid;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.service.AboutAsService;
import org.example.project.valid.RequirePermission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/about")
@RequiredArgsConstructor
public class AboutAsController {
    private final AboutAsService aboutAsService;

    @RequirePermission(Perms.MANAGE_ABOUT)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid AboutAsDto dto){
        ApiResponse apiResponse = aboutAsService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @RequirePermission(Perms.MANAGE_ABOUT)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid AboutAsDto dto){
        ApiResponse apiResponse = aboutAsService.update(id, dto);
        return ResponseEntity.ok(apiResponse);
    }
    @RequirePermission(Perms.MANAGE_ABOUT)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        ApiResponse apiResponse = aboutAsService.delete(id);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<?> list(){
        ApiResponse apiResponse = aboutAsService.list();
        return ResponseEntity.ok(apiResponse);
    }

}

