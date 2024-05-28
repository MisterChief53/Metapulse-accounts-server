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
    /*Creates an instance of item, receives a name, a description, a code, a ip, the owner and the imagepath,
    * first, it checks if the item already exists, if not, it continues creating it*/
    public Item createItem(String name, String description, String code, String ip, String user, String imagePath){

        if (itemRepository.findByName(name) != null) {
            throw new RuntimeException("Item already exist");
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

    /*It returns all the items of a given user*/
    public List<Item> getItemsByUsername(String username) {
        if (userRepository.findByName(username) != null) {
            return itemRepository.findByUsername(username);
        } else {
            throw new RuntimeException("This user doesn't exist");
        }
    }

    /*Returns all the items of a given user that are tradeable*/
    public List<Item> getItemsByUsernameAndStatus(String username) {
        if (userRepository.findByName(username) != null) {
            //System.out.println("Getting items of " + username);
            return itemRepository.findByUsernameAndTradableIsTrue(username);
        } else {
            throw new RuntimeException("This user doesn't exist");
        }
    }
    /*Fetches a single item from a given id*/
    public Item getItemFromId(int id) {
        return itemRepository.findItemById(id);
    }
    /*Checks if a given username corresponds with the username of the owner of an item*/
    public Boolean verifyOwner(Item item, String username) {
        return item.getUsername().equals(username);
    }
    /*Updates a given item*/
    public void updateItem(Item item) {
        itemRepository.save(item);
    }
}
