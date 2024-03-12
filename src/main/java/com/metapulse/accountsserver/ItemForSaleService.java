package com.metapulse.accountsserver;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // Verificar si el item está disponible para la venta y si el usuario tiene suficiente dinero
        ItemForSale itemForSale = itemForSaleRepository.findByItemId(itemId);
        User user = userRepository.findByName(username);

        if (itemForSale != null  && user != null && user.getMoney() >= itemForSale.getPrice()) {
            // Actualizar el saldo del usuario y marcar el item como vendido
            double newBalance = user.getMoney() - itemForSale.getPrice();
            user.setMoney(newBalance);
            //I still need to change the owner on the item
            // Guardar los cambios en la base de datos
            userRepository.save(user);
            itemForSaleRepository.delete(itemForSale);

            return true; // Compra exitosa
        }

        return false; // Compra fallida
    }
}
