package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;


import java.util.Base64;
import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public User registerUser(String name, String password) {
        // Check if username is already taken
        if (userRepository.findByName(name) != null) {
            throw new RuntimeException("Username already exists");
        }

        // Hash the password before saving
        String hashedPassword = new BCryptPasswordEncoder().encode(password);

        // Create and save the user
        User user = new User();
        user.setName(name);
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public String authenticateUser(String name, String password) {
        //System.out.println("Se intenta buscar al usuario");
        User user = userRepository.findByName(name);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //System.out.println("Se ha encontrado el usuario " + user.getName() + " en la funcion authenticateUser");

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            //System.out.println("Se entra dentro del if para iniciar con el token");
            SecretKey secretKey = JwtUtils.getSecretKey("5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437");
            //System.out.println("Se crea la secretKey ");

            ///*
            String token;
            try {
                token = Jwts.builder()
                        .setSubject(String.valueOf(user.getId()))
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 864000000))
                        .signWith(secretKey, SignatureAlgorithm.HS256)
                        .compact();
                //System.out.println("Se ha creado el token");
            } catch (JwtException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al crear el token JWT", e);
            }
            //*/
            //System.out.println("Se entrega el usuario");
            return token;
        } else {
            throw new RuntimeException("Invalid token");
        }
    }

}
