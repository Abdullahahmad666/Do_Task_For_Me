package com.example.dotaskforme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    TextView gotosignin,etemail,etpassword,etconfirmpassword,etfirst,etlast;
    ImageView ivbackarrow;
    Button btnsignup;
    RadioGroup rgoption;
    FirebaseAuth auth;
    RadioButton studentRadioButton,adminRadioButton;

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
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUp.this,Login.class);
                startActivity(i);
                finish();
            }
        });
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
    }

}