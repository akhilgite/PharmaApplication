package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.batcodes.pharmaapplication.helper.Cart;
import com.batcodes.pharmaapplication.helper.CategoryDatabaseHandler;
import com.batcodes.pharmaapplication.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editEmailAddress;
    private EditText editPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private ProgressDialog progressDialog;
    private TextView tvForgotPassword;

    boolean isBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");

        editEmailAddress = findViewById(R.id.editEmailAddress);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.buttonLogin:
                if(isBusy)
                    return;
                String emailId = editEmailAddress.getText().toString();
                String password = editPassword.getText().toString();
                loginUser(emailId, password);
                break;
            case R.id.buttonRegister:
                Intent intentRegister=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intentRegister);
                break;
            case R.id.tvForgotPassword:
                showDialogbox();
                break;
        }
    }

    private void showDialogbox() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Forgot Password?");
        alertDialog.setMessage("Please provide your email associated with E-Medicine so that we can send you reset password link.");
        alertDialog.setCancelable(false);
        android.view.View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_forgot_password, null);
        final EditText input = view.findViewById(R.id.edit_email);
        alertDialog.setView(view);
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                if (input.getText().toString().length() == 0) {
                    Toast.makeText(LoginActivity.this, "Enter email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();
                FirebaseAuth.getInstance().sendPasswordResetEmail(input.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                        progressDialog.cancel();
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void loginUser(String emailId, String password) {
        showProgressDialog();
        isBusy = true;
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(emailId, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                isBusy = false;
                if(task.isSuccessful()){
                    if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                        getUserDetails();
                    }else {
                        hideProgressDialog();
                        Toast.makeText(LoginActivity.this, "Verify your email address.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    hideProgressDialog();
                    Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                        hideProgressDialog();
                        if(task.isSuccessful() && task.getResult().getData()!=null){
                            String name = (String) task.getResult().getData().get("name");
                            String email = (String) task.getResult().getData().get("email");

                            SharedPreferences.Editor editor = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString("name",name);
                            editor.putString("email",email);
                            editor.apply();
                        }

                        Intent intentMain = new Intent(LoginActivity.this,DashboardActivity.class);
                        startActivity(intentMain);
                        finish();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(Constants.IS_ADMIN)
            getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_admin_login:
                /*Intent intentAddProduct=new Intent(LoginActivity.this,VendorDashboard.class);
                startActivity(intentAddProduct);*/



                Intent intentAddProduct=new Intent(LoginActivity.this,AdminLoginActivity.class);
                startActivity(intentAddProduct);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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