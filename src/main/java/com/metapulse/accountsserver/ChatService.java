package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    private final Singleton singleton;

    @Autowired
    public ChatService(Singleton singleton){
        this.singleton =singleton;
    }

    /*Because the chat only has an id, it does not require anything else to create one*/
    public Chat createChat(int index){
        Chat chat = new Chat();
        chatRepository.save(chat);
        if(index ==-1){
            singleton.addChatId(chat.getId());
        }else{
            singleton.setChatId(chat.getId(),index);
        }

        singleton.getChatIds().forEach((id)->System.out.println(id));
        return chat;
    }

    public Chat getChatById(Integer id){
        return chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Chat with ID " + id + " not found"));
    }

}
