package com.example.embr6monitoringapp.Service;

import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import com.example.embr6monitoringapp.DAO.PurposeDAO;
import com.example.embr6monitoringapp.DAO.PurposeDAOImpl;
import com.example.embr6monitoringapp.Models.PurposeModel;
import com.example.embr6monitoringapp.Utils.SyncManager;

public class PurposeServiceImpl implements PurposeService {

    private static final String TAG = "PurposeServiceImpl";

    private final Context context;
    private final String employeeId;
    private final PurposeDAO dao;
    private final SyncManager syncManager;
    private long lastSavedId = -1;


    private final CheckBox cbVerifyAccuracy;
    private final CheckBox cbPMPINNew, cbPMPINRenewal;
    private final CheckBox cbHWGIDNew, cbHWGIDRenewal;
    private final CheckBox cbHWTRNew, cbHWTRRenewal;
    private final CheckBox cbHWTSDNew, cbHWTSDRenewal;
    private final CheckBox cbPOAPCINew, cbPOAPCIRenewal;
    private final CheckBox cbDPNew, cbDPRenewal;
    private final EditText txtOtherPermit;
    private final CheckBox cbOthersPermitNew, cbOthersPermitRenewal;
    private final CheckBox cbDetermineCompliance;
    private final CheckBox cbInvestigate;
    private final CheckBox cbSurvey;
    private final CheckBox cbOthersCEMCRR;
    private final EditText txtOtherSpecify;
    private final EditText etContactName;
    private final EditText etPosition;

    public PurposeServiceImpl(
            Context context,
            String employeeId,
            CheckBox cbVerifyAccuracy,
            CheckBox cbPMPINNew, CheckBox cbPMPINRenewal,
            CheckBox cbHWGIDNew, CheckBox cbHWGIDRenewal,
            CheckBox cbHWTRNew, CheckBox cbHWTRRenewal,
            CheckBox cbHWTSDNew, CheckBox cbHWTSDRenewal,
            CheckBox cbPOAPCINew, CheckBox cbPOAPCIRenewal,
            CheckBox cbDPNew, CheckBox cbDPRenewal,
            EditText txtOtherPermit,
            CheckBox cbOthersPermitNew, CheckBox cbOthersPermitRenewal,
            CheckBox cbDetermineCompliance,
            CheckBox cbInvestigate,
            CheckBox cbSurvey,
            CheckBox cbOthersCEMCRR,
            EditText txtOtherSpecify,
            EditText etContactName,
            EditText etPosition
    ) {
        this.context = context;
        this.employeeId = employeeId;
        this.cbVerifyAccuracy = cbVerifyAccuracy;
        this.cbPMPINNew = cbPMPINNew;
        this.cbPMPINRenewal = cbPMPINRenewal;
        this.cbHWGIDNew = cbHWGIDNew;
        this.cbHWGIDRenewal = cbHWGIDRenewal;
        this.cbHWTRNew = cbHWTRNew;
        this.cbHWTRRenewal = cbHWTRRenewal;
        this.cbHWTSDNew = cbHWTSDNew;
        this.cbHWTSDRenewal = cbHWTSDRenewal;
        this.cbPOAPCINew = cbPOAPCINew;
        this.cbPOAPCIRenewal = cbPOAPCIRenewal;
        this.cbDPNew = cbDPNew;
        this.cbDPRenewal = cbDPRenewal;
        this.txtOtherPermit = txtOtherPermit;
        this.cbOthersPermitNew = cbOthersPermitNew;
        this.cbOthersPermitRenewal = cbOthersPermitRenewal;
        this.cbDetermineCompliance = cbDetermineCompliance;
        this.cbInvestigate = cbInvestigate;
        this.cbSurvey = cbSurvey;
        this.cbOthersCEMCRR = cbOthersCEMCRR;
        this.txtOtherSpecify = txtOtherSpecify;
        this.etContactName = etContactName;
        this.etPosition = etPosition;

        this.dao = new PurposeDAOImpl(context);
        this.syncManager = new SyncManager(context);

        Log.d(TAG, "PurposeServiceImpl initialized for employee: " + employeeId);
    }

    @Override
    public String validate() {
        Log.d(TAG, "Validating form...");

        // At least one purpose must be selected
        boolean anySelected = cbVerifyAccuracy.isChecked() ||
                cbPMPINNew.isChecked() || cbPMPINRenewal.isChecked() ||
                cbHWGIDNew.isChecked() || cbHWGIDRenewal.isChecked() ||
                cbHWTRNew.isChecked() || cbHWTRRenewal.isChecked() ||
                cbHWTSDNew.isChecked() || cbHWTSDRenewal.isChecked() ||
                cbPOAPCINew.isChecked() || cbPOAPCIRenewal.isChecked() ||
                cbDPNew.isChecked() || cbDPRenewal.isChecked() ||
                cbOthersPermitNew.isChecked() || cbOthersPermitRenewal.isChecked() ||
                cbDetermineCompliance.isChecked() ||
                cbInvestigate.isChecked() ||
                cbSurvey.isChecked() ||
                cbOthersCEMCRR.isChecked();

        if (!anySelected) {
            return "Please select at least one purpose of inspection.";
        }

        if (etContactName.getText().toString().trim().isEmpty()) {
            return "Please enter contact name";
        }

        if (etPosition.getText().toString().trim().isEmpty()) {
            return "Please enter position";
        }

        Log.d(TAG, "Validation passed");
        return null;
    }

    @Override
    public boolean save() {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            Log.e(TAG, "Employee ID is null");
            return false;
        }

        try {
            Log.d(TAG, "Saving purpose data for employee: " + employeeId);

            PurposeModel model = new PurposeModel();
            model.setEmployeeId(employeeId);

            // Section 1
            model.setVerifyAccuracy(cbVerifyAccuracy.isChecked() ? 1 : 0);

            // Permits
            model.setPmpinNew(cbPMPINNew.isChecked() ? 1 : 0);
            model.setPmpinRenewal(cbPMPINRenewal.isChecked() ? 1 : 0);
            model.setHwgidNew(cbHWGIDNew.isChecked() ? 1 : 0);
            model.setHwgidRenewal(cbHWGIDRenewal.isChecked() ? 1 : 0);
            model.setHwtrNew(cbHWTRNew.isChecked() ? 1 : 0);
            model.setHwtrRenewal(cbHWTRRenewal.isChecked() ? 1 : 0);
            model.setHwtsdNew(cbHWTSDNew.isChecked() ? 1 : 0);
            model.setHwtsdRenewal(cbHWTSDRenewal.isChecked() ? 1 : 0);
            model.setPoapciNew(cbPOAPCINew.isChecked() ? 1 : 0);
            model.setPoapciRenewal(cbPOAPCIRenewal.isChecked() ? 1 : 0);
            model.setDpNew(cbDPNew.isChecked() ? 1 : 0);
            model.setDpRenewal(cbDPRenewal.isChecked() ? 1 : 0);

            model.setOtherPermit(txtOtherPermit.getText().toString().trim());
            model.setOthersPermitNew(cbOthersPermitNew.isChecked() ? 1 : 0);
            model.setOthersPermitRenewal(cbOthersPermitRenewal.isChecked() ? 1 : 0);

            // Section 2
            model.setDetermineCompliance(cbDetermineCompliance.isChecked() ? 1 : 0);
            model.setInvestigate(cbInvestigate.isChecked() ? 1 : 0);
            model.setSurvey(cbSurvey.isChecked() ? 1 : 0);
            model.setOthersCEMCRR(cbOthersCEMCRR.isChecked() ? 1 : 0);
            model.setOtherSpecify(txtOtherSpecify.getText().toString().trim());

            // Contact
            model.setContactName(etContactName.getText().toString().trim());
            model.setPosition(etPosition.getText().toString().trim());

            model.setIsSynced(0);

            long result = dao.insertPurpose(model);
            if (result != -1) {
                lastSavedId = result;
                Log.d(TAG, "Save successful! Record ID: " + result);
                return true;
            } else {
                Log.e(TAG, "Save failed - insert returned -1");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in save: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void resetForm() {
        cbVerifyAccuracy.setChecked(false);
        cbPMPINNew.setChecked(false);
        cbPMPINRenewal.setChecked(false);
        cbHWGIDNew.setChecked(false);
        cbHWGIDRenewal.setChecked(false);
        cbHWTRNew.setChecked(false);
        cbHWTRRenewal.setChecked(false);
        cbHWTSDNew.setChecked(false);
        cbHWTSDRenewal.setChecked(false);
        cbPOAPCINew.setChecked(false);
        cbPOAPCIRenewal.setChecked(false);
        cbDPNew.setChecked(false);
        cbDPRenewal.setChecked(false);
        txtOtherPermit.setText("");
        cbOthersPermitNew.setChecked(false);
        cbOthersPermitRenewal.setChecked(false);
        cbDetermineCompliance.setChecked(false);
        cbInvestigate.setChecked(false);
        cbSurvey.setChecked(false);
        cbOthersCEMCRR.setChecked(false);
        txtOtherSpecify.setText("");
        etContactName.setText("");
        etPosition.setText("");
        Log.d(TAG, "Form reset");
    }

    @Override
    public void syncNow() {
        Log.d(TAG, "Syncing purpose data...");
        syncManager.syncIfOnline();
    }

    @Override
    public long getLastSavedId() {
        return lastSavedId;
    }
}