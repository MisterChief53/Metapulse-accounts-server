package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public Item createItem(String name, String description, String code, String ip, String user, String imagePath){
        System.out.println("It gets into the createItem function");

        if (itemRepository.findByName(name) != null) {
            throw new RuntimeException("Username already exists");
        }

        Item item = new Item();

        item.setName(name);
        item.setDescription(description);
        item.setCode(code);
        item.setIP(ip);
        item.setUsername(user);
        item.setImagePath(imagePath);

        return itemRepository.save(item);
    }
}
