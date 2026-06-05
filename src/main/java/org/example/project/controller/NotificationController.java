package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.NotificationDto;
import org.example.project.extra.ApiResponse;
import org.example.project.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> send(@RequestBody NotificationDto dto){
        ApiResponse apiResponse = notificationService.sendNotification(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<?> my(Authentication authentication){
        ApiResponse apiResponse = notificationService.getNotifications(authentication);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/read/{id}")
    public ResponseEntity<?> markRead(Authentication authentication, @PathVariable Integer id){
        ApiResponse apiResponse = notificationService.markAsRead(authentication, id);
        return ResponseEntity.ok(apiResponse);
    }
}

