package com.example.dotaskforme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ContactUsFragment extends Fragment {
    ImageButton btnback;
    Context context;
    EditText etName, etEmail, etMessage;
    Button btnSend;
    DatabaseReference database;


    public ContactUsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnback = view.findViewById(R.id.btn_back);
        etName = view.findViewById(R.id.et_contact_us_name);
        etEmail = view.findViewById(R.id.et_contact_us_email);
        etMessage = view.findViewById(R.id.et_contact_us_message);
        btnSend = view.findViewById(R.id.btn_submit_contact_us);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        database = FirebaseDatabase.getInstance().getReference("Contact Us");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String message = etMessage.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
                    Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save data in Firebase
                String id = database.push().getKey(); // Generate unique ID
                ContactUsModel contact = new ContactUsModel(name, email, message);
                database.child(id).setValue(contact)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Message sent!", Toast.LENGTH_SHORT).show();
                            clearForm();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show();
                        });


            }
        });

    }

    private void clearForm() {
        etName.setText("");
        etEmail.setText("");
        etMessage.setText("");
    }
}



