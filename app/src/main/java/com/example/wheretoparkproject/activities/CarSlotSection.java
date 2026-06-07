package com.example.wheretoparkproject.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import com.example.wheretoparkproject.R;
import com.example.wheretoparkproject.data.ParkingRepository;
import com.example.wheretoparkproject.utils.BookingDialogHelper;
import com.example.wheretoparkproject.utils.CommonUtils;
import com.example.wheretoparkproject.utils.ProgressDialogHelper;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public class CarSlotSection extends AppCompatActivity implements View.OnClickListener {
    private AppCompatActivity activity;
    private Context context;
    private boolean isAdminLoggedIn;
    private boolean isBookedCarSlot1;
    private boolean isBookedCarSlot2;
    private boolean isBookedCarSlot3;
    private boolean isBookedCarSlot4;
    private boolean isBookedCarSlot5;
    private boolean isBookedCarSlot6;
    private boolean isBookedCarSlot7;
    private boolean isBookedCarSlot8;
    private boolean isBookedCarSlot9;
    private boolean isBookedCarSlot10;
    private ImageButton ivCarSlot1;
    private ImageButton ivCarSlot2;
    private ImageButton ivCarSlot3;
    private ImageButton ivCarSlot4;
    private ImageButton ivCarSlot5;
    private ImageButton ivCarSlot6;
    private ImageButton ivCarSlot7;
    private ImageButton ivCarSlot8;
    private ImageButton ivCarSlot9;
    private ImageButton ivCarSlot10;
    private ImageView resetAll;
    private CommonUtils utils;
    private ParkingRepository parkingRepository;
    private ValueEventListener firebaseCarListener;
    private AlertDialog progressDialog;
    private String currentUsername;
    private Map<String, String> carSlotBookedBy;

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
        setContentView(R.layout.car_slot_section);
        ActionBar actionBar1 = getSupportActionBar();
        if (actionBar1 != null) {
            actionBar1.setDisplayShowHomeEnabled(true);
            actionBar1.setDisplayHomeAsUpEnabled(true);
            actionBar1.setTitle("Car parking");
            actionBar1.setIcon(R.drawable.title);
        }
        init();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void init() {
        Bundle b = getIntent().getExtras();
        this.isAdminLoggedIn = (b != null && b.containsKey("isAdminLoggedIn")) && b.getBoolean("isAdminLoggedIn");
        this.currentUsername = (b != null && b.containsKey("username")) ? b.getString("username") : null;
        this.utils = new CommonUtils();
        this.context = this;
        this.activity = this;
        this.ivCarSlot1 = (ImageButton) findViewById(R.id.ivCarSlot1);
        this.ivCarSlot1.setOnClickListener(this);
        this.ivCarSlot2 = (ImageButton) findViewById(R.id.ivCarSlot2);
        this.ivCarSlot2.setOnClickListener(this);
        this.ivCarSlot3 = (ImageButton) findViewById(R.id.ivCarSlot3);
        this.ivCarSlot3.setOnClickListener(this);
        this.ivCarSlot4 = (ImageButton) findViewById(R.id.ivCarSlot4);
        this.ivCarSlot4.setOnClickListener(this);
        this.ivCarSlot5 = (ImageButton) findViewById(R.id.ivCarSlot5);
        this.ivCarSlot5.setOnClickListener(this);
        this.ivCarSlot6 = (ImageButton) findViewById(R.id.ivCarSlot6);
        this.ivCarSlot6.setOnClickListener(this);
        this.ivCarSlot7 = (ImageButton) findViewById(R.id.ivCarSlot7);
        this.ivCarSlot7.setOnClickListener(this);
        this.ivCarSlot8 = (ImageButton) findViewById(R.id.ivCarSlot8);
        this.ivCarSlot8.setOnClickListener(this);
        this.ivCarSlot9 = (ImageButton) findViewById(R.id.ivCarSlot9);
        this.ivCarSlot9.setOnClickListener(this);
        this.ivCarSlot10 = (ImageButton) findViewById(R.id.ivCarSlot10);
        this.ivCarSlot10.setOnClickListener(this);
        Button showMapButton = findViewById(R.id.btnShowMap);
        showMapButton.setOnClickListener(v -> startActivity(new Intent(CarSlotSection.this, ParkingMapActivity.class)));
        setAllCarSlotsAvailable();
        parkingRepository = new ParkingRepository();
        progressDialog = ProgressDialogHelper.show(this, "Loading parking...");
        Log.d(ParkingRepository.TAG, "[CarSlot] init: isAdmin=" + this.isAdminLoggedIn + ", attaching Firebase listener");
        firebaseCarListener = parkingRepository.listenCarSlots(new ParkingRepository.SlotStatusListener() {
            @Override
            public void onCarSlots(Map<String, Boolean> slotBooked, Map<String, String> slotBookedBy) {
                Log.d(ParkingRepository.TAG, "[CarSlot] Firebase data received -> applying UI: " + slotBooked + " bookedBy=" + slotBookedBy);
                ProgressDialogHelper.dismiss(CarSlotSection.this, progressDialog);
                progressDialog = null;
                applyCarSlotsFromFirebase(slotBooked, slotBookedBy);
            }
            @Override
            public void onBikeSlots(Map<String, Boolean> slotBooked, Map<String, String> slotBookedBy) {}
            @Override
            public void onError(String message) {
                ProgressDialogHelper.dismiss(CarSlotSection.this, progressDialog);
                progressDialog = null;
                runOnUiThread(() -> Toast.makeText(CarSlotSection.this, "Sync error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void applyCarSlotsFromFirebase(Map<String, Boolean> slotBooked, Map<String, String> slotBookedBy) {
        runOnUiThread(() -> {
            this.isBookedCarSlot1 = Boolean.TRUE.equals(slotBooked.get("C1"));
            this.isBookedCarSlot2 = Boolean.TRUE.equals(slotBooked.get("C2"));
            this.isBookedCarSlot3 = Boolean.TRUE.equals(slotBooked.get("C3"));
            this.isBookedCarSlot4 = Boolean.TRUE.equals(slotBooked.get("C4"));
            this.isBookedCarSlot5 = Boolean.TRUE.equals(slotBooked.get("C5"));
            this.isBookedCarSlot6 = Boolean.TRUE.equals(slotBooked.get("C6"));
            this.isBookedCarSlot7 = Boolean.TRUE.equals(slotBooked.get("C7"));
            this.isBookedCarSlot8 = Boolean.TRUE.equals(slotBooked.get("C8"));
            this.isBookedCarSlot9 = Boolean.TRUE.equals(slotBooked.get("C9"));
            this.isBookedCarSlot10 = Boolean.TRUE.equals(slotBooked.get("C10"));
            this.carSlotBookedBy = slotBookedBy;
            setCarSlotDrawables();
        });
    }

    private void setBookedCarSlot(String slotId, boolean booked) {
        if ("C1".equals(slotId)) this.isBookedCarSlot1 = booked;
        else if ("C2".equals(slotId)) this.isBookedCarSlot2 = booked;
        else if ("C3".equals(slotId)) this.isBookedCarSlot3 = booked;
        else if ("C4".equals(slotId)) this.isBookedCarSlot4 = booked;
        else if ("C5".equals(slotId)) this.isBookedCarSlot5 = booked;
        else if ("C6".equals(slotId)) this.isBookedCarSlot6 = booked;
        else if ("C7".equals(slotId)) this.isBookedCarSlot7 = booked;
        else if ("C8".equals(slotId)) this.isBookedCarSlot8 = booked;
        else if ("C9".equals(slotId)) this.isBookedCarSlot9 = booked;
        else if ("C10".equals(slotId)) this.isBookedCarSlot10 = booked;
    }

    private int getCarSlotDrawable(boolean booked, String slotId) {
        if (!booked) return R.drawable.slot_car_available;
        if (currentUsername != null && carSlotBookedBy != null && currentUsername.equals(carSlotBookedBy.get(slotId)))
            return R.drawable.slot_car_my_booking;
        return R.drawable.slot_car_booked;
    }

    private void setCarSlotDrawables() {
        this.ivCarSlot1.setImageDrawable(getResources().getDrawable(getCarSlotDrawable(this.isBookedCarSlot1, "C1")));
        this.ivCarSlot2.setImageDrawable(getResources().getDrawable(getCarSlotDrawable(this.isBookedCarSlot2, "C2")));
        this.ivCarSlot3.setImageDrawable(getResources().getDrawable(getCarSlotDrawable(this.isBookedCarSlot3, "C3")));
        this.ivCarSlot4.setImageDrawable(getResources().getDrawable(getCarSlotDrawable(this.isBookedCarSlot4, "C4")));
        this.ivCarSlot5.setImageDrawable(getResources().getDrawable(getCarSlotDrawable(this.isBookedCarSlot5, "C5")));
        this.ivCarSlot6.setImageDrawable(getResources().getDrawable(getCarSlotDrawable(this.isBookedCarSlot6, "C6")));
        this.ivCarSlot7.setImageDrawable(getResources().getDrawable(getCarSlotDrawable(this.isBookedCarSlot7, "C7")));
        this.ivCarSlot8.setImageDrawable(getResources().getDrawable(getCarSlotDrawable(this.isBookedCarSlot8, "C8")));
        this.ivCarSlot9.setImageDrawable(getResources().getDrawable(getCarSlotDrawable(this.isBookedCarSlot9, "C9")));
        this.ivCarSlot10.setImageDrawable(getResources().getDrawable(getCarSlotDrawable(this.isBookedCarSlot10, "C10")));
    }

    @Override
    protected void onDestroy() {
        ProgressDialogHelper.dismiss(this, progressDialog);
        progressDialog = null;
        if (parkingRepository != null && firebaseCarListener != null) {
            parkingRepository.removeListener(parkingRepository.getCarRef(), firebaseCarListener);
        }
        super.onDestroy();
    }

    private void setAllCarSlotsAvailable() {
        this.ivCarSlot1.setImageDrawable(getResources().getDrawable(R.drawable.slot_car_available));
        this.ivCarSlot2.setImageDrawable(getResources().getDrawable(R.drawable.slot_car_available));
        this.ivCarSlot3.setImageDrawable(getResources().getDrawable(R.drawable.slot_car_available));
        this.ivCarSlot4.setImageDrawable(getResources().getDrawable(R.drawable.slot_car_available));
        this.ivCarSlot5.setImageDrawable(getResources().getDrawable(R.drawable.slot_car_available));
        this.ivCarSlot6.setImageDrawable(getResources().getDrawable(R.drawable.slot_car_available));
        this.ivCarSlot7.setImageDrawable(getResources().getDrawable(R.drawable.slot_car_available));
        this.ivCarSlot8.setImageDrawable(getResources().getDrawable(R.drawable.slot_car_available));
        this.ivCarSlot9.setImageDrawable(getResources().getDrawable(R.drawable.slot_car_available));
        this.ivCarSlot10.setImageDrawable(getResources().getDrawable(R.drawable.slot_car_available));
    }

    public void onFetchedParking(String result) {
        if (result != null && !result.equalsIgnoreCase("")) {
            setAllCarSlotsAvailable();
            this.isBookedCarSlot1 = false;
            this.isBookedCarSlot2 = false;
            this.isBookedCarSlot3 = false;
            this.isBookedCarSlot4 = false;
            this.isBookedCarSlot5 = false;
            this.isBookedCarSlot6 = false;
            this.isBookedCarSlot7 = false;
            this.isBookedCarSlot8 = false;
            this.isBookedCarSlot9 = false;
            this.isBookedCarSlot10 = false;
            for (int i = 1; i <= 10; i++) {
                String sid = "C" + i;
                if (result.contains(sid)) {
                    this.isBookedCarSlot1 = (i == 1) ? true : this.isBookedCarSlot1;
                    this.isBookedCarSlot2 = (i == 2) ? true : this.isBookedCarSlot2;
                    this.isBookedCarSlot3 = (i == 3) ? true : this.isBookedCarSlot3;
                    this.isBookedCarSlot4 = (i == 4) ? true : this.isBookedCarSlot4;
                    this.isBookedCarSlot5 = (i == 5) ? true : this.isBookedCarSlot5;
                    this.isBookedCarSlot6 = (i == 6) ? true : this.isBookedCarSlot6;
                    this.isBookedCarSlot7 = (i == 7) ? true : this.isBookedCarSlot7;
                    this.isBookedCarSlot8 = (i == 8) ? true : this.isBookedCarSlot8;
                    this.isBookedCarSlot9 = (i == 9) ? true : this.isBookedCarSlot9;
                    this.isBookedCarSlot10 = (i == 10) ? true : this.isBookedCarSlot10;
                }
            }
            setCarSlotDrawables();
            return;
        }
        Toast.makeText(this, "Error in connecting to server. Please try again later.", 1).show();
    }

    public void updateSlotUi(String slotId, boolean booked) {
        runOnUiThread(() -> {
            if ("C1".equals(slotId)) { this.isBookedCarSlot1 = booked; setCarSlotDrawables(); }
            else if ("C2".equals(slotId)) { this.isBookedCarSlot2 = booked; setCarSlotDrawables(); }
            else if ("C3".equals(slotId)) { this.isBookedCarSlot3 = booked; setCarSlotDrawables(); }
            else if ("C4".equals(slotId)) { this.isBookedCarSlot4 = booked; setCarSlotDrawables(); }
            else if ("C5".equals(slotId)) { this.isBookedCarSlot5 = booked; setCarSlotDrawables(); }
            else if ("C6".equals(slotId)) { this.isBookedCarSlot6 = booked; setCarSlotDrawables(); }
            else if ("C7".equals(slotId)) { this.isBookedCarSlot7 = booked; setCarSlotDrawables(); }
            else if ("C8".equals(slotId)) { this.isBookedCarSlot8 = booked; setCarSlotDrawables(); }
            else if ("C9".equals(slotId)) { this.isBookedCarSlot9 = booked; setCarSlotDrawables(); }
            else if ("C10".equals(slotId)) { this.isBookedCarSlot10 = booked; setCarSlotDrawables(); }
        });
    }

    public void onSuccessfulUpdateParking(String result, String slot_id) {
        new CommonUtils().showSuccessAlert(this, slot_id);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ivCarSlot1) handleCarSlotClick("C1", this.isBookedCarSlot1);
        else if (id == R.id.ivCarSlot2) handleCarSlotClick("C2", this.isBookedCarSlot2);
        else if (id == R.id.ivCarSlot3) handleCarSlotClick("C3", this.isBookedCarSlot3);
        else if (id == R.id.ivCarSlot4) handleCarSlotClick("C4", this.isBookedCarSlot4);
        else if (id == R.id.ivCarSlot5) handleCarSlotClick("C5", this.isBookedCarSlot5);
        else if (id == R.id.ivCarSlot6) handleCarSlotClick("C6", this.isBookedCarSlot6);
        else if (id == R.id.ivCarSlot7) handleCarSlotClick("C7", this.isBookedCarSlot7);
        else if (id == R.id.ivCarSlot8) handleCarSlotClick("C8", this.isBookedCarSlot8);
        else if (id == R.id.ivCarSlot9) handleCarSlotClick("C9", this.isBookedCarSlot9);
        else if (id == R.id.ivCarSlot10) handleCarSlotClick("C10", this.isBookedCarSlot10);
    }

    private boolean isBookedByCurrentUser(String slotId) {
        return currentUsername != null && carSlotBookedBy != null && currentUsername.equals(carSlotBookedBy.get(slotId));
    }

    private void handleCarSlotClick(String slotId, boolean currentlyBooked) {
        Log.d(ParkingRepository.TAG, "[CarSlot] Slot CLICKED: " + slotId + " currentlyBooked=" + currentlyBooked + " isAdmin=" + this.isAdminLoggedIn);
        if (!this.utils.isNetworkConnected(this.context)) {
            Log.w(ParkingRepository.TAG, "[CarSlot] No network - showing error");
            this.utils.showNetworkError(this.context);
            return;
        }
        if (currentlyBooked && !this.isAdminLoggedIn && isBookedByCurrentUser(slotId)) {
            Log.d(ParkingRepository.TAG, "[CarSlot] Slot " + slotId + " booked by current user -> show cancel dialog");
            this.utils.showCancelBookingAlert(this.context, slotId, () -> cancelCarSlot(slotId));
            return;
        }
        if (currentlyBooked && !this.isAdminLoggedIn) {
            Log.d(ParkingRepository.TAG, "[CarSlot] Slot " + slotId + " already booked by someone else -> show already booked alert");
            this.utils.showAlreadyBookedAlert(this.context, slotId);
            return;
        }
        if (this.isAdminLoggedIn && currentlyBooked) {
            Log.d(ParkingRepository.TAG, "[CarSlot] Admin releasing slot " + slotId + " -> show reset confirm");
            this.utils.showResetConfirmAlert(this.context, slotId, false,
                    () -> releaseCarSlot(slotId));
            return;
        }
        if (!currentlyBooked) {
            Log.d(ParkingRepository.TAG, "[CarSlot] Slot " + slotId + " available -> show booking dialog");
            BookingDialogHelper.showBookingDialog(this, slotId, true,
                    (s, hours, amount) -> bookCarSlot(s, hours, amount));
        }
    }

    private void bookCarSlot(String slotId, int hours, double amount) {
        Log.d(ParkingRepository.TAG, "[CarSlot] bookCarSlot called: " + slotId + " hours=" + hours + " amount=$" + (int) amount + " user=" + currentUsername);
        progressDialog = ProgressDialogHelper.show(this, "Updating booking...");
        parkingRepository.bookCarSlot(slotId, hours, amount, currentUsername != null ? currentUsername : "",
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(CarSlotSection.this, progressDialog);
                    progressDialog = null;
                    CarSlotSection.this.setBookedCarSlot(slotId, true);
                    if (CarSlotSection.this.carSlotBookedBy == null) CarSlotSection.this.carSlotBookedBy = new HashMap<>();
                    CarSlotSection.this.carSlotBookedBy.put(slotId, CarSlotSection.this.currentUsername != null ? CarSlotSection.this.currentUsername : "");
                    CarSlotSection.this.setCarSlotDrawables();
                    CarSlotSection.this.utils.showAlert(CarSlotSection.this, "Booked! Slot " + slotId + ", " + hours + " hr(s), $" + (int) amount, false, false);
                }),
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(CarSlotSection.this, progressDialog);
                    progressDialog = null;
                    CarSlotSection.this.utils.showCommonAlert(CarSlotSection.this, "Error", "Failed to book. Try again.");
                }));
    }

    private void releaseCarSlot(String slotId) {
        Log.d(ParkingRepository.TAG, "[CarSlot] releaseCarSlot called: " + slotId);
        progressDialog = ProgressDialogHelper.show(this, "Updating...");
        parkingRepository.releaseCarSlot(slotId,
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(CarSlotSection.this, progressDialog);
                    progressDialog = null;
                    onSuccessfulResetParking(slotId);
                }),
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(CarSlotSection.this, progressDialog);
                    progressDialog = null;
                    CarSlotSection.this.utils.showCommonAlert(CarSlotSection.this, "Error", "Failed to release slot.");
                }));
    }

    /** User cancels their own booking; updates Firebase and shows "Booking cancelled". */
    private void cancelCarSlot(String slotId) {
        Log.d(ParkingRepository.TAG, "[CarSlot] cancelCarSlot called: " + slotId);
        progressDialog = ProgressDialogHelper.show(this, "Cancelling...");
        parkingRepository.releaseCarSlot(slotId,
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(CarSlotSection.this, progressDialog);
                    progressDialog = null;
                    onCancelBookingSuccess(slotId);
                }),
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(CarSlotSection.this, progressDialog);
                    progressDialog = null;
                    CarSlotSection.this.utils.showCommonAlert(CarSlotSection.this, "Error", "Failed to cancel booking.");
                }));
    }

    public void onSuccessfulResetParking(String result) {
        this.utils.showResetSuccessAlert(this, result);
    }

    /** Called after user cancels their own booking (same as release success). */
    private void onCancelBookingSuccess(String slotId) {
        runOnUiThread(() -> {
            if (carSlotBookedBy != null) carSlotBookedBy.remove(slotId);
            setBookedCarSlot(slotId, false);
            setCarSlotDrawables();
            this.utils.showAlert(this, "Booking cancelled for " + slotId, false, false);
        });
    }
}
