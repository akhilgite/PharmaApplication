package com.batcodes.pharmaapplication.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.batcodes.pharmaapplication.OrderConfirmation;
import com.batcodes.pharmaapplication.OrderedProductDetailsActivity;
import com.batcodes.pharmaapplication.PaymentActivity;
import com.batcodes.pharmaapplication.R;
import com.batcodes.pharmaapplication.adapter.OrderedProductsListAdapter;
import com.batcodes.pharmaapplication.adapter.interface2.OrderedProductItemClickListener;
import com.batcodes.pharmaapplication.helper.OrderedProductsCache;
import com.batcodes.pharmaapplication.model.Category;
import com.batcodes.pharmaapplication.model.OrderedProducts;
import com.batcodes.pharmaapplication.model.PastOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderedProductsFragment extends Fragment implements OrderedProductItemClickListener {

    ArrayList<HashMap<String, Object>> pastOrderArrayList;
    private ProgressDialog progressDialog;
    RecyclerView rvPastProducts;
    OrderedProductsListAdapter orderedProductsListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_ordered_products, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        pastOrderArrayList = new ArrayList<>();

        rvPastProducts = root.findViewById(R.id.rvPastProducts);
        rvPastProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPastProducts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        loadOrderedProducts();
        return root;
    }

    private void loadOrderedProducts() {
        showProgressDialog();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        hideProgressDialog();
                        if (task.isSuccessful() && task.getResult().getData()!=null) {
                            pastOrderArrayList = (ArrayList<HashMap<String, Object>>) task.getResult().getData().get("pastOrdersList");
                            if (pastOrderArrayList==null)
                                pastOrderArrayList=new ArrayList<>();

                            Collections.reverse(pastOrderArrayList);
                            orderedProductsListAdapter = new OrderedProductsListAdapter(pastOrderArrayList, OrderedProductsFragment.this, getActivity());
                            rvPastProducts.setAdapter(orderedProductsListAdapter);
                            orderedProductsListAdapter.notifyDataSetChanged();
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
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

        Intent intentOrderedProductDetails = new Intent(getActivity(), OrderedProductDetailsActivity.class);
        startActivity(intentOrderedProductDetails);
    }
}