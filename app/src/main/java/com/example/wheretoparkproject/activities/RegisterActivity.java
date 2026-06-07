package com.example.wheretoparkproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wheretoparkproject.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic sign up: insert username + password into Firebase Realtime DB (users table).
 * No email or OTP. On success go to VehicleSelection.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String USERS_NODE = "users";

    private DatabaseReference usersRef;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usersRef = FirebaseDatabase.getInstance().getReference(USERS_NODE);

        usernameInput = findViewById(R.id.register_username);
        passwordInput = findViewById(R.id.register_password);
        confirmPasswordInput = findViewById(R.id.register_confirm_password);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> doRegister());
        findViewById(R.id.register_login_link).setOnClickListener(v -> finish());
    }

    /** Firebase keys cannot contain . $ # [ ] / — we use safe username as key. */
    private static String safeUsernameKey(String username) {
        if (username == null) return "";
        return username.trim().replaceAll("[.$#\\[\\]/\\\\]", "_");
    }

    private void doRegister() {
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";
        String confirm = confirmPasswordInput.getText() != null ? confirmPasswordInput.getText().toString() : "";

        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Enter username");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Enter password");
            return;
        }
        if (!password.equals(confirm)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        String key = safeUsernameKey(username);
        registerButton.setEnabled(false);

        usersRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    registerButton.setEnabled(true);
                    usernameInput.setError("Username already taken");
                    return;
                }
                Map<String, Object> user = new HashMap<>();
                user.put("username", username);
                user.put("password", password);
                usersRef.child(key).setValue(user)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Sign up success: " + username);
                            goToVehicleSelection(username);
                        })
                        .addOnFailureListener(e -> {
                            registerButton.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                registerButton.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToVehicleSelection(String username) {
        Intent i = new Intent(this, VehicleSelection.class);
        i.putExtra("username", username);
        startActivity(i);
        finish();
    }
}
