package edu.school21.sockets.server;

import edu.school21.sockets.services.MessagesService;
import edu.school21.sockets.services.RoomsService;
import edu.school21.sockets.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;


@Component
public class Server {
    private final UsersService usersService;
    private final RoomsService roomsService;
    private final MessagesService messagesService;
    private final CopyOnWriteArrayList<ClientHandler> clients;

    @Autowired
    public Server(UsersService usersService, RoomsService roomsService, MessagesService messagesService) {
        this.usersService = usersService;
        this.roomsService = roomsService;
        this.messagesService = messagesService;
        clients = new CopyOnWriteArrayList<>();
    }

    public void start(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        while (true) {
            Socket client = server.accept();
            System.out.printf("client %s connected\n", client.getPort());
            ClientHandler clientThread = new ClientHandler(client, this, usersService, roomsService, messagesService);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
    }

    public void removeClient(ClientHandler clientThread) {
        System.out.printf("client %s disconnected\n", clientThread.getClient().getPort());
        clients.remove(clientThread);
    }

    public CopyOnWriteArrayList<ClientHandler> getClients() {
        return clients;
    }
}
