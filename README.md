# 🅿️ Where to Park

An Android app for finding and booking parking slots for cars and bikes, with Google Maps integration and Firebase backend — built for Pune, India.

---

## ✨ Features

- 🔐 **Login & Register** — Firebase Authentication for secure user sign-in and account creation
- 🚗 **Car Parking** — 10 car slots (C1–C10), colour-coded for availability — ₹100/hr
- 🛵 **Bike Parking** — 10 bike slots (B1–B10), colour-coded for availability — ₹50/hr
- 📍 **Slot Map View** — Tap the pin on any slot to see its exact location on Google Maps
- 🗺️ **Navigate to Slot** — Get turn-by-turn directions from your current location
- ⏱️ **Hour-based Booking** — Select hours, see live total, confirm booking in one flow
- 🔴🟢 **Live Slot Status** — Green = booked by you, Blue = available, Grey = taken
- 👨‍💼 **Admin Panel** — Admins can register vehicles and manage the parking system
- 🔄 **Firebase Realtime DB** — Slot availability syncs in real time across all users

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java |
| UI | XML Layouts |
| Auth | Firebase Authentication |
| Database | Firebase Realtime Database |
| Maps | Google Maps SDK for Android |
| Build | Gradle |
| IDE | Android Studio |

---

## 📂 Project Structure

```
WhereToParkProject/
│
├── app/
│   ├── src/main/
│   │   ├── java/com/example/wheretoparkproject/
│   │   │   └── activities/
│   │   │       ├── LoginActivity.java          ← App entry point & Firebase login
│   │   │       ├── RegisterActivity.java        ← New user registration
│   │   │       ├── VehicleSelection.java        ← Choose Car or Bike parking
│   │   │       ├── CarSlotSection.java          ← Car slots grid (C1–C10)
│   │   │       ├── BikeSlotSection.java         ← Bike slots grid (B1–B10)
│   │   │       ├── VehicleNo.java               ← Enter vehicle number before booking
│   │   │       ├── ParkingMapActivity.java      ← In-app Google Maps for a slot
│   │   │       ├── AdminChoice.java             ← Admin dashboard
│   │   │       ├── AdminVehicleRegistar.java    ← Admin vehicle registration
│   │   │       └── About.java                  ← App info screen
│   │   │
│   │   ├── res/
│   │   │   ├── layout/                         ← All XML UI layouts
│   │   │   ├── values/
│   │   │   │   ├── strings.xml                 ← App strings
│   │   │   │   ├── colors.xml                  ← Color palette
│   │   │   │   └── themes.xml                  ← App theme
│   │   │   └── xml/
│   │   │       ├── backup_rules.xml
│   │   │       └── data_extraction_rules.xml
│   │   │
│   │   └── AndroidManifest.xml                 ← Permissions, activities, Maps API key
│   │
│   └── build.gradle                            ← App-level dependencies
│
├── screenshots/                                ← App screenshots
├── build.gradle                                ← Project-level Gradle config
├── local.properties                            ← 🔒 API keys (NOT committed to Git)
├── google-services.json                        ← 🔒 Firebase config (NOT committed to Git)
└── .gitignore
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (Hedgehog or newer recommended)
- A [Google Maps API Key](https://console.cloud.google.com/)
- A [Firebase project](https://console.firebase.google.com/) with Auth and Realtime Database enabled
- Android device or emulator running API 21+

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/ariba18/WhereToPark.git
cd WhereToPark
```

**2. Open in Android Studio**
```
File → Open → Select the cloned folder
```

**3. Add your Google Maps API Key**

In `local.properties` (create it if it doesn't exist):
```
MAPS_API_KEY=your_google_maps_api_key_here
```

**4. Add Firebase config**

- Go to [Firebase Console](https://console.firebase.google.com/) → your project → Add Android app
- Download `google-services.json` and place it inside the `app/` folder

**5. Sync & Run**

Click **Sync Now** in Android Studio, then hit ▶️ Run.

> ⚠️ `local.properties` and `google-services.json` contain sensitive keys and are excluded from Git via `.gitignore`. Never commit them.

---

## 🔑 Google Maps API Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create or select a project
3. Enable **Maps SDK for Android** and **Directions API**
4. Go to **Credentials → Create Credentials → API Key**
5. Paste the key into `local.properties`:
   ```
   MAPS_API_KEY=AIza...your_key_here
   ```

---

## 📋 Permissions

| Permission | Why it's needed |
|------------|-----------------|
| `INTERNET` | Firebase & Maps network calls |
| `ACCESS_NETWORK_STATE` | Check connectivity before requests |
| `ACCESS_WIFI_STATE` | Wi-Fi state detection |
| `WRITE_EXTERNAL_STORAGE` | Save local data |
| `READ_EXTERNAL_STORAGE` | Read local data |
| `RECEIVE_BOOT_COMPLETED` | Restart background tasks on reboot |
| `READ_PHONE_STATE` | Device identification |
| `ACCESS_COARSE_LOCATION` | Approximate location for nearby slots |
| `ACCESS_FINE_LOCATION` | Precise GPS for navigation |

---

## 🤝 Contributing

1. Fork the repo
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 📸 Screenshots

| Login | Vehicle Select | Car Slots |
|:-----:|:--------------:|:---------:|
| ![Login](screenshots/login.jpeg) | ![Vehicle](screenshots/vehicle_selection.jpeg) | ![Car Slots](screenshots/car_slots.jpeg) |

| Book Hours | Confirm Booking | Bike Slots |
|:----------:|:---------------:|:----------:|
| ![Book Hours](screenshots/car_booking_hours.jpeg) | ![Confirm](screenshots/car_booking_confirm.jpeg) | ![Bike Slots](screenshots/bike_slots.jpeg) |

| In-App Map | Google Maps View |
|:----------:|:----------------:|
| ![Map](screenshots/map_inapp.jpeg) | ![Google Maps](screenshots/map_google.jpeg) |

---

> ⭐ If this project helped you, give it a star!
