package com.caternation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.caternation.Adapters.BookingAdapter;
import com.caternation.Authentication.LoginActivity;
import com.caternation.CateringSubactivities.BookingsSubactivity;
import com.caternation.CustomDialogs.ProfileDialog;
import com.caternation.Objects.Booking;
import com.google.android.material.button.MaterialButton;
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

public class EditProfileSubactivity extends AppCompatActivity {

    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference userRef;
    private ValueEventListener userVel;

    TextView tvActivityTitle;
    MaterialButton btnBack, btnProfile, btnSave;
    TextInputEditText etFirstName, etLastName, etMobileNumber, etAddressBuilding, etAddressBarangay, etAddressCity;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_edit_profile);
        initiate();
        initiateTopActionBar();
        loadUserDetails();

        btnSave.setOnClickListener(view -> {
            updateProfile();
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initiate() {
        context = this;
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etAddressBuilding = findViewById(R.id.etAddressBuilding);
        etAddressBarangay = findViewById(R.id.etAddressBarangay);
        etAddressCity = findViewById(R.id.etAddressCity);
        btnSave = findViewById(R.id.btnSave);
    }

    private void initiateTopActionBar() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnBack = findViewById(R.id.btnBack);
        btnProfile = findViewById(R.id.btnProfile);

        tvActivityTitle.setText("Edit Profile");
        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(EditProfileSubactivity.this, CateringActivity.class));
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
                startActivity(new Intent(EditProfileSubactivity.this, LoginActivity.class));
                finishAffinity();
                return;
            }
            ProfileDialog profileDialog = new ProfileDialog();
            profileDialog.show(getSupportFragmentManager(), "Profile Dialog");
        });
    }

    private void loadUserDetails() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProfile() {
        String firstName = Objects.requireNonNull(etFirstName.getText()).toString().trim();
        String lastName = Objects.requireNonNull(etLastName.getText()).toString().trim();
        String mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString().trim();
        String addressBuilding = Objects.requireNonNull(etAddressBuilding.getText()).toString().trim();
        String addressBarangay = Objects.requireNonNull(etAddressBarangay.getText()).toString().trim();
        String addressCity =  Objects.requireNonNull(etAddressCity.getText()).toString().trim();

        userRef = caterNationDb.getReference("user_"+currentUser.getUid());
        userRef.child("firstName").setValue(firstName);
        userRef.child("lastName").setValue(lastName);
        userRef.child("mobileNumber").setValue(mobileNumber);
        userRef.child("addressBuilding").setValue(addressBuilding);
        userRef.child("addressBarangay").setValue(addressBarangay);
        userRef.child("addressCity").setValue(addressCity);

        finish();
    }
}