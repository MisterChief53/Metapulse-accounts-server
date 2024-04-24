package com.metapulse.accountsserver;

import jakarta.persistence.*;

@Entity
@Table(name = "trades")
public class Trade {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user1_id")
    private User user1;

    @OneToOne
    @JoinColumn(name = "user2_id")
    private User user2;

    private Double tradableMoneyUser1;

    private Double tradableMoneyUser2;

    private Boolean  acceptedTradeUser1 = false;

    private Boolean  acceptedTradeUser2 = false;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser1() {
        return this.user1;
    }

    public void setUser1(User user) {
        this.user1 = user;
    }

    public User getUser2() {
        return this.user2;
    }

    public void setUser2(User user) {
        this.user2 = user;
    }

    public Double getTradableMoneyUser1() { return this.tradableMoneyUser1; }

    public void setTradableMoneyUser1(Double moneyUser1) { this.tradableMoneyUser1 = moneyUser1; }

    public Double getTradableMoneyUser2() { return this.tradableMoneyUser2; }

    public void setTradableMoneyUser2(Double moneyUser2) { this.tradableMoneyUser2 = moneyUser2; }

    public Boolean getacceptedTradeUser1() {
        return this.acceptedTradeUser1;
    }

    public void setacceptedTradeUser1(Boolean state) {
        this.acceptedTradeUser1 = state;
    }

    public Boolean getacceptedTradeUser2() {
        return this.acceptedTradeUser2;
    }

    public void setacceptedTradeUser2(Boolean state) {
        this.acceptedTradeUser2 = state;
    }
}
