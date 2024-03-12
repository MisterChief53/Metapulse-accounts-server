package com.metapulse.accountsserver;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemForSaleService {

    @Autowired
    private ItemForSaleRepository itemForSaleRepository;

    @Autowired
    private ItemRepository itemRepository; // Assuming you have an ItemRepository

    @Transactional
    public void createItemForSale(int itemId, double price, String description) {

        if(itemForSaleRepository.findByItemId(itemId)){
            throw new RuntimeException("Item already for sale");
        }
        // Load the Item entity using the provided itemId
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item with ID " + itemId + " not found"));

        // Create a new ItemsForSale entity
        ItemsForSale itemsForSale = new ItemsForSale();
        itemsForSale.setItem(item);
        itemsForSale.setPrice(price);
        itemsForSale.setDescription(description);

        // Save the ItemsForSale entity
        itemForSaleRepository.save(itemsForSale);
    }

    public Iterable<ItemsForSale> getAllItemsForSale() {
        return itemForSaleRepository.findAll();
    }

    public ItemsForSale getItemForSaleById(int id) {
        return itemForSaleRepository.findById(id).orElse(null);
    }
}
