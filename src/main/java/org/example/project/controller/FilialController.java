package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.FilialDto;
import org.example.project.entity.Filial;
import org.example.project.extra.ApiResponse;
import org.example.project.service.FilialService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/filials")
@RequiredArgsConstructor
public class FilialController {
    private final FilialService filialService;

   @PostMapping
    public ResponseEntity<?> add(@RequestBody FilialDto dto) {
        ApiResponse apiResponse = filialService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody FilialDto dto) {
        ApiResponse apiResponse = filialService.update(id, dto);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        ApiResponse apiResponse = filialService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(filialService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(filialService.getOne(id));
    }
}
