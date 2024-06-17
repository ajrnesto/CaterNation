package com.caternation.Objects;

import java.util.ArrayList;

public class Order {
    String addressBarangay;
    String addressBuilding;
    String addressCity;
    String firstName;
    String lastName;
    String mobileNumber;
    String notes;
    String status;
    double total;
    String uid;
    String userUid;
    long timestamp;

    public Order() {
    }

    public Order(String addressBarangay, String addressBuilding, String addressCity, String firstName, String lastName, String mobileNumber, String notes, String status, double total, String uid, String userUid, long timestamp) {
        this.addressBarangay = addressBarangay;
        this.addressBuilding = addressBuilding;
        this.addressCity = addressCity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.notes = notes;
        this.status = status;
        this.total = total;
        this.uid = uid;
        this.userUid = userUid;
        this.timestamp = timestamp;
    }

    public String getAddressBarangay() {
        return addressBarangay;
    }

    public void setAddressBarangay(String addressBarangay) {
        this.addressBarangay = addressBarangay;
    }

    public String getAddressBuilding() {
        return addressBuilding;
    }

    public void setAddressBuilding(String addressBuilding) {
        this.addressBuilding = addressBuilding;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
