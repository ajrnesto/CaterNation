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
import com.caternation.Objects.BookingItem;
import com.caternation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class BookingItemAdapter extends RecyclerView.Adapter<BookingItemAdapter.bookingItemViewHolder>{

    private static final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private static final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference bookingItemRef = caterNationDb.getReference("user_"+ Objects.requireNonNull(currentUser).getUid()+"_bookingItem");

    Context context;
    static ArrayList<BookingItem> bookingItemArrayList;
    private BookingItemAdapter.OnBookingItemListener mOnBookingItemListener;

    public BookingItemAdapter(Context context, ArrayList<BookingItem> bookingItemArrayList, BookingItemAdapter.OnBookingItemListener onBookingItemListener) {
        this.context = context;
        this.bookingItemArrayList = bookingItemArrayList;
        this.mOnBookingItemListener = onBookingItemListener;
    }

    @NonNull
    @Override
    public BookingItemAdapter.bookingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_booking_item, parent, false);
        return new BookingItemAdapter.bookingItemViewHolder(view, mOnBookingItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingItemAdapter.bookingItemViewHolder holder, int position) {
        BookingItem bookingItem = bookingItemArrayList.get(position);
        holder.tvName.setText(bookingItem.getName());
        holder.tvSize.setText(bookingItem.getSize()+" pax");
        if (bookingItem.getSize() == 20){
            holder.tvSubtotal.setText("₱"+ DoubleFormatter.currencyFormat(bookingItem.getPrice()));
        }
        else if (bookingItem.getSize() == 50){
            holder.tvSubtotal.setText("₱"+ DoubleFormatter.currencyFormat(bookingItem.getPrice()*2));
        }
        else if (bookingItem.getSize() == 100){
            holder.tvSubtotal.setText("₱"+ DoubleFormatter.currencyFormat(bookingItem.getPrice()*4));
        }
        Picasso.get().load(bookingItem.getThumbnail()).into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return bookingItemArrayList.size();
    }

    public static class bookingItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName, tvSize, tvSubtotal;
        ImageView ivThumbnail;
        BookingItemAdapter.OnBookingItemListener onBookingItemListener;
        public bookingItemViewHolder(@NonNull View itemView, BookingItemAdapter.OnBookingItemListener onBookingItemListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            this.onBookingItemListener = onBookingItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onBookingItemListener.onBookingItemClick(getAdapterPosition());
        }
    }

    public interface OnBookingItemListener{
        void onBookingItemClick(int position);
    }
}