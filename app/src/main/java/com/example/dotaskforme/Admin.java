package com.example.dotaskforme;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FrameLayout fragmentContainer;
    private List<Order> orders;
    private OrderAdapter orderAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration ordersListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        fragmentContainer = findViewById(R.id.fragment_container);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orders = new ArrayList<>();
        orderAdapter = new OrderAdapter(orders);
        recyclerView.setAdapter(orderAdapter);

        // Fetch orders from Firestore
        fetchOrdersFromFirestore();
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

    // Handle item click to show the DetailFragment
    public void onOrderClick(View view) {
        // Hide the RecyclerView and show the fragment container
        recyclerView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        // Get the order data from the view holder (or use the clicked order directly)
        Order clickedOrder = (Order) view.getTag();

        // Sample navigation to the DetailFragment
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", clickedOrder.getTitle());
        bundle.putString("time", clickedOrder.getTime());
        bundle.putString("phone", clickedOrder.getPhone()); // Assuming your Order model has these fields
        bundle.putString("link", clickedOrder.getLink());
        bundle.putString("price", clickedOrder.getPrice());
        detailFragment.setArguments(bundle);

        // Begin the fragment transaction
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
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
