package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.batcodes.pharmaapplication.adapter.CartProductListAdapter;
import com.batcodes.pharmaapplication.adapter.CategorySpinnerAdapter;
import com.batcodes.pharmaapplication.helper.Cart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrescriptionActivity extends AppCompatActivity implements View.OnClickListener {
    private int REQUEST_IMAGE_CAPTURE = 201;
    private int PICK_IMAGE_REQUEST = 202;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    CategorySpinnerAdapter categorySpinnerAdapter;
    private ProgressDialog progressDialog;

    ImageView ivPreview;
    Button btnSelect;

    private boolean viewPrescription = false;
    private String prescriptionImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        if(getIntent().getExtras()!=null && getIntent().getExtras().get("viewPrescription")!=null){
            viewPrescription = (boolean) getIntent().getExtras().get("viewPrescription");
        }

        if(getIntent().getExtras()!=null && getIntent().getExtras().get("prescriptionImageUrl")!=null){
            prescriptionImageUrl = (String) getIntent().getExtras().get("prescriptionImageUrl");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");

        mStorageRef = FirebaseStorage.getInstance().getReference("prescription");
        ivPreview = findViewById(R.id.ivPreview);
        btnSelect = findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(this);

        if(!viewPrescription && Cart.getInstance().getPrescriptionUrl()!=null && Cart.getInstance().getPrescriptionUrl().length()>0){
            loadImage(Cart.getInstance().getPrescriptionUrl());
        }

        if(viewPrescription && prescriptionImageUrl.length()>0){
            btnSelect.setVisibility(View.GONE);
            loadImage(prescriptionImageUrl);
        }
    }

    private void showImageSelectionDialogBox(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(PrescriptionActivity.this);
        builderSingle.setTitle("Select a Photo");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PrescriptionActivity.this, android.R.layout.simple_dropdown_item_1line);
        arrayAdapter.add("    Take Photo...");
        arrayAdapter.add("    Choose from Library...");

        builderSingle.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                if (which==0){
                    dispatchTakePictureIntent();
                }else if (which==1){
                    openFileChooser();
                }
            }
        });
        builderSingle.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(PrescriptionActivity.this,
                        "com.example.android.fileprovider",
                        photoFile);
                mImageUri=photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK && data!=null && data.getData()!=null){
            mImageUri = data.getData();
            //civProfilePic.setImageURI(mImageUri);
            String imageExtn = getFileExtension(mImageUri);
            fileUpload(mImageUri,imageExtn);
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            String imageExtn = getFileExtension(mImageUri);
            fileUpload(mImageUri,imageExtn);
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

    public void showProgressDialog() {
        this.progressDialog.show();
    }

    public void hideProgressDialog() {
        if (this.progressDialog.isShowing()) {
            this.progressDialog.hide();
        }
    }

    private void fileUpload(Uri imageUri, final String imageExtn){
        showProgressDialog();
        if (imageUri!=null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    +"."+imageExtn);

            mUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        hideProgressDialog();
                        String uploadedFilePath = taskSnapshot.getMetadata().getReference().toString();
                        Cart.getInstance().setPrescriptionUrl(uploadedFilePath);
                        loadImage(uploadedFilePath);
                    })
                    .addOnFailureListener(e -> hideProgressDialog());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSelect) {
            showImageSelectionDialogBox();
        }
    }

    private void loadImage(String url) {
        Log.d("TAG", "loadImage: "+url);
        if (url != null && url.length() > 0) {
            FirebaseStorage.getInstance().getReferenceFromUrl(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(ivPreview);
                }
            }).addOnFailureListener(exception -> exception.printStackTrace());
        }
    }
}