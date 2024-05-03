package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.*;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;
    /*Creates a new instance of message, receives the content, the username and the chatId*/
    public void createMessage(String content, String username, int chatId){
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat with ID " + chatId + " not found"));
        Message message = new Message();
        message.setChat(chat);
        String encryptedContent = encryptMessage(content); // Call encryption method
        message.setContent(content);
        message.setEncryptedContent(encryptedContent);
        messageRepository.save(message);
    }

    public List<Message> getMessagesByChatId(int chatId){
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat with ID " + chatId + " not found"));
        List<Message> messages = messageRepository.getAllMessagesByChat(chat);
        for (Message message : messages) {
            message.setContent(decryptMessage(message.getEncryptedContent())); // Call decryption method
        }
        return messages;
    }

    public static String encryptMessage(String content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes); // Encode for easier storage/transmission
    }

    public static String decryptMessage(String encryptedContent, PrivateKey privateKey) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedContent); // Decode from Base64
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }


}
