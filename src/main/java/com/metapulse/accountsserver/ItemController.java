package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

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
