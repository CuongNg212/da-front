package jwd.practice.shopservice.controller;


import jwd.practice.shopservice.dto.MessageDTO;
import jwd.practice.shopservice.service.Service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/message")
public class MessageController {
    MessageService messageService;

    @GetMapping("/send/{senderId}")
    public List<MessageDTO> getMessBySender(@PathVariable int senderId) {
        return messageService.getMessBySender(senderId);
    }

    @GetMapping("/receive/{receiverId}")
    public List<MessageDTO> getMessByReceiver(@PathVariable int receiverId) {
        return messageService.getMessByReceiver(receiverId);
    }

    @PostMapping
    public MessageDTO createMessage(@RequestBody MessageDTO message) {
        return messageService.sendMessage(message);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable Integer id, @RequestBody MessageDTO newMessage) {
        return ResponseEntity.ok(messageService.updateMessage(id, newMessage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<List<MessageDTO>> getLatestMessages(@RequestParam("receiverId") int receiverId) {
        return ResponseEntity.ok(messageService.getMessList(receiverId));
    }

    @GetMapping("/both")
    public ResponseEntity<List<MessageDTO>> getBothMess(@RequestParam("senderId") int senderId, @RequestParam("receiverId") int receiverId) {
        return ResponseEntity.ok(messageService.getMessBySenderAndReceiver(senderId,receiverId));
    }

    @GetMapping("/both2")
    public ResponseEntity<List<MessageDTO>> getBoth2Mess( @RequestParam("receiverId") int receiverId,@RequestParam("senderId") int senderId) {
        return ResponseEntity.ok(messageService.getMessByReceiverAndSender(receiverId,senderId));
    }
}