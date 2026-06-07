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
public class UpdateParkingAsyncTask extends AsyncTask<Void, Void, String> {
    private AppCompatActivity appCompatActivity;
    private String booking_slot_id;
    private boolean isForTwoWheeler;
    private ProgressDialog myProgressDialog;
    private CommonUtils utils = new CommonUtils();

    public UpdateParkingAsyncTask(AppCompatActivity appCompatActivity, String booking_slot_id, boolean isForTwoWheeler) {
        this.appCompatActivity = appCompatActivity;
        this.isForTwoWheeler = isForTwoWheeler;
        this.booking_slot_id = booking_slot_id;
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
        if (result != null && !result.equalsIgnoreCase("")) {
            if (result.contains("Records updated successfully")) {
                if (this.isForTwoWheeler) {
                    ((BikeSlotSection) this.appCompatActivity).onSuccessfulUpdateParking(result, this.booking_slot_id);
                    return;
                } else {
                    ((CarSlotSection) this.appCompatActivity).onSuccessfulUpdateParking(result, this.booking_slot_id);
                    return;
                }
            }
            this.utils.showCommonAlert(this.appCompatActivity, "Booking Failed", "Your parking request failed . Possible reason " + result);
            return;
        }
        this.utils.showCommonAlert(this.appCompatActivity, "Booking Failed", "Some unknown error occured. Please try again later");
    }

    @Override // android.os.AsyncTask
    public String doInBackground(Void... Void) {
        if (this.isForTwoWheeler) {
            String result = this.utils.webserviceCall(this.appCompatActivity, String.valueOf(Constants.API_UPDATE_TWO_WHEELR) + "?slot_id=" + this.booking_slot_id + "&veh_type=T");
            return result;
        }
        String result2 = this.utils.webserviceCall(this.appCompatActivity, String.valueOf(Constants.API_UPDATE_TWO_WHEELR) + "?slot_id=" + this.booking_slot_id + "&veh_type=F");
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
