package com.metapulse.accountsserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class Singleton {
    private static Singleton instance;

    private Set<String> usernames = new HashSet<>();
    private List<Integer> chatIds = new ArrayList<>();


    private int tradeId;

    public Singleton() {
    }


    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int id){
        this.tradeId =id;
    }

    public void addUser(String username) {
        usernames.add(username);
    }

    public void removeUser(String username) {
        usernames.remove(username);
    }

    public Set<String> getUsernames() {
        return usernames;
    }


    public void addChatId(Integer chatId) {
        chatIds.add(chatId);
    }

    public void removeChatId(Integer chatId) {
        chatIds.remove(chatId);
    }

    public List<Integer> getChatIds() {
        return chatIds;
    }
}
