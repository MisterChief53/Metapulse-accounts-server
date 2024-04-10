package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AuthenticationController authenticationController;


    @PostMapping("/sendMessage")
    public ResponseEntity<?> createMessage(@RequestParam Integer chatId, @RequestParam String content, @RequestHeader("Authorization") String token ){
        String username = getUsernameFromToken(token);
        if (username != null) {
            try {
                messageService.createMessage(content,username,chatId);
                return ResponseEntity.ok("Saved the message!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to add the message");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    @GetMapping("/getMessages")
    public ResponseEntity<?> getAllMessagesFromChat(@RequestParam Integer chatId){
        try {
            Chat chat = chatService.getChatById(chatId);
            return ResponseEntity.ok(messageService.getMessagesByChatId(chatId));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to load the messages");
        }

    }

    @PostMapping("/createChat")
    public ResponseEntity<?> createChat(){
        try{

            Chat chat = chatService.createChat();
            return ResponseEntity.ok(chat);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to create chat");
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
