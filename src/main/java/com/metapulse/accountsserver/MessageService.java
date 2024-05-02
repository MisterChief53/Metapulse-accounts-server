package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        message.setContent(content);
        message.setUsername(username);
        messageRepository.save(message);
    }

    public List<Message> getMessagesByChatId(int chatId){
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat with ID " + chatId + " not found"));
        return messageRepository.getAllMessagesByChat(chat);
    }


}
