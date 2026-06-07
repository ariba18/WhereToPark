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

/**
 * Basic login: username + password stored in Firebase Realtime DB (users table).
 * On sign in, check username/password and go to VehicleSelection.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String USERS_NODE = "users";

    private DatabaseReference usersRef;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usersRef = FirebaseDatabase.getInstance().getReference(USERS_NODE);

        usernameInput = findViewById(R.id.login_username);
        passwordInput = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> doLogin());
        findViewById(R.id.login_register_link).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    /** Firebase keys cannot contain . $ # [ ] / — we use safe username as key. */
    private static String safeUsernameKey(String username) {
        if (username == null) return "";
        return username.trim().replaceAll("[.$#\\[\\]/\\\\]", "_");
    }

    private void doLogin() {
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";

        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Enter username");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Enter password");
            return;
        }

        String key = safeUsernameKey(username);
        loginButton.setEnabled(false);

        usersRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loginButton.setEnabled(true);
                if (!snapshot.exists()) {
                    Toast.makeText(LoginActivity.this, "User not found. Sign up first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Object p = snapshot.child("password").getValue();
                String storedPassword = p != null ? p.toString() : "";
                if (!storedPassword.equals(password)) {
                    Toast.makeText(LoginActivity.this, "Wrong password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "Sign in success: " + username);
                goToVehicleSelection(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loginButton.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
