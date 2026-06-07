# Where and How to Verify Firebase Database

This guide explains **where** to look and **how** to check that your parking app data is stored and updated correctly in Firebase.

---

## Where to verify: Firebase Console (Realtime Database)

### Step 1: Open Firebase Console

1. In your browser go to: **https://console.firebase.google.com/**
2. Sign in with the same Google account you used for the project.
3. Click your project name (e.g. **Where to Park** or the name you gave when creating the project).

### Step 2: Open Realtime Database

1. In the **left sidebar**, click **Build** (or the “hamburger” menu if the sidebar is collapsed).
2. Under **Build**, click **Realtime Database**.
3. You’ll see either:
   - A **data view** with a tree (nodes you can expand), or  
   - A button like **Create Database** (if you haven’t created it yet — create it first, then you’ll see the tree).

**Direct link (replace `YOUR_PROJECT_ID` with your Firebase project ID):**  
`https://console.firebase.google.com/project/YOUR_PROJECT_ID/database/YOUR_PROJECT_ID-default-rtdb/data`

You can find your project ID in **Project settings** (gear icon) → **General** → **Project ID**.

---

## How the data is organized (tree structure)

Your app uses this structure:

```
parking
├── car
│   ├── C1    ← slot object (isBooked, startTime, endTime, ...)
│   ├── C2
│   ├── C3
│   ├── C4
│   └── C5
└── bike
    ├── B1
    ├── B2
    ├── B3
    ├── B4
    └── B5
```

- **Where:** Under **parking** → **car** you see slots **C1, C2, C3, C4, C5**.  
- **Where:** Under **parking** → **bike** you see slots **B1, B2, B3, B4, B5**.

---

## How to verify: what to do and what to look for

### 1. Confirm data exists (first time)

- **Where:** Firebase Console → **Realtime Database** → expand **parking**.
- **How:**  
  - Expand **parking** → **car**.  
  - The app **creates the parking node automatically** when you open Car or Bike parking (no booking needed). You should see keys **C1, C2, C3, C4, C5** under **car** and **B1–B5** under **bike** after opening those screens.  
  - Same under **parking** → **bike** for **B1–B5**.

If you don’t see **parking** at all: open the app, tap **Car** or **Bike**, and wait a moment (the app initializes default slots on first load). Then refresh the Firebase Console page.

### 2. Verify a booked slot (e.g. Car slot C2)

- **Where:** Firebase Console → **Realtime Database** → **parking** → **car** → **C2**.
- **How:**  
  - Click **C2** (or expand it).  
  - You should see an object similar to:

| Field        | Example value           | Meaning                    |
|-------------|-------------------------|----------------------------|
| `isBooked`  | `true`                  | Slot is currently booked   |
| `startTime` | `"2026-02-06 14:30:00"` | Booking start time         |
| `endTime`   | `"2026-02-06 16:30:00"` | Booking end time           |
| `vehicleType` | `"car"`               | Car or bike                |
| `slotId`    | `"C2"`                  | Slot identifier            |
| `hours`     | `2`                     | Hours booked               |
| `amount`    | `200`                   | Total amount (e.g. $200)   |

- **Verify:**  
  - `isBooked` is **true**.  
  - `hours` and `amount` match what you chose in the app (e.g. 2 hours → $200 for car).  
  - `startTime` / `endTime` are today’s date and sensible times.

### 3. Verify an available (released) slot

- **Where:** Same path, e.g. **parking** → **car** → **C2** (after releasing C2 from the app, e.g. as admin).
- **How:**  
  - Expand **C2** again.  
  - You should see something like:
    - `isBooked`: **false**
    - `hours`: **0**
    - `amount`: **0**
    - `startTime` / `endTime` may be empty or null.

That confirms the app correctly **updated** Firebase when you released the slot.

### 4. Verify updates in real time

- **Where:** Keep the Firebase Console tab open on **Realtime Database** with **parking** expanded.
- **How:**  
  1. In the app, book a slot (e.g. Car C3, 1 hour).  
  2. In the Console, look at **parking** → **car** → **C3**.  
  3. Without refreshing the page, you should see **C3** appear or change (e.g. `isBooked: true`, `hours: 1`, `amount: 100`).  
  4. Then release C3 (admin) or book then release — **C3** should update again (e.g. `isBooked: false`).

If the Console tree updates when you book/release in the app, Firebase is **receiving and storing** your updates correctly.

---

## Summary: where vs how

| What you want to verify | Where to go | How to check |
|-------------------------|------------|---------------|
| Data exists and structure | Firebase Console → Build → Realtime Database | Expand **parking** → **car** and **parking** → **bike**; confirm C1–C5 and B1–B5 appear after some usage. |
| A specific slot’s booking | Same → **parking** → **car** (or **bike**) → e.g. **C2** | Open C2; confirm `isBooked`, `startTime`, `endTime`, `hours`, `amount` match what you did in the app. |
| Data updates correctly | Same screen, leave it open | Book or release in the app; watch the same node in the Console — it should change without refreshing. |

---

## If you don’t see your project or database

- **No project:** Create a project at https://console.firebase.google.com/ and add your Android app (package `com.example.wheretoparkproject`); download `google-services.json` into the app module.
- **No Realtime Database:** In the project, go to **Build** → **Realtime Database** → **Create Database** (choose region and, for testing, start in test mode).
- **Empty tree:** Open the app and tap **Car** or **Bike** parking. The app creates **parking/car** (C1–C5) and **parking/bike** (B1–B5) with default empty slots on first load. Refresh the Console to see the **parking** node.

Using the steps above, you can verify **where** (Firebase Console → Realtime Database → **parking** → **car** / **bike** → slot) and **how** (expand nodes, check fields, and watch live updates) the Firebase DB is working.
