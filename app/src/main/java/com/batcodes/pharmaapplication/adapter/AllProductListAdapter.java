package com.batcodes.pharmaapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.batcodes.pharmaapplication.R;
import com.batcodes.pharmaapplication.adapter.interface2.AllProductItemClickListener;
import com.batcodes.pharmaapplication.helper.Cart;
import com.batcodes.pharmaapplication.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AllProductListAdapter extends RecyclerView.Adapter<AllProductListAdapter.ItemHolder> implements Filterable {
    private String TAG = getClass().getName();
    private AllProductItemClickListener allProductItemClickListener;
    private ArrayList<Product> productArrayList;
    private ArrayList<Product> productArrayListFiltered;
    private Context context;

    public AllProductListAdapter(ArrayList<Product> productArrayList, AllProductItemClickListener allProductItemClickListener,
    Context context) {
        this.productArrayList=productArrayList;
        this.productArrayListFiltered = productArrayList;
        this.allProductItemClickListener = allProductItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_all_product_list, viewGroup, false);
        return new ItemHolder(view, new MyCustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Product product = productArrayListFiltered.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewDetails.setText(product.getDetails());
        holder.textViewPrice.setText("Rs "+product.getPrice()+"/-");
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());

        holder.imageViewCheck.setVisibility(View.INVISIBLE);
        holder.editTextQty.setVisibility(View.VISIBLE);
        holder.imageViewAddToCart.setVisibility(View.VISIBLE);

        if(Cart.getInstance().getProducts()!=null && Cart.getInstance().getProducts().contains(product)){
            int index = Cart.getInstance().getProducts().indexOf(product);
            Product temp = Cart.getInstance().getProducts().get(index);
            if(temp!=null)
                holder.editTextQty.setText(temp.getRequestedQty());
        }else {
            holder.editTextQty.setText("1");
        }

        loadImage(holder, product.getImage());
    }

    @Override
    public int getItemCount() {
        return productArrayListFiltered.size();
    }

    private Bitmap loadImage(String path) throws FileNotFoundException {
        File file = new File(path);
        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
        return b;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productArrayListFiltered = productArrayList;
                } else {
                    ArrayList<Product> filteredList = new ArrayList<>();
                    for (Product row : productArrayList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    productArrayListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productArrayListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productArrayListFiltered = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewName;
        TextView textViewDetails;
        TextView textViewPrice;
        EditText editTextQty;
        ImageView imageViewAddToCart;
        ImageView imageViewIcon;
        ImageView imageViewCheck;
        public MyCustomEditTextListener myCustomEditTextListener;

        public ItemHolder(View view, MyCustomEditTextListener myCustomEditTextListener) {
            super(view);

            textViewName = view.findViewById(R.id.textViewName);
            textViewDetails = view.findViewById(R.id.textViewDetails);
            textViewPrice = view.findViewById(R.id.textViewPrice);
            editTextQty = view.findViewById(R.id.editTextQty);
            imageViewAddToCart = view.findViewById(R.id.imageViewAddToCart);
            imageViewIcon = view.findViewById(R.id.imageViewIcon);
            imageViewCheck = view.findViewById(R.id.imageViewAddedToCart);

            this.myCustomEditTextListener = myCustomEditTextListener;
            imageViewAddToCart.setOnClickListener(this);
            this.editTextQty.addTextChangedListener(myCustomEditTextListener);
        }

        @Override
        public void onClick(View view) {
            Product product = productArrayListFiltered.get(getAdapterPosition());
            if(product==null || product.getRequestedQty()==null || Integer.parseInt(product.getRequestedQty())<=0){
                Toast.makeText(context, "Enter valid quantity.", Toast.LENGTH_SHORT).show();
                return;
            }

            int requestedQty = Integer.parseInt(product.getRequestedQty());
            int availableQty = Integer.parseInt(product.getQty());
            if(requestedQty>availableQty){
                Toast.makeText(context, "Requested qty is more than available qty, you can order max "+availableQty+" quantity.", Toast.LENGTH_LONG).show();
                return;
            }

            imageViewCheck.setVisibility(View.VISIBLE);
            Toast.makeText(view.getContext(), productArrayList.get(getAdapterPosition()).getRequestedQty(), Toast.LENGTH_SHORT).show();
            allProductItemClickListener.itemClicked(getAdapterPosition());
            allProductItemClickListener.itemClicked(product);
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String stringRequestQty = charSequence.toString();
            if(stringRequestQty.length()>0)
            {
                //int requestedQuantity = Integer.parseInt(stringRequestQty);
                productArrayList.get(position).setRequestedQty(stringRequestQty);
                /*if(requestedQty>availableQty)
                    Toast.makeText(context, "Requested qty is more than available qty, you can order max "+availableQty+" quantity.", Toast.LENGTH_LONG).show();*/
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
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
