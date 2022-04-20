package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.batcodes.pharmaapplication.adapter.OrderedProductsListAdapter;
import com.batcodes.pharmaapplication.adapter.interface2.OrderedProductItemClickListener;
import com.batcodes.pharmaapplication.fragments.OrderedProductsFragment;
import com.batcodes.pharmaapplication.helper.OrderedProductsCache;
import com.batcodes.pharmaapplication.model.PastOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminOrderListActivity extends AppCompatActivity implements OrderedProductItemClickListener {
    String TAG = "AdminOrderListActivity";
    private ArrayList<HashMap<String, Object>> pastOrderArrayList;
    private ProgressDialog progressDialog;
    private RecyclerView rvPastProducts;
    private OrderedProductsListAdapter orderedProductsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        pastOrderArrayList = new ArrayList<>();

        rvPastProducts = findViewById(R.id.rvPastProducts);
        rvPastProducts.setLayoutManager(new LinearLayoutManager(this));
        rvPastProducts.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderedProducts();
    }

    private void loadOrderedProducts() {
        showProgressDialog();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideProgressDialog();
                        if(task.isSuccessful()){
                            pastOrderArrayList=new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                pastOrderArrayList.addAll((Collection<? extends HashMap<String, Object>>) document.get("pastOrdersList"));
                            }

                            //HasMap cannot be sorted
                            //Collections.sort(pastOrderArrayList);

                            Collections.sort(pastOrderArrayList, (Comparator<HashMap<String, Object>>) (o1, o2) -> {
                                long t1=0;
                                if(o1!=null && o1.get("id")!=null)
                                    t1 = (long) o1.get("id");

                                long t2 = 0;
                                if(o2!=null && o2.get("id")!=null)
                                    t2 = (long) o2.get("id");

                                try {
                                    long difference = t1 - t2;
                                    if (difference == 0) {
                                        return 0;
                                    } else if (difference < 0) {
                                        return 1;
                                    } else {
                                        return -1;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            });


                            orderedProductsListAdapter = new OrderedProductsListAdapter(pastOrderArrayList, AdminOrderListActivity.this, AdminOrderListActivity.this);
                            rvPastProducts.setAdapter(orderedProductsListAdapter);
                            orderedProductsListAdapter.notifyDataSetChanged();
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
        Map<String, Object> pastProductMap  = (Map<String, Object>) pastOrderArrayList.get(position);
        ArrayList<HashMap<String, String>> temp = (ArrayList<HashMap<String, String>>) pastProductMap.get("orderedProductsArrayList");
        OrderedProductsCache.getInstance().setOrderedProducts(temp);
        OrderedProductsCache.getInstance().setPastOrder(pastProductMap);
        OrderedProductsCache.getInstance().setPastOrderArrayList(pastOrderArrayList);
        Intent intentOrderedProductDetails = new Intent(this, OrderedProductDetailsActivity.class);
        intentOrderedProductDetails.putExtra("edit_order_status", true);
        intentOrderedProductDetails.putExtra("position", position);
        startActivity(intentOrderedProductDetails);
    }
}