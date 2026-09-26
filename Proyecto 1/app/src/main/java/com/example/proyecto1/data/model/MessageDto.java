package com.example.proyecto1.data.model;

import com.example.proyecto1.domain.model.Message;

public class MessageDto {
    private String id;
    private String senderId;
    private String senderName;
    private String text;
    private String imageUrl;
    private long timestamp;

    public MessageDto() {
    }

    public MessageDto(String id, String senderId, String senderName, String text, String imageUrl, long timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    // Convertir DTO a modelo de Dominio
    public Message toDomain() {
        return new Message(id, senderId, senderName, text, imageUrl, timestamp);
    }

    // Crear DTO a partir del modelo de Dominio
    public static MessageDto fromDomain(Message message) {
        return new MessageDto(
                message.getId(),
                message.getSenderId(),
                message.getSenderName(),
                message.getText(),
                message.getImageUrl(),
                message.getTimestamp()
        );
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}