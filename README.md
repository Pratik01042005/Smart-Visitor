# 🏢 MeetPoint – Visitor Management System (Android)

MeetPoint is an **Android-based Visitor Management System** designed for offices, societies, institutions, and organizations to efficiently manage **visitor entry, exit, approvals, and records** in a digital and secure way.

---

## 📱 App Overview

MeetPoint allows admins to:

* Register visitor entry with photo and details
* Track visitor exit
* Manage visitor vehicles
* Approve / Reject visitors (Super Admin)
* View complete visitor history
* Call “Whom to Meet” directly from the app

---

## 🚀 Features

### 👤 Visitor Entry

* Auto-generated unique token
* Visitor photo capture (Camera)
* Visit date & time
* Purpose of visit
* Temporary & permanent address
* Phone number & email
* Whom to meet (with call button)
* Optional vehicle details (number + photo)

### 🚪 Visitor Exit

* Exit date & time
* Remark
* Will visit again (Yes / No)
* Next visit date & time (if applicable)

### 🛡️ Admin / Super Admin

* View all visitors
* Visitor status:

  * Pending
  * Approved
  * Rejected
* Full visitor detail screen
* Edit / Delete visitor
* Vehicle details view

## 🛠️ Tech Stack

| Layer         | Technology                |
| ------------- | ------------------------- |
| Platform      | Android                   |
| Language      | Java                      |
| IDE           | Android Studio            |
| Database      | Firebase Firestore        |
| Auth          | Firebase Authentication   |
| UI            | XML + Material Components |
| Image Loading | Glide                     |
| Camera        | CameraX                   |
| Architecture  | Activity-based            |
| Min SDK       | 24                        |
| Target SDK    | 34                        |

---

## 📂 Project Structure

```
com.example.meetpoint
│
├── activities
│   ├── AddVisitorActivity.java
│   ├── VisitorDetailActivity.java
│   ├── VisitorDetailSAActivity.java
│
├── adapters
│   ├── VisitorAdapter.java
│   ├── VisitorAdapterSA.java
│
├── models
│   └── VisitorModel.java
│
├── utils
│   └── (helpers if any)
│
└── res
    ├── layout
    ├── drawable
    ├── values
```

---

## 🧾 Firestore Structure

```
Visitors (collection)
 └── visitorId (document)
      ├── token
      ├── name
      ├── purpose
      ├── visitDateTime
      ├── phone
      ├── email
      ├── tempAddressLine
      ├── permAddressLine
      ├── photoUrl
      ├── status (pending / approved / rejected)
      ├── exitDateTime
      ├── againVisit
      ├── nextVisit
      └── timestamp

      └── Vehicle (sub-collection)
           └── vehicle
                ├── vehicleNumber
                └── vehicleImage
```

---

## 🔐 Permissions Used

```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.CALL_PHONE"/>
```

---

## ▶️ How to Run the Project

1. Clone the repository

   ```bash
   git clone https://github.com/your-username/MeetPoint.git
   ```

2. Open in **Android Studio**

3. Connect Firebase:

   * Create Firebase project
   * Add Android app
   * Download `google-services.json`
   * Place it in `app/` folder

4. Sync Gradle

5. Run on real device (camera required)

## 👨‍💻 Developed By

**Pratik Lagad**
Android Developer | Java | Firebase | ML Enthusiast

---
