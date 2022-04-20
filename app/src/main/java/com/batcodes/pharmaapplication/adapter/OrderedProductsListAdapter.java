package com.batcodes.pharmaapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.batcodes.pharmaapplication.R;
import com.batcodes.pharmaapplication.adapter.interface2.CartProductItemClickListener;
import com.batcodes.pharmaapplication.adapter.interface2.OrderedProductItemClickListener;
import com.batcodes.pharmaapplication.helper.Cart;
import com.batcodes.pharmaapplication.model.OrderedProducts;
import com.batcodes.pharmaapplication.model.PastOrder;
import com.batcodes.pharmaapplication.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderedProductsListAdapter extends RecyclerView.Adapter<OrderedProductsListAdapter.ItemHolder> {
    String TAG = getClass().getName();
    OrderedProductItemClickListener orderedProductItemClickListener;
    ArrayList<HashMap<String, Object>> pastOrderArrayList;
    Context context;

    public OrderedProductsListAdapter(ArrayList<HashMap<String, Object>> pastOrderArrayList, OrderedProductItemClickListener orderedProductItemClickListener,
                                      Context context) {
        this.pastOrderArrayList= pastOrderArrayList;
        this.orderedProductItemClickListener = orderedProductItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_ordered_products, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+context.getClass().getName());
        //context.getClass().getName().equalsIgnoreCase("OrderedProductsFragment")

        Map<String, Object> pastProductMap  = (Map<String, Object>) pastOrderArrayList.get(position);

        if(context.getClass().getName().contains("AdminOrderListActivity") && pastProductMap.get("personName")!=null){
            holder.tvName.setText(pastProductMap.get("personName").toString());
        }else holder.tvName.setVisibility(View.GONE);

        holder.tvDateTime.setText(String.format("Order placed on %s", pastProductMap.get("orderDate")));
        holder.tvTotalCost.setText(String.format("Payment Mode: %s/-", pastProductMap.get("paymentMode")));
        holder.tvPaymentMode.setText(String.format("Order status: %s",pastProductMap.get("status")));
    }

    @Override
    public int getItemCount() {
        return pastOrderArrayList==null?0:pastOrderArrayList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDateTime;
        TextView tvTotalCost;
        TextView tvPaymentMode;
        TextView tvName;

        public ItemHolder(View view) {
            super(view);

            tvDateTime = view.findViewById(R.id.tvDateTime);
            tvTotalCost = view.findViewById(R.id.tvTotalCost);
            tvPaymentMode = view.findViewById(R.id.tvPaymentMode);
            tvName = view.findViewById(R.id.tvName);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            orderedProductItemClickListener.itemClicked(getAdapterPosition());
        }
    }
}
