package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.batcodes.pharmaapplication.helper.CategoryDatabaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.Address;

import java.util.HashMap;
import java.util.Map;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextAddressName;
    EditText editTextAddress;
    EditText editTextNumber;
    EditText editTextPincode;

    Button buttonSave;

    CategoryDatabaseHandler categoryDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        categoryDatabaseHandler = new CategoryDatabaseHandler(this);

        editTextAddressName = findViewById(R.id.editAddressName);
        editTextAddress = findViewById(R.id.editAddress);
        editTextNumber = findViewById(R.id.editNumber);
        editTextPincode = findViewById(R.id.editPincode);

        buttonSave=findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                String addressName = editTextAddressName.getText().toString();
                String address = editTextAddress.getText().toString();
                String mobileNumber = editTextNumber.getText().toString();
                String pincode = editTextPincode.getText().toString();

                if(addressName.length()==0){
                    Toast.makeText(this, "Enter address name.", Toast.LENGTH_SHORT).show();
                    return;
                }else if(address.length()==0){
                    Toast.makeText(this, "Enter address.", Toast.LENGTH_SHORT).show();
                    return;
                }else if(mobileNumber.length()==0){
                    Toast.makeText(this, "Enter mobile number.", Toast.LENGTH_SHORT).show();
                    return;
                }else if(pincode.length()==0){
                    Toast.makeText(this, "Enter pincode.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //categoryDatabaseHandler.addAddress(new Address(addressName, address));
                addAddress(addressName, address, mobileNumber, pincode);
                break;
        }
    }

    private void addAddress(String addressName, String address, String mobileNumber, String pincode) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> product = new HashMap<>();
        product.put("uid", uid);
        product.put("addressName", addressName);
        product.put("address", address);
        product.put("mobileNumber", mobileNumber);
        product.put("pincode", pincode);

        // Add a new document with a generated ID
        db.collection("address")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddressActivity.this, "Address saved successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }
}