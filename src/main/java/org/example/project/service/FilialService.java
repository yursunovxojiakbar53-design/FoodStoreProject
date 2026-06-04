package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.FilialDto;
import org.example.project.entity.Filial;
import org.example.project.entity.Location;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.FilialRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilialService {
    private final FilialRepo filialRepo;

    public ApiResponse add(FilialDto dto) {
        Location location = Location.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        Filial filial = Filial.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .workHours(dto.getWorkHours())
                .phoneNumber(dto.getPhoneNumber())
                .location(location)
                .build();

        filialRepo.save(filial);
        return new ApiResponse("Filial added", true, filial);
    }

    public ApiResponse update(Integer id, FilialDto dto) {
        Filial filial = filialRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Location location = Location.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        filial.setTitle(dto.getTitle());
        filial.setDescription(dto.getDescription());
        filial.setWorkHours(dto.getWorkHours());
        filial.setPhoneNumber(dto.getPhoneNumber());
        filial.setLocation(location);
n        filialRepo.save(filial);
        return new ApiResponse("Filial updated", true, filial);
    }

    public ApiResponse delete(Integer id) {
        Filial filial = filialRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        filialRepo.delete(filial);
        return new ApiResponse("Filial deleted", true, null);
    }

    public List<Filial> getAll() {
        return filialRepo.findAll();
    }

    public Filial getOne(Integer id) {
        return filialRepo.findById(id).orElse(null);
    }
}
