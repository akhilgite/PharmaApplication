package com.batcodes.pharmaapplication.model;

public class OrderedProducts {
    String uid;
    String categoryId;
    String name;
    String details;
    String price;
    String requestedQty;
    String image;
    String date;
    String time;
    String prescriptionImage;

    public OrderedProducts(String uid, String categoryId, String name, String details, String price, String requestedQty, String image, String date, String time, String prescriptionImage) {
        this.uid = uid;
        this.categoryId = categoryId;
        this.name = name;
        this.details = details;
        this.price = price;
        this.requestedQty = requestedQty;
        this.image = image;
        this.date = date;
        this.time = time;
        this.prescriptionImage = prescriptionImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(String requestedQty) {
        this.requestedQty = requestedQty;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrescriptionImage() {
        return prescriptionImage;
    }

    public void setPrescriptionImage(String prescriptionImage) {
        this.prescriptionImage = prescriptionImage;
    }
}
