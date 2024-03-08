package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/auth")
public class AuthenticationController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String name, @RequestParam String password) {
        try {
            User user = userService.registerUser(name, password);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to register user");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestParam String name, @RequestParam String password) {
        try {
            String user = userService.authenticateUser(name, password);
            //System.out.println("Usuario autenticado: " + user.getName());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            //System.out.println("Autenticación fallida para el usuario: " + name);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
