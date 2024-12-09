package com;

// Goods class for data encapsulation
public class Goods {
    private int id;
    private String goodsName;
    private String owner;
    private double price;
    private int quantity;

    public Goods(int id, String goodsName, String owner, double price, int quantity) {
        this.id = id;
        this.goodsName = goodsName;
        this.owner = owner;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getGoodsName() { return goodsName; }
    public String getOwner() { return owner; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
}
