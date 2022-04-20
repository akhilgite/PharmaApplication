package com.batcodes.pharmaapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.batcodes.pharmaapplication.AllProductsListActivity;
import com.batcodes.pharmaapplication.SearchProductActivity;
import com.batcodes.pharmaapplication.helper.Cart;
import com.batcodes.pharmaapplication.R;
import com.batcodes.pharmaapplication.adapter.interface2.CategoryItemClickListener;
import com.batcodes.pharmaapplication.adapter.CategoryListAdapter;
import com.batcodes.pharmaapplication.helper.CategoryDatabaseHandler;
import com.batcodes.pharmaapplication.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment implements CategoryItemClickListener, View.OnClickListener {

    ArrayList<Category> categoryArrayList;
    RecyclerView recyclerViewCategory;
    CategoryDatabaseHandler categoryDatabaseHandler;
    CategoryListAdapter categoryListAdapter;
    LinearLayout linearLayoutSearchContainer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        categoryArrayList = new ArrayList<>();

        recyclerViewCategory = root.findViewById(R.id.recyclerViewCategory);
        linearLayoutSearchContainer = root.findViewById(R.id.ll_search_container);
        recyclerViewCategory.setLayoutManager(new GridLayoutManager(getContext(),2));

        categoryDatabaseHandler = new CategoryDatabaseHandler(getContext());

        categoryListAdapter = new CategoryListAdapter(categoryArrayList, this);
        recyclerViewCategory.setAdapter(categoryListAdapter);
        linearLayoutSearchContainer.setOnClickListener(this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
    }

    @Override
    public void itemClicked(int position) {
        Intent intentAddCategory = new Intent(getContext(), AllProductsListActivity.class);
        intentAddCategory.putExtra("categoryId",categoryArrayList.get(position).getCategoryId());
        startActivity(intentAddCategory);
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
                            categoryListAdapter .notifyDataSetChanged();
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void editButtonClicked(int position) {

    }

    @Override
    public void deleteButtonClicked(int position) {

    }

    @Override
    public void onClick(View view) {
        Intent intentSearchProduct = new Intent(getActivity(), SearchProductActivity.class);
        startActivity(intentSearchProduct);
    }
}