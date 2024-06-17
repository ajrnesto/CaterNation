package com.caternation.Authentication;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.caternation.AdminActivity;
import com.caternation.CateringActivity;
import com.caternation.DeliveryActivity;
import com.caternation.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class StartupActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    TextView tvEmailText, tvUserEmail;
    MaterialButton btnDelivery, btnCatering, btnAuth;
    MaterialCardView cvAppTitle, cvDelivery, cvCatering, cvLogin;
    CircularProgressIndicator progressStartup;

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null){
            return;
        }
        if (Objects.requireNonNull(currentUser.getEmail()).toLowerCase().contains("admin")) {
            startActivity(new Intent(StartupActivity.this, AdminActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            tvEmailText.setVisibility(View.VISIBLE);
            tvUserEmail.setVisibility(View.VISIBLE);
            tvUserEmail.setText(currentUser.getEmail());
            btnAuth.setText("Log out");
            btnAuth.setIcon(getDrawable(R.drawable.outline_logout_24));
        }
        else {
            tvEmailText.setVisibility(View.GONE);
            tvUserEmail.setVisibility(View.GONE);
            btnAuth.setText("Sign in to CaterNation");
            btnAuth.setIcon(getDrawable(R.drawable.outline_login_24));
        }

        /*final Handler handler = new Handler();
        // Do something after 2s = 2000ms
        handler.postDelayed(this::loadingComplete, 2000);*/
        loadingComplete();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        initiate();

        btnDelivery.setOnClickListener(view -> {
            startActivity(new Intent(StartupActivity.this, DeliveryActivity.class));
        });

        btnCatering.setOnClickListener(view -> {
            startActivity(new Intent(StartupActivity.this, CateringActivity.class));
        });

        btnAuth.setOnClickListener(view -> {
            if(currentUser != null){
                Toast.makeText(this, "Signed out of "+currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }
            startActivity(new Intent(StartupActivity.this, LoginActivity.class));
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initiate() {
        tvEmailText = findViewById(R.id.tvEmailText);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        cvAppTitle = findViewById(R.id.cvAppTitle);
        btnDelivery = findViewById(R.id.btnDelivery);
        btnCatering = findViewById(R.id.btnCatering);
        btnAuth = findViewById(R.id.btnAuth);
    }

    private void loadingComplete() {
        cvDelivery = findViewById(R.id.cvSignin);
        cvCatering = findViewById(R.id.cvCatering);
        cvLogin = findViewById(R.id.cvLogin);
        progressStartup = findViewById(R.id.progressStartup);

        cvDelivery.setVisibility(View.VISIBLE);
        cvCatering.setVisibility(View.VISIBLE);
        cvLogin.setVisibility(View.VISIBLE);
        progressStartup.setVisibility(View.GONE);
    }
}