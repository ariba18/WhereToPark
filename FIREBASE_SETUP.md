# Firebase setup for WhereToPark

The app uses **Firebase Realtime Database** only: for parking slot sync and for a **basic users table** (username + password). No OTP, no email validation, no Firebase Auth.

1. **Create a Firebase project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Add a project (or use an existing one)

2. **Add an Android app**
   - Register your app with package name: `com.example.wheretoparkproject`
   - Download `google-services.json` and **replace** the placeholder file in `app/google-services.json`

3. **Enable Realtime Database**
   - In Firebase Console → Build → Realtime Database → Create Database
   - Choose a location and start in **test mode** (or set rules as below)
   - Example rules for development (parking + users, open read/write):
     ```json
     {
       "rules": {
         "parking": {
           ".read": true,
           ".write": true
         },
         "users": {
           ".read": true,
           ".write": true
         }
       }
     }
     ```

4. **Database structure**
   - **Parking:** `parking/car/C1` … `C10` and `parking/bike/B1` … `B10`
     - Available: `{ "isBooked": false, "hours": 0, "amount": 0 }` (or empty)
     - Booked: `{ "isBooked": true, "startTime": "...", "endTime": "...", "vehicleType": "car"|"bike", "slotId": "C1", "hours": 3, "amount": 300, "bookedBy": "username" }` — **bookedBy** is the logged-in username; slots booked by the current user show in **green**, others in **grey**.
   - **Users (basic table):** `users/{usernameKey}` with `username`, `password` (plain). Sign up inserts a row; sign in checks username/password and then opens vehicle selection.
   - Car: $100/hr, Bike: $50/hr. All users see the same availability in real time.

After replacing `google-services.json` and creating the database, rebuild and run. Login screen → sign in or sign up (username + password) → vehicle selection.
