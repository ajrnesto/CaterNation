package com.caternation.Objects;

public class Package {
    String name;
    Double price;
    String items;
    int thumbnail;

    public Package() {
    }

    public Package(String name, Double price, String items, int thumbnail) {
        this.name = name;
        this.price = price;
        this.items = items;
        this.thumbnail = thumbnail;
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

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
