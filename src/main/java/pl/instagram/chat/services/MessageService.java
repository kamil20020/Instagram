package pl.instagram.chat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.instagram.chat.models.MessageEntity;
import pl.instagram.chat.MessageRepository;
import pl.instagram.chat.exception.UserIsNotLoggedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    private final AuthService authService;

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
            Sort.by(Sort.Direction.DESC, "creationDate")
        );

        return messageRepository.findConversationMessages(loggedUserAccountId, otherUserAccountId, pageable);
    }

    public MessageEntity create(String receiverAccountId, String content) throws UserIsNotLoggedException {

        String loggedUserAccountId = authService.getLoggedUserAccountId();

        return messageRepository.save(
            MessageEntity.builder()
                .senderAccountId(loggedUserAccountId)
                .receiverAccountId(receiverAccountId)
                .content(content)
                .creationDate(LocalDateTime.now())
                .read(false)
            .build()
        );
    }
}