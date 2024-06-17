package com.caternation.DeliverySubactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.caternation.Authentication.LoginActivity;
import com.caternation.CustomDialogs.ProfileDialog;
import com.caternation.Modules.Generate;
import com.caternation.Objects.CartItem;
import com.caternation.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ConfirmOrderSubactivity extends AppCompatActivity {

    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference userRef, cartRef, ordersRef, userOrdersRef, orderItemsRef, userOrderItemsRef;
    private ValueEventListener cartListener;

    TextView tvActivityTitle;
    TextInputEditText etFirstName, etLastName, etMobileNumber, etAddressBuilding, etAddressBarangay, etAddressCity, etNotes;
    MaterialButton btnBack, btnProfile, btnPlaceOrder;
    CircularProgressIndicator loadingBar;
    MaterialCardView cvDeliveryInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_delivery_information);

        initiate();
        loadUserInformation();
        initiateTopActionBar();
        btnPlaceOrder.setOnClickListener(view -> {
            if (etAddressCity.getText().toString().equals("Sindangan") ||
                    etAddressCity.getText().toString().equals("sindangan") ||
                    etAddressCity.getText().toString().equals("Liloy") ||
                    etAddressCity.getText().toString().equals("liloy")){
            }
            // do nothing
            else {
                Generate.warningDialog(ConfirmOrderSubactivity.this,
                        "Unavailable Delivery Address",
                        "Our valued customer, our services are currently limited within Sindangan and Liloy areas only.\nWe are sorry for the inconvenience.");
                return;
            }

            String firstName = Objects.requireNonNull(etFirstName.getText()).toString();
            String lastName = Objects.requireNonNull(etLastName.getText()).toString();
            String mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString();
            String addressBuilding = Objects.requireNonNull(etAddressBuilding.getText()).toString();
            String addressBarangay = Objects.requireNonNull(etAddressBarangay.getText()).toString();
            String addressCity = Objects.requireNonNull(etAddressCity.getText()).toString();
            String notes = Objects.requireNonNull(etNotes.getText()).toString();

            ordersRef = caterNationDb.getReference("orders").push();
            String orderUid = ordersRef.getKey();
            ordersRef.child("uid").setValue(orderUid);
            ordersRef.child("timestamp").setValue(ServerValue.TIMESTAMP);
            ordersRef.child("userUid").setValue(currentUser.getUid());
            ordersRef.child("status").setValue("Pending");
            ordersRef.child("firstName").setValue(firstName);
            ordersRef.child("lastName").setValue(lastName);
            ordersRef.child("mobileNumber").setValue(mobileNumber);
            ordersRef.child("addressBuilding").setValue(addressBuilding);
            ordersRef.child("addressBarangay").setValue(addressBarangay);
            ordersRef.child("addressCity").setValue(addressCity);
            ordersRef.child("notes").setValue(notes);
            userOrdersRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_orders").child(orderUid);
            userOrdersRef.child("uid").setValue(orderUid);
            userOrdersRef.child("timestamp").setValue(ServerValue.TIMESTAMP);
            userOrdersRef.child("userUid").setValue(currentUser.getUid());
            userOrdersRef.child("status").setValue("Pending");
            userOrdersRef.child("firstName").setValue(firstName);
            userOrdersRef.child("lastName").setValue(lastName);
            userOrdersRef.child("mobileNumber").setValue(mobileNumber);
            userOrdersRef.child("addressBuilding").setValue(addressBuilding);
            userOrdersRef.child("addressBarangay").setValue(addressBarangay);
            userOrdersRef.child("addressCity").setValue(addressCity);
            userOrdersRef.child("notes").setValue(notes);

            cartRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_cart");
            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orderItemsRef = caterNationDb.getReference("orderItems").child(Objects.requireNonNull(orderUid));
                    userOrderItemsRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_orderItems").child(Objects.requireNonNull(orderUid));
                    double total = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                        String itemUid = cartItem.getUid();
                        String name = cartItem.getName();
                        double price = cartItem.getPrice();
                        int thumbnail = cartItem.getThumbnail();
                        int quantity = cartItem.getQuantity();

                        total += price * (double) quantity;

                        orderItemsRef.child(itemUid).child("uid").setValue(itemUid);
                        orderItemsRef.child(itemUid).child("name").setValue(name);
                        orderItemsRef.child(itemUid).child("price").setValue(price);
                        orderItemsRef.child(itemUid).child("thumbnail").setValue(thumbnail);
                        orderItemsRef.child(itemUid).child("quantity").setValue(quantity);
                        userOrderItemsRef.child(itemUid).child("uid").setValue(itemUid);
                        userOrderItemsRef.child(itemUid).child("name").setValue(name);
                        userOrderItemsRef.child(itemUid).child("price").setValue(price);
                        userOrderItemsRef.child(itemUid).child("thumbnail").setValue(thumbnail);
                        userOrderItemsRef.child(itemUid).child("quantity").setValue(quantity);
                    }

                    ordersRef.child("total").setValue(total);
                    userOrdersRef.child("total").setValue(total);
                    cartRef.removeValue();

                    MaterialAlertDialogBuilder dialogComplete = new MaterialAlertDialogBuilder(ConfirmOrderSubactivity.this);
                    dialogComplete.setTitle("Your order has been placed")
                            .setMessage("Thank you for using our services!\nPlease provide the total amount upon delivery.")
                            .setPositiveButton("Okay", (dialogInterface, i) -> startActivity(new Intent(ConfirmOrderSubactivity.this, OrdersSubactivity.class)))
                            .setOnCancelListener(dialogInterface -> startActivity(new Intent(ConfirmOrderSubactivity.this, OrdersSubactivity.class)))
                            .show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right);
    }

    private void initiate() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etAddressBuilding = findViewById(R.id.etAddressBuilding);
        etAddressBarangay = findViewById(R.id.etAddressBarangay);
        etAddressCity = findViewById(R.id.etAddressCity);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        loadingBar = findViewById(R.id.loadingBar);
        cvDeliveryInformation = findViewById(R.id.cvDeliveryInformation);
        etNotes = findViewById(R.id.etNotes);
    }

    private void initiateTopActionBar() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnBack = findViewById(R.id.btnBack);
        btnProfile = findViewById(R.id.btnProfile);

        tvActivityTitle.setText("Delivery Information");
        btnBack.setOnClickListener(view -> {
            finish();
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference firstNameRef = caterNationDb.getReference("user_"+currentUser.getUid()).child("firstName");
            firstNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String firstName = snapshot.getValue().toString();
                    btnProfile.setText(firstName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            btnProfile.setText("Sign in");
        }
        btnProfile.setOnClickListener(view -> {
            if (currentUser == null) {
                startActivity(new Intent(ConfirmOrderSubactivity.this, LoginActivity.class));
                return;
            }
            ProfileDialog profileDialog = new ProfileDialog();
            profileDialog.show(getSupportFragmentManager(), "Profile Dialog");
        });
    }

    private void loadUserInformation() {
        userRef = caterNationDb.getReference("user_"+currentUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = Objects.requireNonNull(snapshot.child("firstName").getValue()).toString();
                String lastName = Objects.requireNonNull(snapshot.child("lastName").getValue()).toString();
                String mobileNumber = Objects.requireNonNull(snapshot.child("mobileNumber").getValue()).toString();
                String addressBuilding = Objects.requireNonNull(snapshot.child("addressBuilding").getValue()).toString();
                String addressBarangay = Objects.requireNonNull(snapshot.child("addressBarangay").getValue()).toString();
                String addressCity = Objects.requireNonNull(snapshot.child("addressCity").getValue()).toString();

                etFirstName.setText(firstName);
                etLastName.setText(lastName);
                etMobileNumber.setText(mobileNumber);
                etAddressBuilding.setText(addressBuilding);
                etAddressBarangay.setText(addressBarangay);
                etAddressCity.setText(addressCity);

                loadingBar.hide();
                cvDeliveryInformation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}