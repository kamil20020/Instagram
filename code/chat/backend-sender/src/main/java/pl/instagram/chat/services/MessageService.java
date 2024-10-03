package pl.instagram.chat.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.instagram.chat.MessagesProducer;
import pl.instagram.chat.models.MessageEntity;
import pl.instagram.chat.MessageRepository;
import pl.instagram.chat.exception.UserIsNotLoggedException;
import pl.instagram.chat.models.MessageResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    private final AuthService authService;
    private final MessagesProducer messagesProducer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<String> getLatestSendersAccountsIds(int page, int size) throws UserIsNotLoggedException {

        String loggedUserAccountId = authService.getLoggedUserAccountId();

        int offset = page * size;

        return messageRepository.findLatestMessagesSenders(loggedUserAccountId, offset, size);
    }

    public Page<MessageEntity> getConversationMessages(String otherUserAccountId, Pageable pageable) throws UserIsNotLoggedException {

        String loggedUserAccountId = authService.getLoggedUserAccountId();

        pageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Direction.ASC, "creationDate")
        );

        return messageRepository.findConversationMessages(loggedUserAccountId, otherUserAccountId, pageable);
    }

    public MessageEntity create(String receiverAccountId, String content) throws UserIsNotLoggedException, JsonProcessingException {

        String loggedUserAccountId = authService.getLoggedUserAccountId();

        MessageEntity createdMessage = messageRepository.save(
            MessageEntity.builder()
                .senderAccountId(loggedUserAccountId)
                .receiverAccountId(receiverAccountId)
                .content(content)
                .creationDate(LocalDateTime.now())
                .read(false)
            .build()
        );

        String convertedMessage = objectMapper.writeValueAsString(createdMessage);

        log.info("Sending message to rabbitmq {}", convertedMessage);

        messagesProducer.sendMessage(convertedMessage);

        return createdMessage;
    }
}
