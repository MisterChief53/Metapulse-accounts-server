package com.metapulse.accountsserver;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(path="/auth")
public class AuthenticationController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String name, @RequestParam String password) {
        if(!name.isEmpty() && !password.isEmpty()){
            try {
                userService.registerUser(name, password);
                return ResponseEntity.ok("User registered successfully");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to register user");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your username or password is empty");
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestParam String name, @RequestParam String password) {
        try {
            String token = userService.authenticateUser(name, password);
            System.out.println(token);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/secure")
    public ResponseEntity<?> secureEndpoint(@RequestHeader("Authorization") String token) {
        Claims claims = userService.getClaimsFromToken(token);

        if (claims != null) {
            String username = (String) claims.get("username");
            return ResponseEntity.ok(username);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        Claims claims = userService.getClaimsFromToken(token);

        if (claims != null) {
            String username = (String) claims.get("username");
            User user = userService.getUserFromName(username);

            Map<String, Object> response = new HashMap<>();
            response.put("money", user.getMoney());
            response.put("name", user.getName());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }
}
