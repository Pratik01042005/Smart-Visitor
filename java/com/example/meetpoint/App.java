package com.example.meetpoint;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 🔥 Initialize Firebase (FIXES splash crash)
        FirebaseApp.initializeApp(this);

        // 🔥 Enable offline persistence (optional but recommended)
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings =
                    new FirebaseFirestoreSettings.Builder()
                            .setPersistenceEnabled(true)
                            .build();
            firestore.setFirestoreSettings(settings);
        } catch (Exception e) {
            Log.e("App", "Firestore persistence already enabled");
        }

        // 🔥 Global crash safety (logs instead of silent crash)
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e("CRASH", "App crashed: " + throwable.getMessage(), throwable);
        });
    }
}
