package com.caternation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caternation.Modules.DoubleFormatter;
import com.caternation.Objects.PackageCartItem;
import com.caternation.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class PackageCartAdapter  extends RecyclerView.Adapter<PackageCartAdapter.packageCartViewHolder>{

    private static final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private static final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference packageCartRef = caterNationDb.getReference("user_"+ Objects.requireNonNull(currentUser).getUid()+"_bookingCart");

    Context context;
    static ArrayList<PackageCartItem> packageCartItemArrayList;
    private PackageCartAdapter.OnPackageCartListener mOnPackageCartListener;

    public PackageCartAdapter(Context context, ArrayList<PackageCartItem> packageCartItemArrayList, PackageCartAdapter.OnPackageCartListener onPackageCartListener) {
        this.context = context;
        this.packageCartItemArrayList = packageCartItemArrayList;
        this.mOnPackageCartListener = onPackageCartListener;
    }

    @NonNull
    @Override
    public PackageCartAdapter.packageCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_package_cart, parent, false);
        return new PackageCartAdapter.packageCartViewHolder(view, mOnPackageCartListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageCartAdapter.packageCartViewHolder holder, int position) {
        PackageCartItem packageCartItem = packageCartItemArrayList.get(position);
        String uid = packageCartItemArrayList.get(position).getUid();
        String packageName = packageCartItem.getName();
        double packagePrice20 = packageCartItem.getPrice();
        int size = packageCartItem.getSize();
        holder.tvName.setText(packageName);
        Picasso.get().load(packageCartItem.getThumbnail()).into(holder.ivThumbnail);
        holder.radioBtn20.setText("₱"+DoubleFormatter.currencyFormat(packagePrice20));
        holder.radioBtn50.setText("₱"+DoubleFormatter.currencyFormat(packagePrice20*2));
        holder.radioBtn100.setText("₱"+DoubleFormatter.currencyFormat(packagePrice20*4));
        if (size == 20){
            holder.radioBtn20.setChecked(true);
            holder.radioBtn50.setChecked(false);
            holder.radioBtn100.setChecked(false);
        }
        else if (size == 50){
            holder.radioBtn20.setChecked(false);
            holder.radioBtn50.setChecked(true);
            holder.radioBtn100.setChecked(false);
        }
        else if (size == 100){
            holder.radioBtn20.setChecked(false);
            holder.radioBtn50.setChecked(false);
            holder.radioBtn100.setChecked(true);
        }
        holder.radioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == holder.radioBtn20.getId()){
                packageCartRef.child(uid).child("size").setValue(20);
            }
            else if (checkedId == holder.radioBtn50.getId()){
                packageCartRef.child(uid).child("size").setValue(50);
            }
            else if (checkedId == holder.radioBtn100.getId()){
                packageCartRef.child(uid).child("size").setValue(100);
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageCartItemArrayList.size();
    }

    public static class packageCartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName;
        RadioGroup radioGroup;
        RadioButton radioBtn20, radioBtn50, radioBtn100;
        ImageView ivThumbnail;
        MaterialButton btnDelete;
        PackageCartAdapter.OnPackageCartListener onPackageCartListener;
        public packageCartViewHolder(@NonNull View itemView, PackageCartAdapter.OnPackageCartListener onPackageCartListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            radioBtn20 = itemView.findViewById(R.id.radioBtn20);
            radioBtn50 = itemView.findViewById(R.id.radioBtn50);
            radioBtn100 = itemView.findViewById(R.id.radioBtn100);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            this.onPackageCartListener = onPackageCartListener;

            itemView.setOnClickListener(this);

            btnDelete.setOnClickListener(view -> {
                MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(itemView.getContext());
                dialogDelete.setTitle("Remove "+ packageCartItemArrayList.get(getAdapterPosition()).getName()+" from the Cart?")
                        .setNeutralButton("Cancel", (dialogInterface, i) -> {

                        })
                        .setNegativeButton("Remove", (dialogInterface, i) -> {
                            packageCartRef.child(packageCartItemArrayList.get(getAdapterPosition()).getUid()).removeValue();
                            Toast.makeText(itemView.getContext(), "Removed "+ packageCartItemArrayList.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
                        })
                        .show();
            });
        }

        @Override
        public void onClick(View view) {
            onPackageCartListener.onPackageCartClick(getAdapterPosition());
        }
    }

    public interface OnPackageCartListener{
        void onPackageCartClick(int position);
    }
}
