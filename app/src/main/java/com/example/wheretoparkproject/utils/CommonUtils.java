package com.example.wheretoparkproject.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.wheretoparkproject.R;
import com.example.wheretoparkproject.asynctask.ResetParkingAsyncTask;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/* loaded from: classes.dex */
public class CommonUtils {
    private InputStream is;
    private String line;
    private String result;

    public String webserviceCall(Context context, String url) {
        try {
            String url2 = String.valueOf(getIpFromSP(context)) + "/" + url;
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("URL :: " + url2);
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url2);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            this.is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = reader.readLine();
                this.line = readLine;
                if (readLine == null) {
                    break;
                }
                sb.append(String.valueOf(this.line) + "\n");
            }
            this.is.close();
            this.result = sb.toString();
            System.out.println("Output from PHP Server :: " + this.result);
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Webservice 2", e.toString());
            this.result = e.getLocalizedMessage();
        }
        return this.result;
    }

    public void showSuccessAlert(Context context, String slot_id) {
        showAlert(context, "Congrats. Your parking slot " + slot_id + " have been successfully booked...", true, true);
    }

    public void showResetSuccessAlert(Context context, String slot_id) {
        showAlert(context, String.valueOf(slot_id) + " slot have been successfully reset...", true, true);
    }

    public void showCommonAlert(Context context, String title, String message) {
        showAlert(context, message, false, false);
    }

    public void showAlert(final Context context, String message, final boolean finishActivity, final boolean backPress) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.alert_all);
        dialog.setCancelable(false);
        TextView text = (TextView) dialog.findViewById(R.id.dialogtext);
        text.setText(message);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogbutton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (backPress) {
                    ((AppCompatActivity) context).onBackPressed();
                }
                if (finishActivity) {
                    ((AppCompatActivity) context).finish();
                }
            }
        });
        dialog.show();
    }

    public void showAlreadyBookedAlert(Context context, String slot_id) {
        showAlert(context, "Parking slot " + slot_id + " is not available or already booked. Please select other available slots.", false, false);
    }

    /** Show "Cancel your booking for {slotId}?"; on confirm run onConfirm (e.g. release slot in Firebase). */
    public void showCancelBookingAlert(final Context context, final String slotId, final Runnable onConfirm) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.alert_all);
        dialog.setCancelable(true);
        TextView text = (TextView) dialog.findViewById(R.id.dialogtext);
        text.setText("Cancel your booking for " + slotId + "?");
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogbutton);
        dialogButton.setText("Yes, cancel");
        dialogButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (onConfirm != null) onConfirm.run();
        });
        dialog.show();
    }

    public void showResetConfirmAlert(final Context context, final String slot_id, final boolean isForTwoWheeler) {
        showResetConfirmAlert(context, slot_id, isForTwoWheeler, null);
    }

    /** If onConfirm is non-null, it is called instead of ResetParkingAsyncTask (e.g. for Firebase release). */
    public void showResetConfirmAlert(final Context context, final String slot_id, final boolean isForTwoWheeler, final Runnable onConfirm) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.alert_all);
        dialog.setCancelable(true);
        TextView text = (TextView) dialog.findViewById(R.id.dialogtext);
        text.setText("Do you want to reset the " + slot_id + " slot?");
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogbutton);
        dialogButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (onConfirm != null) {
                onConfirm.run();
            } else {
                new ResetParkingAsyncTask((AppCompatActivity) context, slot_id, isForTwoWheeler).execute(new Void[0]);
            }
        });
        dialog.show();
    }

    public boolean isNetworkConnected(Context activtiy) {
        ConnectivityManager cmss = (ConnectivityManager) activtiy.getSystemService("connectivity");
        NetworkInfo niss = cmss.getActiveNetworkInfo();
        return niss != null;
    }

    public void showNetworkError(Context context) {
        showAlert(context, "No internet connection available. Please connect to internet to proceed.", false, false);
    }

    public String getIP() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "wtp_ip_file.txt");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (true) {
                String line = br.readLine();
                if (line != null) {
                    text.append(line);
                    text.append('\n');
                } else {
                    br.close();
                    return text.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getIpFromSP(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SP_CONST_NAME, 0);
        return "http://" + preferences.getString(Constants.SP_CONST_IP, Constants.CONST_DEFAULT_IP);
    }

    public void setIpInSP(Context context, String ip) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SP_CONST_NAME, 0).edit();
        editor.putString(Constants.SP_CONST_IP, ip).commit();
    }
}
