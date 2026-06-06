package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.AddressDto;
import jakarta.validation.Valid;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.service.AddressService;
import org.example.project.valid.RequirePermission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;


    @RequirePermission(Perms.MANAGE_OWN_ADDRESS)
    @PostMapping
    public ResponseEntity<?> createAddress(Authentication authentication, @RequestBody @Valid AddressDto dto) {
        ApiResponse apiResponse=addressService.addAddress(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @RequirePermission(Perms.MANAGE_OWN_ADDRESS)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(Authentication authentication, @PathVariable Integer id, @RequestBody @Valid AddressDto dto) {
        ApiResponse apiResponse=addressService.updateAddress(authentication, id, dto);
        return ResponseEntity.status(200).body(apiResponse);
    }

    @RequirePermission(Perms.MANAGE_OWN_ADDRESS)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(Authentication authentication, @PathVariable Integer id) {
        ApiResponse apiResponse=addressService.deleteAddress(authentication, id);
        return ResponseEntity.ok(apiResponse);

    }

    @RequirePermission(Perms.MANAGE_OWN_ADDRESS)
    @GetMapping
    public ResponseEntity<?> getAddresses(Authentication authentication) {
        ApiResponse apiResponse=addressService.getAddresses(authentication);
        return ResponseEntity.ok(apiResponse);
    }
}
