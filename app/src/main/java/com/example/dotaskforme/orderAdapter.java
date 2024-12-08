package com.example.dotaskforme;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;

    // Constructor to pass the list of orders
    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    // ViewHolder class to hold views
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderTitle, orderTime, orderStatusLabel;
        Spinner orderStatusSpinner;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderTitle = itemView.findViewById(R.id.orderTitle);
            orderTime = itemView.findViewById(R.id.orderTime);
            orderStatusLabel = itemView.findViewById(R.id.orderStatusLabel);
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


    @Override    // Bin
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
            // Log statement for debugging
            Log.d("OrderAdapter", "Navigating to DetailFragment...");

            // Create the bundle and pass the order data
            Bundle bundle = new Bundle();
            bundle.putString("title", order.getTitle());
            bundle.putString("time", order.getTime());
            bundle.putString("phone", order.getPhone());
            bundle.putString("link", order.getLink());
            bundle.putString("price", order.getPrice());

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);

            // Check if the context is an instance of Admin
            if (v.getContext() instanceof Admin) {
                Admin adminActivity = (Admin) v.getContext();
                adminActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // Show a Toast or log the issue
                Toast.makeText(v.getContext(), "Unable to navigate to DetailFragment.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    // Return the total number of orders
    @Override
    public int getItemCount() {
        return orders.size();
    }
}
