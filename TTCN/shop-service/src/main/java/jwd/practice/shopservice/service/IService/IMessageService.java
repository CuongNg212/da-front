package jwd.practice.shopservice.service.IService;



import jwd.practice.shopservice.dto.MessageDTO;

import java.util.List;

public interface IMessageService {
    List<MessageDTO> getMessBySender(int id);
    List<MessageDTO> getMessByReceiver(int id);
    MessageDTO sendMessage(MessageDTO MessageDTO);
    MessageDTO updateMessage(int id, MessageDTO MessageDTO);
    void deleteMessage(int id);
    List<MessageDTO> getMessList(int receiverId);
    List<MessageDTO> getMessBySenderAndReceiver(int senderId, int receiverId);
    List<MessageDTO> getMessByReceiverAndSender(int receiverId, int senderId);
}
