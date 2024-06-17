package com.caternation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caternation.Modules.DoubleFormatter;
import com.caternation.Objects.Booking;
import com.caternation.Objects.BookingItem;
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
import java.util.Objects;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.bookingViewHolder>{

    private static final FirebaseDatabase caterNationDb = FirebaseDatabase.getInstance();
    private static final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference bookingItemsRef;

    Context context;
    static ArrayList<Booking> bookingArrayList;
    static ArrayList<BookingItem> bookingItemsArrayList;
    private BookingAdapter.OnBookingListener mOnBookingListener;

    public BookingAdapter(Context context, ArrayList<Booking> bookingArrayList, BookingAdapter.OnBookingListener onBookingListener) {
        this.context = context;
        this.bookingArrayList = bookingArrayList;
        this.mOnBookingListener = onBookingListener;
    }

    @NonNull
    @Override
    public BookingAdapter.bookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_booking, parent, false);
        return new BookingAdapter.bookingViewHolder(view, mOnBookingListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.bookingViewHolder holder, int position) {
        boolean isAdmin = false;
        isAdmin = currentUser.getEmail().toLowerCase().contains("admin");

        Booking booking = bookingArrayList.get(position);
        long timestamp = booking.getTimestamp();
        long startDate = booking.getStartDate();
        long startTime = booking.getStartTime();
        long endDate = booking.getEndDate();
        String status = booking.getStatus();
        String customer = booking.getFirstName() + " " + booking.getLastName();
        String addressBuilding = booking.getAddressBuilding();
        String addressBarangay = booking.getAddressBarangay();
        String addressCity = booking.getAddressCity();
        double total = booking.getTotal();

        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
        holder.tvTimestamp.setText("Booked: "+sdfDate.format(timestamp) + " at " + sdfTime.format(timestamp));
        holder.tvStartDate.setText("Start Date: "+sdfDate.format(startDate));
        holder.tvStartTime.setText("Start Time: "+sdfTime.format(startTime));
        holder.tvEndDate.setText("End Date: "+sdfDate.format(endDate));

        holder.tvStatus.setText(status);
        holder.tvCustomer.setText(customer);
        StringBuilder sbAddress = new StringBuilder();
        sbAddress.append(addressBuilding).append(", ");
        sbAddress.append(addressBarangay).append(", ");
        sbAddress.append(addressCity);
        holder.tvAddress.setText(sbAddress);
        holder.tvTotal.setText("₱"+ DoubleFormatter.currencyFormat(total));

        StringBuilder sbPackage = new StringBuilder();
        bookingItemsArrayList = new ArrayList<>();
        if (isAdmin) {
            bookingItemsRef = caterNationDb.getReference("bookingItems").child(booking.getUid());
        }
        else {
            bookingItemsRef = caterNationDb.getReference("user_"+ Objects.requireNonNull(currentUser).getUid()+"_bookingItems").child(booking.getUid());
        }
        bookingItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingItemsArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BookingItem bookingItem = dataSnapshot.getValue(BookingItem.class);
                    sbPackage.append(bookingItem.getName()).append(" ");
                    sbPackage.append(bookingItem.getSize() + " pax").append(", ");
                }
                sbPackage.delete(sbPackage.length()-2, sbPackage.length()-1); // delete last ","
                holder.tvPackage.setText(sbPackage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingArrayList.size();
    }

    public static class bookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTimestamp, tvStatus, tvCustomer, tvPackage, tvAddress, tvTotal, tvStartDate, tvStartTime, tvEndDate;
        BookingAdapter.OnBookingListener onBookingListener;
        public bookingViewHolder(@NonNull View itemView, BookingAdapter.OnBookingListener onBookingListener) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvPackage = itemView.findViewById(R.id.tvPackage);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            this.onBookingListener = onBookingListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onBookingListener.onBookingClick(getAdapterPosition());
        }
    }

    public interface OnBookingListener{
        void onBookingClick(int position);
    }
}
