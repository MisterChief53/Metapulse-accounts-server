package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.security.*;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;

    private final Singleton singleton;

    private static final String ALGORITHM = "AES";

    public MessageService(Singleton singleton) {
        this.singleton = singleton;
    }

    /*Creates a new instance of message, receives the content, the username and the chatId*/
    public void createMessage(String content, String username, int chatId) throws Exception {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat with ID " + chatId + " not found"));
        Message message = new Message();
        message.setChat(chat);
        String encryptedContent = encryptMessage(content); // Call encryption method
        message.setContent(encryptedContent);
        messageRepository.save(message);
    }

    public List<Message> getMessagesByChatId(int chatId) throws Exception {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat with ID " + chatId + " not found"));
        List<Message> messages = messageRepository.getAllMessagesByChat(chat);
        for (Message message : messages) {
            message.setContent(decryptMessage(message.getContent())); // Call decryption method
        }
        return messages;
    }

    public String encryptMessage(String content) throws Exception {
        Key key = new SecretKeySpec(singleton.getKey().getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decryptMessage(String encryptedContent) throws Exception {
        Key key = new SecretKeySpec(singleton.getKey().getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
        return new String(decryptedBytes);
    }


}
