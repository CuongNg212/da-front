package jwd.practice.shopservice.repository;

import jwd.practice.shopservice.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface Message_Repository extends JpaRepository<Messages, Integer> {
    List<Messages> findBySenderId(int senderId);
    List<Messages> findByReceiverId(int receiverId);
    List<Messages> findBySenderIdAndReceiverId(int senderId, int receiverId);
    List<Messages> findByReceiverIdAndSenderId(int receiverId, int senderId);


    @Query(value = """
        SELECT m.message_id, m.sender_id, m.receiver_id, m.message, m.created_at
                             FROM (
                                 SELECT message_id, sender_id, receiver_id, message, created_at,
                                        ROW_NUMBER() OVER (PARTITION BY sender_id ORDER BY created_at DESC) AS rn
                                 FROM shop.messages
                                 WHERE receiver_id = 1
                             ) m
                             WHERE m.rn = 1;
    """, nativeQuery = true)
    List<Object[]> findLatestMessagesBySender(@Param("receiverId") int receiverId);
}
