package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.batcodes.pharmaapplication.helper.CategoryDatabaseHandler;
import com.batcodes.pharmaapplication.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddCategory extends AppCompatActivity implements View.OnClickListener {

    EditText editTextCategoryName;
    Button buttonAddCategory;
    Button buttonSelectImage;
    CategoryDatabaseHandler categoryDatabaseHandler;
    ImageView imageViewIcon;
    String imagePath;
    Category mCategory;

    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private Uri mImageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle("Loading");
        this.progressDialog.setMessage("Please wait...");

        mStorageRef = FirebaseStorage.getInstance().getReference("categories");
        categoryDatabaseHandler = new CategoryDatabaseHandler(getApplicationContext());

        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        buttonAddCategory = findViewById(R.id.buttonAddCategory);
        buttonSelectImage = findViewById(R.id.buttonSelectIcon);
        imageViewIcon = findViewById(R.id.imageViewIcon);

        buttonAddCategory.setOnClickListener(this);
        buttonSelectImage.setOnClickListener(this);

        if(getIntent().getExtras()!=null)
            mCategory = (Category) getIntent().getExtras().get("edit_category");

        if(mCategory!=null)
            loadCategory();
    }

    private void loadCategory() {
        editTextCategoryName.setText(mCategory.getName());
        loadImage(mCategory.getIcon());
    }

    private void loadImage(String url) {
        showProgressDialog();
        if (url != null && url.length() > 0) {
            FirebaseStorage.getInstance().getReferenceFromUrl(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().into(imageViewIcon);
                    hideProgressDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception exception) {
                    hideProgressDialog();
                    exception.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.buttonSelectIcon:
                imageChooser();
                break;
            case R.id.buttonAddCategory:
                String categoryName = editTextCategoryName.getText().toString();
                boolean result = validate(categoryName);
                if(result){

                    if(mImageUri==null && mCategory!=null){
                        updateProduct();
                    }

                    String fileExtension = getFileExtension(mImageUri);
                    fileUpload(mImageUri, fileExtension);
                    /*Category category = new Category(categoryName, imagePath);
                    categoryDatabaseHandler.addCategory(category);
                    finish();*/
                }
                break;
        }
    }

    private void updateProduct() {
        String categoryName = editTextCategoryName.getText().toString();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> category = new HashMap<>();
        category.put("uid", uid);
        category.put("categoryName", categoryName);
        category.put("categoryImage", mCategory.getIcon());

        // Add a new document with a generated ID
        db.collection("categories")
                .document(mCategory.getCategoryId())
                .set(category)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        if(task.isSuccessful()){
                            Toast.makeText(AddCategory.this, "Category details updated.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 101);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
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
                        try
                        {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver() , mImageUri);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        Log.d(getClass().getName(),"Image Saved Path :: "+directory.getAbsolutePath() );
                        Log.d(getClass().getName(),"Image Saved Path 2 :: "+mypath);
                        imagePath = mypath.toString();
                        imageViewIcon.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void loadImageFromStorage(String path){
        try {
            File f=new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageViewIcon.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private boolean validate(String categoryName) {
        if(categoryName==null || categoryName.length()==0){
            Toast.makeText(getApplicationContext(),"Please enter category name", Toast.LENGTH_LONG);
            return false;
        }
        return true;
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
                            if(mCategory!=null){
                                mCategory.setIcon(uploadedFilePath);
                                updateProduct();
                            }else{
                                uploadCategory(uploadedFilePath);
                            }
                        }
                    })
                    .addOnFailureListener(e -> hideProgressDialog());
        }
    }

    private void uploadCategory(String uploadedFilePath) {
        String categoryName = editTextCategoryName.getText().toString();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> product = new HashMap<>();
        product.put("uid", uid);
        product.put("categoryName", categoryName);
        product.put("categoryImage", uploadedFilePath);

        // Add a new document with a generated ID
        db.collection("categories")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddCategory.this, "Category added successfully.", Toast.LENGTH_SHORT).show();
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

    public void showProgressDialog() {
        this.progressDialog.show();
    }

    public void hideProgressDialog() {
        if (this.progressDialog.isShowing()) {
            this.progressDialog.hide();
        }
    }
}