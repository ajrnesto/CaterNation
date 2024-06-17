package com.caternation.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.caternation.Modules.DoubleFormatter;
import com.caternation.R;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Objects;

public class PackageSizeDialog extends AppCompatDialogFragment {

    private FirebaseUser currentUser;
    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private DatabaseReference packageCartRef, packageCartItemRef;

    RoundedImageView ivThumbnail;
    TextView tvPackageName;
    RadioGroup radioGroup;
    MaterialRadioButton radioBtn20, radioBtn50, radioBtn100;

    String packageName;
    int packageThumbnail;
    double packagePrice;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_package_size, null);
        initiate(view);
        initiatePackageDetails();
        builder.setView(view)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {

                })
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    String uid = packageCartRef.push().getKey();
                    packageCartItemRef = packageCartRef.child(Objects.requireNonNull(uid));
                    packageCartItemRef.child("uid").setValue(uid);
                    packageCartItemRef.child("name").setValue(packageName);
                    packageCartItemRef.child("thumbnail").setValue(packageThumbnail);
                    packageCartItemRef.child("price").setValue(packagePrice);
                    if (radioGroup.getCheckedRadioButtonId() == radioBtn20.getId()){
                        packageCartItemRef.child("size").setValue(20);
                        Toast.makeText(getActivity(), "Added "+packageName+" - 20 pax to cart", Toast.LENGTH_LONG).show();
                    }
                    else if (radioGroup.getCheckedRadioButtonId() == radioBtn50.getId()){
                        packageCartItemRef.child("size").setValue(50);
                        Toast.makeText(getActivity(), "Added "+packageName+" - 50 pax to cart", Toast.LENGTH_LONG).show();
                    }
                    else if (radioGroup.getCheckedRadioButtonId() == radioBtn100.getId()){
                        packageCartItemRef.child("size").setValue(100);
                        Toast.makeText(getActivity(), "Added "+packageName+" - 100 pax to cart", Toast.LENGTH_LONG).show();
                    }
                });
        return builder.create();
    }

    private void initiate(View view) {
        ivThumbnail = view.findViewById(R.id.ivThumbnail);
        tvPackageName = view.findViewById(R.id.tvPackageName);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioBtn20 = view.findViewById(R.id.radioBtn20);
        radioBtn50 = view.findViewById(R.id.radioBtn50);
        radioBtn100 = view.findViewById(R.id.radioBtn100);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        packageCartRef = caterNationDb.getReference("user_"+ Objects.requireNonNull(currentUser).getUid()+"_bookingCart");
    }

    private void initiatePackageDetails() {
        packageName = requireArguments().getString("package_name");
        packageThumbnail = requireArguments().getInt("package_thumbnail");
        packagePrice = requireArguments().getDouble("package_price");

        tvPackageName.setText(packageName);
        ivThumbnail.setImageResource(packageThumbnail);
        radioBtn20.setText("₱"+ DoubleFormatter.currencyFormat(packagePrice) +" - 20 pax");
        radioBtn50.setText("₱"+ DoubleFormatter.currencyFormat(packagePrice*2) +" - 50 pax");
        radioBtn100.setText("₱"+ DoubleFormatter.currencyFormat(packagePrice*4) +" - 100 pax");
    }
}
