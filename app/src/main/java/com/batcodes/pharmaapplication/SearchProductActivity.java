package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.batcodes.pharmaapplication.adapter.AllProductListAdapter;
import com.batcodes.pharmaapplication.adapter.interface2.AllProductItemClickListener;
import com.batcodes.pharmaapplication.helper.Cart;
import com.batcodes.pharmaapplication.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class SearchProductActivity extends AppCompatActivity implements AllProductItemClickListener, View.OnClickListener {
    private ProgressDialog progressDialog;
    private ArrayList<Product> productArrayList;
    private AllProductListAdapter allProductListAdapter;

    private RecyclerView rvAllProducts;
    private EditText etSearch;
    private ImageView ivDelete;
    private FloatingActionButton fabCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        productArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");

        rvAllProducts = findViewById(R.id.recyclerViewProduct);
        etSearch = findViewById(R.id.et_search);
        ivDelete = findViewById(R.id.iv_delete);
        fabCart = findViewById(R.id.fabCart);

        rvAllProducts.setItemViewCacheSize(productArrayList.size());
        rvAllProducts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fabCart.setOnClickListener(this);
        ivDelete.setOnClickListener(this);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                allProductListAdapter.getFilter().filter(s.toString());
            }
        });


        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                            allProductListAdapter = new AllProductListAdapter(productArrayList, SearchProductActivity.this, SearchProductActivity.this);
                            rvAllProducts.setAdapter(allProductListAdapter);
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

    @Override
    public void itemClicked(int position) {

    }

    @Override
    public void itemClicked(Product product) {
        Cart.getInstance().addProduct(product);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabCart:
                ArrayList<Product> productArrayList = Cart.getInstance().getProducts();
                if(productArrayList==null || productArrayList.size()<=0){
                    Toast.makeText(SearchProductActivity.this, "Cart is empty.", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intentCart = new Intent(SearchProductActivity.this, CartActivity.class);
                startActivity(intentCart);
                break;
            case R.id.iv_delete:
                etSearch.setText("");
                break;
        }
    }
}