package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final ItemForSaleRepository itemForSaleRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final TradeRepository tradeRepository;

    private final UserService userService;

    private final ChatRepository chatRepository;

    private final MessageRepository messageRepository;

    private final ChatService  chatService;

    private final Singleton singleton;

    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public DatabaseSeeder(ItemForSaleRepository itemForSaleRepository, ItemRepository itemRepository, UserRepository userRepository, TradeRepository tradeRepository, UserService userService, ChatRepository chatRepository, MessageRepository messageRepository, ChatService chatService,  Singleton singleton, JdbcTemplate jdbcTemplate) {
        this.itemForSaleRepository = itemForSaleRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.tradeRepository = tradeRepository;
        this.userService = userService;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.chatService = chatService;
        this.singleton = singleton;
        this.jdbcTemplate = jdbcTemplate;
    }

    /*This will delete all the tables in database and seed them with specific data*/
    @Override
    public void run(String... args) throws Exception {

        // Delete previous data from all repositories
        //WARNING THIS WILL DELETE ALL THE DATABASE
        this.deleteAllRepositories();

        singleton.setKey(KeyGenerator.generateSecretKey());
        // Seed data into userRepository
        seedUsers();
        // Seed data into itemRepository
        seedItems();
        //Seed chat into chatRepository
        seedChat();




    }

    private void deleteAllRepositories(){
        tradeRepository.deleteAll();
        itemForSaleRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        messageRepository.deleteAll();
        chatRepository.deleteAll();
    }

    private void seedChat(){

        chatService.createChat(-1);
        chatService.createChat(-1);
        chatService.createChat(-1);
    }

    private void seedUsers(){

        userService.registerUser("angel","12345");
        userService.registerUser("edson","12345");

    }

    private void seedItems() {
        // Add your seed data here
        Item item1 = new Item();
        item1.setName("red");
        item1.setDescription("The color red");
        item1.setCode("1");
        item1.setUsername("angel");
        item1.setImagePath("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Rojos.png/640px-Rojos.png");
        item1.setIP("localhost");
        item1.setTradableStatus(false);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("green");
        item2.setDescription("The color green");
        item2.setCode("2");
        item2.setUsername("angel");
        item2.setImagePath("https://upload.wikimedia.org/wikipedia/commons/thumb/d/db/Tipos_de_verde.png/640px-Tipos_de_verde.png");
        item2.setIP("localhost");
        item2.setTradableStatus(false);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("blue");
        item3.setDescription("The color blue");
        item3.setCode("3");
        item3.setUsername("edson");
        item3.setImagePath("https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Tipos_de_azules.png/640px-Tipos_de_azules.png");
        item3.setIP("localhost");
        item3.setTradableStatus(false);
        itemRepository.save(item3);


    }


}
