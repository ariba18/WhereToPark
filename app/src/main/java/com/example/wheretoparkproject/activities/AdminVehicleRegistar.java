package com.example.wheretoparkproject.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wheretoparkproject.R;

/* loaded from: classes.dex */
public class AdminVehicleRegistar extends AppCompatActivity {
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
        setContentView(R.layout.admin_vehicle_registar);
        ActionBar actionBar1 = getSupportActionBar();
        actionBar1.setDisplayShowHomeEnabled(true);
        actionBar1.setIcon(R.drawable.title);
    }

    public void confirm(View view) {
        Animation animrotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        Button button = (Button) findViewById(R.id.confirmbutton);
        view.startAnimation(animrotate);
        EditText vnum = (EditText) findViewById(R.id.vehicleno_textedit);
        String vehicleno_textedit = vnum.getText().toString();
        EditText checkin = (EditText) findViewById(R.id.checkin_textedit);
        String checkin_textedit = checkin.getText().toString();
        EditText checkout = (EditText) findViewById(R.id.checkout_textedit);
        checkout.getText().toString();
        if (vehicleno_textedit.equals("") || checkin_textedit.equals("")) {
            Toast.makeText(this, "Please enter correct Vehicle No. and Check-In!!", 0).show();
            return;
        }
        final Dialog d = new Dialog(this);
        d.requestWindowFeature(1);
        d.setContentView(R.layout.register_alert);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        TextView text = (TextView) d.findViewById(R.id.register_text);
        text.setText("Do you want to save the Vehicle Information?");
        ImageButton no = (ImageButton) d.findViewById(R.id.noButton);
        ImageButton yes = (ImageButton) d.findViewById(R.id.yesButton);
        no.setOnClickListener(new View.OnClickListener() { // from class: com.sheikhibtesam.activities.AdminVehicleRegistar.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                AdminVehicleRegistar.this.finish();
                d.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() { // from class: com.sheikhibtesam.activities.AdminVehicleRegistar.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                AdminVehicleRegistar.this.finish();
                Toast.makeText(AdminVehicleRegistar.this, "Information is saved!!", 0).show();
                d.dismiss();
            }
        });
        d.show();
    }
}
