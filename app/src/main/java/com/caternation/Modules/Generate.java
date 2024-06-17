package com.caternation.Modules;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.caternation.Authentication.LoginActivity;
import com.caternation.DeliveryActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Random;

public class Generate {
    public static void warningDialog(Context context, String title, String message){
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
        materialAlertDialogBuilder.setTitle(title);
        materialAlertDialogBuilder.setMessage(message);
        materialAlertDialogBuilder.setPositiveButton("Okay", (dialogInterface, i) -> { });
        materialAlertDialogBuilder.show();
    }

    public static void loginDialog(Context context){
        MaterialAlertDialogBuilder dialogLoginPrompt = new MaterialAlertDialogBuilder(context);
        dialogLoginPrompt
                .setTitle("Login required")
                .setMessage("You need to sign in to set up orders")
                .setPositiveButton("Log in", (dialogInterface, i) -> {
                    context.startActivity(new Intent(context, LoginActivity.class));
                    ((Activity) context).finish();
                })
                .setNeutralButton("Cancel", (dialogInterface, i) -> {

                })
                .show();
    }

    public static void loginRedirection(Context context){
        context.startActivity(new Intent(context, LoginActivity.class));
        Toast.makeText(context, "Sign in to set up orders", Toast.LENGTH_LONG).show();
    }

    public static int randomNumber(int min, int max){
        final int random = new Random().nextInt((max - min) + 1) + min;
        return random;
    }
}
