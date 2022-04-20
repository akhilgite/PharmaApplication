package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.batcodes.pharmaapplication.adapter.interface2.AllProductItemClickListener;
import com.batcodes.pharmaapplication.adapter.AllProductListAdapter;
import com.batcodes.pharmaapplication.helper.Cart;
import com.batcodes.pharmaapplication.helper.ProductDatabaseHandler;
import com.batcodes.pharmaapplication.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class AllProductsListActivity extends AppCompatActivity implements AllProductItemClickListener {

    RecyclerView recyclerViewAllProducts;
    AllProductListAdapter allProductListAdapter;
    ProductDatabaseHandler productDatabaseHandler;
    ArrayList<Product>  productArrayList;
    String mCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product_list);

        mCategoryId = getIntent().getStringExtra("categoryId");
        productArrayList = new ArrayList<>();
        productDatabaseHandler = new ProductDatabaseHandler(getApplicationContext());

        //productArrayList = (ArrayList<Product>) productDatabaseHandler.getProductsForCategory(categoryId);
        FloatingActionButton fabCart = findViewById(R.id.fabCart);

        recyclerViewAllProducts = findViewById(R.id.recyclerViewAllProducts);

        allProductListAdapter = new AllProductListAdapter(productArrayList, this, this);
        recyclerViewAllProducts.setItemViewCacheSize(productArrayList.size());
        loadProducts(mCategoryId);

        recyclerViewAllProducts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if(allProductListAdapter!=null)
            recyclerViewAllProducts.setAdapter(allProductListAdapter);

        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Product> productArrayList = Cart.getInstance().getProducts();
                if(productArrayList==null || productArrayList.size()<=0){
                    Toast.makeText(AllProductsListActivity.this, "Cart is empty.", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intentCart = new Intent(AllProductsListActivity.this, CartActivity.class);
                startActivity(intentCart);
            }
        });
    }

    private void loadProducts(String mCategoryId) {
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
                                String productName = document.getData().get("productName").toString();
                                String productDetails = document.getData().get("productDetails").toString();
                                String productQty = document.getData().get("productQty").toString();
                                String productPrice = document.getData().get("productPrice").toString();
                                String categoryId =document.getData().get("categoryId").toString();
                                String productImage =document.getData().get("productImage").toString();

                                Product product = new Product(productId,categoryId ,productName, productQty, productDetails, productPrice, productImage);
                                productArrayList.add(product);
                            }

                            ArrayList<Product> temp = new ArrayList<>();
                            for (int i = 0; i < productArrayList.size(); i++) {
                                Product product = productArrayList.get(i);
                                if(product.getCategoryId().equalsIgnoreCase(mCategoryId))
                                    temp.add(product);
                            }

                            productArrayList.clear();
                            productArrayList.addAll(temp);
                            allProductListAdapter.notifyDataSetChanged();
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void itemClicked(int position) {
        //Product product = productArrayList.get(position);
    }

    @Override
    public void itemClicked(Product product) {
        Cart.getInstance().addProduct(product);
    }
}