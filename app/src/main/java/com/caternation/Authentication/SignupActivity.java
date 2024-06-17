package com.caternation.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.caternation.AdminActivity;
import com.caternation.Modules.Generate;
import com.caternation.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference newUserRef;

    MaterialCardView cvSignin, cvSignup, cvSignupAddress;
    CircularProgressIndicator progessSignup;
    MaterialButton btnSignin, btnSignup, btnNext, btnBack;
    TextInputEditText etFirstName, etLastName, etEmail, etPassword, etMobileNumber, etAddressBuilding, etAddressBarangay, etAddressCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initiate();

        btnSignup.setOnClickListener(view -> {
            String firstName = Objects.requireNonNull(etFirstName.getText()).toString();
            String lastName = Objects.requireNonNull(etLastName.getText()).toString();
            String email = Objects.requireNonNull(etEmail.getText()).toString();
            String password = Objects.requireNonNull(etPassword.getText()).toString();
            String mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString();
            String addressBuilding = Objects.requireNonNull(etAddressBuilding.getText()).toString();
            String addressBarangay = Objects.requireNonNull(etAddressBarangay.getText()).toString();
            String addressCity = Objects.requireNonNull(etAddressCity.getText()).toString();

            if (TextUtils.isEmpty(firstName) ||
                    TextUtils.isEmpty(lastName) ||
                    TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(mobileNumber) ||
                    TextUtils.isEmpty(addressBuilding) ||
                    TextUtils.isEmpty(addressBarangay) ||
                    TextUtils.isEmpty(addressCity)) {
                Generate.warningDialog(SignupActivity.this, "Registration failed", "Please fill in all required fields");
                return;
            }

            if (addressCity.equalsIgnoreCase("sindangan") ||
                    addressCity.equalsIgnoreCase("Liloy")) {
            }
            // Do nothing
            else {
                Generate.warningDialog(SignupActivity.this,
                        "Unavailable Delivery Address",
                        "Our valued customer, our services are currently limited within Sindangan and Liloy areas only.\nWe are sorry for the inconvenience.");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Generate.warningDialog(SignupActivity.this, "Invalid email", "Please provide a valid email");
                return;
            }

            if (password.length() < 6) {
                Generate.warningDialog(SignupActivity.this, "Invalid password", "Password should be at least 6 characters long");
                return;
            }

            firebaseRegistration(view, firstName, lastName, email, password, mobileNumber, addressBuilding, addressBarangay, addressCity);
        });

        btnNext.setOnClickListener(view -> {
            cvSignupAddress.setVisibility(View.VISIBLE);
            cvSignup.setVisibility(View.GONE);
        });

        btnBack.setOnClickListener(view -> {
            cvSignupAddress.setVisibility(View.GONE);
            cvSignup.setVisibility(View.VISIBLE);
        });

        btnSignin.setOnClickListener(view -> {
            finish();
        });
    }

    private void firebaseRegistration(View view, String firstName, String lastName, String email,
                                      String password, String mobileNumber, String addressBuilding,
                                      String addressBarangay, String addressCity) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        startLoading();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {
            if (task.isSuccessful()) {
                String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                newUserRef = caterNationDb.getReference("user_"+currentUserId);
                newUserRef.child("uid").setValue(currentUserId);
                newUserRef.child("firstName").setValue(firstName);
                newUserRef.child("lastName").setValue(lastName);
                newUserRef.child("email").setValue(email);
                newUserRef.child("mobileNumber").setValue(mobileNumber);
                newUserRef.child("addressBuilding").setValue(addressBuilding);
                newUserRef.child("addressBarangay").setValue(addressBarangay);
                newUserRef.child("addressCity").setValue(addressCity);
                Toast.makeText(SignupActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                if (email.toLowerCase().contains("admin")){
                    startActivity(new Intent(SignupActivity.this, AdminActivity.class));
                    finish();
                    return;
                }
                startActivity(new Intent(SignupActivity.this, StartupActivity.class));
                finish();
            }
            else {
                Toast.makeText(SignupActivity.this, "The email address has already been taken", Toast.LENGTH_SHORT).show();
                finishLoading();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initiate() {
        btnSignup = findViewById(R.id.btnSignup);
        btnSignin = findViewById(R.id.btnSignin);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etAddressBuilding = findViewById(R.id.etAddressBuilding);
        etAddressBarangay = findViewById(R.id.etAddressBarangay);
        etAddressCity = findViewById(R.id.etAddressCity);
        cvSignin = findViewById(R.id.cvSignin);
        cvSignup = findViewById(R.id.cvSignup);
        cvSignupAddress = findViewById(R.id.cvSignupAddress);
        progessSignup = findViewById(R.id.progressSignup);
    }

    private void startLoading() {
        cvSignin.setVisibility(View.GONE);
        cvSignupAddress.setVisibility(View.GONE);
        progessSignup.setVisibility(View.VISIBLE);
    }

    private void finishLoading() {
        cvSignin.setVisibility(View.VISIBLE);
        cvSignupAddress.setVisibility(View.VISIBLE);
        progessSignup.setVisibility(View.GONE);
    }
}