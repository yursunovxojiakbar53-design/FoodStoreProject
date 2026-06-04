package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.AboutAsDto;
import org.example.project.extra.ApiResponse;
import org.example.project.service.AboutAsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/about")
@RequiredArgsConstructor
public class AboutAsController {
    private final AboutAsService aboutAsService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AboutAsDto dto){
        ApiResponse apiResponse = aboutAsService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AboutAsDto dto){
        ApiResponse apiResponse = aboutAsService.update(id, dto);
        return ResponseEntity.ok(apiResponse);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Integer id){
        ApiResponse apiResponse = aboutAsService.get(id);
        return ResponseEntity.ok(apiResponse);
    }
}

