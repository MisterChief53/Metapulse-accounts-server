package com.metapulse.accountsserver;


import jakarta.persistence.*;

/*The message entity, has an id, the text content, the username of the user that sends the message
* and a reference of the chat that contains the message*/
@Entity
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    @Column(columnDefinition = "TEXT")
    private String content; // Unencrypted message content

    @Column(columnDefinition = "TEXT")
    private String encryptedContent; // Encrypted message content

    private String username;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    public Message() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }
}
