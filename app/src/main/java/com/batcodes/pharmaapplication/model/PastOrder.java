package com.batcodes.pharmaapplication.model;

import java.util.ArrayList;

public class PastOrder implements Comparable<PastOrder>{
    ArrayList<OrderedProducts> orderedProductsArrayList;
    String addressId;
    String paymentMode;
    String orderDate;
    String status;
    long id;
    String personName;
    String uid;
    String prescriptionImageUrl;

    public PastOrder() {
    }

    public PastOrder(ArrayList<OrderedProducts> orderedProductsArrayList, String addressId, String paymentMode, String orderDate) {
        this.orderedProductsArrayList = orderedProductsArrayList;
        this.addressId = addressId;
        this.paymentMode = paymentMode;
        this.orderDate = orderDate;
    }

    public ArrayList<OrderedProducts> getOrderedProductsArrayList() {
        return orderedProductsArrayList;
    }

    public void setOrderedProductsArrayList(ArrayList<OrderedProducts> orderedProductsArrayList) {
        this.orderedProductsArrayList = orderedProductsArrayList;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int compareTo(PastOrder pastOrder) {
        long intObj1 = id;
        long intObj2 = pastOrder.id;

        long difference = intObj1 - intObj2;
        if (difference == 0) {
            return 0;
        } else if (difference < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPrescriptionImageUrl() {
        return prescriptionImageUrl;
    }

    public void setPrescriptionImageUrl(String prescriptionImageUrl) {
        this.prescriptionImageUrl = prescriptionImageUrl;
    }
}
