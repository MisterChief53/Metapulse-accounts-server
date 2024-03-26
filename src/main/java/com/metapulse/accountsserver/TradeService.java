package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeService {
    @Autowired
    private TradeRepository tradeRepository;

    public Integer createTrade (User user1, User user2) {
        Trade trade = new Trade();

        trade.setUser1(user1);
        trade.setUser2(user2);
        trade.setTradableMoneyUser1(0.0);
        trade.setTradableMoneyUser2(0.0);
        trade.setacceptedTradeUser1(false);
        trade.setacceptedTradeUser2(false);

        Trade t = tradeRepository.save(trade);

        return t.getId();
    }

    public Trade getTradeFromId(int id) {
        return tradeRepository.findTradeById(id);
    }
}
