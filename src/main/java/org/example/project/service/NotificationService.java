package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.NotificationDto;
import org.example.project.entity.Notification;
import org.example.project.entity.Users;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.NotificationRepo;
import org.example.project.repository.UsersRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UsersRepo usersRepo;
    private final NotificationRepo notificationRepo;

    private Users getUser(Authentication auth){
        return usersRepo.findByEmail(auth.getName()).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public ApiResponse sendNotification(NotificationDto dto){
        Users user = usersRepo.findById(dto.getUserId()).orElseThrow(() -> new NotFoundException("Target user not found"));
        Notification notification = Notification.builder().message(dto.getMessage()).users(user).isRead(false).build();
        notificationRepo.save(notification);
        return ApiResponse.builder().message("Notification sent").status(true).data(notification).build();
    }

    public ApiResponse getNotifications(Authentication authentication){
        Users user = getUser(authentication);
        List<Notification> list = notificationRepo.findAllByUsers(user);
        return ApiResponse.builder().message("Notifications retrieved").status(true).data(list).build();
    }

    public ApiResponse markAsRead(Authentication authentication, Integer id){
        Users user = getUser(authentication);
        Notification notification = notificationRepo.findById(id).orElseThrow(() -> new NotFoundException("Notification not found"));
        if (!notification.getUsers().getId().equals(user.getId())) throw new NotFoundException("Notification not found for user");
        notification.setRead(true);
        notificationRepo.save(notification);
        return ApiResponse.builder().message("Marked as read").status(true).data(notification).build();
    }

}

