package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rest/messages")
@PreAuthorize("hasAnyRole('ROLE_USER')")
public class MessageRestController {

    private final MessageService messageService;

    public MessageRestController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping(value = "/user/{id}", produces = "application/json")
    public List<Message> getUnreadMessagesForUser(@PathVariable UUID id) {
        return messageService.getUnreadMessagesByUserId(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> oneMessage(@PathVariable UUID id) {
        return
                messageService.getMessageById(id).map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody @Valid Message message) {
        return
                messageService.wasThereSuchMessageInRoom(message.getRoom(), message.getText()) ?
                        new ResponseEntity<Message>(HttpStatus.UNPROCESSABLE_ENTITY) :
                        new ResponseEntity<Message>(messageService.createMessage(message), HttpStatus.OK);
    }
}
