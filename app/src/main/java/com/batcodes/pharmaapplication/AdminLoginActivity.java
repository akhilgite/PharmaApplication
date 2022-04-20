package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.batcodes.pharmaapplication.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class AdminLoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editEmail;
    EditText editPassword;
    Button btnLogin;

    boolean isBusy = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");

        editEmail = findViewById(R.id.editEmailAddress);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.buttonLogin);

        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonLogin:
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                loginUser(email, password);
                break;

            case R.id.buttonRegister:

                break;
        }
    }

    private void loginUser(String emailId, String password) {
        showProgressDialog();
        isBusy = true;
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(emailId, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                isBusy = false;
                hideProgressDialog();
                if(task.isSuccessful()){
                    if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                        /*Intent intentMain = new Intent(AdminLoginActivity.this,DashboardActivity.class);
                        startActivity(intentMain);
                        finish();*/
                        getUserDetails();
                    }else {
                        Toast.makeText(AdminLoginActivity.this, "Verify your email address.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(AdminLoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserDetails(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Object temp = task.getResult().getData().get("isAdmin");
                            if(temp==null || (boolean)temp==false){
                                Toast.makeText(AdminLoginActivity.this, "You don't have admin access.", Toast.LENGTH_SHORT).show();
                            }else{
                                Intent intentVendorDashboard = new Intent(AdminLoginActivity.this, VendorDashboard.class);
                                intentVendorDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intentVendorDashboard);
                            }
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