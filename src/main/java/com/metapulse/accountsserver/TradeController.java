package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // This means that this class is a Controller
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(path="/trade")
public class TradeController {
    @Autowired
    private TradeService tradeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @PostMapping("/create")
    public ResponseEntity<?> createTrade(@RequestParam int user1Id, @RequestParam int user2Id) {
        User user1 = userService.getUserFromId(user1Id); //Gets the first user by its id
        User user2 = userService.getUserFromId(user2Id); //Gets the second user by its id

        if (user1 != null && user2 != null) {
            try {
                int id =tradeService.createTrade(user1, user2);
                return ResponseEntity.ok("Trade created successfully, id = " + id);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to create the trade session");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Some user does not exist");
        }
    }

    @PostMapping("/tradeItem")
    public ResponseEntity<?> tradeItem(@RequestParam int itemId, @RequestParam int userId) {
        User userOwner = userService.getUserFromId(userId);
        Item item = itemService.getItemFromId(itemId);

        if( userOwner != null ) {
            if( item != null ) {
                if( itemService.verifyOwner(item, userOwner.getName()) ) {
                    item.setTradableStatus(true);
                    itemService.updateItem(item);
                    return ResponseEntity.ok("Item is tradable");
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The item does not belong to the user");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The item does not exist");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The user does not exist");
        }
    }

    @GetMapping("/tradeItems")
    public ResponseEntity<?> tradeItem(@RequestParam int tradeId) {
        try {
            Trade trade = tradeService.getTradeFromId(tradeId);
            User user1 = trade.getUser1();
            User user2 = trade.getUser2();

            List<Item> itemsUser1 = itemService.getItemsByUsernameAndStatus(user1.getName());
            List<Item> itemsUser2 = itemService.getItemsByUsernameAndStatus(user2.getName());

            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("Items for User 1:\n");
            for (Item item : itemsUser1) {
                responseBuilder.append("Name: ").append(item.getName()).append("\n");
            }
            responseBuilder.append("\n----------------------\n");
            responseBuilder.append("Items for User 2:\n");
            for (Item item : itemsUser2) {
                responseBuilder.append("Name: ").append(item.getName()).append("\n");
            }

            String response = responseBuilder.toString();

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to find items");
        }
    }
}
