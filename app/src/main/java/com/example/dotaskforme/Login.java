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
                Intent i = new Intent(Login.this,MainActivity.class);
                startActivity(i);
                finish();
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
                        // Get the Google account details
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userName = user.getDisplayName();
                            String email = user.getEmail();
                            String uid = user.getUid();

                            // Show role selection dialog
                            showRoleSelectionDialog(userName, email, uid);
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showRoleSelectionDialog(String userName, String email, String uid) {
        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Role");

        // Add options for role selection
        String[] roles = {"Student", "Admin"};
        builder.setItems(roles, (dialog, which) -> {
            String selectedRole = roles[which];

            // Store user data and role in Firestore
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

                            // Redirect to Login or Main Activity
                            startActivity(new Intent(this, Login.class)); // Replace LoginActivity with your desired activity
                            finish();
                        } else {
                            Toast.makeText(this, "Error saving role: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Show the dialog
        builder.setCancelable(false); // Prevent dialog dismissal without selecting a role
        builder.show();
    }



}