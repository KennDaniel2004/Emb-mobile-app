package com.example.embr6monitoringapp.Controller;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.embr6monitoringapp.Database.DatabaseConnection;
import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.R;
import com.example.embr6monitoringapp.Service.MonitoringProgressService;
import com.example.embr6monitoringapp.Service.MonitoringProgressServiceImpl;
import com.example.embr6monitoringapp.Utils.FindingsSection;
import com.example.embr6monitoringapp.Utils.SyncManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;



public class MonitoringDetailController extends AppCompatActivity {

    private static final String TAG = "MonitoringDetailCtrl";

    ImageButton btnBack;

    TextView tvDetailEmbId, tvDetailReportControl, tvDetailTypeMonitoring, tvDetailDate;
    TextView tvDetailLaws, tvDetailEstablishment, tvDetailProponent;
    TextView tvDetailLocation, tvDetailGeo, tvDetailNature;
    TextView tvDetailYearEstab, tvDetailPsic, tvDetailOperating, tvDetailEmployees;
    TextView tvDetailProductLines, tvDetailProductionRate, tvDetailActualRate;
    TextView tvDetailManagingHead, tvDetailPco, tvDetailPcoAccred;
    TextView tvDetailEffectivity, tvDetailContact;

    TextView tvDetailPurpose, tvDetailContactPerson, tvDetailContactPosition;

    TextView tvDetailCompliance;

    FindingsSection s331, s332, s333, s334, s335, s336, s337;

    CheckBox cbRecConfirmatory, cbRecRegularMonitoring, cbRecIssuanceTempRenewal;
    CheckBox cbRecAccreditationPco, cbRecSubmissionSmrCmr, cbRecIssuanceNomTc;
    CheckBox cbRecIssuanceNov, cbRecSuspensionEcc, cbRecEndorsementPab, cbRecOther;
    EditText etRecOther, etRegularMonitoringDesc;

    EditText etSubmittedBy, etDateSubmitted, etDateTravelConcluded;
    EditText etRecommending1, etRecommending1Position;
    EditText etRecommending2, etRecommending2Position;
    EditText etApprovedBy, etApprovedByPosition;

    Button btnSaveComplete;

    MonitoringProgressService service;
    DatabaseConnection        dbConn;
    FirebaseFirestore         firestore;
    SyncManager               syncManager;

    MonitoringRecord record;
    String           employeeId = "";
    int              recordId   = -1;

    private FindingsSection pendingSection = null;
    private int             pendingSlot    = 0;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null
                                && result.getData().getData() != null
                                && pendingSection != null) {

                            Uri uri = result.getData().getData();

                            // Persist read permission so URI survives process restart
                            getContentResolver().takePersistableUriPermission(
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            String uriStr = uri.toString();

                            if (pendingSlot == 1) {
                                pendingSection.setImageUri1(uriStr);
                                if (pendingSection.ivImage1 != null)
                                    Glide.with(this).load(uri).centerCrop().into(pendingSection.ivImage1);
                                if (pendingSection.ivUploadIcon1 != null)
                                    pendingSection.ivUploadIcon1.setVisibility(View.GONE);
                            } else {
                                pendingSection.setImageUri2(uriStr);
                                if (pendingSection.ivImage2 != null)
                                    Glide.with(this).load(uri).centerCrop().into(pendingSection.ivImage2);
                                if (pendingSection.ivUploadIcon2 != null)
                                    pendingSection.ivUploadIcon2.setVisibility(View.GONE);
                            }

                            pendingSection = null;
                            pendingSlot    = 0;
                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_detail);

        employeeId = getIntent().getStringExtra("EMPLOYEE_ID");
        if (employeeId == null) employeeId = "";
        recordId = getIntent().getIntExtra("RECORD_ID", -1);

        service     = new MonitoringProgressServiceImpl(this);
        dbConn      = DatabaseConnection.getInstance(this);
        firestore   = FirebaseFirestore.getInstance();
        syncManager = new SyncManager(this);

        if (recordId != -1) record = service.getRecordById(recordId);

        bindViews();
        setupSectionTitles();
        setupImagePickers();
        setupDatePickers();
        buttonListeners(new ButtonClick());

        populateGeneralInfo();
        populatePurpose();
        populateCompliance();

        if (record != null) restoreFindings();
    }


    private void buttonListeners(ButtonClick listener) {
        btnBack.setOnClickListener(listener);
        btnSaveComplete.setOnClickListener(listener);
    }


    class ButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if      (id == R.id.btnBack)         finish();
            else if (id == R.id.btnSaveComplete) saveAndComplete();
        }
    }


    private void populateGeneralInfo() {
        if (record == null) return;

        setText(tvDetailEmbId,         "EMB ID No.: " + s(record.getEmbId()));
        setText(tvDetailReportControl, "Report Control: " + s(record.getReportControl()));
        setText(tvDetailTypeMonitoring,"Type of Monitoring: " + s(record.getTypeMonitoring()));
        setText(tvDetailDate,          "Date of Inspection: " + s(record.getDateOfInspection()));
        setText(tvDetailLaws,          "Applicable Laws: " + s(record.getLaws()));
        setText(tvDetailEstablishment, s(record.getNameOfEstablishment()));
        setText(tvDetailProponent,     s(record.getProponent()));
        setText(tvDetailLocation,      s(record.getProjectLocation()));
        setText(tvDetailGeo,           "Geo: " + s(record.getGeoN()) + " N  " + s(record.getGeoE()) + " E");
        setText(tvDetailNature,        s(record.getNatureOfBusiness()));
        setText(tvDetailYearEstab,     s(record.getYearEstablish()));
        setText(tvDetailPsic,          s(record.getPsicCode()));
        setText(tvDetailOperating,     "Operating: " + s(record.getOpHoursDay()) + " hrs/day | "
                + s(record.getOpDayWeek()) + " days/wk | " + s(record.getOpDayYear()) + " days/yr");
        setText(tvDetailEmployees,     "Employees: " + s(record.getNumberOfEmployee())
                + "  (M: " + s(record.getMale()) + "  F: " + s(record.getFemale()) + ")");
        setText(tvDetailProductLines,  s(record.getProductLines()));
        setText(tvDetailProductionRate,s(record.getProductionRate()));
        setText(tvDetailActualRate,    s(record.getActualProductionRate()));
        setText(tvDetailManagingHead,  s(record.getNameOfManagingHead()));
        setText(tvDetailPco,           s(record.getNameOfPCO()));
        setText(tvDetailPcoAccred,     s(record.getPcoAccreditationNo()));
        setText(tvDetailEffectivity,   s(record.getDateOfEffectivity()));
        setText(tvDetailContact,       s(record.getPhoneFaxNo()) + " | " + s(record.getEmailAddress()));
    }

    private void populatePurpose() {
        try {
            SQLiteDatabase db = dbConn.getReadableDatabase();
            Cursor c = db.query("Purpose_Table", null,
                    "Employee_Id = ?", new String[]{employeeId},
                    null, null, "id DESC", "1");

            if (c != null && c.moveToFirst()) {
                StringBuilder sb = new StringBuilder();

                if (getInt(c, "verifyAccuracy") == 1)
                    sb.append("☑ Verify accuracy of information for new permit applications, renewals, or modifications.\n");

                appendPermit(sb, c, "pmpinNew",          "pmpinRenewal",          "PMPIN Application");
                appendPermit(sb, c, "hwgidNew",          "hwgidRenewal",          "Hazardous Waste Generator ID Registration");
                appendPermit(sb, c, "hwtrNew",           "hwtrRenewal",           "Hazardous Waste Transporter Registration");
                appendPermit(sb, c, "hwtsdNew",          "hwtsdRenewal",          "Hazardous Waste TSD Registration");
                appendPermit(sb, c, "poapciNew",         "poapciRenewal",         "Permit to Operate Air Pollution Control Installation");
                appendPermit(sb, c, "dpNew",             "dpRenewal",             "Discharge Permit");

                String otherPermit = getString(c, "otherPermit");
                if (otherPermit != null && !otherPermit.isEmpty())
                    appendPermit(sb, c, "othersPermitNew", "othersPermitRenewal", "Others: " + otherPermit);

                if (getInt(c, "determineCompliance") == 1)
                    sb.append("☑ Determine compliance status with environmental regulations.\n");
                if (getInt(c, "investigate") == 1)
                    sb.append("☑ Investigate community complaints.\n");
                if (getInt(c, "survey") == 1)
                    sb.append("☑ Survey\n");

                String others = getString(c, "otherSpecify");
                if (others != null && !others.isEmpty())
                    sb.append("☑ Others: ").append(others).append("\n");

                setText(tvDetailPurpose, sb.toString().trim());
                setText(tvDetailContactPerson,   getString(c, "contactName"));
                setText(tvDetailContactPosition, getString(c, "position"));
                c.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "populatePurpose: " + e.getMessage(), e);
        }
    }

    private void appendPermit(StringBuilder sb, Cursor c, String newCol, String renewCol, String label) {
        int isNew   = getInt(c, newCol);
        int isRenew = getInt(c, renewCol);
        if (isNew == 1 || isRenew == 1) {
            sb.append("  ").append(label);
            if (isNew   == 1) sb.append(" [New]");
            if (isRenew == 1) sb.append(" [Renewal]");
            sb.append("\n");
        }
    }


    private void populateCompliance() {
        try {
            SQLiteDatabase db = dbConn.getReadableDatabase();
            Cursor c = db.query("Compliance_Status", null,
                    "Employee_Id = ?", new String[]{employeeId},
                    null, null, "id DESC", "1");

            if (c != null && c.moveToFirst()) {
                StringBuilder sb = new StringBuilder();

                appendComplianceRow(sb, "PD 1586",
                        new String[]{"ECC 1", "ECC 2", "ECC 3"},
                        new String[]{"Pd1586_Ecc1", "Pd1586_Ecc2", "Pd1586_Ecc3"},
                        new String[]{"Pd1586_Ecc1_DateFrom", "Pd1586_Ecc2_DateFrom", "Pd1586_Ecc3_DateFrom"},
                        new String[]{"Pd1586_Ecc1_DateTo",   "Pd1586_Ecc2_DateTo",   "Pd1586_Ecc3_DateTo"}, c);

                appendComplianceRow(sb, "RA 6969",
                        new String[]{"DENR Registry ID", "PCL Compliance Cert", "Importer Clearance No.", "CCO Registry", "Permit to Transport", "Copy of COT/TSD"},
                        new String[]{"Ra6969_Denr", "Ra6969_Pcl", "Ra6969_Importer", "Ra6969_Cco", "Ra6969_Permit", "Ra6969_Cot"},
                        new String[]{"Ra6969_Denr_DateFrom","Ra6969_Pcl_DateFrom","Ra6969_Importer_DateFrom","Ra6969_Cco_DateFrom","Ra6969_Permit_DateFrom","Ra6969_Cot_DateFrom"},
                        new String[]{"Ra6969_Denr_DateTo",  "Ra6969_Pcl_DateTo",  "Ra6969_Importer_DateTo",  "Ra6969_Cco_DateTo",  "Ra6969_Permit_DateTo",  "Ra6969_Cot_DateTo"}, c);

                appendComplianceRow(sb, "RA 8749",
                        new String[]{"PO No."},
                        new String[]{"Ra8749_PoNo"},
                        new String[]{"Ra8749_DateFrom"},
                        new String[]{"Ra8749_DateTo"}, c);

                appendComplianceRow(sb, "RA 9275",
                        new String[]{"Discharge Permit No."},
                        new String[]{"Ra9275_Discharge"},
                        new String[]{"Ra9275_DateFrom"},
                        new String[]{"Ra9275_DateTo"}, c);

                appendComplianceRow(sb, "RA 9003",
                        new String[]{"With MOA/Agreement SLF w/ ECC"},
                        new String[]{"Ra9003_Moa"},
                        new String[]{"Ra9003_DateFrom"},
                        new String[]{"Ra9003_DateTo"}, c);

                setText(tvDetailCompliance, sb.toString());
                c.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "populateCompliance: " + e.getMessage(), e);
        }
    }

    private void appendComplianceRow(StringBuilder sb, String lawName,
                                     String[] permitNames, String[] permitCols,
                                     String[] dateFromCols, String[] dateToCols,
                                     Cursor c) {
        boolean first = true;
        for (int i = 0; i < permitNames.length; i++) {
            String permit   = getString(c, permitCols[i]);
            String dateFrom = getString(c, dateFromCols[i]);
            String dateTo   = getString(c, dateToCols[i]);
            if (permit != null && !permit.isEmpty()) {
                String law = first ? lawName : "";
                sb.append(String.format("%-10s  %-28s  %-14s  %s\n",
                        law, permitNames[i] + ": " + permit,
                        dateFrom != null ? dateFrom : "--",
                        dateTo   != null ? dateTo   : "--"));
                first = false;
            }
        }
    }


    private void saveAndComplete() {
        if (record == null) {
            Toast.makeText(this, "No monitoring record found.", Toast.LENGTH_SHORT).show();
            return;
        }

        record.setS331Status(s331.getStatus());    record.setS331Findings(s331.getFindings());
        record.setS331Image1(s331.getImageUri1()); record.setS331Image2(s331.getImageUri2());
        record.setS332Status(s332.getStatus());    record.setS332Findings(s332.getFindings());
        record.setS332Image1(s332.getImageUri1()); record.setS332Image2(s332.getImageUri2());
        record.setS333Status(s333.getStatus());    record.setS333Findings(s333.getFindings());
        record.setS333Image1(s333.getImageUri1()); record.setS333Image2(s333.getImageUri2());
        record.setS334Status(s334.getStatus());    record.setS334Findings(s334.getFindings());
        record.setS334Image1(s334.getImageUri1()); record.setS334Image2(s334.getImageUri2());
        record.setS335Status(s335.getStatus());    record.setS335Findings(s335.getFindings());
        record.setS335Image1(s335.getImageUri1()); record.setS335Image2(s335.getImageUri2());
        record.setS336Status(s336.getStatus());    record.setS336Findings(s336.getFindings());
        record.setS337Status(s337.getStatus());    record.setS337Findings(s337.getFindings());

        record.setRecConfirmatorysampling(cbRecConfirmatory.isChecked()         ? 1 : 0);
        record.setRecRegularMonitoring(cbRecRegularMonitoring.isChecked()       ? 1 : 0);
        record.setRecIssuanceTempRenewalPoaDp(cbRecIssuanceTempRenewal.isChecked() ? 1 : 0);
        record.setRecAccreditationPco(cbRecAccreditationPco.isChecked()         ? 1 : 0);
        record.setRecSubmissionSmrCmr(cbRecSubmissionSmrCmr.isChecked()         ? 1 : 0);
        record.setRecIssuanceNomTc(cbRecIssuanceNomTc.isChecked()               ? 1 : 0);
        record.setRecIssuanceNov(cbRecIssuanceNov.isChecked()                   ? 1 : 0);
        record.setRecSuspensionEcc5DayCdo(cbRecSuspensionEcc.isChecked()        ? 1 : 0);
        record.setRecEndorsementPab(cbRecEndorsementPab.isChecked()             ? 1 : 0);
        record.setRecOther(etRecOther.getText().toString().trim());

        record.setSubmittedBy(etSubmittedBy.getText().toString().trim());
        record.setDateSubmitted(etDateSubmitted.getText().toString().trim());
        record.setDateTravelConcluded(etDateTravelConcluded.getText().toString().trim());
        record.setRecommendingApproval1(etRecommending1.getText().toString().trim());
        record.setRecommendingApproval1Position(etRecommending1Position.getText().toString().trim());
        record.setRecommendingApproval2(etRecommending2.getText().toString().trim());
        record.setRecommendingApproval2Position(etRecommending2Position.getText().toString().trim());
        record.setApprovedBy(etApprovedBy.getText().toString().trim());
        record.setApprovedByPosition(etApprovedByPosition.getText().toString().trim());
        record.setIsComplete(1);

        boolean savedLocally = service.updateFullRecord(record);
        if (!savedLocally) {
            Toast.makeText(this, "Failed to save locally.", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Saved locally!", Toast.LENGTH_SHORT).show();

        syncRecordToFirestore(record);
        syncPurposeToFirestore();
        syncComplianceToFirestore();
    }


    private void syncRecordToFirestore(MonitoringRecord r) {
        Map<String, Object> data = new HashMap<>();

        // Section 1
        data.put("Employee_Id",           r.getEmployeeId());
        data.put("Emb_Id",                r.getEmbId());
        data.put("Report_Control",        r.getReportControl());
        data.put("Type_Monitoring",       r.getTypeMonitoring());
        data.put("Date_of_Inspection",    r.getDateOfInspection());
        data.put("Laws",                  r.getLaws());
        data.put("Name_of_Establishment", r.getNameOfEstablishment());
        data.put("Proponent",             r.getProponent());
        data.put("Mailing_Address",       r.getMailingAddress());
        data.put("Geo_N",                 r.getGeoN());
        data.put("Geo_E",                 r.getGeoE());
        data.put("Project_Location",      r.getProjectLocation());
        data.put("Nature_of_Business",    r.getNatureOfBusiness());
        data.put("Year_Establish",        r.getYearEstablish());
        data.put("PSIC_Code",             r.getPsicCode());
        data.put("Op_Hours_Day",          r.getOpHoursDay());
        data.put("Op_Day_Week",           r.getOpDayWeek());
        data.put("Op_Day_Year",           r.getOpDayYear());
        data.put("Male",                  r.getMale());
        data.put("Female",                r.getFemale());
        data.put("Number_of_Employee",    r.getNumberOfEmployee());
        data.put("Product_Lines",         r.getProductLines());
        data.put("Production_Rate",       r.getProductionRate());
        data.put("Actual_Production_Rate",r.getActualProductionRate());
        data.put("Name_of_Managing_Head", r.getNameOfManagingHead());
        data.put("Name_of_PCO",           r.getNameOfPCO());
        data.put("PCO_Accreditation_No",  r.getPcoAccreditationNo());
        data.put("Date_of_Effectivity",   r.getDateOfEffectivity());
        data.put("Phone_Fax_No",          r.getPhoneFaxNo());
        data.put("Email_Address",         r.getEmailAddress());
        data.put("Year_Covered",          r.getYearCovered());
        data.put("Vol_cu_m",              r.getVolCuM());

        // Section 3.3
        data.put("S331_Status",   r.getS331Status());   data.put("S331_Findings", r.getS331Findings());
        data.put("S331_Image1",   r.getS331Image1());   data.put("S331_Image2",   r.getS331Image2());
        data.put("S332_Status",   r.getS332Status());   data.put("S332_Findings", r.getS332Findings());
        data.put("S332_Image1",   r.getS332Image1());   data.put("S332_Image2",   r.getS332Image2());
        data.put("S333_Status",   r.getS333Status());   data.put("S333_Findings", r.getS333Findings());
        data.put("S333_Image1",   r.getS333Image1());   data.put("S333_Image2",   r.getS333Image2());
        data.put("S334_Status",   r.getS334Status());   data.put("S334_Findings", r.getS334Findings());
        data.put("S334_Image1",   r.getS334Image1());   data.put("S334_Image2",   r.getS334Image2());
        data.put("S335_Status",   r.getS335Status());   data.put("S335_Findings", r.getS335Findings());
        data.put("S335_Image1",   r.getS335Image1());   data.put("S335_Image2",   r.getS335Image2());
        data.put("S336_Status",   r.getS336Status());   data.put("S336_Findings", r.getS336Findings());
        data.put("S337_Status",   r.getS337Status());   data.put("S337_Findings", r.getS337Findings());

        // Section 4
        data.put("Rec_Confirmatory",        r.getRecConfirmatorysampling());
        data.put("Rec_RegularMonitoring",   r.getRecRegularMonitoring());
        data.put("Rec_IssuanceTempRenewal", r.getRecIssuanceTempRenewalPoaDp());
        data.put("Rec_AccreditationPco",    r.getRecAccreditationPco());
        data.put("Rec_SubmissionSmrCmr",    r.getRecSubmissionSmrCmr());
        data.put("Rec_IssuanceNomTc",       r.getRecIssuanceNomTc());
        data.put("Rec_IssuanceNov",         r.getRecIssuanceNov());
        data.put("Rec_SuspensionEcc",       r.getRecSuspensionEcc5DayCdo());
        data.put("Rec_EndorsementPab",      r.getRecEndorsementPab());
        data.put("Rec_Other",               r.getRecOther());

        // Signatures
        data.put("Submitted_By",           r.getSubmittedBy());
        data.put("Date_Submitted",         r.getDateSubmitted());
        data.put("Date_Travel_Concluded",  r.getDateTravelConcluded());
        data.put("Recommending1",          r.getRecommendingApproval1());
        data.put("Recommending1_Position", r.getRecommendingApproval1Position());
        data.put("Recommending2",          r.getRecommendingApproval2());
        data.put("Recommending2_Position", r.getRecommendingApproval2Position());
        data.put("Approved_By",            r.getApprovedBy());
        data.put("Approved_By_Position",   r.getApprovedByPosition());
        data.put("is_complete",            r.getIsComplete());

        Map<String, Object> parent = new HashMap<>();
        parent.put("UserID", r.getEmployeeId());
        firestore.collection("Monitoring_Records")
                .document(r.getEmployeeId())
                .set(parent, SetOptions.merge());

        String docId = r.getEmployeeId() + "_record_" + r.getId();
        firestore.collection("Monitoring_Records")
                .document(r.getEmployeeId())
                .collection("Records")
                .document(docId)
                .set(data)
                .addOnSuccessListener(unused -> {

                    ContentValues cv = new ContentValues();
                    cv.put("is_synced", 1);
                    dbConn.getWritableDatabase().update(
                            "Monitoring_Records", cv,
                            "id = ?", new String[]{String.valueOf(r.getId())});
                    Log.d(TAG, "Monitoring record synced: " + docId);
                    Toast.makeText(this,
                            "Monitoring record synced to cloud!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore sync failed: " + e.getMessage(), e);
                    Toast.makeText(this,
                            "Saved offline. Will sync when online.", Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void syncPurposeToFirestore() {
        try {
            SQLiteDatabase db = dbConn.getReadableDatabase();
            Cursor c = db.query("Purpose_Table", null,
                    "Employee_Id = ?", new String[]{employeeId},
                    null, null, "id DESC", "1");

            if (c == null || !c.moveToFirst()) return;

            int rowId = c.getInt(c.getColumnIndexOrThrow("id"));

            Map<String, Object> data = new HashMap<>();
            String[] cols = {"verifyAccuracy","pmpinNew","pmpinRenewal","hwgidNew","hwgidRenewal",
                    "hwtrNew","hwtrRenewal","hwtsdNew","hwtsdRenewal","poapciNew","poapciRenewal",
                    "dpNew","dpRenewal","otherPermit","othersPermitNew","othersPermitRenewal",
                    "determineCompliance","investigate","survey","otherSpecify","contactName","position"};
            for (String col : cols) {
                int idx = c.getColumnIndex(col);
                if (idx >= 0) data.put(col, c.getString(idx));
            }
            c.close();

            Map<String, Object> parent = new HashMap<>();
            parent.put("UserID", employeeId);
            firestore.collection("Purpose").document(employeeId).set(parent, SetOptions.merge());

            String docId = employeeId + "_purpose_" + rowId;
            firestore.collection("Purpose")
                    .document(employeeId)
                    .collection("Records")
                    .document(docId)
                    .set(data)
                    .addOnSuccessListener(unused -> {
                        ContentValues cv = new ContentValues();
                        cv.put("is_synced", 1);
                        dbConn.getWritableDatabase().update(
                                "Purpose_Table", cv, "id = ?",
                                new String[]{String.valueOf(rowId)});
                        Log.d(TAG, "Purpose synced: " + docId);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Purpose sync failed: " + e.getMessage()));

        } catch (Exception e) {
            Log.e(TAG, "syncPurposeToFirestore: " + e.getMessage(), e);
        }
    }

    private void syncComplianceToFirestore() {
        try {
            SQLiteDatabase db = dbConn.getReadableDatabase();
            Cursor c = db.query("Compliance_Status", null,
                    "Employee_Id = ?", new String[]{employeeId},
                    null, null, "id DESC", "1");

            if (c == null || !c.moveToFirst()) return;

            int rowId = c.getInt(c.getColumnIndexOrThrow("id"));

            Map<String, Object> data = new HashMap<>();
            for (int i = 0; i < c.getColumnCount(); i++) {
                String colName = c.getColumnName(i);
                if (!colName.equals("id")) data.put(colName, c.getString(i));
            }
            c.close();

            Map<String, Object> parent = new HashMap<>();
            parent.put("UserID", employeeId);
            firestore.collection("Compliance_Status").document(employeeId)
                    .set(parent, SetOptions.merge());

            String docId = employeeId + "_compliance_" + rowId;
            firestore.collection("Compliance_Status")
                    .document(employeeId)
                    .collection("Records")
                    .document(docId)
                    .set(data)
                    .addOnSuccessListener(unused -> {
                        ContentValues cv = new ContentValues();
                        cv.put("is_synced", 1);
                        dbConn.getWritableDatabase().update(
                                "Compliance_Status", cv, "id = ?",
                                new String[]{String.valueOf(rowId)});
                        Log.d(TAG, "Compliance synced: " + docId);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Compliance sync failed: " + e.getMessage()));

        } catch (Exception e) {
            Log.e(TAG, "syncComplianceToFirestore: " + e.getMessage(), e);
        }
    }


    private void setupSectionTitles() {
        if (s331 != null && s331.tvTitle != null) s331.tvTitle.setText("3.3.1 Environmental Impact Statement System");
        if (s332 != null && s332.tvTitle != null) s332.tvTitle.setText("3.3.2 Chemical Management");
        if (s333 != null && s333.tvTitle != null) s333.tvTitle.setText("3.3.3 Hazardous Waste Management");
        if (s334 != null && s334.tvTitle != null) s334.tvTitle.setText("3.3.4 Air Quality Management");
        if (s335 != null && s335.tvTitle != null) s335.tvTitle.setText("3.3.5 Water Quality Management");
        if (s336 != null && s336.tvTitle != null) s336.tvTitle.setText("3.3.6 Solid Waste Management");
        if (s337 != null && s337.tvTitle != null) s337.tvTitle.setText("3.3.7 Commitment/s from previous Technical Conference");
    }

    private void setupImagePickers() {
        FindingsSection[] withImages = { s331, s332, s333, s334, s335 };
        for (FindingsSection sec : withImages) {
            if (sec == null) continue;
            final FindingsSection s = sec;
            if (s.frameImage1 != null) s.frameImage1.setOnClickListener(v -> openPicker(s, 1));
            if (s.frameImage2 != null) s.frameImage2.setOnClickListener(v -> openPicker(s, 2));
        }
    }

    private void openPicker(FindingsSection section, int slot) {
        pendingSection = section;
        pendingSlot    = slot;
        Intent intent  = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Evidence Photo"));
    }

    private void setupDatePickers() {
        for (EditText et : new EditText[]{etDateSubmitted, etDateTravelConcluded}) {
            et.setOnClickListener(v -> {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(this,
                        (view, year, month, day) ->
                                et.setText(String.format("%04d-%02d-%02d", year, month + 1, day)),
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                ).show();
            });
        }
    }

    private void restoreFindings() {
        s331.setStatus(record.getS331Status()); setET(s331.etFindings, record.getS331Findings());
        loadImg(s331, record.getS331Image1(), record.getS331Image2());
        s332.setStatus(record.getS332Status()); setET(s332.etFindings, record.getS332Findings());
        loadImg(s332, record.getS332Image1(), record.getS332Image2());
        s333.setStatus(record.getS333Status()); setET(s333.etFindings, record.getS333Findings());
        loadImg(s333, record.getS333Image1(), record.getS333Image2());
        s334.setStatus(record.getS334Status()); setET(s334.etFindings, record.getS334Findings());
        loadImg(s334, record.getS334Image1(), record.getS334Image2());
        s335.setStatus(record.getS335Status()); setET(s335.etFindings, record.getS335Findings());
        loadImg(s335, record.getS335Image1(), record.getS335Image2());
        s336.setStatus(record.getS336Status()); setET(s336.etFindings, record.getS336Findings());
        s337.setStatus(record.getS337Status()); setET(s337.etFindings, record.getS337Findings());

        cbRecConfirmatory.setChecked(record.getRecConfirmatorysampling() == 1);
        cbRecRegularMonitoring.setChecked(record.getRecRegularMonitoring() == 1);
        cbRecIssuanceTempRenewal.setChecked(record.getRecIssuanceTempRenewalPoaDp() == 1);
        cbRecAccreditationPco.setChecked(record.getRecAccreditationPco() == 1);
        cbRecSubmissionSmrCmr.setChecked(record.getRecSubmissionSmrCmr() == 1);
        cbRecIssuanceNomTc.setChecked(record.getRecIssuanceNomTc() == 1);
        cbRecIssuanceNov.setChecked(record.getRecIssuanceNov() == 1);
        cbRecSuspensionEcc.setChecked(record.getRecSuspensionEcc5DayCdo() == 1);
        cbRecEndorsementPab.setChecked(record.getRecEndorsementPab() == 1);
        setET(etRecOther, record.getRecOther());
        setET(etSubmittedBy, record.getSubmittedBy());
        setET(etDateSubmitted, record.getDateSubmitted());
        setET(etDateTravelConcluded, record.getDateTravelConcluded());
        setET(etRecommending1, record.getRecommendingApproval1());
        setET(etRecommending1Position, record.getRecommendingApproval1Position());
        setET(etRecommending2, record.getRecommendingApproval2());
        setET(etRecommending2Position, record.getRecommendingApproval2Position());
        setET(etApprovedBy, record.getApprovedBy());
        setET(etApprovedByPosition, record.getApprovedByPosition());
    }

    private void loadImg(FindingsSection sec, String uri1, String uri2) {
        if (sec == null) return;
        if (uri1 != null && sec.ivImage1 != null) {
            Glide.with(this).load(Uri.parse(uri1)).centerCrop().into(sec.ivImage1);
            sec.setImageUri1(uri1);
            if (sec.ivUploadIcon1 != null) sec.ivUploadIcon1.setVisibility(View.GONE);
        }
        if (uri2 != null && sec.ivImage2 != null) {
            Glide.with(this).load(Uri.parse(uri2)).centerCrop().into(sec.ivImage2);
            sec.setImageUri2(uri2);
            if (sec.ivUploadIcon2 != null) sec.ivUploadIcon2.setVisibility(View.GONE);
        }
    }


    private void bindViews() {
        btnBack         = findViewById(R.id.btnBack);
        btnSaveComplete = findViewById(R.id.btnSaveComplete);

        tvDetailEmbId         = findViewById(R.id.tvDetailEmbId);
        tvDetailReportControl = findViewById(R.id.tvDetailReportControl);
        tvDetailTypeMonitoring= findViewById(R.id.tvDetailTypeMonitoring);
        tvDetailDate          = findViewById(R.id.tvDetailDate);
        tvDetailLaws          = findViewById(R.id.tvDetailLaws);
        tvDetailEstablishment = findViewById(R.id.tvDetailEstablishment);
        tvDetailProponent     = findViewById(R.id.tvDetailProponent);
        tvDetailLocation      = findViewById(R.id.tvDetailLocation);
        tvDetailGeo           = findViewById(R.id.tvDetailGeo);
        tvDetailNature        = findViewById(R.id.tvDetailNature);
        tvDetailYearEstab     = findViewById(R.id.tvDetailYearEstab);
        tvDetailPsic          = findViewById(R.id.tvDetailPsic);
        tvDetailOperating     = findViewById(R.id.tvDetailOperating);
        tvDetailEmployees     = findViewById(R.id.tvDetailEmployees);
        tvDetailProductLines  = findViewById(R.id.tvDetailProductLines);
        tvDetailProductionRate= findViewById(R.id.tvDetailProductionRate);
        tvDetailActualRate    = findViewById(R.id.tvDetailActualRate);
        tvDetailManagingHead  = findViewById(R.id.tvDetailManagingHead);
        tvDetailPco           = findViewById(R.id.tvDetailPco);
        tvDetailPcoAccred     = findViewById(R.id.tvDetailPcoAccred);
        tvDetailEffectivity   = findViewById(R.id.tvDetailEffectivity);
        tvDetailContact       = findViewById(R.id.tvDetailContact);

        tvDetailPurpose         = findViewById(R.id.tvDetailPurpose);
        tvDetailContactPerson   = findViewById(R.id.tvDetailContactPerson);
        tvDetailContactPosition = findViewById(R.id.tvDetailContactPosition);

        tvDetailCompliance = findViewById(R.id.tvDetailCompliance);

        s331 = new FindingsSection(findViewById(R.id.section331), "", true);
        s332 = new FindingsSection(findViewById(R.id.section332), "", true);
        s333 = new FindingsSection(findViewById(R.id.section333), "", true);
        s334 = new FindingsSection(findViewById(R.id.section334), "", true);
        s335 = new FindingsSection(findViewById(R.id.section335), "", true);
        s336 = new FindingsSection(findViewById(R.id.section336), "", false);
        s337 = new FindingsSection(findViewById(R.id.section337), "", false);

        cbRecConfirmatory       = findViewById(R.id.cbRecConfirmatory);
        cbRecRegularMonitoring  = findViewById(R.id.cbRecRegularMonitoring);
        cbRecIssuanceTempRenewal= findViewById(R.id.cbRecIssuanceTempRenewal);
        cbRecAccreditationPco   = findViewById(R.id.cbRecAccreditationPco);
        cbRecSubmissionSmrCmr   = findViewById(R.id.cbRecSubmissionSmrCmr);
        cbRecIssuanceNomTc      = findViewById(R.id.cbRecIssuanceNomTc);
        cbRecIssuanceNov        = findViewById(R.id.cbRecIssuanceNov);
        cbRecSuspensionEcc      = findViewById(R.id.cbRecSuspensionEcc);
        cbRecEndorsementPab     = findViewById(R.id.cbRecEndorsementPab);
        cbRecOther              = findViewById(R.id.cbRecOther);
        etRecOther              = findViewById(R.id.etRecOther);
        etRegularMonitoringDesc = findViewById(R.id.etRegularMonitoringDesc);

        etSubmittedBy           = findViewById(R.id.etSubmittedBy);
        etDateSubmitted         = findViewById(R.id.etDateSubmitted);
        etDateTravelConcluded   = findViewById(R.id.etDateTravelConcluded);
        etRecommending1         = findViewById(R.id.etRecommending1);
        etRecommending1Position = findViewById(R.id.etRecommending1Position);
        etRecommending2         = findViewById(R.id.etRecommending2);
        etRecommending2Position = findViewById(R.id.etRecommending2Position);
        etApprovedBy            = findViewById(R.id.etApprovedBy);
        etApprovedByPosition    = findViewById(R.id.etApprovedByPosition);
    }


    private void setText(TextView tv, String val) { if (tv != null) tv.setText(val != null ? val : "--"); }
    private void setET(EditText et, String val) { if (et != null && val != null) et.setText(val); }
    private String s(String val) { return val != null ? val : "--"; }

    private String getString(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return i >= 0 ? c.getString(i) : null;
    }

    private int getInt(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return i >= 0 ? c.getInt(i) : 0;
    }
}