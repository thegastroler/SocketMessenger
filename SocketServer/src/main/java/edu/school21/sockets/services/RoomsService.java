package edu.school21.sockets.services;

import edu.school21.sockets.models.Room;
import edu.school21.sockets.repositories.RoomsRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomsService {
    private final RoomsRepository roomsRepository;

    public RoomsService(RoomsRepository roomsRepository) {
        this.roomsRepository = roomsRepository;
    }

    public List<Room> getAll() {
        return roomsRepository.findAll();
    }
    
    public void createRoom(String name) {
        Room room = new Room(name);
        roomsRepository.save(room);
    }
}
