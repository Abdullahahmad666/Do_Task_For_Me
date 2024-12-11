package com.example.dotaskforme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private OnOrderClickListener listener;
    private boolean isAdmin;

    // Constructor to pass the list of orders, listener, and isAdmin flag
    public OrderAdapter(List<Order> orders, OnOrderClickListener listener, boolean isAdmin) {
        this.orders = orders;
        this.listener = listener;
        this.isAdmin = isAdmin;
    }

    // ViewHolder class to hold views
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderTitle, orderTime, orderStatusText;
        Spinner orderStatusSpinner;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderTitle = itemView.findViewById(R.id.orderTitle);
            orderTime = itemView.findViewById(R.id.orderTime);
            orderStatusSpinner = itemView.findViewById(R.id.orderStatusSpinner);
            orderStatusText = itemView.findViewById(R.id.orderStatusText);
        }
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (isAdmin) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_card, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_card_user, parent, false);
        }
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.orderTitle.setText(order.getTitle());
        holder.orderTime.setText("Order Time: " + order.getTime());

        if (isAdmin) {
            // Admin Side: Set the spinner with order status
            if (holder.orderStatusSpinner.getAdapter() == null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.orderStatusSpinner.getContext(),
                        android.R.layout.simple_spinner_item, new String[]{"Pending", "Completed", "Cancelled"});
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                holder.orderStatusSpinner.setAdapter(adapter);
            }

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

            // Update the order status when spinner value changes
            // Update the order status when spinner value changes
            holder.orderStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override

                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedStatus = (String) parentView.getItemAtPosition(position);
                    order.setStatus(selectedStatus);  // Update order status in the model

                    // Get the userId (not orderId) for querying
                    String userId = order.getUserId();  // Use the userId to query the document

                    // Reference to Firestore
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    // Use whereEqualTo to match the userId field in Firestore
                    firestore.collection("orders")
                            .whereEqualTo("userId", userId)  // Match the userId field
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // Document found, update the status
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        // Update the document's status
                                        documentSnapshot.getReference()
                                                .update("status", selectedStatus)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Successfully updated status
                                                   // Toast.makeText(parentView.getContext(), "Status updated", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle failure to update status
                                                    Toast.makeText(parentView.getContext(), "Error updating status", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                } else {
                                    // Handle the case where no document was found for the userId
                                    Toast.makeText(parentView.getContext(), "No order found for the specified user", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle Firestore query failure
                                Toast.makeText(parentView.getContext(), "Error finding order", Toast.LENGTH_SHORT).show();
                            });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Handle if needed
                }
            });

        } else {
            // User Side: Display the status in the TextView
            holder.orderStatusText.setText("Status: " + order.getStatus());
        }

        // Handle item click for navigation to detail fragment
        holder.itemView.setTag(order);  // Set the order object as the tag
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order); // Trigger the click listener
            }
        });
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    public interface OnOrderClickListener {
        void onOrderClick(Order clickedOrder);
    }
}
