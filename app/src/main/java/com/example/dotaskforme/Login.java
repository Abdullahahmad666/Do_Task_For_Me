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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

    private TextView gotosignup, forgotPassword;
    private Button signin;
    private ImageView ivGoogle;
    private EditText etEmail, etPassword;

    private FirebaseAuth auth;
    private GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        initUI();
        configureGoogleSignIn();

        ivGoogle.setOnClickListener(view -> signInWithGoogle());
        gotosignup.setOnClickListener(view -> navigateToSignUp());
        signin.setOnClickListener(view -> handleEmailSignIn());
        forgotPassword.setOnClickListener(view -> showForgotPasswordDialog());
    }

    // Initialize UI components
    private void initUI() {
        gotosignup = findViewById(R.id.gotosignup);
        signin = findViewById(R.id.loginbtn);
        forgotPassword = findViewById(R.id.forgot_password);
        ivGoogle = findViewById(R.id.ivgoogle);
        etEmail = findViewById(R.id.etemail);
        etPassword = findViewById(R.id.etpassword);
        auth = FirebaseAuth.getInstance();
    }

    // Configure Google Sign-In options
    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id)) // Web client ID from google-services.json
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);
    }

    // Handle email/password sign-in
    private void handleEmailSignIn() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showToast("Please enter email and password");
            return;
        }

        ProgressDialog progressDialog = showProgressDialog("Logging in...");
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        fetchUserRoleAndNavigate(auth.getCurrentUser());
                    } else {
                        showToast("Login failed: " + task.getException().getMessage());
                    }
                });
    }

    // Start Google Sign-In process
    private void signInWithGoogle() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                authenticateWithGoogle(account);
            } catch (ApiException e) {
                Log.e("GoogleSignIn", "Google sign-in failed", e);
                showToast("Google Sign-In failed");
            }
        }
    }

    // Authenticate with Google and fetch role
    private void authenticateWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        fetchUserRoleAndNavigate(auth.getCurrentUser());
                    } else {
                        showToast("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    // Fetch user role from Firestore and navigate accordingly
    private void fetchUserRoleAndNavigate(FirebaseUser user) {
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            navigateBasedOnRole(role);
                        } else {
                            showRoleSelectionDialog(user.getDisplayName(), user.getEmail(), user.getUid());
                        }
                    } else {
                        showToast("Failed to fetch role: " + task.getException().getMessage());
                    }
                });
    }

    // Navigate to appropriate fragment based on role
    private void navigateBasedOnRole(String role) {
        if ("Student".equals(role)) {
            navigateToFragment(new HomeFragment());
        } else if ("Admin".equals(role)) {
            navigateToFragment(new ManageOrderFragment());
        } else {
            showToast("Invalid role detected!");
        }
    }

    // Show role selection dialog
    private void showRoleSelectionDialog(String userName, String email, String uid) {
        String[] roles = {"Student", "Admin"};
        new AlertDialog.Builder(this)
                .setTitle("Select Your Role")
                .setItems(roles, (dialog, which) -> saveUserRoleAndNavigate(userName, email, uid, roles[which]))
                .setCancelable(false)
                .show();
    }

    // Save user role to Firestore and navigate
    private void saveUserRoleAndNavigate(String userName, String email, String uid, String role) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", userName);
        userData.put("email", email);
        userData.put("role", role);

        db.collection("users")
                .document(uid)
                .set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Role saved successfully!");
                        navigateBasedOnRole(role);
                    } else {
                        showToast("Error saving role: " + task.getException().getMessage());
                    }
                });
    }

    // Navigate to a specific fragment
    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // Use your fragment container ID
        transaction.commit();
    }

    // Show forgot password dialog
    private void showForgotPasswordDialog() {
        EditText etEmail = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Forgot Password?")
                .setView(etEmail)
                .setPositiveButton("Send", (dialog, which) -> sendPasswordResetEmail(etEmail.getText().toString().trim()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Send password reset email
    private void sendPasswordResetEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            showToast("Please provide a valid email address");
            return;
        }

        ProgressDialog progressDialog = showProgressDialog("Sending password reset email...");
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        showToast("Check your inbox for reset instructions.");
                    } else {
                        showToast("Error: " + task.getException().getMessage());
                    }
                });
    }

    // Show a toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Show a progress dialog
    private ProgressDialog showProgressDialog(String message) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.show();
        return progressDialog;
    }

    // Navigate to the SignUp activity
    private void navigateToSignUp() {
        startActivity(new Intent(this, SignUp.class));
        finish();
    }
}
