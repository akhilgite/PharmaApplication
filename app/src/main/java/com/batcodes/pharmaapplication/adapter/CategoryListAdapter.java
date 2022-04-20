package com.batcodes.pharmaapplication.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.batcodes.pharmaapplication.R;
import com.batcodes.pharmaapplication.adapter.interface2.CategoryItemClickListener;
import com.batcodes.pharmaapplication.model.Category;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ItemHolder>{
    private final ArrayList<Category> categoryArrayList;
    public CategoryItemClickListener categoryItemClickListener;

    public CategoryListAdapter(ArrayList<Category> categoryArrayList, CategoryItemClickListener categoryItemClickListener) {
        this.categoryArrayList = categoryArrayList;
        this.categoryItemClickListener = categoryItemClickListener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_category_list, viewGroup, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder viewHolder, final int position) {
        Category category = categoryArrayList.get(position);
        viewHolder.textViewCategoryName.setText(category.getName());

        Log.d("TAG", "onBindViewHolder: "+categoryItemClickListener.getClass().getName());
        if (categoryItemClickListener.getClass().getName().equals("com.batcodes.pharmaapplication.fragments.HomeFragment")){
            viewHolder.ivEdit.setVisibility(View.GONE);
            viewHolder.ivDelete.setVisibility(View.GONE);
        }

        loadImage(viewHolder, category.getIcon());
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    private Bitmap loadImage(String path) throws FileNotFoundException {
        File file = new File(path);
        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
        return b;
    }

    public class ItemHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private final TextView textViewCategoryName;
        private final ImageView imageViewIcon;
        private final ImageView ivEdit;
        private final ImageView ivDelete;

        public ItemHolder(View view) {
            super(view);
            textViewCategoryName = view.findViewById(R.id.textViewCategoryName);
            imageViewIcon = view.findViewById(R.id.imageViewIcon);
            ivEdit = view.findViewById(R.id.ivEdit);
            ivDelete = view.findViewById(R.id.ivDelete);

            ivEdit.setOnClickListener(this);
            ivDelete.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ivEdit:
                    categoryItemClickListener.editButtonClicked(getAdapterPosition());
                    break;
                case R.id.ivDelete:
                    categoryItemClickListener.deleteButtonClicked(getAdapterPosition());
                    break;
                default:
                    categoryItemClickListener.itemClicked(getAdapterPosition());
                     break;
            }
        }
    }

    private void loadImage(final ItemHolder holder, String url) {

        if (url != null && url.length() > 0) {
            FirebaseStorage.getInstance().getReferenceFromUrl(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.imageViewIcon);
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception exception) {
                    exception.printStackTrace();
                }
            });
        }
    }
}
