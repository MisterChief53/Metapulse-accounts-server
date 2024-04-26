package com.metapulse.accountsserver;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AuthenticationController authenticationController;

    private final Singleton singleton;

    @Autowired
    public ChatController(Singleton singleton){
        this.singleton =singleton;
    }


    @PostMapping("/sendMessage")
    public ResponseEntity<?> createMessage(@RequestParam Integer chatId, @RequestParam String content, @RequestHeader("Authorization") String token ){
        String username = getUsernameFromToken(token);
        int id = singleton.getChatIds().get(chatId-1);
        System.out.println(id);
        if (username != null) {
            try {
                messageService.createMessage(content,username,id);
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
            int id =singleton.getChatIds().get(chatId-1);
            Chat chat = chatService.getChatById(id);
            return ResponseEntity.ok(messageService.getMessagesByChatId(chat.getId()));
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

    private final String apiUrl = "http://192.168.100.104:7070/chat/invoke";

    @PostMapping("/sendMessageIA")
    public ResponseEntity<?> createMessageIA(@RequestParam Integer chatId, @RequestParam String content, @RequestHeader("Authorization") String token ){
        String username = getUsernameFromToken(token);
        int id = singleton.getChatIds().get(chatId-1);
        System.out.println(id);
        if (username != null) {
            try {
                System.out.println("Before sending request to inference");
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String requestBody = "{\"input\":{\"message\":\""+content+"\"}}";
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
                System.out.println("after sending request to inference");
                ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
                System.out.println("Response status code: " + response.getStatusCode());
                ObjectMapper mapper = new ObjectMapper();

                Map responseMap = mapper.readValue(response.getBody(), Map.class);
                String aiMessage = (String) ((Map)responseMap.get("output")).get("content");
                messageService.createMessage(content,username,id);
                messageService.createMessage(aiMessage,"mAIni",id);
                System.out.println(aiMessage);
                return ResponseEntity.ok("Saved message");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to add the message");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
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
