package com.example.dotaskforme;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ManageOrderFragment extends Fragment {
    Context context;
    TextView order_now, logout;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    RecyclerView orderRecyclerView;
    OrderAdapter orderAdapter;
    List<Order> orders; // List to store orders

    public ManageOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        order_now = view.findViewById(R.id.order_now);
        logout = view.findViewById(R.id.logout);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and Order List
        orderRecyclerView = view.findViewById(R.id.order_list);
        orders = new ArrayList<>();
        orderAdapter = new OrderAdapter(orders,null); // Pass orders to the adapter
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(context)); // Set linear layout manager
        orderRecyclerView.setAdapter(orderAdapter); // Set adapter to RecyclerView

        // Fetch Orders from Firestore (or your database)
        fetchOrdersFromFirestore();

        // Handle "Order Now" button click
        order_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceOrder PlaceOrder = new PlaceOrder();

                // Use FragmentManager to replace the current fragment
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, PlaceOrder) // fragment_container is your container ID
                        .addToBackStack(null) // Optional: Allows the user to press back and return to the previous fragment
                        .commit();
            }
        });

        // Handle logout button click
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Log Out...");
                progressDialog.show();
                auth.signOut();
                progressDialog.dismiss();
                Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), Login.class);
                startActivity(i);
                if (getActivity() != null) {
                    getActivity().finish();
                }
                progressDialog.dismiss();
            }
        });
    }

    // Fetch orders from Firestore (or other database)
    private void fetchOrdersFromFirestore() {
        firestore.collection("orders")  // Collection name where your orders are stored
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<Order> fetchedOrders = queryDocumentSnapshots.toObjects(Order.class);
                        orders.clear();
                        orders.addAll(fetchedOrders);
                        orderAdapter.notifyDataSetChanged(); // Notify adapter to update the RecyclerView
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error while fetching...", Toast.LENGTH_SHORT).show();
                });
    }
}
