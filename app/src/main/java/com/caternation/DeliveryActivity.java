package com.caternation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.caternation.Adapters.FoodAdapter;
import com.caternation.Authentication.LoginActivity;
import com.caternation.CustomDialogs.ProfileDialog;
import com.caternation.DeliverySubactivities.CartSubactivity;
import com.caternation.DeliverySubactivities.OrdersSubactivity;
import com.caternation.Modules.Generate;
import com.caternation.Objects.Food;
import com.caternation.CustomDialogs.QuantityDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DeliveryActivity extends AppCompatActivity implements FoodAdapter.OnFoodListener {

    private FirebaseUser currentUser;
    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private DatabaseReference cartRef, cartItemRef;

    RecyclerView rvFood;
    MaterialButton btnBack, btnCart, btnOrders, btnProfile;
    TextView tvActivityTitle;
    ChipGroup cgCategories;
    Chip chipMeals, chipDesserts, chipDrinks;

    ArrayList<Food> arrMeals;
    ArrayList<Food> arrDesserts;
    ArrayList<Food> arrDrinks;
    FoodAdapter foodAdapter;
    FoodAdapter.OnFoodListener onFoodListener = this;

    Context context;

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        initiate();
        initiateTopActionBar();
        initiateFoodItems();

        loadRecyclerView(chipMeals.getId());
        cgCategories.setOnCheckedChangeListener((group, checkedId) -> {
            loadRecyclerView(checkedId);
        });

        btnOrders.setOnClickListener(view -> {
            if (currentUser == null) {
                Generate.loginRedirection(DeliveryActivity.this);
                return;
            }
            startActivity(new Intent(DeliveryActivity.this, OrdersSubactivity.class));
        });

        btnCart.setOnClickListener(view -> {
            if (currentUser == null) {
                Generate.loginRedirection(DeliveryActivity.this);
                return;
            }
            startActivity(new Intent(DeliveryActivity.this, CartSubactivity.class));
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initiate() {
        context = this;
        rvFood = findViewById(R.id.rvFood);
        cgCategories = findViewById(R.id.cgCategories);
        chipMeals = findViewById(R.id.chipMeals);
        chipDesserts = findViewById(R.id.chipDesserts);
        chipDrinks = findViewById(R.id.chipDrinks);
        btnCart = findViewById(R.id.btnCart);
        btnOrders = findViewById(R.id.btnOrders);
    }

    private void initiateTopActionBar() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnBack = findViewById(R.id.btnBack);
        btnProfile = findViewById(R.id.btnProfile);

        tvActivityTitle.setText("Food Delivery");
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
                startActivity(new Intent(DeliveryActivity.this, LoginActivity.class));
                return;
            }
            ProfileDialog profileDialog = new ProfileDialog();
            profileDialog.show(getSupportFragmentManager(), "Profile Dialog");
        });
    }

    private void initiateFoodItems() {
        arrMeals = new ArrayList<>();
        arrMeals.add(new Food("Bulalo", 130.00, R.drawable.thumbnail_bulalo));
        arrMeals.add(new Food("Humba", 45.00, R.drawable.thumbnail_humba));
        arrMeals.add(new Food("Lechon Kawali", 65.00, R.drawable.thumbnail_lechon_kawali));
        arrMeals.add(new Food("Pork Adobo", 45.00, R.drawable.thumbnail_pork_adobo));
        arrMeals.add(new Food("Beef Kaldereta", 45.00, R.drawable.thumbnail_beef_kaldereta));
        arrMeals.add(new Food("Bistek", 40.00, R.drawable.thumbnail_bistek));
        arrMeals.add(new Food("Menudo", 40.00, R.drawable.thumbnail_menudo));
        arrMeals.add(new Food("Chicken Adobo", 35.00, R.drawable.thumbnail_chicken_adobo));
        arrMeals.add(new Food("Chicken Afritada", 35.00, R.drawable.thumbnail_chicken_afritada));
        arrMeals.add(new Food("Chicken Curry", 35.00, R.drawable.thumbnail_chicken_curry));

        arrDesserts = new ArrayList<>();
        arrDesserts.add(new Food("Biko", 10.00, R.drawable.thumbnail_biko));
        arrDesserts.add(new Food("Buko Pandan", 15.00, R.drawable.thumbnail_buko_pandan));
        arrDesserts.add(new Food("Buko Salad", 40.00, R.drawable.thumbnail_buko_salad));
        arrDesserts.add(new Food("Egg Pie", 8.00, R.drawable.thumbnail_egg_pie));
        arrDesserts.add(new Food("Halo-halo", 35.00, R.drawable.thumbnail_halo_halo));
        arrDesserts.add(new Food("Leche Flan", 50.00, R.drawable.thumbnail_leche_flan));
        arrDesserts.add(new Food("Macaroons (5 pcs)", 25.00, R.drawable.thumbnail_macaroons));
        arrDesserts.add(new Food("Palitaw (5 pcs)", 15.00, R.drawable.thumbnail_palitaw));
        arrDesserts.add(new Food("Suman (5 pcs)", 20.00, R.drawable.thumbnail_suman));
        arrDesserts.add(new Food("Turon (5 pcs)", 20.00, R.drawable.thumbnail_turon));

        arrDrinks = new ArrayList<>();
        arrDrinks.add(new Food("Buko Juice", 15.00, R.drawable.thumbnail_buko_juice));
        arrDrinks.add(new Food("Calamansi", 20.00, R.drawable.thumbnail_calamansi));
        arrDrinks.add(new Food("Coke", 20.00, R.drawable.thumbnail_coke));
        arrDrinks.add(new Food("Iced Tea", 15.00, R.drawable.thumbnail_iced_tea));
        arrDrinks.add(new Food("Mango Juice", 20.00, R.drawable.thumbnail_mango));
        arrDrinks.add(new Food("Orange Juice", 20.00, R.drawable.thumbnail_orange));
        arrDrinks.add(new Food("Pineapple Juice", 20.00, R.drawable.thumbnail_pineapple_juice));
        arrDrinks.add(new Food("Royal", 20.00, R.drawable.thumbnail_royal));
        arrDrinks.add(new Food("Sago't Gulaman", 15.00, R.drawable.thumbnail_sago_at_gulaman));
        arrDrinks.add(new Food("Sprite", 20.00, R.drawable.thumbnail_sprite));
    }

    private void loadRecyclerView(int checkedId) {
        rvFood = findViewById(R.id.rvFood);
        rvFood.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvFood.setLayoutManager(gridLayoutManager);

        if (checkedId == chipMeals.getId()){
            foodAdapter = new FoodAdapter(context, arrMeals, onFoodListener);
        }
        else if (checkedId == chipDesserts.getId()){
            foodAdapter = new FoodAdapter(context, arrDesserts, onFoodListener);
        }
        else if (checkedId == chipDrinks.getId()){
            foodAdapter = new FoodAdapter(context, arrDrinks, onFoodListener);
        }

        rvFood.setAdapter(foodAdapter);
        foodAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFoodClick(int position) {
        if (currentUser == null) {
            Generate.loginRedirection(DeliveryActivity.this);
            return;
        }
        dialogItemQuantity(position);
    }

    private void dialogItemQuantity(int position) {
        int checkedChipId = cgCategories.getCheckedChipId();

        ArrayList<Food> arrFood = arrMeals;
        if (checkedChipId == chipDesserts.getId()){
            arrFood = arrDesserts;
        }
        else if (checkedChipId == chipDrinks.getId()){
            arrFood = arrDrinks;
        }

        QuantityDialog quantityDialog = new QuantityDialog();
        Bundle bundle = new Bundle();
        bundle.putString("item_name", arrFood.get(position).getName());
        bundle.putInt("item_thumbnail", arrFood.get(position).getThumbnail());
        bundle.putInt("item_quantity", 1);
        bundle.putDouble("item_price", arrFood.get(position).getPrice());
        quantityDialog.setArguments(bundle);
        quantityDialog.show(getSupportFragmentManager(), "Quantity Dialog");
    }
}