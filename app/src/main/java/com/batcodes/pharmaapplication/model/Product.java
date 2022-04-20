package com.batcodes.pharmaapplication.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class Product implements Parcelable {
    String id;
    String productId;
    String categoryId;
    String name;
    String qty;
    String details;
    String price;
    String requestedQty;
    String image;

    public Product(String productId, String categoryId, String name, String qty, String details, String price, String image) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.name = name;
        this.qty = qty;
        this.details = details;
        this.price = price;
        this.image = image;
    }

    protected Product(Parcel in) {
        id = in.readString();
        productId = in.readString();
        categoryId = in.readString();
        name = in.readString();
        qty = in.readString();
        details = in.readString();
        price = in.readString();
        requestedQty = in.readString();
        image = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
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

    public String getCategoryId() {
        return categoryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(productId);
        parcel.writeString(categoryId);
        parcel.writeString(name);
        parcel.writeString(qty);
        parcel.writeString(details);
        parcel.writeString(price);
        parcel.writeString(requestedQty);
        parcel.writeString(image);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId) ;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.productId != null ? this.productId.hashCode() : 0);
        return hash;
    }
}
