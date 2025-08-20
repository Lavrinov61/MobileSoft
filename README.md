# Photo Studio Booking App

This is an Android application for booking photoshoots, built with Kotlin, Material 3 (Expressive), and Firebase.

## Features

- User registration and login via Firebase Authentication.
- A list of available photoshoot services fetched from Firestore.
- A calendar view to select a date and see available time slots.
- A booking form to confirm appointments.
- A dedicated screen for users to view their own past and upcoming bookings.
- Expressive design with custom colors, shapes, and animations.
- Secure data access enforced by Firestore Security Rules.
- Push notifications for booking confirmations (client-side implementation).

## Setup Instructions

To run this application, you need to connect it to your own Firebase project.

### 1. Create a Firebase Project

1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Click "Add project" and follow the on-screen instructions to create a new project.

### 2. Configure Firebase for Android

1.  Inside your new Firebase project, click the Android icon to add a new Android app.
2.  For the "Android package name", enter `com.fmagnus.photostudio`.
3.  Click "Register app".
4.  Download the `google-services.json` file.
5.  **Crucial:** Place the downloaded `google-services.json` file into the `app/` directory of this project, replacing the placeholder file.

### 3. Set up Firebase Services

In the Firebase Console, navigate to the following sections and enable them:

1.  **Authentication:**
    *   Go to the "Authentication" section.
    *   Click "Get started".
    *   On the "Sign-in method" tab, enable the "Email/Password" provider.

2.  **Firestore Database:**
    *   Go to the "Firestore Database" section.
    *   Click "Create database".
    *   Start in **production mode**.
    *   Choose a location for your database.
    *   Click "Enable".

### 4. Configure Firestore Security Rules

1.  In the Firestore Database section, go to the "Rules" tab.
2.  Copy the entire content of the `firestore.rules` file from this project.
3.  Paste it into the rules editor in the Firebase console, completely replacing the default rules.
4.  Click "Publish".

### 5. Add Sample Data (Manual)

To test the app, you need to add some data to your Firestore database manually.

1.  **Create Collections:**
    *   Go to the "Data" tab in Firestore.
    *   Create a collection named `services`.
    *   Create a collection named `schedule_slots`.

2.  **Add a Sample Service:**
    *   In the `services` collection, click "Add document".
    *   Let Firestore generate the Document ID.
    *   Add the following fields:
        *   `name` (String): "Studio Photoshoot"
        *   `description` (String): "A professional one-hour shoot in our studio."
        *   `price` (Number): 150

3.  **Add a Sample Schedule Slot:**
    *   In the `schedule_slots` collection, click "Add document".
    *   Let Firestore generate the Document ID.
    *   Add the following fields:
        *   `serviceId` (String): *Copy the Document ID of the service you just created.*
        *   `isBooked` (Boolean): `false`
        *   `startTime` (Timestamp): *Choose a future date and time.*
        *   `endTime` (Timestamp): *Choose a time one hour after `startTime`.*

Now you can build and run the application on an Android device or emulator. You will be able to see the service, book it, and see it in your "My Bookings" list.
