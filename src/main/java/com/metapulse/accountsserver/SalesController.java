package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller // This means that this class is a Controller
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.100.6:3000"})
@RequestMapping(path="/sales")
public class SalesController {
    @Autowired
    private ItemForSaleService itemForSaleService;

    @Autowired
    AuthenticationController authenticationController;

    @Autowired
    ItemRepository itemRepository;
    /*Receives nothing, returns all the items that are currently for sale*/
    @GetMapping("/items")
    public ResponseEntity<?> getAllItemsForSale() {
        return ResponseEntity.ok(itemForSaleService.getAllItemsForSale());
    }

    /*Receives via path the item id, returns the item for sale instance if it exists, else,
    * returns a not found response*/
    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItemForSaleById(@PathVariable("id") int id) {
        ItemForSale itemForSale = itemForSaleService.getItemForSaleById(id);
        if (itemForSale != null) {
            return ResponseEntity.ok(itemForSale);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }
    }
    /*Adds an item for sale, receives a valid item id, the price, a description and the token,
    * first, it verifies if the token corresponds to a valid user, then , it searches for the item, if the item exists
    * and the username matches the owner of the item, it adds the item for sale, else, throws an internal
    * server error status*/
    @PostMapping("/items")
    public ResponseEntity<?> addItemForSale(@RequestParam int item_id, @RequestParam double price, @RequestParam String description,@RequestHeader("Authorization") String token)  {
        String username = getUsernameFromToken(token);
        if(username!=null){
            Item item = itemRepository.findById(item_id).orElseThrow(() -> new IllegalArgumentException("Item with ID " + item_id + " not found"));
            if(username.equals(item.getUsername())){
                try {
                    itemForSaleService.createItemForSale(item_id, price, description);
                    return ResponseEntity.status(HttpStatus.CREATED).body("Item added for sale successfully");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add item for sale");
                }
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not the owner of the item");
            }
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

    }
    /*Reives via path the id of an item, also via header the token, first
    * checks if the user is valid, then, it tries to complete the sale, if it is successful
    * then it returns an ok respónse, else, it returns a not found response*/
    @PostMapping("/buy/{id}")
    public ResponseEntity<?> buyItem(@PathVariable("id") int id, @RequestHeader("Authorization") String token) {
        String username = getUsernameFromToken(token);

        if (username != null) {
            try {
                boolean success = itemForSaleService.buyItem(id, username);
                if (success) {
                    return ResponseEntity.ok("Item purchased successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found or already sold");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to buy item");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }
    /*Method used to retreive the username of a given token, it uses the claims stored in it*/
    private String getUsernameFromToken(String token) {

        ResponseEntity<?> response = authenticationController.secureEndpoint(token);
        if (response.getStatusCode() == HttpStatus.OK) {
            return (String) response.getBody();
        } else {
            return null;
        }
    }
}
