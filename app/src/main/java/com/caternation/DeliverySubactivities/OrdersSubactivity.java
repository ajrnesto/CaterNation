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
import android.widget.Toast;

import com.caternation.Adapters.CartAdapter;
import com.caternation.Adapters.FoodAdapter;
import com.caternation.Adapters.OrderAdapter;
import com.caternation.AdminActivity;
import com.caternation.Authentication.LoginActivity;
import com.caternation.CustomDialogs.ProfileDialog;
import com.caternation.DeliveryActivity;
import com.caternation.Modules.DoubleFormatter;
import com.caternation.Objects.CartItem;
import com.caternation.Objects.Food;
import com.caternation.Objects.Order;
import com.caternation.Objects.OrderItem;
import com.caternation.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class OrdersSubactivity extends AppCompatActivity implements OrderAdapter.OnOrderListener {

    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference myOrdersRef;
    private ValueEventListener myOrdersListener;

    TextView tvActivityTitle, tvEmptyOrders;
    MaterialButton btnBack, btnProfile;
    RecyclerView rvOrders;
    CircularProgressIndicator loadingBar;

    ArrayList<Order> arrOrders;
    OrderAdapter orderAdapter;
    OrderAdapter.OnOrderListener onOrderListener = this;

    Context context;

    @Override
    protected void onResume() {
        super.onResume();
        loadRecyclerView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_orders);

        initiate();
        initiateTopActionBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myOrdersRef.removeEventListener(myOrdersListener);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OrdersSubactivity.this, DeliveryActivity.class));
        finish();
    }

    private void initiate() {
        rvOrders = findViewById(R.id.rvOrders);
        loadingBar = findViewById(R.id.loadingBar);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);

        context = this;
    }

    private void initiateTopActionBar() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnBack = findViewById(R.id.btnBack);
        btnProfile = findViewById(R.id.btnProfile);

        tvActivityTitle.setText("My Orders");
        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(OrdersSubactivity.this, DeliveryActivity.class));
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
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
        else {
            btnProfile.setText("Sign in");
        }
        btnProfile.setOnClickListener(view -> {
            if (currentUser == null) {
                startActivity(new Intent(OrdersSubactivity.this, LoginActivity.class));
                finishAffinity();
                return;
            }
            ProfileDialog profileDialog = new ProfileDialog();
            profileDialog.show(getSupportFragmentManager(), "Profile Dialog");
        });
    }

    private void loadRecyclerView() {
        arrOrders = new ArrayList<>();
        rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrdersSubactivity.this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rvOrders.setLayoutManager(linearLayoutManager);

        myOrdersRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_orders");
        myOrdersListener = myOrdersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    tvEmptyOrders.setVisibility(View.VISIBLE);
                }
                arrOrders.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Order order = dataSnapshot.getValue(Order.class);
                    arrOrders.add(order);
                    orderAdapter.notifyDataSetChanged();
                }
                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        orderAdapter = new OrderAdapter(context, arrOrders, onOrderListener);
        rvOrders.setAdapter(orderAdapter);
    }

    @Override
    public void onOrderClick(int position) {
        Intent intentViewOrders = new Intent(OrdersSubactivity.this, ViewOrderSubactivity.class);
        intentViewOrders.putExtra("order_uid", arrOrders.get(position).getUid());
        intentViewOrders.putExtra("user_uid", arrOrders.get(position).getUserUid());
        startActivity(intentViewOrders);
    }
}