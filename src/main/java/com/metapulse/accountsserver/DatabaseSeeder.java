package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final ItemForSaleRepository itemForSaleRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final TradeRepository tradeRepository;

    private final UserService userService;



    @Autowired
    public DatabaseSeeder(ItemForSaleRepository itemForSaleRepository, ItemRepository itemRepository, UserRepository userRepository, TradeRepository tradeRepository, UserService userService) {
        this.itemForSaleRepository = itemForSaleRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.tradeRepository = tradeRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        // Delete previous data from all repositories
        //WARNING THIS WILL DELETE ALL THE DATABASE
        this.deleteAllRepositories();

        // Seed data into userRepository
        seedUsers();
        // Seed data into itemRepository
        seedItems();
        
         */


    }

    private void deleteAllRepositories(){
        itemForSaleRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        tradeRepository.deleteAll();
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
