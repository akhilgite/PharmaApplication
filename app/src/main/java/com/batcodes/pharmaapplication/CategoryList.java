package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.batcodes.pharmaapplication.adapter.GridSpacingItemDecoration;
import com.batcodes.pharmaapplication.adapter.interface2.CategoryItemClickListener;
import com.batcodes.pharmaapplication.adapter.CategoryListAdapter;
import com.batcodes.pharmaapplication.helper.CategoryDatabaseHandler;
import com.batcodes.pharmaapplication.model.Category;
import com.batcodes.pharmaapplication.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CategoryList extends AppCompatActivity implements View.OnClickListener, CategoryItemClickListener {

    Button buttonAddCategory;
    RecyclerView recyclerViewCategory;
    CategoryListAdapter categoryListAdapter;
    CategoryDatabaseHandler databaseHandler;
    ArrayList<Category> categoryArrayList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle("Loading");
        this.progressDialog.setMessage("Please wait...");

        categoryArrayList = new ArrayList<>();
        databaseHandler = new CategoryDatabaseHandler(getApplicationContext());

        buttonAddCategory = findViewById(R.id.buttonAddCategory);
        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);

        buttonAddCategory.setOnClickListener(this);
        recyclerViewCategory.setLayoutManager(new GridLayoutManager(this,2));
        int spanCount = 2; // 3 columns
        int spacing = 16; // 50px
        boolean includeEdge = false;
        recyclerViewCategory.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
    }

    @Override
    protected void onResume() {
        super.onResume();
        categoryListAdapter = new CategoryListAdapter( categoryArrayList, this);
        recyclerViewCategory.setAdapter(categoryListAdapter);

        loadCategories();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonAddCategory:
                Intent intentAddCategory = new Intent(CategoryList.this, AddCategory.class);
                startActivity(intentAddCategory);
                break;
        }
    }

    private void loadCategories(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            categoryArrayList.clear();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d("TAG", "onComplete: categoryId : "+document.getId());
                                Log.d("TAG", "onComplete: uid : "+document.getData().get("uid"));
                                Log.d("TAG", "onComplete: categoryImage : "+document.getData().get("categoryImage"));
                                Log.d("TAG", "onComplete: name : "+document.getData().get("categoryName"));

                                String categoryId = document.getId();
                                String categoryName = document.getData().get("categoryName").toString();
                                String categoryImage = document.getData().get("categoryImage").toString();
                                categoryArrayList.add(new Category(categoryId,categoryName ,categoryImage));
                            }
                            categoryListAdapter.notifyDataSetChanged();
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void editButtonClicked(int position) {
        Category category = categoryArrayList.get(position);
        Intent intentEditCategory = new Intent(this, AddCategory.class);
        intentEditCategory.putExtra("edit_category", category);
        startActivity(intentEditCategory);
    }

    @Override
    public void deleteButtonClicked(int position) {
        showProgressDialog();

        Category category = categoryArrayList.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("categories")
                .document(category.getCategoryId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        if(task.isSuccessful()){
                            categoryArrayList.remove(position);
                            categoryListAdapter.notifyDataSetChanged();
                            Toast.makeText(CategoryList.this, category.getName()+" deleted successfully.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void itemClicked(int position) {

    }

    public void showProgressDialog() {
        this.progressDialog.show();
    }

    public void hideProgressDialog() {
        if (this.progressDialog.isShowing()) {
            this.progressDialog.hide();
        }
    }
}