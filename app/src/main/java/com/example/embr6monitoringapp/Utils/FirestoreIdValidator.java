package com.example.embr6monitoringapp.Utils;

// ============================================================
//  FirestoreIdValidator.java
//
//  Checks Firestore → Registered_User collection to see if
//  the given Employee ID was pre-registered by the Admin
//  via the Web App before allowing mobile registration.
//
//  Returns result via a simple callback so Firestore's
//  async call doesn't block the main thread.
// ============================================================

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreIdValidator {

    public interface ValidationCallback {
        /** Called on the main thread with the result */
        void onResult(ValidationResult result);
    }

    public enum ValidationResult {
        /** ID exists in Firestore and is still pending (not yet completed) → allow registration */
        ID_FOUND_PENDING,

        /** ID exists but already completed by mobile app → already registered */
        ID_ALREADY_COMPLETED,

        /** ID does not exist in Firestore → admin has not pre-registered it */
        ID_NOT_FOUND,

        /** Firestore call failed (no internet, config error, etc.) */
        ERROR
    }

    /**
     * Checks whether the given Employee ID exists in
     * Firestore → Registered_User → {employeeId} document.
     *
     * The document ID in Firestore IS the Employee ID
     * (e.g. "EMBR6-00012"), set by the web admin app.
     *
     * @param employeeId the ID the user typed in the register form
     * @param callback   called back on main thread with the result
     */
    public static void validate(String employeeId, ValidationCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db
                .collection("Registered_User")
                .document(employeeId);

        docRef.get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) {
                        // Document does not exist at all →
                        // Admin has never registered this ID
                        callback.onResult(ValidationResult.ID_NOT_FOUND);
                        return;
                    }

                    // Document exists — check if already completed
                    // A completed user has First_Name filled in by the mobile app
                    String firstName = snapshot.getString("First_Name");

                    if (firstName != null && !firstName.isEmpty()) {
                        // Already completed by mobile app — cannot register twice
                        callback.onResult(ValidationResult.ID_ALREADY_COMPLETED);
                    } else {
                        // Pending — admin registered it, user hasn't filled info yet
                        // → this is the correct state to allow registration
                        callback.onResult(ValidationResult.ID_FOUND_PENDING);
                    }

                })
                .addOnFailureListener(e -> {
                    // Network error or Firestore config issue
                    callback.onResult(ValidationResult.ERROR);
                });
    }
}