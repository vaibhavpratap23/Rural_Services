package com.gigfinder.repository;

import com.gigfinder.model.Message;
import com.gigfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrderByCreatedAtDesc(User sender, User receiver);
    List<Message> findByReceiverOrderByCreatedAtDesc(User receiver);
    List<Message> findBySenderOrderByCreatedAtDesc(User sender);
    
    @Query("SELECT m FROM Message m WHERE (m.sender = ?1 AND m.receiver = ?2) OR (m.sender = ?2 AND m.receiver = ?1) ORDER BY m.createdAt DESC")
    List<Message> findConversationBetweenUsers(User user1, User user2);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = ?1 AND m.isRead = false")
    Long countUnreadMessagesByReceiver(User receiver);
}
