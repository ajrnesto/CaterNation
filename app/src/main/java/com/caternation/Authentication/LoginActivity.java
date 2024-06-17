package com.caternation.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.caternation.AdminActivity;
import com.caternation.Modules.Generate;
import com.caternation.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    MaterialCardView cvSignin, cvSignup;
    TextInputEditText etEmail, etPassword;
    MaterialButton btnSignin, btnSignup;
    CircularProgressIndicator progressLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initiate();

        btnSignin.setOnClickListener(this::firebaseLogin);

        btnSignup.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initiate() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignup = findViewById(R.id.btnSignup);
        cvSignin = findViewById(R.id.cvSignin);
        cvSignup = findViewById(R.id.cvSignup);
        progressLogin = findViewById(R.id.progressLogin);
    }

    private void firebaseLogin(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        startLoading();

        String email = Objects.requireNonNull(etEmail.getText()).toString();
        String password = Objects.requireNonNull(etPassword.getText()).toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Generate.warningDialog(LoginActivity.this, "Registration failed", "Please fill in all required fields");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                if (email.toLowerCase().contains("admin")){
                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    finish();
                    return;
                }
                Toast.makeText(this, "Logged in as "+email, Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                finishLoading();
            }
        });
    }

    private void startLoading() {
        cvSignin.setVisibility(View.GONE);
        cvSignup.setVisibility(View.GONE);
        progressLogin.setVisibility(View.VISIBLE);
    }

    private void finishLoading() {
        cvSignin.setVisibility(View.VISIBLE);
        cvSignup.setVisibility(View.VISIBLE);
        progressLogin.setVisibility(View.GONE);
    }
}