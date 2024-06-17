package com.caternation.Modules;

import android.content.Context;
import android.content.SharedPreferences;

public class Cache {
    public static String getString(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("caternation_cache", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void setString(Context context, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences("caternation_cache", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static int getInt(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("caternation_cache", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static void setInt(Context context, String key, int value){
        SharedPreferences sharedPreferences = context.getSharedPreferences("caternation_cache", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("caternation_cache", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void setBoolean(Context context, String key, boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences("caternation_cache", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }
}
