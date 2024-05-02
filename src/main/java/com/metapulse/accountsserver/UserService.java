package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    /*The user repository link*/
    @Autowired
    private UserRepository userRepository;

    private final Singleton singleton;

    @Autowired
    public UserService(Singleton singleton){
        this.singleton =singleton;
    }

    /*It receives a name and a password, f*/
    public User registerUser(String name, String password) {
        if (userRepository.findByName(name) != null) {
            throw new RuntimeException("Username already exists");
        }

        // Hash the password before saving
        String hashedPassword = new BCryptPasswordEncoder().encode(password);

        // Create and save the user
        User user = new User();
        user.setName(name);
        user.setPassword(hashedPassword);
        user.setMoney(500.0);
        user.setTradeInvitation(Boolean.FALSE);
        return userRepository.save(user);
    }

    public String authenticateUser(String name, String password) {
        User user = userRepository.findByName(name);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            SecretKey secretKey = JwtUtils.getSecretKey();

            String token;
            try {
                token = Jwts.builder()
                        .setSubject(String.valueOf(user.getId()))
                        .claim("username", user.getName())
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 14400000))
                        .signWith(secretKey, SignatureAlgorithm.HS256)
                        .compact();
            } catch (JwtException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al crear el token JWT", e);
            }
            singleton.addUser(name);
            return token;
        } else {
            throw new RuntimeException("Invalid token");
        }
    }

    public Claims getClaimsFromToken(String token) {
        SecretKey secretKey = JwtUtils.getSecretKey();

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public void changeRequestStatus(){
        List<User> users = (List<User>) userRepository.findAll();
        users.forEach(user -> {
            user.setTradeInvitation(!user.getTradeInvitation());
            userRepository.save(user);
        });
        System.out.println("Se cambio el trade invitation");
    }

    public User getUserFromId(int id) {
        return userRepository.findById(id);
    }

    public User getUserFromName(String username) { return userRepository.findByName(username); }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}
