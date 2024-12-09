package com;

public class lover {
    private int id;
    private String goodsName;
    private String owner;
    private double price;

    public lover(int id, String goodsName, String owner, double price) {
        this.id = id;
        this.goodsName = goodsName;
        this.owner = owner;
        this.price = price;
    }

    public int getId() {return id;}
    public String getGoodsName() { return goodsName; }
    public String getOwner() { return owner; }
    public double getPrice() { return price; }
}
