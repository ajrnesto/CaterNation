package com.caternation.CustomDialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.caternation.Authentication.LoginActivity;
import com.caternation.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileAdminDialog  extends AppCompatDialogFragment {

    private FirebaseUser currentUser;
    private final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private DatabaseReference userRef;

    TextView tvFullname, tvEmail;
    MaterialButton btnEditProfile, btnLogout;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile_admin, null);
        initiate(view);
        loadUserInformation();
        buttonHandler();
        builder.setView(view);
        return builder.create();
    }

    private void initiate(View view) {
        tvFullname = view.findViewById(R.id.tvFullname);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
    }

    private void loadUserInformation() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = caterNationDb.getReference("user_"+ Objects.requireNonNull(currentUser).getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = Objects.requireNonNull(snapshot.child("firstName").getValue()).toString();
                String lastName = Objects.requireNonNull(snapshot.child("lastName").getValue()).toString();
                tvFullname.setText(firstName+" "+lastName);
                tvEmail.setText(currentUser.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void buttonHandler() {
        btnLogout.setOnClickListener(view -> {
            MaterialAlertDialogBuilder dialogLogout = new MaterialAlertDialogBuilder(getActivity());
            dialogLogout.setTitle("Confirm logout")
                    .setNeutralButton("Cancel", (dialogInterface, i) -> {

                    })
                    .setNegativeButton("Log out", (dialogInterface, i) -> {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        ((Activity) requireContext()).finish();
                    }).show();
        });
    }
}
