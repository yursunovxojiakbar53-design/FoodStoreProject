package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.AddressDto;
import org.example.project.entity.Address;
import org.example.project.entity.Location;
import org.example.project.entity.Users;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.AddressRepo;
import org.example.project.repository.UsersRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final UsersRepo usersRepo;
    private final AddressRepo addressRepo;

    private Users getUserFromAuthentication(Authentication auth) {
        return usersRepo.findByEmail(auth.getName()).orElseThrow(() -> new NotFoundException("User not authenticated"));
    }
    public ApiResponse addAddress(Authentication authentication, AddressDto dto) {
        Users user = getUserFromAuthentication(authentication);
        Location location= Location.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        Address address = Address.builder()
                .location(location)
                .user(user)
                .floor(dto.getFloor())
                .title(dto.getTitle())
                .house(dto.getHouse())
                .extrance(dto.getExtrance())
                .noteToCourier(dto.getNoteToCourier())
                .apartment(dto.getApartment())
                .build();

        Address saved = addressRepo.save(address);
        return ApiResponse.builder().message("Address added successfully").status(true).data(saved).build();
    }

    public ApiResponse updateAddress(Authentication authentication, Integer addressId, AddressDto dto) {
        Users user = getUserFromAuthentication(authentication);
        Address address = addressRepo.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new NotFoundException("Address not found"));

        Location location= Location.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        address.setLocation(location);
        address.setFloor(dto.getFloor());
        address.setTitle(dto.getTitle());
        address.setHouse(dto.getHouse());
        address.setExtrance(dto.getExtrance());
        address.setNoteToCourier(dto.getNoteToCourier());
        address.setApartment(dto.getApartment());

        addressRepo.save(address);
        return ApiResponse.builder().message("Address updated successfully").status(true).build();
    }
    public ApiResponse deleteAddress(Authentication authentication, Integer addressId) {
        Users user = getUserFromAuthentication(authentication);
        Address address = addressRepo.findByIdAndUserId(addressId, user.getId()).orElseThrow(() -> new NotFoundException("Address not found"));

        addressRepo.delete(address);
        return ApiResponse.builder().message("Address deleted successfully").status(true).build();
    }
    public ApiResponse getAddresses(Authentication authentication) {
        Users user = getUserFromAuthentication(authentication);
        return ApiResponse.builder().message("Addresses retrieved successfully").status(true).data(addressRepo.findAllByUserId(user.getId())).build();
    }
}
