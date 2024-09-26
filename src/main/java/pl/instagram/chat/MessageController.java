package pl.instagram.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.chat.models.CreateMessage;
import pl.instagram.chat.models.MessageEntity;
import pl.instagram.chat.services.MessageService;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/latest-users")
    public ResponseEntity<List<String>> getLatestConversationsUsers(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size){

        List<String> foundSendersAccountsIds = messageService.getLatestSendersAccountsIds(page, size);

        return ResponseEntity.ok(foundSendersAccountsIds);
    }

    @GetMapping("/{otherUserAccountId}")
    public ResponseEntity<Page<MessageEntity>> getConversationMessages(@PathVariable("otherUserAccountId") String otherUserAccountId, Pageable pageable){

        Page<MessageEntity> foundConversationMessagesPage = messageService.getConversationMessages(otherUserAccountId, pageable);

        return ResponseEntity.ok(foundConversationMessagesPage);
    }

    @PostMapping
    public ResponseEntity<MessageEntity> createMessage(@RequestBody @Valid CreateMessage createMessage) throws JsonProcessingException {

        MessageEntity createdMessage = messageService.create(createMessage.receiverAccountId(), createMessage.content());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }
}
