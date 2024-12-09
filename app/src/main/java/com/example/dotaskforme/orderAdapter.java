package com.example.dotaskforme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private OnOrderClickListener listener; // Declare listener

    // Constructor to pass the list of orders and listener
    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    // ViewHolder class to hold views
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderTitle, orderTime;
        Spinner orderStatusSpinner;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderTitle = itemView.findViewById(R.id.orderTitle);
            orderTime = itemView.findViewById(R.id.orderTime);
            orderStatusSpinner = itemView.findViewById(R.id.orderStatusSpinner);
        }
    }

    // Inflate the card layout and return the ViewHolder
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.orderTitle.setText(order.getTitle());
        holder.orderTime.setText("Order Time: " + order.getTime());

        // Set the adapter only once to avoid re-initializing it every time
        if (holder.orderStatusSpinner.getAdapter() == null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    holder.orderStatusSpinner.getContext(),
                    android.R.layout.simple_spinner_item,
                    new String[]{"Pending", "Completed", "Cancelled"}
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.orderStatusSpinner.setAdapter(adapter);
        }

        // Set the correct spinner selection based on order status
        String[] statusOptions = {"Pending", "Completed", "Cancelled"};
        int statusIndex = -1;
        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equals(order.getStatus())) {
                statusIndex = i;
                break;
            }
        }

        if (statusIndex != -1) {
            holder.orderStatusSpinner.setSelection(statusIndex);
        }

        // Handle item click for navigation to detail fragment
        holder.itemView.setTag(order);  // Set the order object as the tag
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order); // Trigger the click listener
            }
        });
    }

    // Return the total number of orders
    @Override
    public int getItemCount() {
        return orders.size();
    }

    // Interface to handle order item click
    public interface OnOrderClickListener {
        void onOrderClick(Order clickedOrder);
    }
}
