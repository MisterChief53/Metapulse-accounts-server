package com.metapulse.accountsserver;

import com.sun.tools.jconsole.JConsoleContext;
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

    @Autowired
    AuthenticationController authenticationController;

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
                    item.setTradableStatus(!item.getTradableStatus());
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

    @GetMapping("/tradeData")
    public ResponseEntity<?> tradeData(@RequestParam int tradeId) {
        try {
            Trade trade = tradeService.getTradeFromId(tradeId);
            User user1 = trade.getUser1();
            User user2 = trade.getUser2();

            List<Item> itemsUser1 = itemService.getItemsByUsernameAndStatus(user1.getName());
            List<Item> itemsUser2 = itemService.getItemsByUsernameAndStatus(user2.getName());

            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("User 1 trade status: ").append((trade.getacceptedTradeUser1())).append(("\n"));
            responseBuilder.append("Items of User 1 to trade: ").append(trade.getTradableMoneyUser1()).append("\n");
            responseBuilder.append("Items of User 1 to trade:\n");
            for (Item item : itemsUser1) {
                responseBuilder.append("Name: ").append(item.getName()).append("\n");
            }
            responseBuilder.append("\n----------------------\n");
            responseBuilder.append("User 2 trade status: ").append((trade.getacceptedTradeUser1())).append(("\n"));
            responseBuilder.append("Items of User 2 to trade: ").append(trade.getTradableMoneyUser2()).append("\n");
            responseBuilder.append("Items of User 2 to trade:\n");
            for (Item item : itemsUser2) {
                responseBuilder.append("Name: ").append(item.getName()).append("\n");
            }

            String response = responseBuilder.toString();

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to find items");
        }
    }

    @PostMapping("/tradeMoney")
    public ResponseEntity<?> tradeMoney(@RequestParam Double money, @RequestParam int userId, @RequestParam int tradeId) {
        User userOwner = userService.getUserFromId(userId);
        Trade trade = tradeService.getTradeFromId(tradeId);
        User user;

        if( userOwner != null ) {
            if(trade.getUser1().getId() == userId) {
                user = trade.getUser1();
                if(money <= user.getMoney()) {
                    trade.setTradableMoneyUser1(money);
                    tradeService.updateTrade(trade);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User 1 trade money updated");
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The user doesn't have enough money");
                }
            } else if (trade.getUser2().getId() == userId) {
                user = trade.getUser2();
                if(money <= user.getMoney()) {
                    trade.setTradableMoneyUser2(money);
                    tradeService.updateTrade(trade);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User 2 trade money updated");
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The user doesn't have enough money");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The user is not part of the trade");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The user does not exist");
        }
    }

    @PostMapping("/execute")
    public ResponseEntity<?> executeTrade(@RequestParam int tradeId, @RequestParam int userId) {
        try {
            Trade trade = tradeService.getTradeFromId(tradeId);
            User user1 = trade.getUser1();
            User user2 = trade.getUser2();

            if(trade.getUser1().getId() == userId) {
                trade.setacceptedTradeUser1(!trade.getacceptedTradeUser1());
                tradeService.updateTrade(trade);
            } else if (trade.getUser2().getId() == userId) {
                trade.setacceptedTradeUser2(!trade.getacceptedTradeUser2());
                tradeService.updateTrade(trade);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The user is not part of the trade");
            }

            if (trade.getacceptedTradeUser1() && trade.getacceptedTradeUser2()) {
                user1.setMoney(user1.getMoney() - trade.getTradableMoneyUser1() + trade.getTradableMoneyUser2());
                user2.setMoney(user2.getMoney() - trade.getTradableMoneyUser2() + trade.getTradableMoneyUser1());
                userService.updateUser(user1);
                userService.updateUser(user2);

                List<Item> itemsUser1 = itemService.getItemsByUsernameAndStatus(user1.getName());
                List<Item> itemsUser2 = itemService.getItemsByUsernameAndStatus(user2.getName());

                for (Item item : itemsUser1) {
                    item.setUsername(user2.getName());
                    item.setTradableStatus(false);
                    itemService.updateItem(item);
                }

                for (Item item : itemsUser2) {
                    item.setUsername(user1.getName());
                    item.setTradableStatus(false);
                    itemService.updateItem(item);
                }

                tradeService.deleteTrade(trade);

                return ResponseEntity.ok("Trade executed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Both users must accept the trade");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to execute trade");
        }
    }

    @GetMapping("/hasRequest")
    public ResponseEntity<ResponseDTO> getUserTradeInvitation(@RequestHeader("Authorization") String token) {
        String username = getUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.notFound().build();
        }
        User user = userService.getUserFromName(username);
        ResponseDTO responseDTO = new ResponseDTO(user.getTradeInvitation());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/setRequest")
    public ResponseEntity<?> setUsersTradeInvitation(@RequestHeader("Authorization") String token){
        String username = getUsernameFromToken(token);
        if(username != null){
            userService.changeRequestStatus();
            return ResponseEntity.ok("Trade invitation modified successfully");

        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }


    private String getUsernameFromToken(String token) {

        ResponseEntity<?> response = authenticationController.secureEndpoint(token);
        if (response.getStatusCode() == HttpStatus.OK) {
            return (String) response.getBody();
        } else {
            return null;
        }
    }
}
