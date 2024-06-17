package com.caternation.Objects;

public class OrderItem {
    String name;
    double price;
    int quantity;
    int thumbnail;
    String uid;

    public OrderItem() {
    }

    public OrderItem(String name, double price, int quantity, int thumbnail, String uid) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.thumbnail = thumbnail;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
