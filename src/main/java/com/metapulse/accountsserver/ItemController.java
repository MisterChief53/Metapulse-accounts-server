package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(path="/items")
public class ItemController {
    @Autowired
    private ItemService itemService;
    /*Gets all the items of a given user, it receives a name and returns a list filled with
    * the items*/
    @PostMapping("/getItemsUser")
    public ResponseEntity<?> getItemsUser(@RequestParam String name) {
        List<Item> items;

        try {
            items = itemService.getItemsByUsername(name);
            return ResponseEntity.ok(items);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to find items");
        }
    }
}
