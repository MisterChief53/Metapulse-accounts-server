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

    /*This endpoint receives the chatid, which represents the chat that receives the message,
    * the content, and the authorization token of the user to check if it is a authorized user,
    * first we create a new instance of message, if it is created as intended, it returns an ok response, else
    * it returns a bad request*/
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

    /*This endpoin fetches all the messages of a chat, it receives the chatId,
    * then it searches by the id the messages and return them in the response*/
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

    /*This endpoint retreives the messages from a chat without decripting the message*/
    @GetMapping("/getEncrypted")
    public ResponseEntity<?> getAllEncryptedMessagesFromChat(@RequestParam Integer chatId){
        try {
            int id =singleton.getChatIds().get(chatId-1);
            Chat chat = chatService.getChatById(id);
            return ResponseEntity.ok(messageService.getEncryptedMessagesByChatId(chat.getId()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to load the messages");
        }

    }

    /*This endpoint creates an instance of chat*/
    @PostMapping("/createChat")
    public ResponseEntity<?> createChat(){
        try{
            Chat chat = chatService.createChat(-1);
            return ResponseEntity.ok(chat);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to create chat");
        }
    }

    private final String apiUrl = "http://192.168.100.104:7070/chat/invoke";
    /*This endpoint saves a conversation between a user and the ai, it receives the same
    * parameters as the normal sendMessage, chatId, content and token, but we make a request to the AI
    * api, which needs a request body containing the input and a header containing the content type, which
    * is application_json, the api returns a response entity with the status code and the output, that also has
    * a content field, which has the message from the AI, to retreive it, it uses a mapper, after getting the message,
    * it saves all in the corresponding chat*/
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
