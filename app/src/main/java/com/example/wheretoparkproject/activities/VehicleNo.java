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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.wheretoparkproject.R;
import com.example.wheretoparkproject.utils.CommonUtils;
import com.example.wheretoparkproject.utils.Constants;

/* loaded from: classes.dex */
public class VehicleNo extends AppCompatActivity {
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
        setContentView(R.layout.vehicle_no);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.title);
    }

    public void about(View v) {
        Intent aboutIntent = new Intent(this, About.class);
        startActivity(aboutIntent);
    }

    public void nextact(View v) {
        CommonUtils utils = new CommonUtils();
        String tempIp = utils.getIpFromSP(this);
        if (tempIp != null && !tempIp.equalsIgnoreCase("") && !tempIp.equalsIgnoreCase(Constants.CONST_DEFAULT_IP)) {
            Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
            ImageButton imageButton = (ImageButton) findViewById(R.id.nextbutton);
            v.startAnimation(animRotate);
            EditText vno = (EditText) findViewById(R.id.vehicleno);
            String vehicleno = vno.getText().toString();
            if (vehicleno.equals("")) {
                Toast.makeText(this, "Please enter your Vehicle No.", 0).show();
                return;
            } else if (vehicleno.equals("ZZ00")) {
                Intent i = new Intent(this, AdminChoice.class);
                startActivity(i);
                return;
            } else {
                Intent intent = new Intent(this, VehicleSelection.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isAdminLoggedIn", false);
                intent.putExtras(bundle);
                startActivity(intent);
                return;
            }
        }
        utils.showAlert(this, "No server ip found. Please update server ip from about/settings", false, false);
    }
}
