package com.metapulse.accountsserver;

import org.springframework.data.repository.CrudRepository;

public interface  ItemForSaleRepository extends CrudRepository<ItemForSale, Integer> {
    ItemForSale findByItemId(int itemId);
}
