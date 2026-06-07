package com.example.wheretoparkproject.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Firebase Realtime Database for parking slots.
 * Structure: parking/car/C1..C10, parking/bike/B1..B10
 * Each slot: { isBooked, startTime, endTime, vehicleType, slotId, hours, amount, bookedBy }
 */
public class ParkingRepository {
    public static final String TAG = "DebugWTP";
    private static final String ROOT = "parking";
    private static final String CAR = "car";
    private static final String BIKE = "bike";

    public static final String[] CAR_SLOT_IDS = {"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10"};
    public static final String[] BIKE_SLOT_IDS = {"B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10"};

    private static final SimpleDateFormat END_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private final DatabaseReference carRef;
    private final DatabaseReference bikeRef;

    public ParkingRepository() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        carRef = db.getReference(ROOT).child(CAR);
        bikeRef = db.getReference(ROOT).child(BIKE);
    }

    public interface SlotStatusListener {
        void onCarSlots(Map<String, Boolean> slotBooked, Map<String, String> slotBookedBy);
        void onBikeSlots(Map<String, Boolean> slotBooked, Map<String, String> slotBookedBy);
        void onError(String message);
    }

    public ValueEventListener listenCarSlots(SlotStatusListener listener) {
        Log.d(TAG, "[Firebase] Attaching listener for car slots: " + carRef.getPath());
        return carRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    Log.d(TAG, "[Firebase] Car slots empty — initializing parking/car so it appears in Firebase");
                    ensureDefaultCarSlots();
                }
                releaseExpiredSlots(snapshot, carRef, CAR, CAR_SLOT_IDS);
                Map<String, Boolean> slotBooked = parseSlotsWithExpiry(snapshot, CAR_SLOT_IDS);
                Map<String, String> slotBookedBy = parseSlotsBookedByWithExpiry(snapshot, CAR_SLOT_IDS);
                Log.d(TAG, "[Firebase] Car slots FETCHED: " + slotBooked + " bookedBy=" + slotBookedBy);
                if (listener != null) listener.onCarSlots(slotBooked, slotBookedBy);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "[Firebase] Car slots listen FAILED: " + error.getMessage(), error.toException());
                if (listener != null) listener.onError(error.getMessage());
            }
        });
    }

    public ValueEventListener listenBikeSlots(SlotStatusListener listener) {
        Log.d(TAG, "[Firebase] Attaching listener for bike slots: " + bikeRef.getPath());
        return bikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    Log.d(TAG, "[Firebase] Bike slots empty — initializing parking/bike so it appears in Firebase");
                    ensureDefaultBikeSlots();
                }
                releaseExpiredSlots(snapshot, bikeRef, BIKE, BIKE_SLOT_IDS);
                Map<String, Boolean> slotBooked = parseSlotsWithExpiry(snapshot, BIKE_SLOT_IDS);
                Map<String, String> slotBookedBy = parseSlotsBookedByWithExpiry(snapshot, BIKE_SLOT_IDS);
                Log.d(TAG, "[Firebase] Bike slots FETCHED: " + slotBooked + " bookedBy=" + slotBookedBy);
                if (listener != null) listener.onBikeSlots(slotBooked, slotBookedBy);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "[Firebase] Bike slots listen FAILED: " + error.getMessage(), error.toException());
                if (listener != null) listener.onError(error.getMessage());
            }
        });
    }

    /** Create parking/car and parking/bike with default empty slots so the tree appears in Firebase Console. */
    private void ensureDefaultCarSlots() {
        for (String slotId : CAR_SLOT_IDS) {
            ParkingSlot slot = new ParkingSlot(false, null, null, CAR, slotId, 0, 0);
            carRef.child(slotId).setValue(slot.toMap())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "[Firebase] Initialized car slot: " + slotId))
                    .addOnFailureListener(e -> Log.e(TAG, "[Firebase] Init car slot failed: " + slotId + " " + e.getMessage()));
        }
    }

    private void ensureDefaultBikeSlots() {
        for (String slotId : BIKE_SLOT_IDS) {
            ParkingSlot slot = new ParkingSlot(false, null, null, BIKE, slotId, 0, 0);
            bikeRef.child(slotId).setValue(slot.toMap())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "[Firebase] Initialized bike slot: " + slotId))
                    .addOnFailureListener(e -> Log.e(TAG, "[Firebase] Init bike slot failed: " + slotId + " " + e.getMessage()));
        }
    }

    private Map<String, Boolean> parseSlots(DataSnapshot snapshot, String[] slotIds) {
        Map<String, Boolean> map = new HashMap<>();
        for (String id : slotIds) map.put(id, false);
        for (DataSnapshot child : snapshot.getChildren()) {
            String key = child.getKey();
            if (key == null) continue;
            Object val = child.getValue();
            boolean booked = false;
            if (val instanceof Map) {
                Object b = ((Map<?, ?>) val).get("isBooked");
                booked = b instanceof Boolean && (Boolean) b;
            } else if (val != null && val.toString().trim().length() > 0) {
                booked = true;
            }
            map.put(key, booked);
        }
        return map;
    }

    private Map<String, String> parseSlotsBookedBy(DataSnapshot snapshot, String[] slotIds) {
        Map<String, String> map = new HashMap<>();
        for (String id : slotIds) map.put(id, null);
        for (DataSnapshot child : snapshot.getChildren()) {
            String key = child.getKey();
            if (key == null) continue;
            Object val = child.getValue();
            String bookedBy = null;
            if (val instanceof Map) {
                Object o = ((Map<?, ?>) val).get("bookedBy");
                bookedBy = o != null ? o.toString() : null;
            }
            map.put(key, bookedBy);
        }
        return map;
    }

    /** If slot is booked and endTime is in the past, release it in Firebase (auto-expire). */
    private void releaseExpiredSlots(DataSnapshot snapshot, DatabaseReference ref, String vehicleType, String[] slotIds) {
        long now = System.currentTimeMillis();
        for (DataSnapshot child : snapshot.getChildren()) {
            String slotId = child.getKey();
            if (slotId == null) continue;
            Object val = child.getValue();
            if (!(val instanceof Map)) continue;
            Map<?, ?> map = (Map<?, ?>) val;
            Object b = map.get("isBooked");
            if (!(b instanceof Boolean) || !(Boolean) b) continue;
            Object endTimeObj = map.get("endTime");
            if (endTimeObj == null || endTimeObj.toString().trim().isEmpty()) continue;
            try {
                Date endDate = END_TIME_FORMAT.parse(endTimeObj.toString().trim());
                if (endDate != null && endDate.getTime() < now) {
                    Log.d(TAG, "[Firebase] Auto-releasing expired slot: " + slotId + " endTime=" + endTimeObj);
                    ParkingSlot slot = new ParkingSlot(false, null, null, vehicleType, slotId, 0, 0);
                    ref.child(slotId).setValue(slot.toMap())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "[Firebase] Expired slot released: " + slotId))
                            .addOnFailureListener(e -> Log.e(TAG, "[Firebase] Expired release failed: " + slotId, e));
                }
            } catch (ParseException e) {
                Log.w(TAG, "[Firebase] Parse endTime failed for " + slotId + ": " + endTimeObj, e);
            }
        }
    }

    /** Like parseSlots but treats expired slots (endTime in past) as not booked so UI shows them available immediately. */
    private Map<String, Boolean> parseSlotsWithExpiry(DataSnapshot snapshot, String[] slotIds) {
        Map<String, Boolean> map = new HashMap<>();
        for (String id : slotIds) map.put(id, false);
        long now = System.currentTimeMillis();
        for (DataSnapshot child : snapshot.getChildren()) {
            String key = child.getKey();
            if (key == null) continue;
            Object val = child.getValue();
            boolean booked = false;
            if (val instanceof Map) {
                Map<?, ?> m = (Map<?, ?>) val;
                Object b = m.get("isBooked");
                booked = b instanceof Boolean && (Boolean) b;
                if (booked) {
                    Object endTimeObj = m.get("endTime");
                    if (endTimeObj != null && !endTimeObj.toString().trim().isEmpty()) {
                        try {
                            Date endDate = END_TIME_FORMAT.parse(endTimeObj.toString().trim());
                            if (endDate != null && endDate.getTime() < now) booked = false;
                        } catch (ParseException ignored) { }
                    }
                }
            } else if (val != null && val.toString().trim().length() > 0) {
                booked = true;
            }
            map.put(key, booked);
        }
        return map;
    }

    /** Like parseSlotsBookedBy but clears bookedBy for expired slots. */
    private Map<String, String> parseSlotsBookedByWithExpiry(DataSnapshot snapshot, String[] slotIds) {
        Map<String, String> map = new HashMap<>();
        for (String id : slotIds) map.put(id, null);
        long now = System.currentTimeMillis();
        for (DataSnapshot child : snapshot.getChildren()) {
            String key = child.getKey();
            if (key == null) continue;
            Object val = child.getValue();
            String bookedBy = null;
            if (val instanceof Map) {
                Map<?, ?> m = (Map<?, ?>) val;
                Object b = m.get("isBooked");
                boolean booked = b instanceof Boolean && (Boolean) b;
                if (booked) {
                    Object endTimeObj = m.get("endTime");
                    boolean expired = false;
                    if (endTimeObj != null && !endTimeObj.toString().trim().isEmpty()) {
                        try {
                            Date endDate = END_TIME_FORMAT.parse(endTimeObj.toString().trim());
                            if (endDate != null && endDate.getTime() < now) expired = true;
                        } catch (ParseException ignored) { }
                    }
                    if (!expired) {
                        Object o = m.get("bookedBy");
                        bookedBy = o != null ? o.toString() : null;
                    }
                }
            }
            map.put(key, bookedBy);
        }
        return map;
    }

    /** Book a car slot with start/end time, hours, amount, bookedBy (username). */
    public void bookCarSlot(String slotId, int hours, double amount, String bookedBy, Runnable onSuccess, Runnable onError) {
        long now = System.currentTimeMillis();
        long endMs = now + (hours * 3600L * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String startTime = sdf.format(new Date(now));
        String endTime = sdf.format(new Date(endMs));
        ParkingSlot slot = new ParkingSlot(true, startTime, endTime, "car", slotId, hours, amount, bookedBy);
        Log.d(TAG, "[Firebase] WRITE bookCarSlot: slotId=" + slotId + " bookedBy=" + bookedBy + " hours=" + hours + " amount=" + amount);
        carRef.child(slotId).setValue(slot.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "[Firebase] bookCarSlot SUCCESS: " + slotId);
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "[Firebase] bookCarSlot FAILED: " + slotId + " " + e.getMessage(), e);
                    if (onError != null) onError.run();
                });
    }

    /** Book a bike slot with bookedBy (username). */
    public void bookBikeSlot(String slotId, int hours, double amount, String bookedBy, Runnable onSuccess, Runnable onError) {
        long now = System.currentTimeMillis();
        long endMs = now + (hours * 3600L * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String startTime = sdf.format(new Date(now));
        String endTime = sdf.format(new Date(endMs));
        ParkingSlot slot = new ParkingSlot(true, startTime, endTime, "bike", slotId, hours, amount, bookedBy);
        Log.d(TAG, "[Firebase] WRITE bookBikeSlot: slotId=" + slotId + " bookedBy=" + bookedBy + " hours=" + hours + " amount=" + amount);
        bikeRef.child(slotId).setValue(slot.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "[Firebase] bookBikeSlot SUCCESS: " + slotId);
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "[Firebase] bookBikeSlot FAILED: " + slotId + " " + e.getMessage(), e);
                    if (onError != null) onError.run();
                });
    }

    /** Release a car slot (set isBooked false, clear booking data). */
    public void releaseCarSlot(String slotId, Runnable onSuccess, Runnable onError) {
        Log.d(TAG, "[Firebase] WRITE releaseCarSlot: slotId=" + slotId);
        ParkingSlot slot = new ParkingSlot(false, null, null, "car", slotId, 0, 0);
        carRef.child(slotId).setValue(slot.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "[Firebase] releaseCarSlot SUCCESS: " + slotId);
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "[Firebase] releaseCarSlot FAILED: " + slotId + " " + e.getMessage(), e);
                    if (onError != null) onError.run();
                });
    }

    /** Release a bike slot. */
    public void releaseBikeSlot(String slotId, Runnable onSuccess, Runnable onError) {
        Log.d(TAG, "[Firebase] WRITE releaseBikeSlot: slotId=" + slotId);
        ParkingSlot slot = new ParkingSlot(false, null, null, "bike", slotId, 0, 0);
        bikeRef.child(slotId).setValue(slot.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "[Firebase] releaseBikeSlot SUCCESS: " + slotId);
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "[Firebase] releaseBikeSlot FAILED: " + slotId + " " + e.getMessage(), e);
                    if (onError != null) onError.run();
                });
    }

    public void removeListener(DatabaseReference ref, ValueEventListener listener) {
        if (ref != null && listener != null) ref.removeEventListener(listener);
    }

    public DatabaseReference getCarRef() { return carRef; }
    public DatabaseReference getBikeRef() { return bikeRef; }
}
