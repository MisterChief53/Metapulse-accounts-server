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
import java.util.Objects;

@Service
public class UserService {
    /*The user repository link*/
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    private final Singleton singleton;

    @Autowired
    public UserService(Singleton singleton){
        this.singleton =singleton;
    }

    /*It receives a name and a password, checks if the given name does not exist in database,
    * if not, encrypts the password, and creates the user*/
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


    /*Receives a name and a password, first, find the user in database, then, encrypts the plain text
    * password just received, if the encrypted passwords match,  then a token is build with the username in it
    * , which is returned*/
    public String authenticateUser(String name, String password, int source) {
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
                throw new RuntimeException("There has been a mistake creating the JWT", e);
            }
            if(source==1){
                singleton.addUser(name);
                System.out.println(singleton.getUsernames());
            }

            return token;
        } else {
            throw new RuntimeException("Invalid token");
        }
    }

    public void logout(String username) throws Exception {
        if(singleton.getUsernames().contains(username)){
            singleton.removeUser(username);
            chatService.createChat(0);

            if(!messageService.getEncryptedMessagesByChatId(singleton.getChatIds().get(1)).isEmpty()){
                Message message = messageService.getEncryptedMessagesByChatId(singleton.getChatIds().get(1)).get(0);
                if(Objects.equals(message.getUsername(), username)){
                    chatService.createChat(1);
                }
            }
            if(!messageService.getEncryptedMessagesByChatId(singleton.getChatIds().get(2)).isEmpty()){
                Message message = messageService.getEncryptedMessagesByChatId(singleton.getChatIds().get(2)).get(0);
                if(Objects.equals(message.getUsername(), username)){
                    chatService.createChat(2);
                }
            }
        }

        System.out.println(singleton.getUsernames());
    }

    /*Returns the information contained in the token*/
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
        System.out.println("Trade invitation changed");
    }

    public User getUserFromId(int id) {
        return userRepository.findById(id);
    }

    public User getUserFromName(String username) { return userRepository.findByName(username); }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}
