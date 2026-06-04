package org.example.project.repository;

import org.example.project.entity.Notification;
import org.example.project.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUser(Users user);
}

