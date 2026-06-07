package com.example.wheretoparkproject.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.example.wheretoparkproject.R;
import com.example.wheretoparkproject.utils.CommonUtils;
import com.example.wheretoparkproject.utils.Constants;

/* loaded from: classes.dex */
public class About extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private EditText etvServerIp;
    private ImageButton imgbtnSaveIp;
    private CommonUtils utils;

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
        setContentView(R.layout.about);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.title);
        actionBar.hide();
        init();
    }

    private void init() {
        this.utils = new CommonUtils();
        this.context = this;
        this.etvServerIp = (EditText) findViewById(R.id.etvServerIp);
        String tempIp = this.utils.getIpFromSP(this.context);
        if (tempIp != null && !tempIp.equalsIgnoreCase("") && !tempIp.equalsIgnoreCase(Constants.CONST_DEFAULT_IP)) {
            try {
                String[] temp = tempIp.split("//");
                this.etvServerIp.setText(temp[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.imgbtnSaveIp = (ImageButton) findViewById(R.id.imgbtnSaveIp);
        this.imgbtnSaveIp.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v.getId() == R.id.imgbtnSaveIp) {
            String ip = this.etvServerIp.getText().toString();
            if (ip != null && !ip.equalsIgnoreCase("")) {
                this.utils.setIpInSP(this.context, ip);
                this.utils.showAlert(this.context, String.valueOf(ip) + " Address saved successfully", true, false);
                return;
            }
            this.utils.showAlert(this.context, "Please enter valid ip address", false, false);
        }
    }
}
