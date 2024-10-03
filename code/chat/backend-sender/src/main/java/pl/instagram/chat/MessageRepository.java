package pl.instagram.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.instagram.chat.models.MessageEntity;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository <MessageEntity, UUID> {

    @Query(value =
        """
            SELECT m1.sender_account_id
            FROM (
                SELECT MAX(m.creation_date) as max_date, m.sender_account_id
                FROM Messages m
                WHERE m.receiver_account_id = ?1
                GROUP BY m.sender_account_id
            ) m1
            ORDER BY m1.max_date DESC
            OFFSET ?2
            LIMIT ?3
        """,
        nativeQuery = true
    )
    List<String> findLatestMessagesSenders(String receiverAccountId, int offset, int size);

    @Query(value =
        """
            SELECT message
            FROM MessageEntity message
            WHERE 
                (message.receiverAccountId = :receiverAccountId AND message.senderAccountId = :senderAccountId) 
                OR 
                (message.receiverAccountId = :senderAccountId AND message.senderAccountId = :receiverAccountId)
        """
    )
    Page<MessageEntity> findConversationMessages(@Param("receiverAccountId") String receiverAccountId, @Param("senderAccountId") String senderAccountId, Pageable pageable);
}
