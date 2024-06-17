package com.caternation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caternation.Modules.DoubleFormatter;
import com.caternation.Objects.Food;
import com.caternation.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.foodViewHolder>{

    Context context;
    ArrayList<Food> foodArrayList;
    private OnFoodListener mOnFoodListener;

    public FoodAdapter(Context context, ArrayList<Food> foodArrayList, OnFoodListener onFoodListener) {
        this.context = context;
        this.foodArrayList = foodArrayList;
        this.mOnFoodListener = onFoodListener;
    }

    @NonNull
    @Override
    public foodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_food, parent, false);
        return new foodViewHolder(view, mOnFoodListener);
    }

    @Override
    public void onBindViewHolder(@NonNull foodViewHolder holder, int position) {
        Food food = foodArrayList.get(position);
        holder.tvName.setText(food.getName());
        holder.tvPrice.setText("₱"+DoubleFormatter.currencyFormat(food.getPrice()));
        Picasso.get().load(food.getThumbnail()).into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return foodArrayList.size();
    }

    public static class foodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName, tvPrice;
        ImageView ivThumbnail;
        OnFoodListener onFoodListener;
        public foodViewHolder(@NonNull View itemView, OnFoodListener onFoodListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvSubtotal);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            this.onFoodListener = onFoodListener;

            itemView.setOnClickListener(this); // ("this" refers to the View.OnClickListener context)
        }

        @Override
        public void onClick(View view) {
            onFoodListener.onFoodClick(getAdapterPosition());
        }
    }

    public interface OnFoodListener{
        void onFoodClick(int position);
    }
}