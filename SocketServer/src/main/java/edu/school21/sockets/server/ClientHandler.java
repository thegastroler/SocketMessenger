package edu.school21.sockets.server;

import edu.school21.sockets.services.ChatState;
import edu.school21.sockets.services.MessagesService;
import edu.school21.sockets.services.RoomsService;
import edu.school21.sockets.services.UsersService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    private final Server server;
    private final Socket client;
    private final UsersService usersService;
    private final RoomsService roomsService;
    private final MessagesService messagesService;
    private final CommandProcessor commandProcessor;
    private String username;
    private Long roomId;
    private final PrintWriter out;
    private boolean isAuthenticated;

    public ClientHandler(Socket client, Server server, UsersService usersService, RoomsService roomsService, MessagesService messagesService) throws IOException {
        this.client = client;
        this.usersService = usersService;
        this.roomsService = roomsService;
        this.messagesService = messagesService;
        this.server = server;
        out = new PrintWriter(client.getOutputStream(), true);
        isAuthenticated = false;
        commandProcessor = new CommandProcessor(this);
        commandProcessor.setChatState(ChatState.WELCOME);
        out.println("Hello from Server!");
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            commandProcessor.sendMessage(null);

            String message;
            while (commandProcessor.getChatState() != ChatState.EXIT && (message = in.readLine()) != null) {
                commandProcessor.processCommand(message);
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            out.println("Internal server error");
        } finally {
            closeConn();
        }
    }

    public void closeConn() {
        try {
            if (!client.isClosed()) {
                server.removeClient(this);
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getClient() {
        return this.client;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public PrintWriter getOut() {
        return out;
    }

    public UsersService getUsersService() {
        return usersService;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CopyOnWriteArrayList<ClientHandler> getClients() {
        return server.getClients();
    }

    public void printIncomingMessage(String message, String name) {
        commandProcessor.sendMessage(message, name);
    }

    public RoomsService getRoomsService() {
        return roomsService;
    }

    public MessagesService getMessagesService() {
        return messagesService;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}

