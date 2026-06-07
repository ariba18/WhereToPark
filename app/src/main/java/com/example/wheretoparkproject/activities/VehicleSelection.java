package com.example.wheretoparkproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import com.example.wheretoparkproject.R;

/* loaded from: classes.dex */
public class VehicleSelection extends AppCompatActivity {
    private boolean isAdminLoggedIn;
    private String currentUsername;

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityHoneycomb, android.app.Activity, android.view.LayoutInflater.Factory2
    public /* bridge */ /* synthetic */ View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(view, str, context, attributeSet);
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity, android.view.LayoutInflater.Factory
    public /* bridge */ /* synthetic */ View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(str, context, attributeSet);
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_selection);
        Bundle b = getIntent().getExtras();
        this.isAdminLoggedIn = (b != null && b.containsKey("isAdminLoggedIn")) ? b.getBoolean("isAdminLoggedIn") : false;
        this.currentUsername = (b != null && b.containsKey("username")) ? b.getString("username") : null;
        ActionBar actionBar1 = getSupportActionBar();
        if (actionBar1 != null) {
            actionBar1.setDisplayShowHomeEnabled(true);
            actionBar1.setTitle("Where to park");
            actionBar1.setIcon(R.drawable.title);
        }

        Button carButton = findViewById(R.id.car_button);
        Button bikeButton = findViewById(R.id.bike_button);
        carButton.setOnClickListener(v -> car_select(v));
        bikeButton.setOnClickListener(v -> bike_select(v));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vehicle_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void car_select(View view) {
        Animation animscale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        view.startAnimation(animscale);
        Intent carsection = new Intent(this, CarSlotSection.class);
        carsection.putExtra("isAdminLoggedIn", this.isAdminLoggedIn);
        if (this.currentUsername != null) carsection.putExtra("username", this.currentUsername);
        startActivity(carsection);
    }

    public void bike_select(View view) {
        Animation animscale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        view.startAnimation(animscale);
        Intent bikesection = new Intent(this, BikeSlotSection.class);
        bikesection.putExtra("isAdminLoggedIn", this.isAdminLoggedIn);
        if (this.currentUsername != null) bikesection.putExtra("username", this.currentUsername);
        startActivity(bikesection);
    }
}
