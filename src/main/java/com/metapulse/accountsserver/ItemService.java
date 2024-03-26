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
        item.setTradableStatus(false);

        return itemRepository.save(item);
    }

    public List<Item> getItemsByUsername(String username) {
        if (userRepository.findByName(username) != null) {
            return itemRepository.findByUsername(username);
        } else {
            throw new RuntimeException("This user doesn't exists");
        }
    }

    public List<Item> getItemsByUsernameAndStatus(String username) {
        if (userRepository.findByName(username) != null) {
            System.out.println("Getting items of " + username);
            return itemRepository.findByUsernameAndTradableIsTrue(username);
        } else {
            throw new RuntimeException("This user doesn't exists");
        }
    }

    public Item getItemFromId(int id) {
        return itemRepository.findItemById(id);
    }

    public Boolean verifyOwner(Item item, String username) {
        return item.getUsername().equals(username);
    }

    public void updateItem(Item item) {
        itemRepository.save(item);
    }
}
