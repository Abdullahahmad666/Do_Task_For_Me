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

        signInClient = GoogleSignInClient.(this, gso);

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
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(this, "Signed in as " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        // Redirect to Main Activity
                        startActivity(new Intent(Login.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}