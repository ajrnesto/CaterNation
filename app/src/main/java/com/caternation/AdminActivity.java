package com.caternation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.caternation.Adapters.BookingAdapter;
import com.caternation.Adapters.FoodAdapter;
import com.caternation.Adapters.OrderAdapter;
import com.caternation.Authentication.LoginActivity;
import com.caternation.CateringSubactivities.BookingsSubactivity;
import com.caternation.CateringSubactivities.ViewBookingSubactivity;
import com.caternation.CustomDialogs.ProfileAdminDialog;
import com.caternation.DeliverySubactivities.ViewOrderSubactivity;
import com.caternation.Objects.Booking;
import com.caternation.Objects.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements OrderAdapter.OnOrderListener, BookingAdapter.OnBookingListener {

    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference ordersRef, bookingsRef;
    private ValueEventListener ordersListener, bookingsListener;

    TextView tvActivityTitle, tvEmptyOrders, tvEmptyBookings;
    MaterialButton btnBack, btnProfile;
    RecyclerView rvAdmin;
    CircularProgressIndicator loadingBar;
    ChipGroup cgCategories;
    Chip chipOrders, chipBookings;

    ArrayList<Order> arrOrders;
    OrderAdapter orderAdapter;
    OrderAdapter.OnOrderListener onOrderListener = this;

    ArrayList<Booking> arrBookings;
    BookingAdapter bookingAdapter;
    BookingAdapter.OnBookingListener onBookingListener = this;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initiate();
        initiateTopActionBar();

        loadRecyclerView(chipOrders.getId());
        cgCategories.setOnCheckedChangeListener((group, checkedId) -> {
            loadRecyclerView(checkedId);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initiate() {
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);
        tvEmptyBookings = findViewById(R.id.tvEmptyBookings);
        cgCategories = findViewById(R.id.cgCategories);
        chipOrders = findViewById(R.id.chipOrders);
        chipBookings = findViewById(R.id.chipBookings);
        loadingBar = findViewById(R.id.loadingBar);
        context = this;
    }

    private void initiateTopActionBar() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnBack = findViewById(R.id.btnBack);
        btnProfile = findViewById(R.id.btnProfile);

        tvActivityTitle.setText("Admin Dashboard");
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, r.getDisplayMetrics());
        tvActivityTitle.setPadding(px, 0, 0, 0);
        btnBack.setVisibility(View.GONE);

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
                startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                return;
            }
            ProfileAdminDialog profileadminDialog = new ProfileAdminDialog();
            profileadminDialog.show(getSupportFragmentManager(), "Profile Admin Dialog");
        });
    }

    private void loadRecyclerView(int checkedId) {
        arrOrders = new ArrayList<>();
        arrBookings = new ArrayList<>();
        rvAdmin = findViewById(R.id.rvAdmin);
        rvAdmin.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AdminActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rvAdmin.setLayoutManager(linearLayoutManager);

        if (checkedId == chipOrders.getId()){
            ordersRef = caterNationDb.getReference("orders");
            ordersListener = ordersRef.orderByChild("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        tvEmptyOrders.setVisibility(View.VISIBLE);
                    }
                    else {
                        tvEmptyOrders.setVisibility(View.GONE);
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
        }
        else if (checkedId == chipBookings.getId()){
            bookingsRef = caterNationDb.getReference("bookings");
            bookingsListener = bookingsRef.orderByChild("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        tvEmptyBookings.setVisibility(View.VISIBLE);
                    }
                    else {
                        tvEmptyBookings.setVisibility(View.GONE);
                    }
                    arrBookings.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Booking booking = dataSnapshot.getValue(Booking.class);
                        arrBookings.add(booking);
                        bookingAdapter.notifyDataSetChanged();
                    }
                    loadingBar.hide();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            bookingAdapter = new BookingAdapter(context, arrBookings, onBookingListener);
        }

        if (checkedId == chipOrders.getId()) {
            rvAdmin.setAdapter(orderAdapter);
            orderAdapter.notifyDataSetChanged();
            tvEmptyBookings.setVisibility(View.GONE);
        }
        else if (checkedId == chipBookings.getId()) {
            rvAdmin.setAdapter(bookingAdapter);
            bookingAdapter.notifyDataSetChanged();
            tvEmptyOrders.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOrderClick(int position) {
        Intent intentViewOrders = new Intent(AdminActivity.this, ViewOrderSubactivity.class);
        intentViewOrders.putExtra("order_uid", arrOrders.get(position).getUid());
        intentViewOrders.putExtra("user_uid", arrOrders.get(position).getUserUid());
        startActivity(intentViewOrders);
    }

    @Override
    public void onBookingClick(int position) {
        Intent intentViewOrders = new Intent(AdminActivity.this, ViewBookingSubactivity.class);
        intentViewOrders.putExtra("booking_uid", arrBookings.get(position).getUid());
        intentViewOrders.putExtra("user_uid", arrBookings.get(position).getUserUid());
        startActivity(intentViewOrders);
    }
}