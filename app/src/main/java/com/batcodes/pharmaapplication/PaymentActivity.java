package com.batcodes.pharmaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.batcodes.pharmaapplication.helper.Cart;
import com.batcodes.pharmaapplication.helper.CategoryDatabaseHandler;
import com.batcodes.pharmaapplication.helper.ProductDatabaseHandler;
import com.batcodes.pharmaapplication.model.OrderedProducts;
import com.batcodes.pharmaapplication.model.PastOrder;
import com.batcodes.pharmaapplication.model.Product;
import com.batcodes.pharmaapplication.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {

    RadioButton radioCreditCard;
    RadioButton radioGooglePay;
    RadioButton radioPhonePay;
    RadioButton radioGiftCard;

    CategoryDatabaseHandler categoryDatabaseHandler;
    String modeOfPayment;
    ArrayList<PastOrder> pastOrderArrayList;
    PastOrder pastOrder;
    TextView tvCartValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        pastOrderArrayList = new ArrayList<>();
        categoryDatabaseHandler = new CategoryDatabaseHandler(this);

        radioCreditCard = findViewById(R.id.radioCreditCard);
        radioGooglePay = findViewById(R.id.radioGooglePay);
        radioPhonePay = findViewById(R.id.radioPhonePay);
        radioGiftCard = findViewById(R.id.radioGiftCard);
        tvCartValue = findViewById(R.id.tvCartValue);

        if(Cart.getInstance()!=null){
            String strTotalCartCValue = "Total Cart Value: "+Cart.getInstance().getCartValue();
            tvCartValue.setText(strTotalCartCValue);
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radioCreditCard:
                    if (checked){
                        modeOfPayment = "Credit Card";
                        radioGooglePay.setChecked(false);
                        radioPhonePay.setChecked(false);
                        radioGiftCard.setChecked(false);
                    }
                break;
            case R.id.radioGooglePay:
                    if (checked) {
                        modeOfPayment = "Google Pay";
                        radioCreditCard.setChecked(false);
                        radioPhonePay.setChecked(false);
                        radioGiftCard.setChecked(false);
                    }
                    break;
            case R.id.radioPhonePay:
                    if (checked) {
                        modeOfPayment = "Phone Pay";
                        radioCreditCard.setChecked(false);
                        radioGooglePay.setChecked(false);
                        radioGiftCard.setChecked(false);
                    }
                    break;
            case R.id.radioGiftCard:
                    if (checked) {
                        modeOfPayment = "Gift Card";
                        radioCreditCard.setChecked(false);
                        radioGooglePay.setChecked(false);
                        radioPhonePay.setChecked(false);
                    }
                    break;
        }
    }

    public void onButtonPayClick(View view) {
        placeOrder();
    }

    private void placeOrder() {
        updateProduct();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREF_NAME,MODE_PRIVATE);
        String personName = sharedPreferences.getString("name","");

        String datePattern = Constants.DATE_PATTERN;
        SimpleDateFormat sdfDate = new SimpleDateFormat(datePattern);
        String date = sdfDate.format(new Date());

        String timePattern = Constants.TIME_PATTERN;
        SimpleDateFormat sdfTime = new SimpleDateFormat(timePattern);
        String time = sdfTime.format(new Date());

        String tempModeOfPayment = modeOfPayment;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        //public OrderedProducts(int categoryId, String name, String details, String price, String requestedQty, String image)

        ArrayList<OrderedProducts> orderedProducts = new ArrayList<>();
        ArrayList<Product> productArrayList = Cart.getInstance().getProducts();

        for (int i = 0; i < productArrayList.size(); i++) {
            Product product = productArrayList.get(i);
            orderedProducts.add(new OrderedProducts(uid,product.getCategoryId(),
                    product.getName(),
                    product.getDetails(),
                    product.getPrice(),
                    product.getRequestedQty(),
                    product.getImage(),
                    date,
                    time,
                    Cart.getInstance().getPrescriptionUrl()));
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        pastOrder = new PastOrder();
        pastOrder.setOrderDate(date+" "+time);
        pastOrder.setPaymentMode(tempModeOfPayment);
        pastOrder.setOrderedProductsArrayList(orderedProducts);
        pastOrder.setStatus(Constants.ORDER_STATUS_ORDERED);
        pastOrder.setId(System.currentTimeMillis());
        pastOrder.setPersonName(personName);
        pastOrder.setUid(uid);
        pastOrder.setPrescriptionImageUrl(Cart.getInstance().getPrescriptionUrl());
        pastOrderArrayList.add(pastOrder);

        db.collection("orders")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "onComplete: "+task.getResult());
                            pastOrderArrayList = (ArrayList<PastOrder>) task.getResult().get("pastOrdersList");
                            Log.d("TAG", "onComplete: "+pastOrderArrayList);

                            if(pastOrderArrayList==null)
                                pastOrderArrayList = new ArrayList<>();

                            pastOrderArrayList.add(pastOrder);

                            Map<String, Object> productMap = new HashMap<>();
                            productMap.put("pastOrdersList", pastOrderArrayList);
                            db.collection("orders")
                                    .document(uid)
                                    .set(productMap);
                            Intent intentOrderConfirmation = new Intent(PaymentActivity.this, OrderConfirmation.class);
                            startActivity(intentOrderConfirmation);
                            //categoryListAdapter.notifyDataSetChanged();
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        Cart.getInstance().emptyCart();
        Cart.getInstance().setCartValue(0);
        Cart.getInstance().setPrescriptionUrl("");
    }

    private void updateProduct(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<Product> productArrayList = Cart.getInstance().getProducts();
        for (int i = 0; i < productArrayList.size(); i++) {
            Product product = productArrayList.get(i);

            int remainingQty = Integer.parseInt(product.getQty()) - Integer.parseInt(product.getRequestedQty());
            product.setQty(String.valueOf(remainingQty));;

            Map<String, Object> productMap = new HashMap<>();
            productMap.put("uid", uid);
            productMap.put("productName", product.getName());
            productMap.put("productQty", product.getQty());
            productMap.put("productDetails", product.getDetails());
            productMap.put("productPrice", product.getPrice());
            productMap.put("productImage", product.getImage());
            productMap.put("categoryId", product.getCategoryId());
            db.collection("products")
                    .document(product.getProductId())
                    .set(productMap);
        }
    }
}