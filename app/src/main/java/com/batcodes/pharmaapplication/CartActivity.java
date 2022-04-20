package com.batcodes.pharmaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.batcodes.pharmaapplication.adapter.interface2.CartProductItemClickListener;
import com.batcodes.pharmaapplication.adapter.CartProductListAdapter;
import com.batcodes.pharmaapplication.helper.Cart;
import com.batcodes.pharmaapplication.helper.CategoryDatabaseHandler;
import com.batcodes.pharmaapplication.model.Product;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements View.OnClickListener, CartProductItemClickListener {

    RecyclerView rvCartProducts;
    Button buttonCheckout;
    CartProductListAdapter cartProductListAdapter;
    TextView tvCartValue;
    CategoryDatabaseHandler categoryDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //categoryDatabaseHandler = new CategoryDatabaseHandler(this);

        rvCartProducts = findViewById(R.id.rvCartProducts);
        buttonCheckout = findViewById(R.id.buttonCheckout);
        tvCartValue = findViewById(R.id.tvCartValue);

        buttonCheckout.setOnClickListener(this);
        cartProductListAdapter = new CartProductListAdapter(this, this);
        rvCartProducts.setLayoutManager(new LinearLayoutManager(this));
        rvCartProducts.setAdapter(cartProductListAdapter);

        ArrayList<Product> productArrayList = Cart.getInstance().getProducts();
        if(productArrayList!=null){
            int totalValue = 0;
            for (int i = 0; i < productArrayList.size(); i++) {
                totalValue+=Integer.parseInt(productArrayList.get(i).getPrice()) * Integer.parseInt(productArrayList.get(i).getRequestedQty());
            }

            Cart.getInstance().setCartValue(totalValue);
            tvCartValue.setText("Total Cart Value: Rs "+totalValue);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonCheckout:
                Intent intentAddress = new Intent(CartActivity.this, AddressListActivity.class);
                startActivity(intentAddress);
                break;
        }
    }

    //delete product
    @Override
    public void itemClicked(int position) {

    }
}