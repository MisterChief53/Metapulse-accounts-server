package com.metapulse.accountsserver;

import com.sun.tools.jconsole.JConsoleContext;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller // This means that this class is a Controller
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(path="/trade")
public class TradeController {
    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    AuthenticationController authenticationController;

    private final Singleton singleton;

    public TradeController(Singleton singleton) {
        this.singleton = singleton;
    }
    /*Creates an instance of a trade, retreives the users from the singleton
    * because  the system only has two users per session in the world*/
    @PostMapping("/create")
    public ResponseEntity<?> createTrade() {
        List<String> usernames = new ArrayList<>(singleton.getUsernames());
        User user1 = userService.getUserFromName(usernames.get(0)); //Gets the first user by its id
        User user2 = userService.getUserFromName(usernames.get(1)); //Gets the second user by its id

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
    /*Sets a given item as tradable, receives the item id and a token to check if the item
    * acctually belongs to the user*/
    @PostMapping("/tradeItem")
    public ResponseEntity<?> tradeItem(@RequestParam int itemId, @RequestHeader("Authorization") String token) {
        String username = getUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.notFound().build();
        }
        Item item = itemService.getItemFromId(itemId);

        if( item != null ) {
            if( itemService.verifyOwner(item, username) ) {
                item.setTradableStatus(!item.getTradableStatus());
                itemService.updateItem(item);
                return ResponseEntity.ok("Item is tradable");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The item does not belong to the user");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The item does not exist");
        }
    }

    @GetMapping("/tradeItems")
    public ResponseEntity<?> tradeItem() {
        try {
            Trade trade = tradeService.getTradeFromId(singleton.getTradeId());
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

    @GetMapping("/tradeInformation")
    public ResponseEntity<?> tradeInformation(@RequestHeader("Authorization") String token){
        try{
            String username = getUsernameFromToken(token);
            User user;
            Trade trade = tradeService.getTradeFromId(singleton.getTradeId());
            if(Objects.equals(username, trade.getUser1().getName())){
                user = trade.getUser2();
            }else{
                user = trade.getUser1();
            }
            List<Item> itemsUser = itemService.getItemsByUsernameAndStatus(user.getName());
            return ResponseEntity.ok(itemsUser);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to find items");
        }
        
    }

    @GetMapping("/allTrades")
    public @ResponseBody Iterable<Trade> getAllItems() {
        return tradeRepository.findAll();
    }

    @GetMapping("/tradeData")
    public ResponseEntity<?> tradeData() {
        try {
            Trade trade = tradeService.getTradeFromId(singleton.getTradeId());
            User user1 = trade.getUser1();
            User user2 = trade.getUser2();

            List<Item> itemsUser1 = itemService.getItemsByUsernameAndStatus(user1.getName());
            List<Item> itemsUser2 = itemService.getItemsByUsernameAndStatus(user2.getName());

            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("User 1 trade status: ").append((trade.getacceptedTradeUser1())).append(("\n"));
            responseBuilder.append("Money of User 1 to trade: ").append(trade.getTradableMoneyUser1()).append("\n");
            responseBuilder.append("Items of User 1 to trade:\n");
            for (Item item : itemsUser1) {
                responseBuilder.append("Name: ").append(item.getName()).append("\n");
            }
            responseBuilder.append("\n----------------------\n");
            responseBuilder.append("User 2 trade status: ").append((trade.getacceptedTradeUser2())).append(("\n"));
            responseBuilder.append("Money of User 2 to trade: ").append(trade.getTradableMoneyUser2()).append("\n");
            responseBuilder.append("Items of User 2 to trade:\n");
            for (Item item : itemsUser2) {
                responseBuilder.append("Name: ").append(item.getName()).append("\n");
            }

            String response = responseBuilder.toString();

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to find trades");
        }
    }

    /*Returns the amount of money a given user is willing to trade, it requires the token of the user*/
    @GetMapping("/tradeMoney")
    public ResponseEntity<?> tradeMoney(@RequestHeader("Authorization") String token) {
        Claims claims = userService.getClaimsFromToken(token);

        if (claims != null) {
            String username = (String) claims.get("username");

            Trade trade = tradeService.getTradeFromId(singleton.getTradeId());

            Map<String, Object> response = new HashMap<>();

            if(Objects.equals(trade.getUser1().getName(), username)){
                response.put("tradeMoney", trade.getTradableMoneyUser1());
                response.put("tradeMoneyOtherUser", trade.getTradableMoneyUser2());
            } else {
                response.put("tradeMoney", trade.getTradableMoneyUser2());
                response.put("tradeMoneyOtherUser", trade.getTradableMoneyUser1());
            }

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    /*Updates the amount of money a given user is willing to trade, it requires the moneyString and the token
    * of the user*/
    @PutMapping("/tradeMoney")
    public ResponseEntity<?> tradeMoney(@RequestParam String moneyString, @RequestHeader("Authorization") String token) {
        double money;
        try{
            money = Double.parseDouble(moneyString);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This isn't a double");
        }

        String username = getUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.notFound().build();
        }

        User userOwner = userService.getUserFromName(username);
        Integer userId = userOwner.getId();
        Trade trade = tradeService.getTradeFromId(singleton.getTradeId());
        User user;

        if( userOwner != null ) {
            if(trade.getUser1().getId() == userId) {
                user = trade.getUser1();
                if(money <= user.getMoney()) {
                    trade.setTradableMoneyUser1(money);
                    tradeService.updateTrade(trade);
                    return ResponseEntity.ok("User 1 trade money updated");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("The user doesn't have enough money");
                }
            } else if (trade.getUser2().getId() == userId) {
                user = trade.getUser2();
                if(money <= user.getMoney()) {
                    trade.setTradableMoneyUser2(money);
                    tradeService.updateTrade(trade);
                    return ResponseEntity.ok("User 2 trade money updated");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("The user doesn't have enough money");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The user is not part of the trade");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The user does not exist");
        }
    }
    /*Rejects the trade, it requires the token of the user, first, checks if it is valid,
    * then, it checks if the user of the token is the user that is participating in the trade,
    * if it is, sets the acceptedTrade as false*/
    @PutMapping("/rejectTrade")
    public ResponseEntity<?> rejectTrade(@RequestHeader("Authorization") String token) {
        try {
            String username = getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.notFound().build();
            }

            Trade trade = tradeService.getTradeFromId(singleton.getTradeId());
            User user1 = trade.getUser1();
            User user2 = trade.getUser2();

            if(Objects.equals(user1.getName(), username)) {
                trade.setacceptedTradeUser1(false);
                tradeService.updateTrade(trade);
                return ResponseEntity.ok("User1 rejected successfully");
            } else if (Objects.equals(user2.getName(), username)) {
                trade.setacceptedTradeUser2(false);
                tradeService.updateTrade(trade);
                return ResponseEntity.ok("User2 rejected successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The user is not part of the trade");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to reject trade");
        }
    }

    /*Accepts the trade, same as the method above, but sets the accepted trade as true*/
    @PutMapping("/acceptTrade")
    public ResponseEntity<?> acceptTrade(@RequestHeader("Authorization") String token){
        try {
            String username = getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.notFound().build();
            }

            Trade trade = tradeService.getTradeFromId(singleton.getTradeId());
            User user1 = trade.getUser1();
            User user2 = trade.getUser2();

            System.out.println("Trying to accept the trade");

            if(Objects.equals(user1.getName(), username)) {
                trade.setacceptedTradeUser1(true);
                tradeRepository.save(trade);
                System.out.println(username + " is accepting the trade");
                return ResponseEntity.ok("User1 accepted successfully");
            } else if (Objects.equals(user2.getName(), username)) {
                trade.setacceptedTradeUser2(true);
                tradeRepository.save(trade);
                System.out.println(username + " is accepting the trade");
                return ResponseEntity.ok("User2 accepted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The user is not part of the trade");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to accept trade");
        }
    }
    /*Execute the trade, checks if both users have accepted the trade, if so, it updates the
    * money of both users, then it fetches the tradeable items of both, and change the owner name to correspond
    * with the new owner */
    @PostMapping("/execute")
    public ResponseEntity<?> executeTrade() {
        try {
            Trade trade = tradeService.getTradeFromId(singleton.getTradeId());
            User user1 = trade.getUser1();
            User user2 = trade.getUser2();

            System.out.println("Trying to execute the trade");

            if (trade.getacceptedTradeUser1() && trade.getacceptedTradeUser2()) {
                System.out.println("Executing trade");
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

                //tradeService.deleteTrade(trade); The trade doesn't need to be deleted anymore
                trade.setacceptedTradeUser1(false);
                trade.setacceptedTradeUser2(false);
                trade.setTradableMoneyUser1(0.0);
                trade.setTradableMoneyUser2(0.0);

                tradeService.deleteTrade(trade);

                return ResponseEntity.ok("Trade executed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Both users must accept the trade");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to execute trade");
        }
    }
    /*Checks if there is a trade request returns a boolean*/
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
    /*Sends a trade request, need an authorization token, it changes the requestStatus for all the users
    * in the system, which should be two at max*/
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
