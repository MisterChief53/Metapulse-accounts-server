package com.metapulse.accountsserver;

import org.springframework.data.repository.CrudRepository;

public interface  TradeRepository extends CrudRepository<Trade, Integer> {
    Trade findTradeById(Integer Id);
}
