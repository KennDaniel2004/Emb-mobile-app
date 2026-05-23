package com.example.embr6monitoringapp.Controller;

// ============================================================
//  RegisterController.java  — UPDATED
//
//  CHANGE: Before registering, the Employee ID is validated
//  against Firestore → Registered_User collection.
//
//  Flow:
//  1. User fills form and taps Register
//  2. Basic field validation (same as before)
//  3. *** NEW *** Firestore check:
//       - ID not found       → block, show error
//       - ID already done    → block, show error
//       - ID found (pending) → proceed with local registration
//  4. Local SQLite registration (same as before)
//  5. SyncManager syncs to Firestore (same as before)
//
//  Return codes from authService.register():
//    0  → success
//   -1  → passwords don't match
//   -2  → duplicate Employee ID or Username in SQLite
//   -3  → Employee ID not pre-registered by Admin (NEW)
//   -4  → Employee ID already completed by another user (NEW)
// ============================================================

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.embr6monitoringapp.Models.UserModel;
import com.example.embr6monitoringapp.R;
import com.example.embr6monitoringapp.Service.AuthService;
import com.example.embr6monitoringapp.Service.AuthServiceImpl;
import com.example.embr6monitoringapp.Utils.FirestoreIdValidator;
import com.example.embr6monitoringapp.Utils.SyncManager;

public class RegisterController extends AppCompatActivity {

    private EditText    etEmployeeId, etPosition;
    private EditText    etLastName, etFirstName, etMiddleName;
    private EditText    etUsername, etPassword, etConfirmPassword;
    private ImageButton btnTogglePassword, btnToggleConfirmPassword;
    private Button      registerBtn;
    private TextView    loginText;
    private AuthService authService;
    private SyncManager syncManager;
    private boolean isPasswordVisible        = false;
    private boolean isConfirmPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        authService = new AuthServiceImpl(this);
        syncManager = new SyncManager(this);

        bindViews();
        setupPasswordToggles();
        setupRegisterButton();
        setupLoginLink();
    }

    private void bindViews() {
        etEmployeeId             = findViewById(R.id.employeeId);
        etPosition               = findViewById(R.id.Position);
        etLastName               = findViewById(R.id.Lastname);
        etFirstName              = findViewById(R.id.Firstname);
        etMiddleName             = findViewById(R.id.Middlename);
        etUsername               = findViewById(R.id.username);
        etPassword               = findViewById(R.id.password);
        etConfirmPassword        = findViewById(R.id.Confirmpassword);
        btnTogglePassword        = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnTogglePassword1);
        registerBtn              = findViewById(R.id.RegisterBtn);
        loginText                = findViewById(R.id.Login);
    }

    private void setupPasswordToggles() {
        btnTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            etPassword.setTransformationMethod(isPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(isPasswordVisible
                    ? R.drawable.ic_eye_on
                    : R.drawable.ic_eye_off);
            etPassword.setSelection(etPassword.getText().length());
        });

        btnToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            etConfirmPassword.setTransformationMethod(isConfirmPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            btnToggleConfirmPassword.setImageResource(isConfirmPasswordVisible
                    ? R.drawable.ic_eye_on
                    : R.drawable.ic_eye_off);
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });
    }

    private void setupRegisterButton() {
        registerBtn.setOnClickListener(v -> {

            // ── Step 1: Collect fields ────────────────────────────
            String empId       = etEmployeeId.getText().toString().trim();
            String position    = etPosition.getText().toString().trim();
            String lastName    = etLastName.getText().toString().trim();
            String firstName   = etFirstName.getText().toString().trim();
            String middleName  = etMiddleName.getText().toString().trim();
            String username    = etUsername.getText().toString().trim();
            String password    = etPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            // ── Step 2: Basic field validation (unchanged) ────────
            if (empId.isEmpty() || position.isEmpty() || lastName.isEmpty()
                    || firstName.isEmpty() || username.isEmpty()
                    || password.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this,
                        "Please fill all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            registerBtn.setEnabled(false);

            ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("Verifying Employee ID…");
            progress.setCancelable(false);
            progress.show();


            FirestoreIdValidator.validate(empId, result -> {


                progress.dismiss();

                switch (result) {

                    case ID_NOT_FOUND:
                        // Admin has never registered this ID in the web app
                        etEmployeeId.setError(
                                "Employee ID not found. Contact your administrator.");
                        etEmployeeId.requestFocus();
                        Toast.makeText(this,
                                "Employee ID \"" + empId + "\" is not registered in the system. " +
                                        "Please contact the administrator.",
                                Toast.LENGTH_LONG).show();
                        registerBtn.setEnabled(true);
                        break;

                    case ID_ALREADY_COMPLETED:
                        // Another user already registered with this ID
                        etEmployeeId.setError(
                                "This Employee ID is already registered.");
                        etEmployeeId.requestFocus();
                        Toast.makeText(this,
                                "Employee ID \"" + empId + "\" is already registered. " +
                                        "Please contact the administrator if this is an error.",
                                Toast.LENGTH_LONG).show();
                        registerBtn.setEnabled(true);
                        break;

                    case ERROR:
                        // Could not reach Firestore — no internet or config issue
                        Toast.makeText(this,
                                "Could not verify Employee ID. " +
                                        "Please check your internet connection and try again.",
                                Toast.LENGTH_LONG).show();
                        registerBtn.setEnabled(true);
                        break;

                    case ID_FOUND_PENDING:
                        // ✅ ID exists and is pending — proceed with registration
                        proceedWithRegistration(
                                empId, position, lastName, firstName,
                                middleName, username, password, confirmPass
                        );
                        break;
                }
            });
        });
    }


    private void proceedWithRegistration(
            String empId, String position,
            String lastName, String firstName, String middleName,
            String username, String password, String confirmPass) {

        UserModel newUser = new UserModel();
        newUser.setEmployeeId(empId);
        newUser.setPosition(position);
        newUser.setLastName(lastName);
        newUser.setFirstName(firstName);
        newUser.setMiddleName(middleName);
        newUser.setUsername(username);
        newUser.setPassword(password);

        int result = authService.register(newUser, confirmPass);

        switch (result) {
            case 0:
                Toast.makeText(this,
                        "Registered Successfully!", Toast.LENGTH_SHORT).show();
                syncManager.syncIfOnline();
                finish();
                break;

            case -1:
                etPassword.setError("Passwords do not match");
                etConfirmPassword.setError("Passwords do not match");
                etConfirmPassword.requestFocus();
                Toast.makeText(this,
                        "Passwords do not match.", Toast.LENGTH_SHORT).show();
                registerBtn.setEnabled(true);
                break;

            case -2:
            default:
                Toast.makeText(this,
                        "Registration failed. Employee ID or Username already exists.",
                        Toast.LENGTH_LONG).show();
                registerBtn.setEnabled(true);
                break;
        }
    }

    private void setupLoginLink() {
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginController.class));
            finish();
        });
    }

    public static class TestController {
    }
}