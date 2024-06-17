package com.caternation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.caternation.Adapters.PackageAdapter;
import com.caternation.Authentication.LoginActivity;
import com.caternation.CateringSubactivities.BookingsSubactivity;
import com.caternation.CateringSubactivities.PackageCartSubactivity;
import com.caternation.CustomDialogs.PackageSizeDialog;
import com.caternation.CustomDialogs.ProfileDialog;
import com.caternation.CustomDialogs.QuantityDialog;
import com.caternation.DeliverySubactivities.CartSubactivity;
import com.caternation.DeliverySubactivities.OrdersSubactivity;
import com.caternation.Modules.Generate;
import com.caternation.Objects.Food;
import com.caternation.Objects.Package;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CateringActivity extends AppCompatActivity implements PackageAdapter.OnPackageListener {

    private FirebaseUser currentUser;
    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private DatabaseReference cartRef, cartItemRef;

    RecyclerView rvPackages;
    MaterialButton btnBack, btnCart, btnBookings, btnProfile;
    TextView tvActivityTitle;

    ArrayList<Package> arrPackages;
    PackageAdapter foodAdapter;
    PackageAdapter.OnPackageListener onPackageListener = this;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catering);

        initiate();
        initiateTopActionBar();
        initiatePackageItems();
        loadRecyclerView();

        btnCart.setOnClickListener(view -> {
            if (currentUser == null) {
                Generate.loginRedirection(CateringActivity.this);
                return;
            }
            startActivity(new Intent(CateringActivity.this, PackageCartSubactivity.class));
        });

        btnBookings.setOnClickListener(view -> {
            if (currentUser == null) {
                Generate.loginRedirection(CateringActivity.this);
                return;
            }
            startActivity(new Intent(CateringActivity.this, BookingsSubactivity.class));
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initiate() {
        context = this;
        btnCart = findViewById(R.id.btnCart);
        btnBookings = findViewById(R.id.btnBookings);
    }

    private void initiateTopActionBar() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnBack = findViewById(R.id.btnBack);
        btnProfile = findViewById(R.id.btnProfile);

        tvActivityTitle.setText("Catering Service");
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
                startActivity(new Intent(CateringActivity.this, LoginActivity.class));
                return;
            }
            ProfileDialog profileDialog = new ProfileDialog();
            profileDialog.show(getSupportFragmentManager(), "Profile Dialog");
        });
    }

    private void initiatePackageItems() {
        arrPackages = new ArrayList<>();
        arrPackages.add(new Package("Meat Overload Package", 5500.0, "Cordon Bleu\n" +
                "Chicken Wings\n" +
                "Buttered Chicken\n" +
                "Pork Sisig\n" +
                "Lechon Kawali\n" +
                "Liempo\n" +
                "Beef Steak\n" +
                "Beef Tapa", R.drawable.thumbnail_meat_overload));
        arrPackages.add(new Package("Snacks and Everything Nice", 4500.0, "Onion Rings\n" +
                "Potato Wedges\n" +
                "Lumpia Shanghai\n" +
                "Sushi\n" +
                "Tempura\n" +
                "Pizza Burger\n" +
                "Nacho Fries\n" +
                "Cucumber Lemonade", R.drawable.thumbnail_snacks_and_everything_nice));
        arrPackages.add(new Package("Snacks and Viand", 5300.0, "King Burger\n" +
                "Palabok\n" +
                "Crispy Pata\n" +
                "Mac and Cheese\n" +
                "Pork Barbecue\n" +
                "Pork/Chicken Japanese Siomai\n" +
                "Chicken Inasal\n" +
                "Mashed potato", R.drawable.thumbnail_snacks_and_viand));
        arrPackages.add(new Package("Green and Healthy Package", 2800.0, "Vegetable Fried Rice\n" +
                "Coleslaw Salad\n" +
                "Caesar Salad\n" +
                "Tofu\n" +
                "Steamed mix Vegetables\n" +
                "Pinakbet\n" +
                "Chopsuey (Non-meat)\n" +
                "Cucumber Lemonade", R.drawable.thumbnail_green_and_healthy));
        arrPackages.add(new Package("Non-pork Package", 3700.0, "Buffalo Wings\n" +
                "Chicken Fingers\n" +
                "Chicken Inasal\n" +
                "Beef Ribs\n" +
                "Rack of Lamb\n" +
                "Rotisserie Chicken\n" +
                "Beef Steak\n" +
                "Chicken King Burger", R.drawable.thumbnail_non_pork));
    }

    private void loadRecyclerView() {
        rvPackages = findViewById(R.id.rvPackages);
        rvPackages.setHasFixedSize(true);
        rvPackages.setLayoutManager(new LinearLayoutManager(this));

        foodAdapter = new PackageAdapter(context, arrPackages, onPackageListener);
        rvPackages.setAdapter(foodAdapter);
        foodAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPackageClick(int position) {
        if (currentUser == null) {
            Generate.loginRedirection(CateringActivity.this);
            return;
        }
        dialogPackageSize(position);
    }

    private void dialogPackageSize(int position) {
        PackageSizeDialog packageSizeDialog = new PackageSizeDialog();
        Bundle bundle = new Bundle();
        bundle.putString("package_name", arrPackages.get(position).getName());
        bundle.putInt("package_thumbnail", arrPackages.get(position).getThumbnail());
        bundle.putDouble("package_price", arrPackages.get(position).getPrice());
        packageSizeDialog.setArguments(bundle);

        packageSizeDialog.show(getSupportFragmentManager(), "Package Size Dialog");
    }
}