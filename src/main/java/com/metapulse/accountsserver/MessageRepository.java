package com.metapulse.accountsserver;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Integer> {
    List<Message> getAllMessagesByChat(Chat chat);
}
