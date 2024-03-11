package com.metapulse.accountsserver;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String name;

    private String description;

    private String code;

    private String worldIP;

    private String user;

    private String imagePath;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIP() { return worldIP; }

    public void setIP(String ip) { this.worldIP = ip; }

    public String getUser() { return this.user; }

    public void setUser(String user) { this.user = user; }

    public String getImagePath() { return this.imagePath; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

}
