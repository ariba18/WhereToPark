package com.example.wheretoparkproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
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

/* loaded from: classes.dex */
public class BikeSlotSection extends AppCompatActivity implements View.OnClickListener {
    private AppCompatActivity activity;
    private ImageButton bikeSlot1;
    private ImageButton bikeSlot2;
    private ImageButton bikeSlot3;
    private ImageButton bikeSlot4;
    private ImageButton bikeSlot5;
    private ImageButton bikeSlot6;
    private ImageButton bikeSlot7;
    private ImageButton bikeSlot8;
    private ImageButton bikeSlot9;
    private ImageButton bikeSlot10;
    private Context context;
    private boolean isAdminLoggedIn;
    private boolean isBookedBikeSlot1;
    private boolean isBookedBikeSlot2;
    private boolean isBookedBikeSlot3;
    private boolean isBookedBikeSlot4;
    private boolean isBookedBikeSlot5;
    private boolean isBookedBikeSlot6;
    private boolean isBookedBikeSlot7;
    private boolean isBookedBikeSlot8;
    private boolean isBookedBikeSlot9;
    private boolean isBookedBikeSlot10;
    private ImageView resetAll;
    private CommonUtils utils;
    private ParkingRepository parkingRepository;
    private ValueEventListener firebaseBikeListener;
    private AlertDialog progressDialog;
    private String currentUsername;
    private Map<String, String> bikeSlotBookedBy;

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
        setContentView(R.layout.bike_slot_section);
        ActionBar actionBar1 = getSupportActionBar();
        if (actionBar1 != null) {
            actionBar1.setDisplayShowHomeEnabled(true);
            actionBar1.setDisplayHomeAsUpEnabled(true);
            actionBar1.setTitle("Bike parking");
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
        this.context = this;
        this.activity = this;
        this.utils = new CommonUtils();
        this.bikeSlot1 = (ImageButton) findViewById(R.id.bikeSlot1);
        this.bikeSlot1.setOnClickListener(this);
        this.bikeSlot2 = (ImageButton) findViewById(R.id.bikeSlot2);
        this.bikeSlot2.setOnClickListener(this);
        this.bikeSlot3 = (ImageButton) findViewById(R.id.bikeSlot3);
        this.bikeSlot3.setOnClickListener(this);
        this.bikeSlot4 = (ImageButton) findViewById(R.id.bikeSlot4);
        this.bikeSlot4.setOnClickListener(this);
        this.bikeSlot5 = (ImageButton) findViewById(R.id.bikeSlot5);
        this.bikeSlot5.setOnClickListener(this);
        this.bikeSlot6 = (ImageButton) findViewById(R.id.bikeSlot6);
        this.bikeSlot6.setOnClickListener(this);
        this.bikeSlot7 = (ImageButton) findViewById(R.id.bikeSlot7);
        this.bikeSlot7.setOnClickListener(this);
        this.bikeSlot8 = (ImageButton) findViewById(R.id.bikeSlot8);
        this.bikeSlot8.setOnClickListener(this);
        this.bikeSlot9 = (ImageButton) findViewById(R.id.bikeSlot9);
        this.bikeSlot9.setOnClickListener(this);
        this.bikeSlot10 = (ImageButton) findViewById(R.id.bikeSlot10);
        this.bikeSlot10.setOnClickListener(this);
        Button showMapButton = findViewById(R.id.btnShowMap);
        showMapButton.setOnClickListener(v -> startActivity(new Intent(BikeSlotSection.this, ParkingMapActivity.class)));
        setAllBikeSlotsAvailable();
        parkingRepository = new ParkingRepository();
        progressDialog = ProgressDialogHelper.show(this, "Loading parking...");
        Log.d(ParkingRepository.TAG, "[BikeSlot] init: isAdmin=" + this.isAdminLoggedIn + ", attaching Firebase listener");
        firebaseBikeListener = parkingRepository.listenBikeSlots(new ParkingRepository.SlotStatusListener() {
            @Override
            public void onCarSlots(Map<String, Boolean> slotBooked, Map<String, String> slotBookedBy) {}
            @Override
            public void onBikeSlots(Map<String, Boolean> slotBooked, Map<String, String> slotBookedBy) {
                Log.d(ParkingRepository.TAG, "[BikeSlot] Firebase data received -> applying UI: " + slotBooked + " bookedBy=" + slotBookedBy);
                ProgressDialogHelper.dismiss(BikeSlotSection.this, progressDialog);
                progressDialog = null;
                applyBikeSlotsFromFirebase(slotBooked, slotBookedBy);
            }
            @Override
            public void onError(String message) {
                ProgressDialogHelper.dismiss(BikeSlotSection.this, progressDialog);
                progressDialog = null;
                runOnUiThread(() -> Toast.makeText(BikeSlotSection.this, "Sync error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void applyBikeSlotsFromFirebase(Map<String, Boolean> slotBooked, Map<String, String> slotBookedBy) {
        runOnUiThread(() -> {
            this.isBookedBikeSlot1 = Boolean.TRUE.equals(slotBooked.get("B1"));
            this.isBookedBikeSlot2 = Boolean.TRUE.equals(slotBooked.get("B2"));
            this.isBookedBikeSlot3 = Boolean.TRUE.equals(slotBooked.get("B3"));
            this.isBookedBikeSlot4 = Boolean.TRUE.equals(slotBooked.get("B4"));
            this.isBookedBikeSlot5 = Boolean.TRUE.equals(slotBooked.get("B5"));
            this.isBookedBikeSlot6 = Boolean.TRUE.equals(slotBooked.get("B6"));
            this.isBookedBikeSlot7 = Boolean.TRUE.equals(slotBooked.get("B7"));
            this.isBookedBikeSlot8 = Boolean.TRUE.equals(slotBooked.get("B8"));
            this.isBookedBikeSlot9 = Boolean.TRUE.equals(slotBooked.get("B9"));
            this.isBookedBikeSlot10 = Boolean.TRUE.equals(slotBooked.get("B10"));
            this.bikeSlotBookedBy = slotBookedBy;
            setBikeSlotDrawables();
        });
    }

    private void setBookedBikeSlot(String slotId, boolean booked) {
        if ("B1".equals(slotId)) this.isBookedBikeSlot1 = booked;
        else if ("B2".equals(slotId)) this.isBookedBikeSlot2 = booked;
        else if ("B3".equals(slotId)) this.isBookedBikeSlot3 = booked;
        else if ("B4".equals(slotId)) this.isBookedBikeSlot4 = booked;
        else if ("B5".equals(slotId)) this.isBookedBikeSlot5 = booked;
        else if ("B6".equals(slotId)) this.isBookedBikeSlot6 = booked;
        else if ("B7".equals(slotId)) this.isBookedBikeSlot7 = booked;
        else if ("B8".equals(slotId)) this.isBookedBikeSlot8 = booked;
        else if ("B9".equals(slotId)) this.isBookedBikeSlot9 = booked;
        else if ("B10".equals(slotId)) this.isBookedBikeSlot10 = booked;
    }

    private int getBikeSlotDrawable(boolean booked, String slotId) {
        if (!booked) return R.drawable.slot_bike_available;
        if (currentUsername != null && bikeSlotBookedBy != null && currentUsername.equals(bikeSlotBookedBy.get(slotId)))
            return R.drawable.slot_bike_my_booking;
        return R.drawable.slot_bike_booked;
    }

    private void setBikeSlotDrawables() {
        this.bikeSlot1.setImageDrawable(getResources().getDrawable(getBikeSlotDrawable(this.isBookedBikeSlot1, "B1")));
        this.bikeSlot2.setImageDrawable(getResources().getDrawable(getBikeSlotDrawable(this.isBookedBikeSlot2, "B2")));
        this.bikeSlot3.setImageDrawable(getResources().getDrawable(getBikeSlotDrawable(this.isBookedBikeSlot3, "B3")));
        this.bikeSlot4.setImageDrawable(getResources().getDrawable(getBikeSlotDrawable(this.isBookedBikeSlot4, "B4")));
        this.bikeSlot5.setImageDrawable(getResources().getDrawable(getBikeSlotDrawable(this.isBookedBikeSlot5, "B5")));
        this.bikeSlot6.setImageDrawable(getResources().getDrawable(getBikeSlotDrawable(this.isBookedBikeSlot6, "B6")));
        this.bikeSlot7.setImageDrawable(getResources().getDrawable(getBikeSlotDrawable(this.isBookedBikeSlot7, "B7")));
        this.bikeSlot8.setImageDrawable(getResources().getDrawable(getBikeSlotDrawable(this.isBookedBikeSlot8, "B8")));
        this.bikeSlot9.setImageDrawable(getResources().getDrawable(getBikeSlotDrawable(this.isBookedBikeSlot9, "B9")));
        this.bikeSlot10.setImageDrawable(getResources().getDrawable(getBikeSlotDrawable(this.isBookedBikeSlot10, "B10")));
    }

    @Override
    protected void onDestroy() {
        ProgressDialogHelper.dismiss(this, progressDialog);
        progressDialog = null;
        if (parkingRepository != null && firebaseBikeListener != null) {
            parkingRepository.removeListener(parkingRepository.getBikeRef(), firebaseBikeListener);
        }
        super.onDestroy();
    }

    private void setAllBikeSlotsAvailable() {
        this.bikeSlot1.setImageDrawable(getResources().getDrawable(R.drawable.slot_bike_available));
        this.bikeSlot2.setImageDrawable(getResources().getDrawable(R.drawable.slot_bike_available));
        this.bikeSlot3.setImageDrawable(getResources().getDrawable(R.drawable.slot_bike_available));
        this.bikeSlot4.setImageDrawable(getResources().getDrawable(R.drawable.slot_bike_available));
        this.bikeSlot5.setImageDrawable(getResources().getDrawable(R.drawable.slot_bike_available));
        this.bikeSlot6.setImageDrawable(getResources().getDrawable(R.drawable.slot_bike_available));
        this.bikeSlot7.setImageDrawable(getResources().getDrawable(R.drawable.slot_bike_available));
        this.bikeSlot8.setImageDrawable(getResources().getDrawable(R.drawable.slot_bike_available));
        this.bikeSlot9.setImageDrawable(getResources().getDrawable(R.drawable.slot_bike_available));
        this.bikeSlot10.setImageDrawable(getResources().getDrawable(R.drawable.slot_bike_available));
    }

    public void onFetchedParking(String result) {
        if (result != null && !result.equalsIgnoreCase("")) {
            setAllBikeSlotsAvailable();
            this.isBookedBikeSlot1 = false;
            this.isBookedBikeSlot2 = false;
            this.isBookedBikeSlot3 = false;
            this.isBookedBikeSlot4 = false;
            this.isBookedBikeSlot5 = false;
            this.isBookedBikeSlot6 = false;
            this.isBookedBikeSlot7 = false;
            this.isBookedBikeSlot8 = false;
            this.isBookedBikeSlot9 = false;
            this.isBookedBikeSlot10 = false;
            for (int i = 1; i <= 10; i++) {
                String sid = "B" + i;
                if (result.contains(sid)) {
                    if (i == 1) this.isBookedBikeSlot1 = true;
                    else if (i == 2) this.isBookedBikeSlot2 = true;
                    else if (i == 3) this.isBookedBikeSlot3 = true;
                    else if (i == 4) this.isBookedBikeSlot4 = true;
                    else if (i == 5) this.isBookedBikeSlot5 = true;
                    else if (i == 6) this.isBookedBikeSlot6 = true;
                    else if (i == 7) this.isBookedBikeSlot7 = true;
                    else if (i == 8) this.isBookedBikeSlot8 = true;
                    else if (i == 9) this.isBookedBikeSlot9 = true;
                    else if (i == 10) this.isBookedBikeSlot10 = true;
                }
            }
            setBikeSlotDrawables();
            return;
        }
        Toast.makeText(this, "Error in connecting to server. Please try again later.", 1).show();
    }

    public void updateSlotUi(String slotId, boolean booked) {
        runOnUiThread(() -> {
            if ("B1".equals(slotId)) { this.isBookedBikeSlot1 = booked; setBikeSlotDrawables(); }
            else if ("B2".equals(slotId)) { this.isBookedBikeSlot2 = booked; setBikeSlotDrawables(); }
            else if ("B3".equals(slotId)) { this.isBookedBikeSlot3 = booked; setBikeSlotDrawables(); }
            else if ("B4".equals(slotId)) { this.isBookedBikeSlot4 = booked; setBikeSlotDrawables(); }
            else if ("B5".equals(slotId)) { this.isBookedBikeSlot5 = booked; setBikeSlotDrawables(); }
            else if ("B6".equals(slotId)) { this.isBookedBikeSlot6 = booked; setBikeSlotDrawables(); }
            else if ("B7".equals(slotId)) { this.isBookedBikeSlot7 = booked; setBikeSlotDrawables(); }
            else if ("B8".equals(slotId)) { this.isBookedBikeSlot8 = booked; setBikeSlotDrawables(); }
            else if ("B9".equals(slotId)) { this.isBookedBikeSlot9 = booked; setBikeSlotDrawables(); }
            else if ("B10".equals(slotId)) { this.isBookedBikeSlot10 = booked; setBikeSlotDrawables(); }
        });
    }

    public void onSuccessfulUpdateParking(String result, String slot_id) {
        this.utils.showSuccessAlert(this, slot_id);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bikeSlot1) handleBikeSlotClick("B1", this.isBookedBikeSlot1);
        else if (id == R.id.bikeSlot2) handleBikeSlotClick("B2", this.isBookedBikeSlot2);
        else if (id == R.id.bikeSlot3) handleBikeSlotClick("B3", this.isBookedBikeSlot3);
        else if (id == R.id.bikeSlot4) handleBikeSlotClick("B4", this.isBookedBikeSlot4);
        else if (id == R.id.bikeSlot5) handleBikeSlotClick("B5", this.isBookedBikeSlot5);
        else if (id == R.id.bikeSlot6) handleBikeSlotClick("B6", this.isBookedBikeSlot6);
        else if (id == R.id.bikeSlot7) handleBikeSlotClick("B7", this.isBookedBikeSlot7);
        else if (id == R.id.bikeSlot8) handleBikeSlotClick("B8", this.isBookedBikeSlot8);
        else if (id == R.id.bikeSlot9) handleBikeSlotClick("B9", this.isBookedBikeSlot9);
        else if (id == R.id.bikeSlot10) handleBikeSlotClick("B10", this.isBookedBikeSlot10);
    }

    private boolean isBookedByCurrentUser(String slotId) {
        return currentUsername != null && bikeSlotBookedBy != null && currentUsername.equals(bikeSlotBookedBy.get(slotId));
    }

    private void handleBikeSlotClick(String slotId, boolean currentlyBooked) {
        Log.d(ParkingRepository.TAG, "[BikeSlot] Slot CLICKED: " + slotId + " currentlyBooked=" + currentlyBooked + " isAdmin=" + this.isAdminLoggedIn);
        if (!this.utils.isNetworkConnected(this.context)) {
            Log.w(ParkingRepository.TAG, "[BikeSlot] No network - showing error");
            this.utils.showNetworkError(this.context);
            return;
        }
        if (currentlyBooked && !this.isAdminLoggedIn && isBookedByCurrentUser(slotId)) {
            Log.d(ParkingRepository.TAG, "[BikeSlot] Slot " + slotId + " booked by current user -> show cancel dialog");
            this.utils.showCancelBookingAlert(this.context, slotId, () -> cancelBikeSlot(slotId));
            return;
        }
        if (currentlyBooked && !this.isAdminLoggedIn) {
            Log.d(ParkingRepository.TAG, "[BikeSlot] Slot " + slotId + " already booked by someone else -> show already booked alert");
            this.utils.showAlreadyBookedAlert(this.context, slotId);
            return;
        }
        if (this.isAdminLoggedIn && currentlyBooked) {
            Log.d(ParkingRepository.TAG, "[BikeSlot] Admin releasing slot " + slotId + " -> show reset confirm");
            this.utils.showResetConfirmAlert(this.context, slotId, true,
                    () -> releaseBikeSlot(slotId));
            return;
        }
        if (!currentlyBooked) {
            Log.d(ParkingRepository.TAG, "[BikeSlot] Slot " + slotId + " available -> show booking dialog");
            BookingDialogHelper.showBookingDialog(this, slotId, false,
                    (s, hours, amount) -> bookBikeSlot(s, hours, amount));
        }
    }

    private void bookBikeSlot(String slotId, int hours, double amount) {
        Log.d(ParkingRepository.TAG, "[BikeSlot] bookBikeSlot called: " + slotId + " hours=" + hours + " amount=$" + (int) amount + " user=" + currentUsername);
        progressDialog = ProgressDialogHelper.show(this, "Updating booking...");
        parkingRepository.bookBikeSlot(slotId, hours, amount, currentUsername != null ? currentUsername : "",
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(BikeSlotSection.this, progressDialog);
                    progressDialog = null;
                    BikeSlotSection.this.setBookedBikeSlot(slotId, true);
                    if (BikeSlotSection.this.bikeSlotBookedBy == null) BikeSlotSection.this.bikeSlotBookedBy = new HashMap<>();
                    BikeSlotSection.this.bikeSlotBookedBy.put(slotId, BikeSlotSection.this.currentUsername != null ? BikeSlotSection.this.currentUsername : "");
                    BikeSlotSection.this.setBikeSlotDrawables();
                    BikeSlotSection.this.utils.showAlert(BikeSlotSection.this, "Booked! Slot " + slotId + ", " + hours + " hr(s), $" + (int) amount, false, false);
                }),
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(BikeSlotSection.this, progressDialog);
                    progressDialog = null;
                    BikeSlotSection.this.utils.showCommonAlert(BikeSlotSection.this, "Error", "Failed to book. Try again.");
                }));
    }

    private void releaseBikeSlot(String slotId) {
        Log.d(ParkingRepository.TAG, "[BikeSlot] releaseBikeSlot called: " + slotId);
        progressDialog = ProgressDialogHelper.show(this, "Updating...");
        parkingRepository.releaseBikeSlot(slotId,
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(BikeSlotSection.this, progressDialog);
                    progressDialog = null;
                    onSuccessfulResetParking(slotId);
                }),
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(BikeSlotSection.this, progressDialog);
                    progressDialog = null;
                    BikeSlotSection.this.utils.showCommonAlert(BikeSlotSection.this, "Error", "Failed to release slot.");
                }));
    }

    /** User cancels their own booking; updates Firebase and shows "Booking cancelled". */
    private void cancelBikeSlot(String slotId) {
        Log.d(ParkingRepository.TAG, "[BikeSlot] cancelBikeSlot called: " + slotId);
        progressDialog = ProgressDialogHelper.show(this, "Cancelling...");
        parkingRepository.releaseBikeSlot(slotId,
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(BikeSlotSection.this, progressDialog);
                    progressDialog = null;
                    onCancelBookingSuccess(slotId);
                }),
                () -> runOnUiThread(() -> {
                    ProgressDialogHelper.dismiss(BikeSlotSection.this, progressDialog);
                    progressDialog = null;
                    BikeSlotSection.this.utils.showCommonAlert(BikeSlotSection.this, "Error", "Failed to cancel booking.");
                }));
    }

    private void onCancelBookingSuccess(String slotId) {
        runOnUiThread(() -> {
            if (bikeSlotBookedBy != null) bikeSlotBookedBy.remove(slotId);
            setBookedBikeSlot(slotId, false);
            setBikeSlotDrawables();
            this.utils.showAlert(this, "Booking cancelled for " + slotId, false, false);
        });
    }

    public void onSuccessfulResetParking(String result) {
        this.utils.showResetSuccessAlert(this, result);
    }
}
