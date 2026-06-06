package com.example.taskmaxing.mapper;

import com.example.taskmaxing.model.dto.response.MessageResponse;
import com.example.taskmaxing.model.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "sender.username", target = "senderName")
    MessageResponse toResponse(Message message);
}
