package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    public Item createItem(String name, String description, String code, String ip, String user, String imagePath){

        if (itemRepository.findByName(name) != null) {
            throw new RuntimeException("Item already exists");
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

    public List<Item> getItemsByUsername(String username) {
        if (userRepository.findByName(username) != null) {
            return itemRepository.findByUsername(username);
        } else {
            throw new RuntimeException("This user doesn't exists");
        }
    }
}
