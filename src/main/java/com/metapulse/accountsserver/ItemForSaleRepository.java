package com.metapulse.accountsserver;

import org.springframework.data.repository.CrudRepository;

public interface  ItemForSaleRepository extends CrudRepository<ItemsForSale, Integer> {
    boolean findByItemId(int itemId);
}
