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
            // Guardar los cambios en la base de datos
            userRepository.save(buyer);
            userRepository.save(seller);
            itemRepository.save(item);
            itemForSaleRepository.delete(itemForSale);

            return true; // Compra exitosa
        }

        return false; // Compra fallida
    }
}
