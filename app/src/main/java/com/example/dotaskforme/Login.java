package com.example.dotaskforme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    TextView gotosignup, forgot_password;
    Button signin;
    FirebaseAuth auth;
   TextInputEditText etemail, etpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        // Signup Button Click Listener
        gotosignup.setOnClickListener(view -> {
            Intent i = new Intent(Login.this, SignUp.class);
            startActivity(i);
            finish();
        });

        // Signin Button Click Listener
        signin.setOnClickListener(view -> {
            String email = etemail.getText().toString().trim();
            String password = etpassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            ProgressDialog progressDialog = new ProgressDialog(Login.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            // Ensure auth is initialized
            if (auth == null) {
                Toast.makeText(Login.this, "Authentication service not initialized.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss(); // Always dismiss ProgressDialog

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("users")
                                        .document(uid)
                                        .get()
                                        .addOnCompleteListener(roleTask -> {
                                            if (roleTask.isSuccessful()) {
                                                DocumentSnapshot document = roleTask.getResult();
                                                if (document != null && document.exists()) {
                                                    String role = document.getString("role");
                                                    navigateBasedOnRole(role);
                                                } else {
                                                    Toast.makeText(Login.this, "User role not found in the database.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                String error = roleTask.getException() != null
                                                        ? roleTask.getException().getMessage()
                                                        : "Unknown error";
                                                Toast.makeText(Login.this, "Failed to fetch role: " + error, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(Login.this, "Failed to retrieve user information.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Unknown error";
                            Toast.makeText(Login.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Forgot Password Button Click Listener
        forgot_password.setOnClickListener(view -> {
            EditText etEmail = new EditText(view.getContext());
            AlertDialog.Builder forgetPasswordDialog = new AlertDialog.Builder(view.getContext())
                    .setTitle("Forget Password Email...")
                    .setView(etEmail)
                    .setPositiveButton("Send", (dialogInterface, i) -> {
                        String email = etEmail.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            etEmail.setError("Give valid email address");
                        } else {
                            ProgressDialog progressDialog = new ProgressDialog(Login.this);
                            progressDialog.setMessage("Sending Email...");
                            progressDialog.show();
                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Login.this, "Check your inbox...", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    });
                        }
                    })
                    .setNegativeButton("Cancel", null);
            forgetPasswordDialog.show();
        });
    }

    private void navigateBasedOnRole(String role) {
        if ("Student".equals(role)) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("targetFragment", "HomeFragment"); // Example fragment for Student
            startActivity(intent);
        } else if ("Admin".equals(role)) {
            Intent intent = new Intent(Login.this, Admin.class); // Navigate directly to Admin activity
            startActivity(intent);
        } else {
            Toast.makeText(Login.this, "Invalid role detected!", Toast.LENGTH_SHORT).show();
        }
    }


    public void init() {
        gotosignup = findViewById(R.id.gotosignup);
        signin = findViewById(R.id.loginbtn);
        forgot_password = findViewById(R.id.forgot_password);
        auth = FirebaseAuth.getInstance();
        etemail = findViewById(R.id.etemail);
        etpassword = findViewById(R.id.etpassword);
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment); // Replace with your container ID
        fragmentTransaction.commit();
    }
}
