package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VendorDashboard extends AppCompatActivity implements View.OnClickListener {

    Button buttonProduct, buttonCategory, buttonOrderes;
    TextView tvProduct, tvCategory, tvOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dashboard);

        tvProduct = findViewById(R.id.tvProduct);
        tvCategory = findViewById(R.id.tvCategory);
        tvOrders = findViewById(R.id.tvOrders);

        tvProduct.setOnClickListener(this);
        tvCategory.setOnClickListener(this);
        tvOrders.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tvProduct:
                Intent intentProduct = new Intent(VendorDashboard.this, ProductList.class);
                startActivity(intentProduct);
                break;
            case R.id.tvCategory:
                Intent intentCategory = new Intent(VendorDashboard.this, CategoryList.class);
                startActivity(intentCategory);
                break;
            case R.id.tvOrders:
                Intent intentOrderList = new Intent(VendorDashboard.this, AdminOrderListActivity.class);
                startActivity(intentOrderList);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vendor_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                Intent intentAddPrescription = new Intent(VendorDashboard.this, LoginActivity.class);
                startActivity(intentAddPrescription);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}