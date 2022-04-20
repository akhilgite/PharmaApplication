package com.batcodes.pharmaapplication.helper;

import com.batcodes.pharmaapplication.model.Product;

import java.util.ArrayList;

public class Cart {
    private static Cart instance;
    public ArrayList<Product> products;
    String prescriptionUrl;
    int cartValue;
    String personName;

    private Cart(){
        products = new ArrayList<>();
    }

    public static Cart getInstance(){
        if(instance==null){
            instance = new Cart();
            return instance;
        }else return instance;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        if(products !=null && product!=null){
            if(products.contains(product)){
                products.remove(product);
            }
            products.add(product);
        }
    }

    public void emptyCart(){
        products.clear();
    }

    public String getPrescriptionUrl() {
        return prescriptionUrl;
    }

    public void setPrescriptionUrl(String prescriptionUrl) {
        this.prescriptionUrl = prescriptionUrl;
    }

    public int getCartValue() {
        return cartValue;
    }

    public void setCartValue(int cartValue) {
        this.cartValue = cartValue;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
