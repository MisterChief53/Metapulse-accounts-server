package com.metapulse.accountsserver;


import jakarta.persistence.*;

@Entity
@Table(name = "itemForSale")
public class ItemForSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private double price;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;


    public ItemForSale() {
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Item getItem() {
        return item;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
