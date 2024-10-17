package edu.school21.sockets.models;

public class Message {
    private Long id;
    private Long roomId;
    private User author;
    private String text;

    public Message() {}

    public Message(Long roomId, User author, String text) {
        this.roomId = roomId;
        this.author = author;
        this.text = text;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Long getRoomId() {
        return roomId;
    }

    public User getAuthor() {
        return author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", authorId=" + author +
                ", text='" + text + '\'' +
                '}';
    }
}
