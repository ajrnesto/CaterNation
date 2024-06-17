package com.caternation.Objects;

public class BookingItem {
    String uid;
    String name;
    double price;
    int size;
    int thumbnail;

    public BookingItem() {
    }

    public BookingItem(String uid, String name, double price, int size, int thumbnail) {
        this.uid = uid;
        this.name = name;
        this.price = price;
        this.size = size;
        this.thumbnail = thumbnail;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
