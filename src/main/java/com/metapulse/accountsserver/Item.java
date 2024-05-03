package com.metapulse.accountsserver;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.net.InetAddress;
import java.net.UnknownHostException;


/*The item entity, it has an id, a name, a description, the code that tells us which item it is,
* the username of the user that owns it, the path of the image that represents it, and a boolean
* that helps us to know if it is being traded*/
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

    private String username;

    private String imagePath;

    private Boolean tradable;

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

    public String getUsername() { return this.username; }

    public void setUsername(String username) { this.username = username; }

    public String getImagePath() { return this.imagePath; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Boolean getTradableStatus() { return this.tradable; }

    public void setTradableStatus(Boolean tradable) {this.tradable = tradable; }

}
