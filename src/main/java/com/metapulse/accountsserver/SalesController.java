package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller // This means that this class is a Controller
@RequestMapping(path="/sales")
public class SalesController {
    @Autowired
    private ItemForSaleService itemForSaleService;

    @GetMapping("/items")
    public ResponseEntity<?> getAllItemsForSale() {
        return ResponseEntity.ok(itemForSaleService.getAllItemsForSale());
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItemForSaleById(int id) {
        ItemsForSale itemsForSale = itemForSaleService.getItemForSaleById(id);
        if (itemsForSale != null) {
            return ResponseEntity.ok(itemsForSale);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItemForSale(@RequestParam int item_id, @RequestParam double price, @RequestParam String description)  {
        try {
            itemForSaleService.createItemForSale(item_id, price, description);
            return ResponseEntity.status(HttpStatus.CREATED).body("Item added for sale successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add item for sale");
        }
    }
}