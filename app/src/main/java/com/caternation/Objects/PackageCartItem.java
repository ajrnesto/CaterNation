package com.caternation.Objects;

public class PackageCartItem {
    String uid;
    String name;
    Double price;
    int size;
    int thumbnail;

    public PackageCartItem() {
    }

    public PackageCartItem(String uid, String name, Double price, int size, int thumbnail) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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
