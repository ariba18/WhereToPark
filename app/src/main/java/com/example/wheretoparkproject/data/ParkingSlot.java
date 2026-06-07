package com.example.wheretoparkproject.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Model for a parking slot in Firebase.
 * Fields: isBooked, startTime, endTime, vehicleType, slotId, hours, amount, bookedBy
 */
public class ParkingSlot {
    public boolean isBooked;
    public String startTime;
    public String endTime;
    public String vehicleType;
    public String slotId;
    public int hours;
    public double amount;
    /** Username who booked this slot; null if not booked or released. */
    public String bookedBy;

    public ParkingSlot() {
    }

    public ParkingSlot(boolean isBooked, String startTime, String endTime, String vehicleType,
                       String slotId, int hours, double amount) {
        this(isBooked, startTime, endTime, vehicleType, slotId, hours, amount, null);
    }

    public ParkingSlot(boolean isBooked, String startTime, String endTime, String vehicleType,
                       String slotId, int hours, double amount, String bookedBy) {
        this.isBooked = isBooked;
        this.startTime = startTime;
        this.endTime = endTime;
        this.vehicleType = vehicleType;
        this.slotId = slotId;
        this.hours = hours;
        this.amount = amount;
        this.bookedBy = bookedBy;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("isBooked", isBooked);
        if (startTime != null) map.put("startTime", startTime);
        if (endTime != null) map.put("endTime", endTime);
        if (vehicleType != null) map.put("vehicleType", vehicleType);
        if (slotId != null) map.put("slotId", slotId);
        map.put("hours", hours);
        map.put("amount", amount);
        if (bookedBy != null) map.put("bookedBy", bookedBy);
        return map;
    }

    public static ParkingSlot fromMap(Map<String, Object> map) {
        ParkingSlot s = new ParkingSlot();
        if (map == null) return s;
        Object b = map.get("isBooked");
        s.isBooked = b instanceof Boolean && (Boolean) b;
        s.startTime = (String) map.get("startTime");
        s.endTime = (String) map.get("endTime");
        s.vehicleType = (String) map.get("vehicleType");
        s.slotId = (String) map.get("slotId");
        Object h = map.get("hours");
        s.hours = h instanceof Number ? ((Number) h).intValue() : 0;
        Object a = map.get("amount");
        s.amount = a instanceof Number ? ((Number) a).doubleValue() : 0;
        s.bookedBy = (String) map.get("bookedBy");
        return s;
    }
}
