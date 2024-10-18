package edu.school21.sockets.services;

import edu.school21.sockets.models.Message;
import edu.school21.sockets.repositories.MessagesRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessagesService {
    private final MessagesRepository messagesRepository;

    public MessagesService(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    public List<Message> getLast30(Long roomId) {
        return messagesRepository.get30LastRoomMessages(roomId);
    }

    public void saveMessage(Message message) {
        messagesRepository.save(message);
    }
}
