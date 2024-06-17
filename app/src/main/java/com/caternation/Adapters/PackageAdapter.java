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
import com.caternation.Objects.Package;
import com.caternation.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.packageViewHolder>{

    Context context;
    ArrayList<Package> packageArrayList;
    private PackageAdapter.OnPackageListener mOnPackageListener;

    public PackageAdapter(Context context, ArrayList<Package> packageArrayList, PackageAdapter.OnPackageListener onPackageListener) {
        this.context = context;
        this.packageArrayList = packageArrayList;
        this.mOnPackageListener = onPackageListener;
    }

    @NonNull
    @Override
    public PackageAdapter.packageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_package, parent, false);
        return new PackageAdapter.packageViewHolder(view, mOnPackageListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageAdapter.packageViewHolder holder, int position) {
        Package aPackage = packageArrayList.get(position);
        holder.tvName.setText(aPackage.getName());
        holder.tvPrice.setText("₱"+ DoubleFormatter.currencyFormat(aPackage.getPrice())+" (20 Pax)");
        holder.tvItems.setText(aPackage.getItems());
        Picasso.get().load(aPackage.getThumbnail()).into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return packageArrayList.size();
    }

    public static class packageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName, tvPrice, tvItems;
        ImageView ivThumbnail;
        PackageAdapter.OnPackageListener onPackageListener;
        public packageViewHolder(@NonNull View itemView, PackageAdapter.OnPackageListener onPackageListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvSubtotal);
            tvItems = itemView.findViewById(R.id.tvItems);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            this.onPackageListener = onPackageListener;

            itemView.setOnClickListener(this); // ("this" refers to the View.OnClickListener context)
        }

        @Override
        public void onClick(View view) {
            onPackageListener.onPackageClick(getAdapterPosition());
        }
    }

    public interface OnPackageListener{
        void onPackageClick(int position);
    }
}
