package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.LocationDto;
import org.example.project.entity.Location;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.LocationRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepo locationRepo;

    public ApiResponse create(LocationDto dto){
        Location location = Location.builder().latitude(dto.getLatitude()).longitude(dto.getLongitude()).build();
        locationRepo.save(location);
        return ApiResponse.builder().message("Location created").status(true).data(location).build();
    }

    public ApiResponse update(Integer id, LocationDto dto){
        Location location = locationRepo.findById(id).orElseThrow(() -> new NotFoundException("Location not found"));
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        locationRepo.save(location);
        return ApiResponse.builder().message("Location updated").status(true).data(location).build();
    }

    public ApiResponse delete(Integer id){
        Location location = locationRepo.findById(id).orElseThrow(() -> new NotFoundException("Location not found"));
        locationRepo.delete(location);
        return ApiResponse.builder().message("Location deleted").status(true).build();
    }

    public ApiResponse getAll(){
        List<Location> list = locationRepo.findAll();
        return ApiResponse.builder().message("Locations retrieved").status(true).data(list).build();
    }

    public ApiResponse getById(Integer id){
        Location location = locationRepo.findById(id).orElseThrow(() -> new NotFoundException("Location not found"));
        return ApiResponse.builder().message("Location retrieved").status(true).data(location).build();
    }
}

