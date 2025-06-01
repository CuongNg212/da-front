package jwd.practice.shopservice.service.Service;

import jwd.practice.shopservice.dto.MessageDTO;
import jwd.practice.shopservice.dto.response.UserResponse;
import jwd.practice.shopservice.entity.Messages;
import jwd.practice.shopservice.mapper.IMessageMapper;
import jwd.practice.shopservice.mapper.httpClient.AuthClient;
import jwd.practice.shopservice.repository.Message_Repository;
import jwd.practice.shopservice.service.IService.IMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageService implements IMessageService {
    Message_Repository messageRepository;
    IMessageMapper messageMapper;
    AuthClient authClient;

    @Override
    public List<MessageDTO> getMessBySender(int id) {
        List<Messages> messages = messageRepository.findBySenderId(id);
        return messages.stream()
                .map(messageMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessByReceiver(int id) {
        List<Messages> messages = messageRepository.findByReceiverId(id);
        return messages.stream()
                .map(messageMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MessageDTO sendMessage(MessageDTO messageDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated user: " + authentication.getName());

        // Lấy thông tin sender từ User Service
        UserResponse senderProfile = authClient.getUserById(messageDto.getSenderId()).getResult();
        System.out.println("sender: " + senderProfile.getUserId());
        if (senderProfile == null) {
            throw new RuntimeException("Sender not found with ID: " + messageDto.getSenderId());
        }

        // Lấy thông tin receiver từ User Service
        UserResponse receiverProfile = authClient.getUserById(messageDto.getReceiverId()).getResult();
        System.out.println("receriver: " + receiverProfile.getUsername());
        if (receiverProfile == null) {
            throw new RuntimeException("Receiver not found with ID: " + messageDto.getReceiverId());
        }

        Messages newMessage = messageMapper.toMessages(messageDto, senderProfile, receiverProfile);
//        // Tạo đối tượng Messages
//        Messages newMessage = Messages.builder()
//                .senderId(senderProfile.getUserId())
//                .receiverId(receiverProfile.getUserId())
//                .message(messageDto.getMessage())
//                .createdAt(Instant.now())  // Đảm bảo thời gian gửi tin nhắn
//                .build();
//
//        // Lưu tin nhắn vào database
//        Messages savedMessage = messageRepository.save(newMessage);
//
//        // Trả về DTO sau khi lưu
//        return MessageDTO.builder()
//                .messageId(savedMessage.getMessageId())
//                .senderId(savedMessage.getSenderId())
//                .receiverId(savedMessage.getReceiverId())
//                .message(savedMessage.getMessage())
//                .createdAt(savedMessage.getCreatedAt())
//                .build();

        return messageMapper.toMessageDTO(messageRepository.save(newMessage));
    }


    @Override
    public MessageDTO updateMessage(int id, MessageDTO newMessage) {
        Messages messages = messageRepository.findById(id).orElse(null);
        messageMapper.updateMessageDTO(messages, newMessage);
        return messageMapper.toMessageDTO(messageRepository.save(messages));
    }

    @Override
    public void deleteMessage(int id) {
        messageRepository.deleteById(id);
    }

    @Override
    public List<MessageDTO> getMessList(int receiverId) {
        List<Object[]> rawResults = messageRepository.findLatestMessagesBySender(receiverId);
        return rawResults.stream()
                .map(row -> new MessageDTO(
                        Integer.parseInt(row[0].toString()), // messageId
                        Integer.parseInt(row[1].toString()), // senderId
                        Integer.parseInt(row[2].toString()), // receiverId
                        row[3].toString(),               // message
                        ((Timestamp) row[4]).toInstant() // Chuyển đổi Timestamp sang Instant
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessBySenderAndReceiver(int senderId, int receiverId) {
        List<Messages> messages = messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        return  messages.stream()
                .map(messageMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessByReceiverAndSender(int receiverId, int senderId) {
        List<Messages> messages = messageRepository.findByReceiverIdAndSenderId(receiverId, senderId);
        return  messages.stream()
                .map(messageMapper::toMessageDTO)
                .collect(Collectors.toList());
    }
}
