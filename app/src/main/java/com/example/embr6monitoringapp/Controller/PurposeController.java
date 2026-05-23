package com.example.embr6monitoringapp.Controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.embr6monitoringapp.R;
import com.example.embr6monitoringapp.Service.PurposeService;
import com.example.embr6monitoringapp.Service.PurposeServiceImpl;

public class PurposeController extends AppCompatActivity {

    private static final String TAG = "PurposeController";

    // View references
    private CheckBox cbVerifyAccuracy;
    private CheckBox cbPMPINNew, cbPMPINRenewal;
    private CheckBox cbHWGIDNew, cbHWGIDRenewal;
    private CheckBox cbHWTRNew, cbHWTRRenewal;
    private CheckBox cbHWTSDNew, cbHWTSDRenewal;
    private CheckBox cbPOAPCINew, cbPOAPCIRenewal;
    private CheckBox cbDPNew, cbDPRenewal;
    private EditText txtOtherPermit;
    private CheckBox cbOthersPermitNew, cbOthersPermitRenewal;
    private CheckBox cbDetermineCompliance;
    private CheckBox cbInvestigate;
    private CheckBox cbSurvey;
    private CheckBox cbOthersCEMCRR;
    private EditText txtOtherSpecify;
    private EditText etContactName;
    private EditText etPosition;

    private Button btnSave;
    private android.widget.ImageButton btnBack;

    private PurposeService service;
    private String employeeId;
    private long lastSavedRecordId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Try different layout names
            int layoutId = getResources().getIdentifier("purpose_activity", "layout", getPackageName());
            if (layoutId == 0) {
                layoutId = getResources().getIdentifier("purpose", "layout", getPackageName());
            }
            if (layoutId == 0) {
                Toast.makeText(this, "Layout not found! Check your XML file name", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            setContentView(layoutId);

            // Get employeeId from Intent
            employeeId = getIntent().getStringExtra("EMPLOYEE_ID");
            if (employeeId == null || employeeId.isEmpty()) {
                Toast.makeText(this, "Employee ID not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Log.d(TAG, "PurposeController started with Employee ID: " + employeeId);
            Toast.makeText(this, "Purpose Form Loaded", Toast.LENGTH_SHORT).show();

            // Initialize views
            boolean viewsLoaded = initViews();
            if (!viewsLoaded) {
                Toast.makeText(this, "Error loading form views", Toast.LENGTH_LONG).show();
                return;
            }

            // Initialize service
            service = new PurposeServiceImpl(
                    this, employeeId,
                    cbVerifyAccuracy,
                    cbPMPINNew, cbPMPINRenewal,
                    cbHWGIDNew, cbHWGIDRenewal,
                    cbHWTRNew, cbHWTRRenewal,
                    cbHWTSDNew, cbHWTSDRenewal,
                    cbPOAPCINew, cbPOAPCIRenewal,
                    cbDPNew, cbDPRenewal,
                    txtOtherPermit,
                    cbOthersPermitNew, cbOthersPermitRenewal,
                    cbDetermineCompliance,
                    cbInvestigate,
                    cbSurvey,
                    cbOthersCEMCRR,
                    txtOtherSpecify,
                    etContactName,
                    etPosition
            );

            // Setup save button
            setupSaveButton();
            setupBackButton();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean initViews() {
        try {
            // Section 1 - Verify Accuracy
            cbVerifyAccuracy = findViewById(R.id.cbVerifyAccuracy);
            if (cbVerifyAccuracy == null) { Log.e(TAG, "cbVerifyAccuracy not found"); return false; }

            // Permits
            cbPMPINNew = findViewById(R.id.cbPMPINNew);
            cbPMPINRenewal = findViewById(R.id.cbPMPINRenewal);
            cbHWGIDNew = findViewById(R.id.cbHWGIDNew);
            cbHWGIDRenewal = findViewById(R.id.cbHWGIDRenewal);
            cbHWTRNew = findViewById(R.id.cbHWTRNew);
            cbHWTRRenewal = findViewById(R.id.cbHWTRRenewal);
            cbHWTSDNew = findViewById(R.id.cbHWTSDNew);
            cbHWTSDRenewal = findViewById(R.id.cbHWTSDRenewal);
            cbPOAPCINew = findViewById(R.id.cbPOAPCINew);
            cbPOAPCIRenewal = findViewById(R.id.cbPOAPCIRenewal);
            cbDPNew = findViewById(R.id.cbDPNew);
            cbDPRenewal = findViewById(R.id.cbDPRenewal);

            txtOtherPermit = findViewById(R.id.txtOtherPermit);
            cbOthersPermitNew = findViewById(R.id.cbOthersPermitNew);
            cbOthersPermitRenewal = findViewById(R.id.cbOthersPermitRenewal);

            // Section 2
            cbDetermineCompliance = findViewById(R.id.cbDetermineCompliance);
            cbInvestigate = findViewById(R.id.cbInvestigate);
            cbSurvey = findViewById(R.id.cbSurvey);
            cbOthersCEMCRR = findViewById(R.id.cbOthersCEMCRR);
            txtOtherSpecify = findViewById(R.id.txtOtherSpecify);

            // Contact info
            etContactName = findViewById(R.id.etContactName);
            etPosition = findViewById(R.id.etPosition);

            // Buttons
            btnSave = findViewById(R.id.btnSave);
            btnBack = findViewById(R.id.btnBack);

            Log.d(TAG, "All views initialized successfully");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            return false;
        }
    }

    private void setupSaveButton() {
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                try {
                    Toast.makeText(PurposeController.this, "Saving...", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Save button clicked");
                    String error = service.validate();
                    if (error != null) {
                        Toast.makeText(PurposeController.this, error, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(PurposeController.this, "Validation passed, saving to database...", Toast.LENGTH_SHORT).show();
                    boolean saved = service.save();
                    if (saved) {
                        lastSavedRecordId = service.getLastSavedId();
                        Log.d(TAG, "Save successful! Record ID: " + lastSavedRecordId);

                        Toast.makeText(PurposeController.this,
                                "Purpose saved! ID: " + lastSavedRecordId,
                                Toast.LENGTH_LONG).show();
                        service.syncNow();
                        Toast.makeText(PurposeController.this, "Opening Compliance Status...", Toast.LENGTH_SHORT).show();

                        try {
                            Intent intent = new Intent(PurposeController.this, ComplianceStatusController.class);
                            intent.putExtra("EMPLOYEE_ID", employeeId);
                            intent.putExtra("PURPOSE_RECORD_ID", lastSavedRecordId);
                            intent.putExtra("RECORD_ID", lastSavedRecordId);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Log.e(TAG, "Navigation error: " + e.getMessage(), e);
                            Toast.makeText(PurposeController.this, "Error opening Compliance: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Log.e(TAG, "Save failed");
                        Toast.makeText(PurposeController.this,
                                "Failed to save data. Please check your entries.",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error saving: " + e.getMessage(), e);
                    Toast.makeText(PurposeController.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.e(TAG, "btnSave is null!");
            Toast.makeText(this, "Save button not found!", Toast.LENGTH_LONG).show();
        }
    }

    private void setupBackButton() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }
    }
}