package com.example.dotaskforme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    TextView gotosignin,etemail,etpassword,etconfirmpassword,etfirst,etlast;
    ImageView ivbackarrow,ivgoogle;
    Button btnsignup;
    RadioGroup rgoption;
    FirebaseAuth auth;
    RadioButton studentRadioButton,adminRadioButton;
    private static final int RC_SIGN_IN = 100; // Request code for Google Sign-In
    private GoogleSignInClient signInClient;  // Google Sign-In Client
    private FirebaseUser user;                // Current Firebase User


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();


        gotosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUp.this,Login.class);
                startActivity(i);
                finish();
            }
        });
        rgoption.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_student) {
                // Perform actions for "Student"
                Toast.makeText(this, "Student role selected", Toast.LENGTH_SHORT).show();
                // You can add specific logic here for Student
            } else if (checkedId == R.id.rb_admin) {
                // Perform actions for "Admin"
                Toast.makeText(this, "Admin role selected", Toast.LENGTH_SHORT).show();
                // You can add specific logic here for Admin
            }
        });

        ivbackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUp.this,Login.class);
                startActivity(i);
                finish();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id)) // Web client ID from google-services.json
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, gso);
        ivgoogle.setOnClickListener(view -> signInWithGoogle());


        btnsignup.setOnClickListener(view -> {
            String firstName = etfirst.getText().toString().trim();
            String lastName = etlast.getText().toString().trim();
            String email = etemail.getText().toString().trim();
            String password = etpassword.getText().toString();
            String confirmPassword = etconfirmpassword.getText().toString();
            String role;

            // Get selected radio button value
            if (studentRadioButton.isChecked()) {
                role = "Student";
            } else if (adminRadioButton.isChecked()) {
                role = "Admin";
            } else {
                role = "";
            }

            // Validation checks
            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email)
                    || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(SignUp.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(role)) {
                Toast.makeText(SignUp.this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();

            // Sign up with Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            String userID = auth.getCurrentUser().getUid();

                            // Store user data in Firestore
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("firstName", firstName);
                            data.put("lastName", lastName);
                            data.put("email", email);
                            data.put("role", role);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users")
                                    .document(userID)
                                    .set(data)
                                    .addOnCompleteListener(storeTask -> {
                                        if (storeTask.isSuccessful()) {
                                            Toast.makeText(SignUp.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUp.this, Login.class));
                                            finish();
                                        } else {
                                            Toast.makeText(SignUp.this, "Firestore error: " + storeTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUp.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });



    }
    public void init()
    {
        etfirst = findViewById(R.id.etfirst);
        etlast = findViewById(R.id.etlast);
        etemail = findViewById(R.id.etemail);
        etpassword = findViewById(R.id.etpassword);
        etconfirmpassword = findViewById(R.id.etconfirm);
        rgoption = findViewById(R.id.role_radio_group);
        gotosignin = findViewById(R.id.gotosignin);
        ivbackarrow = findViewById(R.id.ivbackarrow);
        btnsignup = findViewById(R.id.btnsignup);
        studentRadioButton = findViewById(R.id.rb_student);
        adminRadioButton = findViewById(R.id.rb_admin);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ivgoogle = findViewById(R.id.ivgoogle);
    }
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
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    Toast.makeText(this, "Google sign-in canceled", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                if (e.getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    Toast.makeText(this, "Google sign-in canceled by user.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("GoogleSignIn", "Google sign-in failed", e);
                    Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
                            String email = user.getEmail();
                            String userName = user.getDisplayName();

                            // Show role selection dialog for new users
                            showRoleSelectionDialog(userName, email, uid);
                        }
                    } else {
                        Log.e("FirebaseAuth", "Google authentication failed", task.getException());
                        Toast.makeText(this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

            // Save user data in Firestore
            db.collection("users")
                    .document(uid)
                    .set(userData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Role saved successfully!", Toast.LENGTH_SHORT).show();

                            // Navigate based on role
                            if ("Student".equals(selectedRole)) {
                                // Navigate to Student Home Activity/Fragment
                                startActivity(new Intent(SignUp.this, MainActivity.class));
                            } else if ("Admin".equals(selectedRole)) {
                                // Navigate to Admin Home Activity/Fragment
                                navigateToFragment(new ManageOrderFragment());
                            }
                            finish(); // Close Sign-Up Activity
                        } else {
                            Toast.makeText(this, "Error saving role: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setCancelable(false);
        builder.show();
    }
    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment); // Replace with your container ID
        fragmentTransaction.commit();
    }



}