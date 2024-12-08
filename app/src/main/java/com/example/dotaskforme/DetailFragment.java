package com.example.dotaskforme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {

    private TextView titleTextView, deadlineTextView, phoneTextView, linkTextView, priceTextView;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Initialize views
        titleTextView = view.findViewById(R.id.titleTextView);
        deadlineTextView = view.findViewById(R.id.deadlineTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        linkTextView = view.findViewById(R.id.linkTextView);
        priceTextView = view.findViewById(R.id.priceTextView);

        // Get the arguments (order details)
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title");
            String time = args.getString("time");
            String phone = args.getString("phone");
            String link = args.getString("link");
            String price = args.getString("price");

            // Set the values to the TextViews
            titleTextView.setText("Title: " + title);
            deadlineTextView.setText("Deadline: " + time);
            phoneTextView.setText("Phone: " + phone);
            linkTextView.setText("Link: " + link);
            priceTextView.setText("Price: " + price);
        }

        return view;
    }
}
