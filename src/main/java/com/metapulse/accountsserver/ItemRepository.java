package com.metapulse.accountsserver;

import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Integer>{
    Item findByName(String name);
}
