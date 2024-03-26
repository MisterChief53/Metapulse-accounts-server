package com.metapulse.accountsserver;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Integer>{
    Item findByName(String name);

    Item findItemById(Integer Id);

    List<Item> findByUsername(String username);

    List<Item> findByUsernameAndTradableIsTrue(String username);
}
