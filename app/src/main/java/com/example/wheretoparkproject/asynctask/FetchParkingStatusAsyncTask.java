package com.example.wheretoparkproject.asynctask;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import com.example.wheretoparkproject.activities.BikeSlotSection;
import com.example.wheretoparkproject.activities.CarSlotSection;
import com.example.wheretoparkproject.utils.CommonUtils;
import com.example.wheretoparkproject.utils.Constants;

/* loaded from: classes.dex */
public class FetchParkingStatusAsyncTask extends AsyncTask<Void, Void, String> {
    private AppCompatActivity appCompatActivity;
    private boolean isForTwoWheeler;
    private ProgressDialog myProgressDialog;

    public FetchParkingStatusAsyncTask(AppCompatActivity appCompatActivity, boolean isForTwoWheeler) {
        this.appCompatActivity = appCompatActivity;
        this.isForTwoWheeler = isForTwoWheeler;
    }

    @Override // android.os.AsyncTask
    protected void onPreExecute() {
        showProgressDialog("Loading ... ");
        super.onPreExecute();
    }

    @Override // android.os.AsyncTask
    @SuppressLint({"NewApi"})
    public void onPostExecute(String result) {
        cancelProgressDialog();
        if (this.isForTwoWheeler) {
            if (result.contains("T1") || result.contains("T2") || result.contains("T3") || result.contains("T4") || result.contains("T5") || result.contains("0")) {
                ((BikeSlotSection) this.appCompatActivity).onFetchedParking(result);
            } else {
                new CommonUtils().showAlert(this.appCompatActivity, result, false, true);
            }
        } else if (result.contains("F1") || result.contains("F2") || result.contains("F3") || result.contains("F4") || result.contains("F5") || result.contains("0")) {
            ((CarSlotSection) this.appCompatActivity).onFetchedParking(result);
        } else {
            new CommonUtils().showAlert(this.appCompatActivity, result, false, true);
        }
    }

    @Override // android.os.AsyncTask
    public String doInBackground(Void... Void) {
        CommonUtils utils = new CommonUtils();
        if (this.isForTwoWheeler) {
            String result = utils.webserviceCall(this.appCompatActivity, Constants.API_FETCH_PARKED_TWO_WHEELRS);
            return result;
        }
        String result2 = utils.webserviceCall(this.appCompatActivity, Constants.API_FETCH_PARKED_FOUR_WHEELRS);
        return result2;
    }

    private void cancelProgressDialog() {
        if (this.myProgressDialog != null && this.myProgressDialog.isShowing()) {
            this.myProgressDialog.dismiss();
        }
    }

    private void showProgressDialog(String dialogText) {
        cancelProgressDialog();
        this.myProgressDialog = ProgressDialog.show(this.appCompatActivity, "", dialogText, true, false, null);
    }
}
