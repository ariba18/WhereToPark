package com.example.wheretoparkproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import com.example.wheretoparkproject.R;

/* loaded from: classes.dex */
public class AdminChoice extends AppCompatActivity {
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
        setContentView(R.layout.admin_choice);
        ActionBar actionBar1 = getSupportActionBar();
        actionBar1.setDisplayShowHomeEnabled(true);
        actionBar1.setIcon(R.drawable.title);
    }

    public void vehicle_registar(View view) {
        Animation animscale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        Button button = (Button) findViewById(R.id.register_button);
        view.startAnimation(animscale);
        Intent register = new Intent(this, AdminVehicleRegistar.class);
        startActivity(register);
        finish();
    }

    public void slot_registar(View view) {
        Animation animscale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        Button button = (Button) findViewById(R.id.slot_button);
        view.startAnimation(animscale);
        Intent vehicleselection = new Intent(this, VehicleSelection.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isAdminLoggedIn", true);
        vehicleselection.putExtras(bundle);
        startActivity(vehicleselection);
        finish();
    }
}
