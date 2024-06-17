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
import com.caternation.Objects.OrderItem;
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

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.orderItemViewHolder>{

    private static final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private static final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference orderItemRef = caterNationDb.getReference("user_"+ Objects.requireNonNull(currentUser).getUid()+"_orderItem");

    Context context;
    static ArrayList<OrderItem> orderItemArrayList;
    private OrderItemAdapter.OnOrderItemListener mOnOrderItemListener;

    public OrderItemAdapter(Context context, ArrayList<OrderItem> orderItemArrayList, OrderItemAdapter.OnOrderItemListener onOrderItemListener) {
        this.context = context;
        this.orderItemArrayList = orderItemArrayList;
        this.mOnOrderItemListener = onOrderItemListener;
    }

    @NonNull
    @Override
    public OrderItemAdapter.orderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_order_item, parent, false);
        return new OrderItemAdapter.orderItemViewHolder(view, mOnOrderItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.orderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItemArrayList.get(position);
        holder.tvName.setText(orderItem.getName());
        holder.tvPrice.setText("₱"+ DoubleFormatter.currencyFormat(orderItem.getPrice()));
        holder.tvSubtotal.setText("₱"+ DoubleFormatter.currencyFormat(orderItem.getPrice() * (double) orderItem.getQuantity()));
        holder.tvQuantity.setText("x"+ orderItem.getQuantity());
        Picasso.get().load(orderItem.getThumbnail()).into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return orderItemArrayList.size();
    }

    public static class orderItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName, tvPrice, tvSubtotal, tvQuantity;
        ImageView ivThumbnail;
        OrderItemAdapter.OnOrderItemListener onOrderItemListener;
        public orderItemViewHolder(@NonNull View itemView, OrderItemAdapter.OnOrderItemListener onOrderItemListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            this.onOrderItemListener = onOrderItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onOrderItemListener.onOrderItemClick(getAdapterPosition());
        }
    }

    public interface OnOrderItemListener{
        void onOrderItemClick(int position);
    }
}
