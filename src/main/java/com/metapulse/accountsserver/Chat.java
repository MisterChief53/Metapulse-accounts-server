package com.metapulse.accountsserver;

import jakarta.persistence.*;

@Entity
@Table(name = "Chat")
public class Chat {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    public Chat() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
