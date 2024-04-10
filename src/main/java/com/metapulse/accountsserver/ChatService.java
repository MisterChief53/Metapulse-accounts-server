package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    public Chat createChat(){
        Chat chat = new Chat();
        chatRepository.save(chat);
        return chat;
    }

    public Chat getChatById(Integer id){
        return chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Chat with ID " + id + " not found"));
    }

}
