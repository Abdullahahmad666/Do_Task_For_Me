package com.example.dotaskforme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {

    private RecyclerView recyclerView;
    private FrameLayout fragmentContainer;
    private List<Order> orders;
    private OrderAdapter orderAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration ordersListener;
    private TextView logoutText;
    private FirebaseAuth auth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your); // Ensure this is your correct layout

        // Initialize views
        recyclerView = findViewById(R.id.order_list);  // updated to match XML id
        fragmentContainer = findViewById(R.id.fragment_container); // updated to match XML i
        logoutText = findViewById(R.id.logout); // updated to match XML id

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orders = new ArrayList<>();
        orderAdapter = new OrderAdapter(orders, this); // Pass the activity as the listener
        recyclerView.setAdapter(orderAdapter);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch orders from Firestore
        fetchOrdersFromFirestore();

        // Handle "Logout" button click
        logoutText.setOnClickListener(v -> {
            auth.signOut();

            Toast.makeText(Admin.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Admin.this,Login.class);
            startActivity(i);
            finish();
        });
    }

    private void fetchOrdersFromFirestore() {
        // Reference to "orders" collection
        CollectionReference ordersCollection = db.collection("orders");

        // Fetch orders with real-time listener
        ordersListener = ordersCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(Admin.this, "Failed to fetch orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Clear the list to avoid duplicates
            orders.clear();

            // Loop through the documents and add them to the list
            if (queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Order order = document.toObject(Order.class);
                    orders.add(order);
                }

                // Notify the adapter that data has changed
                orderAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onOrderClick(Order clickedOrder) {
        // Hide the RecyclerView and any other components
        recyclerView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        // Create and set up the DetailFragment
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", clickedOrder.getTitle());
        bundle.putString("time", clickedOrder.getTime());
        bundle.putString("phone", clickedOrder.getPhone()); // Assuming your Order model has these fields
        bundle.putString("link", clickedOrder.getLink());
        bundle.putString("price", clickedOrder.getPrice());
        detailFragment.setArguments(bundle);

        // Begin the fragment transaction to display the DetailFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null) // If you want to allow the user to navigate back
                .commit();
    }

    @Override
    public void onBackPressed() {
        // When back is pressed, show the RecyclerView again and hide the fragment container
        recyclerView.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);

        super.onBackPressed(); // Continue with the back stack operation
    }
}