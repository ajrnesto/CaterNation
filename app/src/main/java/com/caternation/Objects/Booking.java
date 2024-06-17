package com.caternation.Objects;

public class Booking {
    String uid;
    String userUid;
    String firstName;
    String lastName;
    String mobileNumber;
    String addressBuilding;
    String addressBarangay;
    String addressCity;
    long timestamp;
    long startDate;
    long startTime;
    long endDate;
    String status;
    double total;

    public Booking() {
    }

    public Booking(String uid, String userUid, String firstName, String lastName, String mobileNumber, String addressBuilding, String addressBarangay, String addressCity, long timestamp, long startDate, long startTime, long endDate, String status, double total) {
        this.uid = uid;
        this.userUid = userUid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.addressBuilding = addressBuilding;
        this.addressBarangay = addressBarangay;
        this.addressCity = addressCity;
        this.timestamp = timestamp;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.status = status;
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

    public String getAddressBuilding() {
        return addressBuilding;
    }

    public void setAddressBuilding(String addressBuilding) {
        this.addressBuilding = addressBuilding;
    }

    public String getAddressBarangay() {
        return addressBarangay;
    }

    public void setAddressBarangay(String addressBarangay) {
        this.addressBarangay = addressBarangay;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
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
}
