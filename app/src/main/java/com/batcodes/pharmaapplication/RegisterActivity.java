package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.batcodes.pharmaapplication.helper.CategoryDatabaseHandler;
import com.batcodes.pharmaapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editName;
    EditText editEmailAddress;
    EditText editPhone;
    EditText editPassword;
    EditText editConfirmPassword;
    Button buttonRegister;
    private ProgressDialog progressDialog;

    CategoryDatabaseHandler categoryDatabaseHandler = new CategoryDatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle("Loading");
        this.progressDialog.setMessage("Please wait...");

        editName = findViewById(R.id.editName);
        editEmailAddress = findViewById(R.id.editEmailAddress);
        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editPasswordConfirm);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.buttonRegister:

                String name = editName.getText().toString();
                String emailAddress = editEmailAddress.getText().toString();
                String phone = editPhone.getText().toString();
                String password = editPassword.getText().toString();

                //categoryDatabaseHandler.addUser(new User(name, emailAddress, phone, password));
                registerUser(emailAddress, password, name, phone);
                break;
        }
    }

    public void registerUser(String email, String password, String name, String phone) {
        showProgressDialog();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressDialog();
                if(task.isSuccessful()){
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                String uid = firebaseAuth.getUid();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                // Create a new user with a first and last name
                                Map<String, Object> user = new HashMap<>();
                                user.put("uid", uid);
                                user.put("email", email);
                                user.put("name", name);
                                user.put("phone", phone);

                                // Add a new document with a generated ID
                                db.collection("users")
                                        .document(uid)
                                        .set(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    finish();
                                                    Toast.makeText(RegisterActivity.this, "Verification mail has been sent to your email address. Request you to verify.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }   else {
                                Toast.makeText(RegisterActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }   else {
                    Toast.makeText(RegisterActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
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