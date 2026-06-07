# Debugging & Verifying Firebase Data

## 1. View logs (Android Studio / Logcat)

All app actions use the same log tag so you can filter in one place.

- **Tag:** `WhereToPark`
- In **Logcat**, set the filter to: `tag:WhereToPark` (or search for `WhereToPark`).

### What you’ll see

| Action | Log message (pattern) |
|--------|------------------------|
| Screen open | `[CarSlot] init: isAdmin=... attaching Firebase listener` |
| Firebase listener attached | `[Firebase] Attaching listener for car slots: /parking/car` |
| Data received from Firebase | `[Firebase] Car slots FETCHED: {C1=false, C2=true, ...}` |
| User taps a slot | `[CarSlot] Slot CLICKED: C2 currentlyBooked=false isAdmin=false` |
| Booking dialog shown | `[Booking] Dialog SHOW: slotId=C2 vehicle=Car rate=$100/hr` |
| User taps Next | `[Booking] User tapped Next: slotId=C2 hours=2 amount=$200` |
| User confirms | `[Booking] User CONFIRMED: slotId=C2 hours=2 amount=$200 -> calling Firebase` |
| Write to Firebase | `[Firebase] WRITE bookCarSlot: slotId=C2 hours=2 amount=200 start=... end=...` |
| Write success | `[Firebase] bookCarSlot SUCCESS: C2` |
| Write failure | `[Firebase] bookCarSlot FAILED: C2 <message>` |
| UI updated from Firebase | `[CarSlot] Firebase data received -> applying UI: {...}` |

Same pattern applies for bike slots with `[BikeSlot]` and `[Firebase] ... bike ...`.

---

## 2. Verify data in Firebase (Realtime Database)

You can confirm that data is stored and updated correctly in Firebase.

1. Open [Firebase Console](https://console.firebase.google.com/) → your project.
2. Go to **Build → Realtime Database**.
3. You’ll see the tree:
   - `parking`
     - `car` → `C1`, `C2`, `C3`, `C4`, `C5`
     - `bike` → `B1`, `B2`, `B3`, `B4`, `B5`

### What to check

- **After opening Car/Bike screen:**  
  If the app has written at least once, you’ll see keys under `parking/car` and `parking/bike`.  
  Listener logs in Logcat confirm that **data is being fetched** (`[Firebase] ... FETCHED: ...`).

- **After booking a slot (e.g. C2):**  
  Under `parking/car/C2` you should see an object like:
  - `isBooked: true`
  - `startTime`, `endTime` (date/time strings)
  - `vehicleType: "car"`
  - `slotId: "C2"`
  - `hours`, `amount`

- **After releasing (admin):**  
  Same path (e.g. `parking/car/C2`) should show `isBooked: false` and usually `hours: 0`, `amount: 0`.

- **Real-time:**  
  Leave the Firebase Console tab open and book/release from the app; the tree updates live. That confirms **data is being updated** in Firebase.

---

## 3. Quick checklist

- **Data fetched?**  
  Logcat shows `[Firebase] Car slots FETCHED: ...` / `Bike slots FETCHED: ...` when you open the screen and when data changes.

- **Data written?**  
  Logcat shows `[Firebase] WRITE bookCarSlot/...` then `... SUCCESS: <slotId>` (or `... FAILED`).

- **Data correct in Firebase?**  
  Firebase Console → Realtime Database → `parking/car` and `parking/bike` show the same slot IDs and `isBooked` (and booking details when booked).

Using **Logcat with tag `WhereToPark`** plus **Firebase Console → Realtime Database** is enough to verify that data is fetched and updated properly.
