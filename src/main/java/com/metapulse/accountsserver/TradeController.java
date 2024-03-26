package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller // This means that this class is a Controller
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(path="/trade")
public class TradeController {
    @Autowired
    private TradeService tradeService;

    @Autowired
    private UserService userService;

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
}
