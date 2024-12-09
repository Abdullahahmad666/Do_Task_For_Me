package com.example.dotaskforme;

public class Order {
    private String title;
    private String time;
    private String status;
    private String phone;
    private String link;
    private String price;

    // No-argument constructor (needed for Firestore)
    public Order() {
        // Firestore uses this constructor for deserialization
    }

    // Constructor with arguments (if you need it for other purposes)
    public Order(String title, String time, String status, String phone, String link, String price) {
        this.title = title;
        this.time = time;
        this.status = status;
        this.phone = phone;
        this.link = link;
        this.price = price;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}