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
    /*The service that links the controller with the repository*/
    @Autowired
    private UserService userService;

    /*
    * First, we check if the parameters have something inside them, then,
    * we use the userService to save the new user, after that, we return an OK response,
    * if there are any exceptions, it returns a bad request or an unauthorized status*/
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
    /*We use the user sevice to authenticate the user, if it is ok, it returns the token,
    * if not, we return an unauthorized response*/
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


    /*Verify if the token returns a valid username, it receives a token and returns the username*/
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


    /*Receives a token and returns a response containing the money and name of a user*/
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
