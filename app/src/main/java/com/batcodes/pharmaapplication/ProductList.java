package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.batcodes.pharmaapplication.adapter.ProductListAdapter;
import com.batcodes.pharmaapplication.adapter.interface2.ProductListItemClickListener;
import com.batcodes.pharmaapplication.helper.ProductDatabaseHandler;
import com.batcodes.pharmaapplication.model.Category;
import com.batcodes.pharmaapplication.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductList extends AppCompatActivity implements View.OnClickListener, ProductListItemClickListener {

    Button buttonAddProduct;
    ProductDatabaseHandler productDatabaseHandler;
    ArrayList<Product> productArrayList;
    RecyclerView recyclerViewProduct;
    ProductListAdapter productListAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        productArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        productDatabaseHandler = new ProductDatabaseHandler(getApplicationContext());

        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        recyclerViewProduct = findViewById(R.id.recyclerViewProduct);

        buttonAddProduct.setOnClickListener(this);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));
        productListAdapter = new ProductListAdapter(productArrayList, this);
        recyclerViewProduct.setAdapter(productListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonAddProduct:
                Intent intentAddProduct = new Intent(ProductList.this, AddProduct.class);
                startActivityForResult(intentAddProduct, 101);
                break;
        }
    }

    private void loadProducts(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productArrayList.clear();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());

                                String productId = document.getId();
                                String productName= "";
                                productName = document.getData().get("productName").toString();
                                String productDetails = document.getData().get("productDetails").toString();
                                String productQty = document.getData().get("productQty").toString();
                                String productPrice = document.getData().get("productPrice").toString();
                                String categoryId =document.getData().get("categoryId").toString();
                                String productImage =document.getData().get("productImage").toString();
                                productArrayList.add(new Product(productId,categoryId ,productName, productQty, productDetails, productPrice, productImage));
                            }
                            productListAdapter.notifyDataSetChanged();
                        } else {
                        }
                    }
                });
    }

    @Override
    public void editButtonClicked(int position) {
        Product product = productArrayList.get(position);
        Intent intentEditProduct = new Intent(this, AddProduct.class);
        intentEditProduct.putExtra("edit_product", product);
        startActivity(intentEditProduct);
    }

    @Override
    public void deleteButtonClicked(int position) {
        showProgressDialog();

        Product product = productArrayList.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("products")
                .document(product.getProductId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        if(task.isSuccessful()){
                            Toast.makeText(ProductList.this, product.getName()+" deleted successfully.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void showProgressDialog() {
        this.progressDialog.show();
    }

    public void hideProgressDialog() {
        if (this.progressDialog.isShowing()) {
            this.progressDialog.hide();
        }
    }
}