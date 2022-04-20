package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.batcodes.pharmaapplication.adapter.OrderedProductDetailsListAdapter;
import com.batcodes.pharmaapplication.helper.OrderedProductsCache;
import com.batcodes.pharmaapplication.model.OrderedProducts;
import com.batcodes.pharmaapplication.model.PastOrder;
import com.batcodes.pharmaapplication.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrderedProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private OrderedProductDetailsListAdapter orderedProductDetailsListAdapter;
    private ArrayList<HashMap<String, String>> orderedProductsArrayList;
    private RecyclerView rvOrderedProducts;
    private boolean editOrderStatus;
    private int position;
    private String orderStatus;
    private String prescriptionImageUrl;

    private Button btnDeliver;
    private Button btnPrescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_product_details);
        if(getIntent().getExtras()!=null){
            editOrderStatus = (boolean) getIntent().getExtras().get("edit_order_status");
            position = (int) getIntent().getExtras().get("position");
        }

        rvOrderedProducts = findViewById(R.id.rv_ordered_product_details);
        btnDeliver = findViewById(R.id.btnDeliver);
        btnPrescription = findViewById(R.id.btnPrescription);

        btnDeliver.setOnClickListener(this);
        btnPrescription.setOnClickListener(this);

        rvOrderedProducts.setLayoutManager(new LinearLayoutManager(this));
        orderedProductsArrayList = OrderedProductsCache.getInstance().getOrderedProducts();


        if(orderedProductsArrayList!=null){
            orderedProductDetailsListAdapter = new OrderedProductDetailsListAdapter(orderedProductsArrayList);
            rvOrderedProducts.setAdapter(orderedProductDetailsListAdapter);
        }

        if(OrderedProductsCache.getInstance().getPastOrder()!=null && OrderedProductsCache.getInstance().getPastOrder().get("status")!=null){
            orderStatus = (String) OrderedProductsCache.getInstance().getPastOrder().get("status");
        }

        if(OrderedProductsCache.getInstance().getPastOrder()!=null && OrderedProductsCache.getInstance().getPastOrder().get("prescriptionImageUrl")!=null){
            prescriptionImageUrl = (String) OrderedProductsCache.getInstance().getPastOrder().get("prescriptionImageUrl");
            btnPrescription.setVisibility(View.VISIBLE);
        }else {
            btnPrescription.setVisibility(View.GONE);
        }

        if(editOrderStatus && !orderStatus.equalsIgnoreCase(Constants.ORDER_STATUS_DELIVERED)){
            btnDeliver.setVisibility(View.VISIBLE);
        }else {
            btnDeliver.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (view.getId()==R.id.btnDeliver){

            ArrayList<HashMap<String, Object>> pastOrderArrayList = OrderedProductsCache.getInstance().getPastOrderArrayList();

            Map<String, Object> pastOrder = (Map<String, Object>) pastOrderArrayList.get(position);
            pastOrder.put("status", Constants.ORDER_STATUS_DELIVERED);

            Map<String, Object> pastOrderList = new HashMap<>();
            pastOrderList.put("pastOrdersList", OrderedProductsCache.getInstance().getPastOrderArrayList());

            db.collection("orders")
                    .document((String) pastOrder.get("uid"))
                    .set(pastOrderList)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("TAG", "onComplete: ");
                                finish();
                            }
                        }
                    });
        }else if(view.getId() == R.id.btnPrescription){
            Intent intentPrescription = new Intent(this, PrescriptionActivity.class);
            intentPrescription.putExtra("viewPrescription",true);
            intentPrescription.putExtra("prescriptionImageUrl",prescriptionImageUrl);
            startActivity(intentPrescription);
        }

    }
}