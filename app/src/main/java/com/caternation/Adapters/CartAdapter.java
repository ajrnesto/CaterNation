package com.caternation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caternation.Modules.DoubleFormatter;
import com.caternation.Objects.CartItem;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.cartViewHolder>{

    private static final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private static final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference cartRef = caterNationDb.getReference("user_"+Objects.requireNonNull(currentUser).getUid()+"_cart");

    Context context;
    static ArrayList<CartItem> cartItemArrayList;
    private CartAdapter.OnCartListener mOnCartListener;

    public CartAdapter(Context context, ArrayList<CartItem> cartItemArrayList, CartAdapter.OnCartListener onCartListener) {
        this.context = context;
        this.cartItemArrayList = cartItemArrayList;
        this.mOnCartListener = onCartListener;
    }

    @NonNull
    @Override
    public CartAdapter.cartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_cart, parent, false);
        return new CartAdapter.cartViewHolder(view, mOnCartListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.cartViewHolder holder, int position) {
        CartItem cartItem = cartItemArrayList.get(position);
        holder.tvName.setText(cartItem.getName());
        holder.tvPrice.setText("₱"+ DoubleFormatter.currencyFormat(cartItem.getPrice()));
        holder.tvSubtotal.setText("₱"+ DoubleFormatter.currencyFormat(cartItem.getPrice() * (double) cartItem.getQuantity()));
        holder.tvQuantity.setText("x"+ cartItem.getQuantity());
        Picasso.get().load(cartItem.getThumbnail()).into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return cartItemArrayList.size();
    }

    public static class cartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName, tvPrice, tvSubtotal, tvQuantity;
        ImageView ivThumbnail;
        MaterialButton btnDelete, btnDecrement, btnIncrement;
        CartAdapter.OnCartListener onCartListener;
        public cartViewHolder(@NonNull View itemView, CartAdapter.OnCartListener onCartListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDecrement = itemView.findViewById(R.id.btnDecrement);
            btnIncrement = itemView.findViewById(R.id.btnIncrement);
            this.onCartListener = onCartListener;

            itemView.setOnClickListener(this);

            btnDecrement.setOnClickListener(view -> {
                CartItem cartItem = cartItemArrayList.get(getAdapterPosition());
                if (cartItem.getQuantity() <= 1){
                    return;
                }
                cartItem.setQuantity(cartItem.getQuantity()-1);
                tvSubtotal.setText("₱"+ DoubleFormatter.currencyFormat(cartItem.getPrice() * (double) cartItem.getQuantity()));
                tvQuantity.setText("x"+ cartItem.getQuantity());
                cartRef.child(cartItem.getUid()).child("quantity").setValue(ServerValue.increment(-1));
            });

            btnIncrement.setOnClickListener(view -> {
                CartItem cartItem = cartItemArrayList.get(getAdapterPosition());
                cartItem.setQuantity(cartItem.getQuantity()+1);
                tvSubtotal.setText("₱"+ DoubleFormatter.currencyFormat(cartItem.getPrice() * (double) cartItem.getQuantity()));
                tvQuantity.setText("x"+ cartItem.getQuantity());
                cartRef.child(cartItem.getUid()).child("quantity").setValue(ServerValue.increment(1));
            });
            
            btnDelete.setOnClickListener(view -> {
                MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(itemView.getContext());
                dialogDelete.setTitle("Remove "+ cartItemArrayList.get(getAdapterPosition()).getName()+" from the cart?")
                        .setNeutralButton("Cancel", (dialogInterface, i) -> {

                        })
                        .setNegativeButton("Remove", (dialogInterface, i) -> {
                            cartRef.child(cartItemArrayList.get(getAdapterPosition()).getUid()).removeValue();
                            Toast.makeText(itemView.getContext(), "Removed "+ cartItemArrayList.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
                        })
                        .show();
            });
        }

        @Override
        public void onClick(View view) {
            onCartListener.onCartClick(getAdapterPosition());
        }
    }



    public interface OnCartListener{
        void onCartClick(int position);
    }
}
