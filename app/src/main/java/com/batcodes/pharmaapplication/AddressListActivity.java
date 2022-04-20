package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.batcodes.pharmaapplication.adapter.AddressListAdapter;
import com.batcodes.pharmaapplication.adapter.interface2.AddresstItemClickListener;
import com.batcodes.pharmaapplication.helper.CategoryDatabaseHandler;
import com.batcodes.pharmaapplication.model.Address;
import com.batcodes.pharmaapplication.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AddressListActivity extends AppCompatActivity implements AddresstItemClickListener, View.OnClickListener {

    RecyclerView rvAddressList;
    CategoryDatabaseHandler categoryDatabaseHandler;
    ArrayList<Address> addressArrayList;
    AddressListAdapter addressListAdapter;
    Button btnCheckout;
    Address mSelectedAddress = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        addressArrayList = new ArrayList<>();
        categoryDatabaseHandler = new CategoryDatabaseHandler(this);

        rvAddressList = findViewById(R.id.rvAddressList);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvAddressList.setLayoutManager(new LinearLayoutManager(this));
        btnCheckout.setOnClickListener(this);
    }

    @Override
    public void itemClicked(int position) {
        if(addressArrayList!=null){
            mSelectedAddress = addressArrayList.get(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        addressListAdapter = new AddressListAdapter(addressArrayList,this);
        rvAddressList.setAdapter(addressListAdapter);

        loadAddress();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_address_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_address:
                Intent intentAddressList=new Intent(AddressListActivity.this,AddressActivity.class);
                startActivity(intentAddressList);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCheckout:

                if(mSelectedAddress==null){
                    Toast.makeText(this, "Please select address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intentAddNewAddress = new Intent(this, PaymentActivity.class);
                startActivity(intentAddNewAddress);
                break;
        }
    }

    private void loadAddress(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("address")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            addressArrayList.clear();
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            String uid = firebaseAuth.getUid();

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                if(document.getData().get("uid").toString().equalsIgnoreCase(uid)){
                                    String addressName = document.getData().get("addressName").toString();
                                    String address = document.getData().get("address").toString();
                                    String mobileNumber = "";
                                    String pincode = "";

                                    if(document.getData().get("mobileNumber")!=null)
                                        mobileNumber = document.getData().get("mobileNumber").toString();
                                    if(document.getData().get("pincode")!=null)
                                        pincode = document.getData().get("pincode").toString();

                                    Address temp = new Address(addressName, address);
                                    temp.setMobileNumber(mobileNumber);
                                    temp.setPincode(pincode);

                                    addressArrayList.add(temp);
                                }
                            }
                            addressListAdapter.notifyDataSetChanged();

                        } else {
                        }

                        if(addressArrayList==null || addressArrayList.size()<=0){
                            Intent intentAddressList = new Intent(AddressListActivity.this, AddressActivity.class);
                            startActivity(intentAddressList);
                        }
                    }
                });
    }
}