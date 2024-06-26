package com.metapulse.accountsserver;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "users") // Specify a different table name, since postgres uses "user" as reserved name.
public class User {

    /*The user class that represents the user table, it has an integer id, a name which is
    * unique, a password that is saved as a hash, the amount of money a user owns, and a boolean
    * that helps us to now if the user has a trade invitation*/

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String name;

    private String password;

    private Double money;

    private Boolean tradeInvitation;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getMoney() { return this.money; }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Boolean getTradeInvitation() {
        return tradeInvitation;
    }

    public void setTradeInvitation(Boolean tradeInvitation) {
        this.tradeInvitation = tradeInvitation;
    }



}
