package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.batcodes.pharmaapplication.adapter.CategorySpinnerAdapter;
import com.batcodes.pharmaapplication.adapter.ProductListAdapter;
import com.batcodes.pharmaapplication.helper.CategoryDatabaseHandler;
import com.batcodes.pharmaapplication.helper.ProductDatabaseHandler;
import com.batcodes.pharmaapplication.model.Category;
import com.batcodes.pharmaapplication.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddProduct extends AppCompatActivity {
    EditText editTextName, editTextQty, editTextDetails, editTextPrice;
    Spinner spinnerCategory;
    Button buttonAdd;
    CategoryDatabaseHandler categoryDatabaseHandler;
    ProductDatabaseHandler productDatabaseHandler;
    String selectedCategoryId;
    String imagePath;
    ImageView imageViewIcon;
    Button buttonSelectImage;
    ArrayList<Category> categoryArrayList;

    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private Uri mImageUri;
    CategorySpinnerAdapter categorySpinnerAdapter;
    private ProgressDialog progressDialog;

    Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        categoryArrayList = new ArrayList<>();
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle("Loading");
        this.progressDialog.setMessage("Please wait...");

        mStorageRef = FirebaseStorage.getInstance().getReference("products");
        categoryDatabaseHandler = new CategoryDatabaseHandler(getApplicationContext());
        productDatabaseHandler = new ProductDatabaseHandler(getApplicationContext());

        editTextName = findViewById(R.id.editTextName);
        editTextQty = findViewById(R.id.editTextQty);
        editTextDetails = findViewById(R.id.editTextDetails);
        editTextPrice = findViewById(R.id.editTextPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonAdd = findViewById(R.id.buttonAdd);
        imageViewIcon = findViewById(R.id.imageViewImage);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);

        categorySpinnerAdapter = new CategorySpinnerAdapter(this, categoryArrayList);
        spinnerCategory.setAdapter(categorySpinnerAdapter);
        loadCategories();

        spinnerCategory.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id){
                        Category clickedItem = (Category)
                                parent.getItemAtPosition(position);
                        String name = clickedItem.getName();
                        Toast.makeText(AddProduct.this, name + " selected", Toast.LENGTH_SHORT).show();
                        selectedCategoryId = clickedItem.getCategoryId();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent){
                    }
                });

        buttonAdd.setOnClickListener(view -> {
            if(mProduct!=null){
                mProduct.setName(editTextName.getText().toString());
                mProduct.setDetails(editTextDetails.getText().toString());
                mProduct.setQty(editTextQty.getText().toString());
                mProduct.setPrice(editTextPrice.getText().toString());

                if(mImageUri==null){
                    updateProduct();
                    return;
                }
            }

            if(mImageUri!=null){
                String fileExtension = getFileExtension(mImageUri);
                fileUpload(mImageUri, fileExtension);
            }
        });

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        if(getIntent().getExtras()!=null)
            mProduct = (Product) getIntent().getExtras().get("edit_product");
    }

    private void loadProduct() {
        if(categoryArrayList==null || categoryArrayList.size()<=0)
            return;

        editTextName.setText(mProduct.getName());
        editTextQty.setText(mProduct.getQty());
        editTextDetails.setText(mProduct.getDetails());
        editTextPrice.setText(mProduct.getPrice());

        int i = 0;
        for (; i < categoryArrayList.size(); i++) {
            Category item = categoryArrayList.get(i);
            if(item.getCategoryId().equalsIgnoreCase(mProduct.getCategoryId())){
                break;
            }
        }
        spinnerCategory.setSelection(i);
        loadImage(mProduct.getImage());
    }

    private void loadImage(String url) {
        if (url != null && url.length() > 0) {
            FirebaseStorage.getInstance().getReferenceFromUrl(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(imageViewIcon);
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception exception) {
                    exception.printStackTrace();
                }
            });
        }
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 101);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                mImageUri = data.getData();
                if (null != mImageUri) {
                    Log.d(getClass().getName(),"Image URI :: "+mImageUri.toString() );
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                    File mypath=new File(directory,"ic_"+System.currentTimeMillis()+".jpg");

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mypath);
                        Bitmap bitmap = null;
                        try{
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver() , mImageUri);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        assert bitmap != null;
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        Log.d(getClass().getName(),"Image Saved Path :: "+mypath);
                        imagePath = mypath.toString();
                        imageViewIcon.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            assert fos != null;
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    public String getFileExtension(Uri uri) {
        String extension;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(this.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }
        return extension;
    }

    private void fileUpload(Uri imageUri, final String imageExtn){
        showProgressDialog();
        if (imageUri!=null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    +"."+imageExtn);

            mUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String uploadedFilePath = taskSnapshot.getMetadata().getReference().toString();
                            if(mProduct!=null){
                                mProduct.setImage(uploadedFilePath);
                                updateProduct();
                            }else {
                                uploadProduct(uploadedFilePath);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                        }
                    });
        }
    }

    private void updateProduct(){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> product = new HashMap<>();
        product.put("uid", uid);
        product.put("productName", mProduct.getName());
        product.put("productQty", mProduct.getQty());
        product.put("productDetails", mProduct.getDetails());
        product.put("productPrice", mProduct.getPrice());
        product.put("productImage", mProduct.getImage());
        product.put("categoryId", selectedCategoryId);

        db.collection("products")
                .document(mProduct.getProductId())
                .set(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        if(task.isSuccessful()){
                            Toast.makeText(AddProduct.this, "Product details updated.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
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
                                String categoryId = document.getId();
                                String categoryName = document.getData().get("categoryName").toString();
                                String categoryImage = document.getData().get("categoryImage").toString();
                                categoryArrayList.add(new Category(categoryId,categoryName ,categoryImage));
                            }
                            categorySpinnerAdapter.notifyDataSetChanged();

                            if(mProduct!=null){
                                loadProduct();
                            }
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void uploadProduct(String uploadedFilePath) {
        String productName = editTextName.getText().toString();
        String productQty = editTextQty.getText().toString();
        String productDetails = editTextDetails.getText().toString();
        String productPrice = editTextPrice.getText().toString();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> product = new HashMap<>();
        product.put("uid", uid);
        product.put("productName", productName);
        product.put("productQty", productQty);
        product.put("productDetails", productDetails);
        product.put("productPrice", productPrice);
        product.put("productImage", uploadedFilePath);
        product.put("categoryId", selectedCategoryId);

        // Add a new document with a generated ID
        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddProduct.this, "Product added successfully.", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                        hideProgressDialog();
                    }
                });
    }

    private boolean validate(String productName, String productQty, int selectedCategoryId, String productDetails, String productPrice, String imagePath) {
        boolean result = true;
        return result;
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