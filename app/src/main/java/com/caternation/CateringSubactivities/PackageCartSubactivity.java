package com.caternation.CateringSubactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.caternation.Adapters.PackageCartAdapter;
import com.caternation.Authentication.LoginActivity;
import com.caternation.CustomDialogs.ProfileDialog;
import com.caternation.DeliverySubactivities.ConfirmOrderSubactivity;
import com.caternation.Modules.DoubleFormatter;
import com.caternation.Objects.PackageCartItem;
import com.caternation.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PackageCartSubactivity extends AppCompatActivity implements PackageCartAdapter.OnPackageCartListener {

    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference packageCartRef, cartItemRef;
    private ValueEventListener packageCartListener;

    private RecyclerView rvPackageCart;
    MaterialButton btnBack, btnProfile, btnPlaceOrder;
    TextView tvActivityTitle, tvEmptyCart, tvTotal;
    CircularProgressIndicator loadingBar;

    ArrayList<PackageCartItem> arrPackageCartItem;
    PackageCartAdapter packageCartAdapter;
    PackageCartAdapter.OnPackageCartListener onPackageCartListener = this;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        setContentView(R.layout.subactivity_package_cart);
        initiate();
        initiateTopActionBar();
        loadRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        packageCartRef.removeEventListener(packageCartListener);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initiate() {
        rvPackageCart = findViewById(R.id.rvPackageCart);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        tvTotal = findViewById(R.id.tvTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        loadingBar = findViewById(R.id.progress_circular);
        loadingBar.show();
        context = this;
    }

    private void initiateTopActionBar() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnBack = findViewById(R.id.btnBack);
        btnProfile = findViewById(R.id.btnProfile);

        tvActivityTitle.setText("My Cart");
        btnBack.setOnClickListener(view -> {
            finish();
        });
        btnPlaceOrder.setOnClickListener(view -> {
            startActivity(new Intent(PackageCartSubactivity.this, ConfirmBookingSubactivity.class));
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
                startActivity(new Intent(PackageCartSubactivity.this, LoginActivity.class));
                return;
            }
            ProfileDialog profileDialog = new ProfileDialog();
            profileDialog.show(getSupportFragmentManager(), "Profile Dialog");
        });
    }

    private void loadRecyclerView() {
        arrPackageCartItem = new ArrayList<>();
        rvPackageCart = findViewById(R.id.rvPackageCart);
        rvPackageCart.setHasFixedSize(true);
        rvPackageCart.setLayoutManager(new LinearLayoutManager(PackageCartSubactivity.this));

        packageCartRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_bookingCart");
        packageCartListener = packageCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrPackageCartItem.clear();
                double total = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PackageCartItem packageCartItem = dataSnapshot.getValue(PackageCartItem.class);
                    if (packageCartItem.getSize() == 20){
                        total += packageCartItem.getPrice();
                    }
                    else if (packageCartItem.getSize() == 50){
                        total += packageCartItem.getPrice()*2;
                    }
                    else if (packageCartItem.getSize() == 100){
                        total += packageCartItem.getPrice()*4;
                    }
                    arrPackageCartItem.add(packageCartItem);
                }
                packageCartAdapter.notifyDataSetChanged();

                if (!snapshot.exists()) {
                    tvEmptyCart.setVisibility(View.VISIBLE);
                    btnPlaceOrder.setEnabled(false);
                }
                else {
                    tvEmptyCart.setVisibility(View.GONE);
                    btnPlaceOrder.setEnabled(true);
                }
                loadingBar.hide();
                tvTotal.setText(DoubleFormatter.currencyFormat(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        packageCartAdapter = new PackageCartAdapter(context, arrPackageCartItem, onPackageCartListener);
        rvPackageCart.setAdapter(packageCartAdapter);
        packageCartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPackageCartClick(int position) {

    }
}