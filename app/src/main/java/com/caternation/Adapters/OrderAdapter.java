package com.caternation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caternation.Modules.DoubleFormatter;
import com.caternation.Objects.Order;
import com.caternation.Objects.OrderItem;
import com.caternation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.orderViewHolder>{

    private static final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private static final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference orderItemsRef;

    Context context;
    static ArrayList<Order> orderArrayList;
    static ArrayList<OrderItem> orderItemsArrayList;
    private OrderAdapter.OnOrderListener mOnOrderListener;

    public OrderAdapter(Context context, ArrayList<Order> orderArrayList, OrderAdapter.OnOrderListener onOrderListener) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.mOnOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public OrderAdapter.orderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_order, parent, false);
        return new OrderAdapter.orderViewHolder(view, mOnOrderListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.orderViewHolder holder, int position) {
        boolean isAdmin = false;
        isAdmin = currentUser.getEmail().toLowerCase().contains("admin");

        Order order = orderArrayList.get(position);
        long timestamp = order.getTimestamp();
        String status = order.getStatus();
        String customer = order.getFirstName() + " " + order.getLastName();
        String addressBuilding = order.getAddressBuilding();
        String addressBarangay = order.getAddressBarangay();
        String addressCity = order.getAddressCity();
        double total = order.getTotal();

        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
        holder.tvTimestamp.setText(sdfDate.format(timestamp) + " at " + sdfTime.format(timestamp));

        holder.tvStatus.setText(status);
        holder.tvCustomer.setText(customer);
        StringBuilder sbAddress = new StringBuilder();
        sbAddress.append(addressBuilding).append(", ");
        sbAddress.append(addressBarangay).append(", ");
        sbAddress.append(addressCity);
        holder.tvAddress.setText(sbAddress);
        holder.tvTotal.setText("₱"+DoubleFormatter.currencyFormat(total));

        StringBuilder sbFood = new StringBuilder();
        orderItemsArrayList = new ArrayList<>();
        if (isAdmin) {
            orderItemsRef = caterNationDb.getReference("orderItems").child(order.getUid());
        }
        else {
            orderItemsRef = caterNationDb.getReference("user_"+ Objects.requireNonNull(currentUser).getUid()+"_orderItems").child(order.getUid());
        }
        orderItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderItemsArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    OrderItem orderItem = dataSnapshot.getValue(OrderItem.class);
                    sbFood.append("x").append(orderItem.getQuantity()).append(" ");
                    sbFood.append(orderItem.getName()).append(", ");
                }
                sbFood.delete(sbFood.length()-2, sbFood.length()-1); // delete last ","
                holder.tvFood.setText(sbFood);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public static class orderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTimestamp, tvStatus, tvCustomer, tvFood, tvAddress, tvTotal;
        OrderAdapter.OnOrderListener onOrderListener;
        public orderViewHolder(@NonNull View itemView, OrderAdapter.OnOrderListener onOrderListener) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvFood = itemView.findViewById(R.id.tvFood);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            this.onOrderListener = onOrderListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onOrderListener.onOrderClick(getAdapterPosition());
        }
    }

    public interface OnOrderListener{
        void onOrderClick(int position);
    }
}
