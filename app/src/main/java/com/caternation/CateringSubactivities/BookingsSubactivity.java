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

import com.caternation.Adapters.BookingAdapter;
import com.caternation.Authentication.LoginActivity;
import com.caternation.CateringActivity;
import com.caternation.CustomDialogs.ProfileDialog;
import com.caternation.DeliveryActivity;
import com.caternation.Objects.Booking;
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

public class BookingsSubactivity extends AppCompatActivity implements BookingAdapter.OnBookingListener {

    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference myBookingsRef;
    private ValueEventListener myBookingsListener;

    TextView tvActivityTitle, tvEmptyBookings;
    MaterialButton btnBack, btnProfile;
    RecyclerView rvBookings;
    CircularProgressIndicator loadingBar;

    ArrayList<Booking> arrBookings;
    BookingAdapter bookingAdapter;
    BookingAdapter.OnBookingListener onBookingListener = this;

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
        setContentView(R.layout.subactivity_bookings);

        initiate();
        initiateTopActionBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myBookingsRef.removeEventListener(myBookingsListener);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(BookingsSubactivity.this, CateringActivity.class));
        finish();
    }

    private void initiate() {
        rvBookings = findViewById(R.id.rvBookings);
        loadingBar = findViewById(R.id.loadingBar);
        tvEmptyBookings = findViewById(R.id.tvEmptyBookings);

        context = this;
    }

    private void initiateTopActionBar() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnBack = findViewById(R.id.btnBack);
        btnProfile = findViewById(R.id.btnProfile);

        tvActivityTitle.setText("My Bookings");
        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(BookingsSubactivity.this, CateringActivity.class));
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
                startActivity(new Intent(BookingsSubactivity.this, LoginActivity.class));
                finishAffinity();
                return;
            }
            ProfileDialog profileDialog = new ProfileDialog();
            profileDialog.show(getSupportFragmentManager(), "Profile Dialog");
        });
    }

    private void loadRecyclerView() {
        arrBookings = new ArrayList<>();
        rvBookings = findViewById(R.id.rvBookings);
        rvBookings.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BookingsSubactivity.this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rvBookings.setLayoutManager(linearLayoutManager);

        myBookingsRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_bookings");
        myBookingsListener = myBookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    tvEmptyBookings.setVisibility(View.VISIBLE);
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
        rvBookings.setAdapter(bookingAdapter);
    }

    @Override
    public void onBookingClick(int position) {
        Intent intentViewOrders = new Intent(BookingsSubactivity.this, ViewBookingSubactivity.class);
        intentViewOrders.putExtra("booking_uid", arrBookings.get(position).getUid());
        intentViewOrders.putExtra("user_uid", arrBookings.get(position).getUserUid());
        startActivity(intentViewOrders);
    }
}