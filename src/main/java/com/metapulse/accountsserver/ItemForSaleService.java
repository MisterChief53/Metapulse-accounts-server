package com.metapulse.accountsserver;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ItemForSaleService {

    @Autowired
    private ItemForSaleRepository itemForSaleRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    /*It creates an instance of item for sale, receives a valid itemId, the price and the description
    * first, it finds the corresponding item, then it creates the itemForSale with the information and save it,
    * the transactional tag return all the changes made if there is any exception*/
    @Transactional
    public void createItemForSale(int itemId, double price, String description) {

        if(itemForSaleRepository.findByItemId(itemId) != null){
            throw new RuntimeException("Item already for sale");
        }
        // Load the Item entity using the provided itemId
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item with ID " + itemId + " not found"));

        // Create a new ItemsForSale entity
        ItemForSale itemForSale = new ItemForSale();
        itemForSale.setItem(item);
        itemForSale.setPrice(price);
        itemForSale.setDescription(description);

        // Save the ItemsForSale entity
        itemForSaleRepository.save(itemForSale);
    }

    public Iterable<ItemForSale> getAllItemsForSale() {
        return itemForSaleRepository.findAll();
    }

    public ItemForSale getItemForSaleById(int id) {
        return itemForSaleRepository.findById(id).orElse(null);
    }

    /*This receives an itemId and a username, first, search if the item is actually for sale,
    * the user is valid and the given user has enough money to buy the item, then we fetch
    * for the original owner of the item, update the balance of both users and change the owner of the
    * item, then it deletes the instance of ItemForSale*/
    @Transactional
    public boolean buyItem(int itemId, String username) {

        //Check if  the item is for sale and if the user that intends to buy it has enough balance
        ItemForSale itemForSale = itemForSaleRepository.findByItemId(itemId);
        User buyer = userRepository.findByName(username);

        if (itemForSale != null  && buyer != null && buyer.getMoney() >= itemForSale.getPrice()) {
            //Get the information of the item that is being purchased and the user that sells it
            Item  item = itemForSale.getItem();
            User seller = userRepository.findByName(item.getUsername());

            //Update the balance of the seller
            double newBalance = seller.getMoney() + itemForSale.getPrice();
            seller.setMoney(newBalance);

            //Update the balance of the buyer
            newBalance = buyer.getMoney() - itemForSale.getPrice();
            buyer.setMoney(newBalance);

            //Change the owner of the item
            item.setUsername(buyer.getName());
            // Save changes on database
            userRepository.save(buyer);
            userRepository.save(seller);
            itemRepository.save(item);
            itemForSaleRepository.delete(itemForSale);

            return true; // Fulfilled
        }

        return false; // Failed
    }
}
