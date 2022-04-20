package com.batcodes.pharmaapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.batcodes.pharmaapplication.helper.Cart;
import com.batcodes.pharmaapplication.R;
import com.batcodes.pharmaapplication.adapter.interface2.CartProductItemClickListener;
import com.batcodes.pharmaapplication.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CartProductListAdapter extends RecyclerView.Adapter<CartProductListAdapter.ItemHolder> {

    CartProductItemClickListener cartProductItemClickListener;
    ArrayList<Product> productArrayList;
    Context context;

    public CartProductListAdapter(CartProductItemClickListener cartProductItemClickListener,
                                  Context context) {
        this.productArrayList= Cart.getInstance().getProducts();
        this.cartProductItemClickListener = cartProductItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_cart_products, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewDetails.setText(product.getDetails());
        holder.textViewPrice.setText("Rs "+product.getPrice()+"/-");
        holder.textViewQuantity.setText("Requested Quantity : "+product.getRequestedQty());

        loadImage(holder,product.getImage());
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
        TextView textViewName;
        TextView textViewDetails;
        TextView textViewPrice;
        ImageView imageViewIcon;
        TextView textViewQuantity;

        public ItemHolder(View view) {
            super(view);

            textViewName = view.findViewById(R.id.textViewName);
            textViewDetails = view.findViewById(R.id.textViewDetails);
            textViewPrice = view.findViewById(R.id.textViewPrice);
            imageViewIcon = view.findViewById(R.id.imageViewIcon);
            textViewQuantity = view.findViewById(R.id.textViewQuantity);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(view.getContext(), productArrayList.get(getAdapterPosition()).getRequestedQty(), Toast.LENGTH_SHORT).show();
            cartProductItemClickListener.itemClicked(getAdapterPosition());
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
