package com.metapulse.accountsserver;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Integer>{
    Item findByName(String name);

    List<Item> findByUsername(String username);
}
