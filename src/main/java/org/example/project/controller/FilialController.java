package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.FilialDto;
import org.example.project.entity.Filial;
import org.example.project.extra.ApiResponse;
import org.example.project.service.FilialService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filials")
@RequiredArgsConstructor
public class FilialController {
    private final FilialService filialService;
n    @PostMapping
    public ApiResponse add(@RequestBody FilialDto dto) {
        return filialService.add(dto);
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Integer id, @RequestBody FilialDto dto) {
        return filialService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return filialService.delete(id);
    }

    @GetMapping
    public List<Filial> getAll() {
        return filialService.getAll();
    }

    @GetMapping("/{id}")
    public Filial getOne(@PathVariable Integer id) {
        return filialService.getOne(id);
    }
}
