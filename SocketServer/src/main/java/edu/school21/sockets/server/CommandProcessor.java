package edu.school21.sockets.server;

import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.Room;
import edu.school21.sockets.models.User;
import edu.school21.sockets.services.ChatState;
import org.springframework.dao.DuplicateKeyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandProcessor {
    private final ClientHandler ch;
    private ChatState chatState;

    public CommandProcessor(ClientHandler clientHandler) {
        this.ch = clientHandler;
    }

    public void processCommand(String message) throws IOException {
        if (ch.isAuthenticated()) {
            if (chatState == ChatState.MAIN_MENU) {
                processMainMenu(message);
            } else if (chatState == ChatState.ROOMS_LIST) {
                processRoomList(message);
            } else if (chatState == ChatState.ROOM_CHAT) {
                if (message.equals("Exit")) {
                    ch.setRoomId(null);
                    roomsList();
                } else {
                    broadcastMessage(message);
                }
            }
        } else {
            processNotAuth(message);
        }
    }

    private void processRoomList(String message) {
        ArrayList<Room> rooms = new ArrayList<>(ch.getRoomsService().getAll());
        selectRoom(rooms, message);
    }

    public void processMainMenu(String message) throws IOException {
        switch (message) {
            case "1":
                createRoom();
                break;
            case "2":
                roomsList();
                break;
            case "3":
                exit();
                break;
            default:
                sendMessage(null);
        }
    }

    public void processNotAuth(String message) throws IOException {
        switch (message) {
            case "1":
                signIn();
                break;
            case "2":
                signUp();
                break;
            case "3":
                exit();
                break;
            default:
                sendMessage(null);
        }
    }

    private void createRoom() throws IOException {
        ClientHandler ch = this.ch;
        BufferedReader in = new BufferedReader(new InputStreamReader(ch.getClient().getInputStream()));
        sendMessage("Enter room name:");
        String name = in.readLine();
        ch.getRoomsService().createRoom(name);
        sendMessage("Successful!");
        sendMessage(null);
    }

    private void roomsList() {
        chatState = ChatState.ROOMS_LIST;
        ClientHandler ch = this.ch;
        ArrayList<Room> rooms = new ArrayList<>(ch.getRoomsService().getAll());
        if (!rooms.isEmpty()) {
            String roomsList = roomsToString(rooms);
            sendMessage(roomsList);
        }
    }

    private String roomsToString(ArrayList<Room> rooms) {
        String[] roomsArr = new String[rooms.size() + 1];
        int i = 0;
        for (Room room : rooms) {
            roomsArr[i] = String.format("%d. %s", ++i, room.getName());
        }
        roomsArr[i] = String.format("%d. Exit", ++i);
        return "Rooms:\n" + String.join("\n", roomsArr);
    }

    private void selectRoom(ArrayList<Room> rooms, String message) {
        try {
            int item = Integer.parseInt(message);
            if (item >= 1 && item <= rooms.size()) {
                joinRoom(rooms.get(item - 1));
            } else if (item == rooms.size() + 1) {
                chatState = ChatState.MAIN_MENU;
                sendMessage(null);
            } else {
                printRoomList(rooms);
            }
        } catch (NumberFormatException e) {
            printRoomList(rooms);
        }
    }

    private void printRoomList(ArrayList<Room> rooms) {
        String roomsList = roomsToString(rooms);
        sendMessage(roomsList);
    }

    private void joinRoom(Room room) {
        sendMessage(room.getName() + " ---");
        List<Message> messageList = ch.getMessagesService().getLast30(room.getId());
        if (!messageList.isEmpty()) {
            List<String> stringArrayList = new ArrayList<>();
            for (Message msg : messageList) {
                stringArrayList.add(String.format("%s: %s", msg.getAuthor().getEmail(), msg.getText()));
            }
            sendMessage(String.join("\n", stringArrayList));
        }
        chatState = ChatState.ROOM_CHAT;
        ch.setRoomId(room.getId());
    }

    private void signUp() throws IOException {
        ClientHandler ch = this.ch;
        BufferedReader in = new BufferedReader(new InputStreamReader(ch.getClient().getInputStream()));
        sendMessage("Enter username:");
        String username = in.readLine();
        sendMessage("Enter password:");
        String password = in.readLine();
        try {
            registerUser(username, password);
            sendMessage("Successful!");
            chatState = ChatState.WELCOME;
            sendMessage(null);
        } catch (DuplicateKeyException e) {
            sendMessage("User with such username already exists!");
            sendMessage(null);
        }
    }

    private void signIn() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(ch.getClient().getInputStream()));
        sendMessage("Enter username:");
        String username = in.readLine();
        sendMessage("Enter password:");
        String password = in.readLine();
        ch.setAuthenticated(signIn(username, password));
        if (ch.isAuthenticated()) {
            sendMessage("Authentication success");
            ch.setUsername(username);
            chatState = ChatState.MAIN_MENU;
            sendMessage(null);
        } else {
            sendMessage("Authentication error");
            chatState = ChatState.WELCOME;
            sendMessage(null);
        }
    }

    private void exit() {
        sendMessage("You have left the chat.");
        chatState = ChatState.EXIT;
        ch.closeConn();
    }


    private void registerUser(String username, String password) throws DuplicateKeyException {
        ch.getUsersService().signUp(username, password);
    }

    private boolean signIn(String username, String password) {
        return ch.getUsersService().signIn(username, password);
    }

    private void broadcastMessage(String message) {
        Optional<User> optionalUser = ch.getUsersService().findByEmail(ch.getUsername());
        User user = optionalUser.get();
        ch.getMessagesService().saveMessage(new Message(ch.getRoomId(), user, message));
        for (ClientHandler clientThread : ch.getClients()) {
            System.out.println(ch.getRoomId());
            System.out.println(clientThread.getRoomId());
            if (clientThread.getRoomId() != null && clientThread.getRoomId().equals(ch.getRoomId())) {
                clientThread.printIncomingMessage(message, ch.getUsername());
            }
        }
    }

    public void sendMessage(String message, String name) {
        if (ch.getOut() != null && ch.isAuthenticated()) {
            sendMessage(String.format("%s: %s", name, message));
        }
    }

    public void sendMessage(String message) {
        if (ch.getOut() == null) {
            return;
        }
        if (chatState == ChatState.WELCOME && message == null) {
            ch.getOut().println("1. signIn\n2. SignUp\n3. Exit");
        } else if (chatState == ChatState.MAIN_MENU && message == null) {
            ch.getOut().println("1.Create room\n2.Choose room\n3.Exit");
        } else {
            ch.getOut().println(message);
        }
    }

    public ChatState getChatState() {
        return chatState;
    }

    public void setChatState(ChatState chatState) {
        this.chatState = chatState;
    }
}
