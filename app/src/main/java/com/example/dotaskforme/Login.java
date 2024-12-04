package com.example.dotaskforme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    TextView gotosignup,forgot_password;
    private static final int RC_SIGN_IN = 100;
    Button signin;
    FirebaseAuth auth;
    FirebaseUser user;
    GoogleSignInClient signInClient;
    ImageView ivgoogle;
    EditText etemail,etpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id)) // Use web client ID from google-services.json
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, gso);

        // Google Sign-In ImageView Click Listener
        ivgoogle.setOnClickListener(view -> signInWithGoogle());
        gotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this,SignUp.class);
                startActivity(i);
                finish();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etemail.getText().toString().trim();
                String password = etpassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("Logging in...");
                progressDialog.show();

                // Sign in with Firebase Authentication
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                // Get the current user's ID
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();

                                    // Fetch role from Firestore
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("users")
                                            .document(uid)
                                            .get()
                                            .addOnCompleteListener(roleTask -> {
                                                if (roleTask.isSuccessful()) {
                                                    DocumentSnapshot document = roleTask.getResult();
                                                    if (document.exists()) {
                                                        String role = document.getString("role");

                                                        // Navigate based on role
                                                        if ("Student".equals(role)) {
                                                            // Navigate to HomeFragment
                                                            navigateToFragment(new HomeFragment());
                                                        } else if ("Admin".equals(role)) {
                                                            // Navigate to ManageOrdersFragment
                                                            navigateToFragment(new ManageOrderFragment());
                                                        } else {
                                                            Toast.makeText(Login.this, "Invalid role detected!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(Login.this, "Role not found for the user!", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(Login.this, "Failed to fetch role: " + roleTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        forgot_password.setOnClickListener(view -> {
            EditText etEmail = new EditText(view.getContext());
            AlertDialog.Builder forgetPasswordDialog = new AlertDialog.Builder(view.getContext())
                    .setTitle("Forget Password Email...")
                    .setView(etEmail)
                    .setPositiveButton("Send", (dialogInterface, i) -> {
                        String email = etEmail.getText().toString().trim();
                        if(TextUtils.isEmpty(email)) {
                            etEmail.setError("Give valid email address");
                        }
                        else
                        {
                            ProgressDialog progressDialog = new ProgressDialog(Login.this);
                            progressDialog.show();
                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(task -> {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(Login.this, "Check your inbox...", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
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
    public void init()
    {
        gotosignup = findViewById(R.id.gotosignup);
        signin = findViewById(R.id.loginbtn);
        forgot_password = findViewById(R.id.forgot_password);
        ivgoogle = findViewById(R.id.ivgoogle);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        etemail = findViewById(R.id.etemail);
        etpassword = findViewById(R.id.etpassword);
    }
    private void signInWithGoogle() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e("GoogleSignIn", "Google sign-in failed", e);
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();

                            // Fetch user role from Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users")
                                    .document(uid)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot document = task1.getResult();
                                            if (document.exists()) {
                                                String role = document.getString("role");

                                                if ("Student".equals(role)) {
                                                    // Navigate to HomeFragment
                                                    navigateToFragment(new HomeFragment());
                                                } else if ("Admin".equals(role)) {
                                                    // Navigate to AdminFragment
                                                    navigateToFragment(new ManageOrderFragment());
                                                } else {
                                                    Toast.makeText(this, "Invalid role!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(this, "Role not found. Please select role.", Toast.LENGTH_SHORT).show();
                                                showRoleSelectionDialog(user.getDisplayName(), user.getEmail(), uid);
                                            }
                                        } else {
                                            Toast.makeText(this, "Failed to fetch role: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void navigateToFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment); // Replace with your container ID
        fragmentTransaction.commit();
    }


    private void showRoleSelectionDialog(String userName, String email, String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Role");

        String[] roles = {"Student", "Admin"};
        builder.setItems(roles, (dialog, which) -> {
            String selectedRole = roles[which];

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("name", userName);
            userData.put("email", email);
            userData.put("role", selectedRole);

            db.collection("users")
                    .document(uid)
                    .set(userData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Role saved successfully!", Toast.LENGTH_SHORT).show();

                            // Navigate based on role
                            if ("Student".equals(selectedRole)) {
                                navigateToFragment(new HomeFragment());
                            } else if ("Admin".equals(selectedRole)) {
                                navigateToFragment(new ManageOrderFragment());
                            }
                        } else {
                            Toast.makeText(this, "Error saving role: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setCancelable(false);
        builder.show();
    }



}