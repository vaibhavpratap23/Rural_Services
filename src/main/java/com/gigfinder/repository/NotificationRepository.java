package com.gigfinder.repository;

import com.gigfinder.model.Notification;
import com.gigfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndReadStatusOrderByCreatedAtDesc(User user, Boolean readStatus);
    
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.readStatus = true WHERE n.user = :user AND n.id = :notificationId")
    int markAsRead(@Param("user") User user, @Param("notificationId") Long notificationId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.readStatus = true WHERE n.user = :user")
    int markAllAsRead(@Param("user") User user);
    
    Long countByUserAndReadStatus(User user, Boolean readStatus);
}






