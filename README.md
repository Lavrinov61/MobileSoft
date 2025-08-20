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

### 5. Add Sample Data (Automated Seeding)

To test the app, you can automatically import sample data into Firestore using the provided script. This is much easier than adding it manually.

**Prerequisites:**
- You need to have [Node.js](https://nodejs.org/) installed on your computer.
- You need your **Service Account Key** file. This is a JSON file you can generate from the Firebase Console (`Project Settings` -> `Service accounts` -> `Generate new private key`). **This key is secret and should not be shared publicly.**

**Steps:**

1.  **Place Service Account Key:**
    *   Rename your downloaded service account key file to `serviceAccountKey.json`.
    *   Place this file inside the `importer/` directory in this project.

2.  **Install Dependencies:**
    *   Open a terminal or command prompt.
    *   Navigate to the `importer/` directory:
        ```bash
        cd importer
        ```
    *   Install the necessary Node.js packages:
        ```bash
        npm install
        ```

3.  **Run the Import Script:**
    *   While still in the `importer/` directory, run the script:
        ```bash
        npm start
        ```

The script will connect to your Firestore database and automatically create the `services` and `schedule_slots` collections with sample data.

### 6. Run the App

Now you can build and run the application on an Android device or emulator. You will be able to see the services, book one, and see it in your "My Bookings" list.
