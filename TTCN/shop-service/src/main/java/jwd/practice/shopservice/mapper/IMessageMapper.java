package jwd.practice.shopservice.mapper;


import jwd.practice.shopservice.dto.MessageDTO;
import jwd.practice.shopservice.dto.response.UserResponse;
import jwd.practice.shopservice.entity.Messages;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IMessageMapper {
    MessageDTO toMessageDTO(Messages messages);
    Messages toMessages(MessageDTO messageDTO, UserResponse sender, UserResponse receiver);
    void updateMessageDTO(@MappingTarget Messages messages ,MessageDTO messageDTO);
}
