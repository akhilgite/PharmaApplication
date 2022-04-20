package com.batcodes.pharmaapplication.helper;

import com.batcodes.pharmaapplication.model.OrderedProducts;
import com.batcodes.pharmaapplication.model.PastOrder;
import com.batcodes.pharmaapplication.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderedProductsCache {
    private static OrderedProductsCache instance;
    public ArrayList<HashMap<String, String>> orderedProducts;
    public Map<String, Object> pastOrder;
    public ArrayList<HashMap<String, Object>> pastOrderArrayList;

    private OrderedProductsCache(){
        orderedProducts = new ArrayList<>();
    }

    public static OrderedProductsCache getInstance(){
        if(instance==null){
            instance = new OrderedProductsCache();
            return instance;
        }else return instance;
    }

    public ArrayList<HashMap<String, String>> getOrderedProducts() {
        return orderedProducts;
    }

    public void setOrderedProducts(ArrayList<HashMap<String, String>> orderedProducts) {
        this.orderedProducts = orderedProducts;
    }

    public void clear(){
        orderedProducts.clear();
    }

    public Map<String, Object> getPastOrder() {
        return pastOrder;
    }

    public void setPastOrder(Map<String, Object> pastOrder) {
        this.pastOrder = pastOrder;
    }

    public ArrayList<HashMap<String, Object>> getPastOrderArrayList() {
        return pastOrderArrayList;
    }

    public void setPastOrderArrayList(ArrayList<HashMap<String, Object>> pastOrderArrayList) {
        this.pastOrderArrayList = pastOrderArrayList;
    }
}
