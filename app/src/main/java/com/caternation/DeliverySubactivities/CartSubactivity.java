package com.caternation.DeliverySubactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.caternation.Adapters.CartAdapter;
import com.caternation.Authentication.LoginActivity;
import com.caternation.CustomDialogs.ProfileDialog;
import com.caternation.Modules.DoubleFormatter;
import com.caternation.Objects.CartItem;
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

public class CartSubactivity extends AppCompatActivity implements CartAdapter.OnCartListener {

    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference cartRef, cartItemRef;
    private ValueEventListener cartListener;

    private RecyclerView rvCart;
    MaterialButton btnBack, btnProfile, btnPlaceOrder;
    TextView tvActivityTitle, tvEmptyCart, tvTotal;
    CircularProgressIndicator loadingBar;

    ArrayList<CartItem> arrCartItem;
    CartAdapter cartAdapter;
    CartAdapter.OnCartListener onCartListener = this;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_cart);

        initiate();
        initiateTopActionBar();
        loadRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cartRef.removeEventListener(cartListener);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right);
    }

    private void initiate() {
        rvCart = findViewById(R.id.rvCart);
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
            startActivity(new Intent(CartSubactivity.this, ConfirmOrderSubactivity.class));
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
                startActivity(new Intent(CartSubactivity.this, LoginActivity.class));
                return;
            }
            ProfileDialog profileDialog = new ProfileDialog();
            profileDialog.show(getSupportFragmentManager(), "Profile Dialog");
        });
    }

    private void loadRecyclerView() {
        arrCartItem = new ArrayList<>();
        rvCart = findViewById(R.id.rvCart);
        rvCart.setHasFixedSize(true);
        rvCart.setLayoutManager(new LinearLayoutManager(CartSubactivity.this));

        cartRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_cart");
        cartListener = cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrCartItem.clear();
                double total = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                    total += cartItem.getPrice() * (double) cartItem.getQuantity();
                    arrCartItem.add(cartItem);
                }
                cartAdapter.notifyDataSetChanged();

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

        cartAdapter = new CartAdapter(context, arrCartItem, onCartListener);
        rvCart.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCartClick(int position) {

    }
}