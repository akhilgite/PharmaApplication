package com.batcodes.pharmaapplication.adapter.interface2;

import com.batcodes.pharmaapplication.model.Product;

public interface AllProductItemClickListener {
    void itemClicked(int position);
    void itemClicked(Product product);
}
