package com.gigfinder.controller;

import com.gigfinder.model.Notification;
import com.gigfinder.model.User;
import com.gigfinder.repository.NotificationRepository;
import com.gigfinder.repository.UserRepository;
import com.gigfinder.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications() {
        try {
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                return ResponseEntity.status(401).body(null);
            }

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Notification> notifications = notificationRepository
                    .findByUserOrderByCreatedAtDesc(user);

            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        try {
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                return ResponseEntity.status(401).body(null);
            }

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Notification> notifications = notificationRepository
                    .findByUserAndReadStatusOrderByCreatedAtDesc(user, false);

            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("Error fetching unread notifications: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long id) {
        try {
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                return ResponseEntity.status(401).body(null);
            }

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            int updatedRows = notificationRepository.markAsRead(user, id);
            
            if (updatedRows > 0) {
                return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
            } else {
                return ResponseEntity.status(404).body(Map.of("message", "Notification not found or already read"));
            }
        } catch (Exception e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Error marking notification as read: " + e.getMessage()));
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllNotificationsAsRead() {
        try {
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                return ResponseEntity.status(401).body(null);
            }

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            int updatedRows = notificationRepository.markAllAsRead(user);

            return ResponseEntity.ok(Map.of("message", "All notifications marked as read", "updatedCount", updatedRows));
        } catch (Exception e) {
            System.err.println("Error marking all notifications as read: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Error marking all notifications as read: " + e.getMessage()));
        }
    }

    @GetMapping("/count/unread")
    public ResponseEntity<Map<String, Long>> getUnreadNotificationCount() {
        try {
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                return ResponseEntity.status(401).body(null);
            }

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Long unreadCount = notificationRepository.countByUserAndReadStatus(user, false);

            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            System.err.println("Error counting unread notifications: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}






