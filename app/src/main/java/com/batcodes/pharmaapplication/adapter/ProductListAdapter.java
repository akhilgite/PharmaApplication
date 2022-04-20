package com.batcodes.pharmaapplication.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.batcodes.pharmaapplication.R;
import com.batcodes.pharmaapplication.adapter.interface2.ProductListItemClickListener;
import com.batcodes.pharmaapplication.model.Category;
import com.batcodes.pharmaapplication.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ItemHolder>{

    private ArrayList<Product> productArrayList;
    private ProductListItemClickListener productListItemClickListener;

    public ProductListAdapter(ArrayList<Product> productArrayList, ProductListItemClickListener productListItemClickListener) {
        this.productArrayList = productArrayList;
        this.productListItemClickListener = productListItemClickListener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_product_list, viewGroup, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder viewHolder, final int position) {
        Product product = productArrayList.get(position);
        viewHolder.textViewProductName.setText(product.getName());
        viewHolder.textViewProductDetails.setText(product.getDetails());
        String productPrice = "Rs "+product.getPrice()+"/-";
        viewHolder.textViewProductPrice.setText(productPrice);
        loadImage(viewHolder, product.getImage());
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    private Bitmap loadImage(String path) throws FileNotFoundException {
        File file = new File(path);
        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
        return b;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewProductName;
        private final TextView textViewProductDetails;
        private final TextView textViewProductPrice;
        private final ImageView imageViewIcon;
        //private final Button btnEdit;
        //private final Button btnDelete;

        private final ImageView ivEdit;
        private final ImageView ivDelete;

        public ItemHolder(View view) {
            super(view);

            textViewProductName = (TextView) view.findViewById(R.id.textViewProductName);
            textViewProductDetails = view.findViewById(R.id.textViewProductDetails);
            textViewProductPrice = view.findViewById(R.id.textViewProductPrice);
            imageViewIcon = view.findViewById(R.id.imageViewIcon);
            //btnEdit = view.findViewById(R.id.btnEdit);
            //btnDelete = view.findViewById(R.id.btnDelete);

            ivEdit = view.findViewById(R.id.ivEdit);
            ivDelete = view.findViewById(R.id.ivDelete);

            //btnEdit.setOnClickListener(this);
            //btnDelete.setOnClickListener(this);

            ivEdit.setOnClickListener(this);
            ivDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ivEdit:
                    if(productListItemClickListener!=null)
                        productListItemClickListener.editButtonClicked(getAdapterPosition());
                    break;
                case R.id.ivDelete:
                    if(productListItemClickListener!=null)
                        productListItemClickListener.deleteButtonClicked(getAdapterPosition());
                    break;
            }
        }
    }

    private void loadImage(final ItemHolder holder, String url) {

        if (url != null && url.length() > 0) {
            FirebaseStorage.getInstance().getReferenceFromUrl(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(holder.imageViewIcon);
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception exception) {
                    exception.printStackTrace();
                }
            });
        }
    }
}
