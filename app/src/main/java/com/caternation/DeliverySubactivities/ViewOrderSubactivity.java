package com.caternation.DeliverySubactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.caternation.Adapters.OrderItemAdapter;
import com.caternation.Authentication.LoginActivity;
import com.caternation.CustomDialogs.ProfileDialog;
import com.caternation.Modules.DoubleFormatter;
import com.caternation.Objects.OrderItem;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewOrderSubactivity extends AppCompatActivity implements OrderItemAdapter.OnOrderItemListener {

    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference orderRef, orderItemsRef, statusRef, userStatusRef;
    private ValueEventListener orderDetailsListener;

    TextView tvActivityTitle, tvStatus, tvTimestamp, tvCustomer, tvMobileNumber, tvAddress, tvTotal;
    MaterialButton btnBack, btnProfile;
    RecyclerView rvViewOrder;
    CircularProgressIndicator loadingBar;
    AppCompatSpinner spnrStatus;

    ArrayList<OrderItem> arrOrderItems;
    OrderItemAdapter orderItemAdapter;
    OrderItemAdapter.OnOrderItemListener onOrderItemListener = this;

    Context context;
    String orderUid, userUid;
    boolean isAdmin = false;
    private static final String[] statuses = {"Pending", "Delivery", "Completed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_view_order);
        initiate();
        initiateTopActionBar();
        loadOrderDetails();
        initiateSpinner();
        loadRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        orderItemsRef.removeEventListener(orderDetailsListener);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initiate() {
        rvViewOrder = findViewById(R.id.rvViewOrder);
        loadingBar = findViewById(R.id.loadingBar);
        tvStatus = findViewById(R.id.tvStatus);
        tvCustomer = findViewById(R.id.tvCustomer);
        tvMobileNumber = findViewById(R.id.tvMobileNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvTotal = findViewById(R.id.tvTotal);

        orderUid = getIntent().getStringExtra("order_uid");
        userUid = getIntent().getStringExtra("user_uid");
        context = this;

        isAdmin = currentUser.getEmail().toLowerCase().contains("admin");
    }

    private void initiateTopActionBar() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnBack = findViewById(R.id.btnBack);
        btnProfile = findViewById(R.id.btnProfile);

        tvActivityTitle.setText("Order Details");
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
                startActivity(new Intent(ViewOrderSubactivity.this, LoginActivity.class));
                finishAffinity();
                return;
            }
            ProfileDialog profileDialog = new ProfileDialog();
            profileDialog.show(getSupportFragmentManager(), "Profile Dialog");
        });
    }

    private void initiateSpinner() {
        if (!isAdmin) {
            return;
        }
        spnrStatus = findViewById(R.id.spnrStatus);
        spnrStatus.setVisibility(View.VISIBLE);
        tvStatus.setVisibility(View.GONE);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(ViewOrderSubactivity.this, R.layout.spinner_selected, statuses);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item);
        spnrStatus.setAdapter(adapterSpinner);

        statusRef = caterNationDb.getReference("orders").child(orderUid).child("status");
        userStatusRef = caterNationDb.getReference("user_"+userUid+"_orders").child(orderUid).child("status");
        statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue().toString();
                if (status.equals("Pending")){
                    spnrStatus.setSelection(0);
                }
                else if (status.equals("Delivery")){
                    spnrStatus.setSelection(1);
                }
                else if (status.equals("Completed")){
                    spnrStatus.setSelection(2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spnrStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0){
                    statusRef.setValue("Pending");
                    userStatusRef.setValue("Pending");
                }
                else if (position == 1){
                    statusRef.setValue("Delivery");
                    userStatusRef.setValue("Delivery");
                }
                if (position == 2){
                    statusRef.setValue("Completed");
                    userStatusRef.setValue("Completed");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadOrderDetails() {
        if (isAdmin) {
            orderRef = caterNationDb.getReference("orders").child(orderUid);
        }
        else {
            orderRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_orders").child(orderUid);
        }
        orderDetailsListener = orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.child("firstName").getValue().toString();
                String lastName = snapshot.child("lastName").getValue().toString();
                String mobileNumber = snapshot.child("mobileNumber").getValue().toString();
                String addressBuilding = snapshot.child("addressBuilding").getValue().toString();
                String addressBarangay = snapshot.child("addressBarangay").getValue().toString();
                String addressCity = snapshot.child("addressCity").getValue().toString();
                long timestamp = Long.parseLong(snapshot.child("timestamp").getValue().toString());
                String total = DoubleFormatter.currencyFormat(Double.parseDouble(snapshot.child("total").getValue().toString()));

                tvCustomer.setText(firstName+" "+lastName);
                tvMobileNumber.setText(mobileNumber);
                tvAddress.setText(addressBuilding+", "+addressBarangay+", "+addressCity);
                tvTotal.setText(total);

                SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy");
                SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
                tvTimestamp.setText(sdfDate.format(timestamp) + " at " + sdfTime.format(timestamp));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadRecyclerView() {
        arrOrderItems = new ArrayList<>();
        rvViewOrder = findViewById(R.id.rvViewOrder);
        rvViewOrder.setHasFixedSize(true);
        rvViewOrder.setLayoutManager(new LinearLayoutManager(ViewOrderSubactivity.this));

        if (isAdmin) {
            orderItemsRef = caterNationDb.getReference("orderItems").child(orderUid);
        }
        else {
            orderItemsRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_orderItems").child(orderUid);
        }

        orderItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrOrderItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    OrderItem orderItem = dataSnapshot.getValue(OrderItem.class);
                    arrOrderItems.add(orderItem);
                    orderItemAdapter.notifyDataSetChanged();
                }
                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        orderItemAdapter = new OrderItemAdapter(context, arrOrderItems, onOrderItemListener);
        rvViewOrder.setAdapter(orderItemAdapter);
        orderItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOrderItemClick(int position) {

    }
}