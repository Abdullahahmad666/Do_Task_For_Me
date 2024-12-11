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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        orderAdapter = new OrderAdapter(orders,null,false); // Pass orders to the adapter
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
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Log Out...");
                progressDialog.show();

                // Sign out from Firebase
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                // Check if the user is signed in with Google
                GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
                if (googleSignInAccount != null) {
                    // User is signed in with Google, sign out from Google
                    GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Handle Google sign-out success or failure
                                    if (task.isSuccessful()) {
                                        Log.d("SignOut", "Google sign-out successful");
                                    } else {
                                        Log.e("SignOut", "Google sign-out failed", task.getException());
                                    }

                                    // Proceed with Firebase logout after Google sign-out
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show();

                                    // Navigate to Login Activity
                                    Intent i = new Intent(getActivity(), Login.class);
                                    startActivity(i);
                                    if (getActivity() != null) {
                                        getActivity().finish();
                                    }
                                }
                            });
                } else {
                    // If not signed in with Google, just proceed with Firebase logout
                    progressDialog.dismiss();
                    Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show();

                    // Navigate to Login Activity
                    Intent i = new Intent(getActivity(), Login.class);
                    startActivity(i);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

    }

    // Fetch orders from Firestore (or other database)
    private void fetchOrdersFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();  // Get the current user's UID

            // Query Firestore to fetch orders where userId matches the current user's ID
            firestore.collection("orders")
                    .whereEqualTo("userId", userId)  // Filter orders by userId
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            // Convert the fetched documents into a list of Order objects
                            List<Order> fetchedOrders = queryDocumentSnapshots.toObjects(Order.class);

                            // Clear the previous orders and add the new ones
                            orders.clear();
                            orders.addAll(fetchedOrders);

                            // Notify the adapter to refresh the data
                            orderAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "No orders found for the current user.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to fetch orders
                        Toast.makeText(context, "Error while fetching orders.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Handle the case where the user is not logged in
            Toast.makeText(context, "You must be logged in to fetch orders.", Toast.LENGTH_SHORT).show();
        }
    }

}
