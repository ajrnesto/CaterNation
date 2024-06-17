package com.caternation.Objects;

public class CartItem {
    String uid;
    String name;
    Double price;
    int thumbnail;
    int quantity;

    public CartItem() {
    }

    public CartItem(String uid, String name, Double price, int thumbnail, int quantity) {
        this.uid = uid;
        this.name = name;
        this.price = price;
        this.thumbnail = thumbnail;
        this.quantity = quantity;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
