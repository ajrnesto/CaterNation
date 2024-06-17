package com.caternation.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.caternation.Modules.DoubleFormatter;
import com.caternation.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Objects;

public class QuantityDialog extends AppCompatDialogFragment {

    private FirebaseUser currentUser;
    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private DatabaseReference cartRef, cartItemRef;

    RoundedImageView ivThumbnail;
    TextView tvItemName, tvItemPrice, tvItemQuantity;
    MaterialButton btnDecrement, btnIncrement;

    String itemName;
    int itemThumbnail, itemQuantity;
    double itemPrice;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_item_quantity, null);
        initiate(view);
        initiateItemDetails();
        incrementDecrementHandler();
        builder.setView(view)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {

                })
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    String uid = cartRef.push().getKey();
                    cartItemRef = cartRef.child(Objects.requireNonNull(uid));
                    cartItemRef.child("uid").setValue(uid);
                    cartItemRef.child("name").setValue(itemName);
                    cartItemRef.child("price").setValue(itemPrice);
                    cartItemRef.child("quantity").setValue(itemQuantity);
                    cartItemRef.child("thumbnail").setValue(itemThumbnail);

                    Toast.makeText(getActivity(), "Added x"+itemQuantity+" "+itemName+" to cart", Toast.LENGTH_LONG).show();
                });
        return builder.create();
    }

    private void initiate(View view) {
        ivThumbnail = view.findViewById(R.id.ivThumbnail);
        tvItemName = view.findViewById(R.id.tvItemName);
        tvItemPrice = view.findViewById(R.id.tvItemPrice);
        tvItemQuantity = view.findViewById(R.id.tvItemQuantity);
        btnDecrement = view.findViewById(R.id.btnDecrement);
        btnIncrement = view.findViewById(R.id.btnIncrement);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        cartRef = caterNationDb.getReference("user_"+ Objects.requireNonNull(currentUser).getUid()+"_cart");
    }

    private void initiateItemDetails() {
        itemName = requireArguments().getString("item_name");
        itemThumbnail = requireArguments().getInt("item_thumbnail");
        itemQuantity = requireArguments().getInt("item_quantity");
        itemPrice = requireArguments().getDouble("item_price");

        tvItemName.setText(itemName);
        ivThumbnail.setImageResource(itemThumbnail);
        tvItemPrice.setText("₱"+DoubleFormatter.currencyFormat(itemPrice));
        tvItemQuantity.setText("x"+itemQuantity);
    }

    private void incrementDecrementHandler() {
        btnIncrement.setOnClickListener(view -> {
            itemQuantity++;
            tvItemQuantity.setText("x"+itemQuantity);
            tvItemPrice.setText("₱"+DoubleFormatter.currencyFormat(itemPrice * (double) itemQuantity));
        });
        btnDecrement.setOnClickListener(view -> {
            if (itemQuantity <= 1) {
                return;
            }
            itemQuantity--;
            tvItemQuantity.setText("x"+itemQuantity);
            tvItemPrice.setText("₱"+DoubleFormatter.currencyFormat(itemPrice * Double.valueOf(itemQuantity)));
        });
    }
}
