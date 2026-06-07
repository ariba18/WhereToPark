package com.example.wheretoparkproject.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.wheretoparkproject.R;
import com.example.wheretoparkproject.data.ParkingRepository;

/**
 * Shows booking flow: 1) Select hours + rate info, 2) Confirm summary.
 * Car: $100/hr, Bike: $50/hr.
 */
public class BookingDialogHelper {
    public static final int CAR_RATE_PER_HOUR = 100;
    public static final int BIKE_RATE_PER_HOUR = 50;

    public interface BookingCallback {
        void onConfirm(String slotId, int hours, double amount);
    }

    /** Show "Do you want to book?" dialog with hours picker, then confirmation. */
    public static void showBookingDialog(Context context, String slotId, boolean isCar,
                                         BookingCallback callback) {
        int rate = isCar ? CAR_RATE_PER_HOUR : BIKE_RATE_PER_HOUR;
        String vehicleLabel = isCar ? "Car" : "Bike";
        Log.d(ParkingRepository.TAG, "[Booking] Dialog SHOW: slotId=" + slotId + " vehicle=" + vehicleLabel + " rate=$" + rate + "/hr");

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_booking);
        dialog.setCancelable(true);

        TextView title = dialog.findViewById(R.id.dialog_booking_title);
        title.setText("Book this parking?");

        TextView rateText = dialog.findViewById(R.id.dialog_booking_rate);
        rateText.setText(vehicleLabel + ": $" + rate + " per hour");

        NumberPicker hoursPicker = dialog.findViewById(R.id.dialog_booking_hours);
        hoursPicker.setMinValue(1);
        hoursPicker.setMaxValue(24);
        hoursPicker.setValue(1);

        TextView amountText = dialog.findViewById(R.id.dialog_booking_amount);
        View.OnClickListener updateAmount = v -> {
            int h = hoursPicker.getValue();
            double total = h * rate;
            amountText.setText("Total: $" + (int) total);
        };
        hoursPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            double total = newVal * rate;
            amountText.setText("Total: $" + (int) total);
        });
        amountText.setText("Total: $" + rate);

        Button cancel = dialog.findViewById(R.id.dialog_booking_cancel);
        cancel.setOnClickListener(v -> {
            Log.d(ParkingRepository.TAG, "[Booking] User CANCELLED (hours dialog)");
            dialog.dismiss();
        });

        Button next = dialog.findViewById(R.id.dialog_booking_confirm);
        next.setOnClickListener(v -> {
            int hours = hoursPicker.getValue();
            double amount = hours * rate;
            Log.d(ParkingRepository.TAG, "[Booking] User tapped Next: slotId=" + slotId + " hours=" + hours + " amount=$" + (int) amount);
            dialog.dismiss();
            showConfirmDialog(context, slotId, isCar, hours, amount, callback);
        });

        dialog.show();
    }

    private static void showConfirmDialog(Context context, String slotId, boolean isCar,
                                          int hours, double amount, BookingCallback callback) {
        Dialog confirm = new Dialog(context);
        confirm.setContentView(R.layout.dialog_booking_confirm);
        confirm.setCancelable(true);

        TextView summary = confirm.findViewById(R.id.dialog_confirm_summary);
        summary.setText(String.format("Slot %s, %d hour(s), Total $%d", slotId, hours, (int) amount));

        Button cancelBtn = confirm.findViewById(R.id.dialog_confirm_cancel);
        cancelBtn.setOnClickListener(v -> {
            Log.d(ParkingRepository.TAG, "[Booking] User CANCELLED (confirm dialog)");
            confirm.dismiss();
        });

        Button okBtn = confirm.findViewById(R.id.dialog_confirm_ok);
        okBtn.setOnClickListener(v -> {
            Log.d(ParkingRepository.TAG, "[Booking] User CONFIRMED: slotId=" + slotId + " hours=" + hours + " amount=$" + (int) amount + " -> calling Firebase");
            confirm.dismiss();
            // Post to main looper (not the dialog view) so callback runs after dialog is gone; okBtn.post() can drop after dismiss()
            Runnable runCallback = () -> {
                if (callback != null) callback.onConfirm(slotId, hours, amount);
            };
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(runCallback);
            } else {
                new Handler(Looper.getMainLooper()).post(runCallback);
            }
        });

        confirm.show();
    }
}
