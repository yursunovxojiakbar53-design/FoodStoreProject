package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.LocationDto;
import org.example.project.extra.ApiResponse;
import org.example.project.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LocationDto dto){
        ApiResponse apiResponse = locationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody LocationDto dto){
        ApiResponse apiResponse = locationService.update(id, dto);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        ApiResponse apiResponse = locationService.delete(id);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<?> list(){
        ApiResponse apiResponse = locationService.getAll();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Integer id){
        ApiResponse apiResponse = locationService.getById(id);
        return ResponseEntity.ok(apiResponse);
    }
}

