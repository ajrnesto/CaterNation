package com.caternation.CateringSubactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caternation.Authentication.LoginActivity;
import com.caternation.CateringActivity;
import com.caternation.CustomDialogs.ProfileDialog;
import com.caternation.Modules.Generate;
import com.caternation.Objects.Booking;
import com.caternation.Objects.BookingItem;
import com.caternation.Objects.PackageCartItem;
import com.caternation.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ConfirmBookingSubactivity extends AppCompatActivity {

    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference userRef, packageCartRef, bookingsRef, userBookingsRef, bookingItemsRef, userBookingItemsRef;
    private ValueEventListener cartListener;

    TextView tvActivityTitle;
    TextInputEditText etFirstName, etLastName, etMobileNumber, etAddressBuilding, etAddressBarangay, etAddressCity, etDate, etTime;
    MaterialButton btnBack, btnProfile, btnPlaceOrder;
    CircularProgressIndicator loadingBar;
    MaterialCardView cvCateringInformation;

    MaterialDatePicker.Builder<Pair<Long, Long>> builder;
    MaterialDatePicker<Pair<Long, Long>> dateRangePicker;
    MaterialTimePicker timePicker;
    long timePickerGetSelection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_confirm_booking);
        
        initiate();
        initiateDatePicker();
        initiateTimePicker();
        initiateTopActionBar();
        loadUserInformation();
        btnPlaceOrder.setOnClickListener(view -> {
            if (etAddressCity.getText().toString().equals("Sindangan") ||
                    etAddressCity.getText().toString().equals("sindangan") ||
                    etAddressCity.getText().toString().equals("Liloy") ||
                    etAddressCity.getText().toString().equals("liloy")){
            }
            // do nothing
            else {
                Generate.warningDialog(ConfirmBookingSubactivity.this,
                        "Unavailable Event Location",
                        "Our valued customer, our services are currently limited within Sindangan and Liloy areas only.\nWe are sorry for the inconvenience.");
                return;
            }

            if (TextUtils.isEmpty(etDate.getText().toString())) {
                Generate.warningDialog(ConfirmBookingSubactivity.this,
                        "Event date is required",
                        "Please fill in all required fields");
                return;
            }

            String firstName = Objects.requireNonNull(etFirstName.getText()).toString();
            String lastName = Objects.requireNonNull(etLastName.getText()).toString();
            String mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString();
            String addressBuilding = Objects.requireNonNull(etAddressBuilding.getText()).toString();
            String addressBarangay = Objects.requireNonNull(etAddressBarangay.getText()).toString();
            String addressCity = Objects.requireNonNull(etAddressCity.getText()).toString();
            Long startDate = Objects.requireNonNull(dateRangePicker.getSelection()).first;
            Long endDate = Objects.requireNonNull(dateRangePicker.getSelection()).second;

            bookingsRef = caterNationDb.getReference("bookings").push();
            String bookingUid = bookingsRef.getKey();
            userBookingsRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_bookings").child(bookingUid);

            packageCartRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_bookingCart");
            packageCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    bookingItemsRef = caterNationDb.getReference("bookingItems").child(Objects.requireNonNull(bookingUid));
                    userBookingItemsRef = caterNationDb.getReference("user_"+currentUser.getUid()+"_bookingItems").child(Objects.requireNonNull(bookingUid));
                    double total = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        PackageCartItem packageCartItem = dataSnapshot.getValue(PackageCartItem.class);
                        String itemUid = packageCartItem.getUid();
                        String name = packageCartItem.getName();
                        double price = packageCartItem.getPrice();
                        int thumbnail = packageCartItem.getThumbnail();
                        int size = packageCartItem.getSize();

                        if (size == 20){
                            total += price;
                        }
                        else if (size == 50){
                            total += price*2;
                        }
                        else if (size == 100){
                            total += price*4;
                        }

                        BookingItem bookingItem = new BookingItem(itemUid, name, price, size, thumbnail);

                        bookingItemsRef.child(itemUid).setValue(bookingItem);
                        userBookingItemsRef.child(itemUid).setValue(bookingItem);
                    }

                    Booking booking = new Booking(
                            bookingUid,
                            currentUser.getUid(),
                            firstName,
                            lastName,
                            mobileNumber,
                            addressBuilding,
                            addressBarangay,
                            addressCity,
                            System.currentTimeMillis(),
                            startDate,
                            timePickerGetSelection,
                            endDate,
                            "Pending",
                            total
                    );
                    bookingsRef.setValue(booking);
                    userBookingsRef.setValue(booking);
                    packageCartRef.removeValue();

                    MaterialAlertDialogBuilder dialogComplete = new MaterialAlertDialogBuilder(ConfirmBookingSubactivity.this);
                    dialogComplete.setTitle("Your event has been booked")
                            .setMessage("Thank you for using our services!\nPlease wait for our agent to contact you for more details regarding your event.")
                            .setPositiveButton("Okay", (dialogInterface, i) -> startActivity(new Intent(ConfirmBookingSubactivity.this, CateringActivity.class)))
                            .setOnCancelListener(dialogInterface -> startActivity(new Intent(ConfirmBookingSubactivity.this, CateringActivity.class)))
                            .show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right);
    }

    private void initiate() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etAddressBuilding = findViewById(R.id.etAddressBuilding);
        etAddressBarangay = findViewById(R.id.etAddressBarangay);
        etAddressCity = findViewById(R.id.etAddressCity);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        loadingBar = findViewById(R.id.loadingBar);
        cvCateringInformation = findViewById(R.id.cvCateringInformation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
    }

    private void initiateDatePicker() {
        builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("When will your event take place?")
                .setSelection(new Pair(System.currentTimeMillis(), System.currentTimeMillis()));
        dateRangePicker = builder.build();
        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
            etDate.setText(sdf.format(Objects.requireNonNull(dateRangePicker.getSelection()).first) + " - " + sdf.format(Objects.requireNonNull(dateRangePicker.getSelection()).second));
            etDate.setEnabled(true);
        });
        dateRangePicker.addOnNegativeButtonClickListener(view -> {
            etDate.setEnabled(true);
        });
        dateRangePicker.addOnCancelListener(dialogInterface -> {
            etDate.setEnabled(true);
        });
        dateRangePicker.addOnDismissListener(dialogInterface -> {
            etDate.setEnabled(true);
        });
        etDate.setOnClickListener(view -> {
            etDate.setEnabled(false);
            dateRangePicker.show(getSupportFragmentManager(), "Date Picker");
        });
    }

    private void initiateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Time of Reservation")
                .build();
        timePicker.addOnPositiveButtonClickListener(view -> {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            timePickerGetSelection = calendar.getTimeInMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            etTime.setText(sdf.format(timePickerGetSelection));
            etTime.setEnabled(true);
        });
        timePicker.addOnNegativeButtonClickListener(view -> {
            etTime.setEnabled(true);
        });
        timePicker.addOnCancelListener(dialogInterface -> {
            etTime.setEnabled(true);
        });
        etTime.setOnClickListener(view -> {
            etTime.setEnabled(false);
            timePicker.show(getSupportFragmentManager(), "Date Picker");
        });
    }

    private void initiateTopActionBar() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnBack = findViewById(R.id.btnBack);
        btnProfile = findViewById(R.id.btnProfile);

        tvActivityTitle.setText("Catering Information");
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
                startActivity(new Intent(ConfirmBookingSubactivity.this, LoginActivity.class));
                return;
            }
            ProfileDialog profileDialog = new ProfileDialog();
            profileDialog.show(getSupportFragmentManager(), "Profile Dialog");
        });
    }

    private void loadUserInformation() {
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

                loadingBar.hide();
                cvCateringInformation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}