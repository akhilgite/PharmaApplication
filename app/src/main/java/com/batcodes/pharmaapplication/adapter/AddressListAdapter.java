package com.batcodes.pharmaapplication.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.batcodes.pharmaapplication.R;
import com.batcodes.pharmaapplication.adapter.interface2.AddresstItemClickListener;
import com.batcodes.pharmaapplication.adapter.interface2.CategoryItemClickListener;
import com.batcodes.pharmaapplication.model.Address;
import com.batcodes.pharmaapplication.model.Category;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ItemHolder>{

    private ArrayList<Address> addressArrayList;
    public AddresstItemClickListener addresstItemClickListener;
    public static int previousClickPosition=-1;

    public AddressListAdapter(ArrayList<Address> addressArrayList, AddresstItemClickListener addresstItemClickListener) {
        this.addressArrayList = addressArrayList;
        this.addresstItemClickListener = addresstItemClickListener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_address_list, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder viewHolder, final int position) {
        Address address = addressArrayList.get(position);
        viewHolder.textViewAddressName .setText(address.getName());
        viewHolder.textViewAddressDetail.setText(address.getAddress());
        if(address.getMobileNumber()!=null && address.getPincode()!=null)
            viewHolder.textViewOtherDetails.setText("Mobile Number: "+address.getMobileNumber()+"\nPincode: "+address.getPincode());

        if (!address.isSelected())
            viewHolder.ivChecked.setVisibility(View.GONE);
        else viewHolder.ivChecked.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return addressArrayList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private final TextView textViewAddressName;
        private final TextView textViewAddressDetail;
        private final ImageView ivChecked;
        private final TextView textViewOtherDetails;


        public ItemHolder(View view) {
            super(view);
            textViewAddressName = view.findViewById(R.id.textViewAddressName);
            textViewAddressDetail = view.findViewById(R.id.textViewAddressDetail);
            textViewOtherDetails = view.findViewById(R.id.textViewOtherDetails);

            ivChecked = view.findViewById(R.id.ivChecked);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("AddressListAdapter", "onClick: Previous Position :: "+previousClickPosition);
            if(previousClickPosition!=-1)
                addressArrayList.get(previousClickPosition).setSelected(false);
            addresstItemClickListener.itemClicked(getAdapterPosition());
            addressArrayList.get(getAdapterPosition()).setSelected(true);

            int position = getAdapterPosition();
            previousClickPosition = position;
            notifyDataSetChanged();
        }
    }
}
