package com.example.embr6monitoringapp.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;

import com.example.embr6monitoringapp.Database.DatabaseConnection;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SyncManager {

    private static final String TAG = "SyncManager";
    private static final String COL_REGISTERED_USER          = "Registered_User";
    private static final String COL_ESTABLISHMENT_MONITORING = "Establishment_Monitoring";
    private static final String COL_PURPOSE                  = "Purpose";
    private static final String COL_COMPLIANCE               = "Compliance_Status";
    private static final String COL_MONITORING_RECORDS       = "Monitoring_Records";

    private final Context            context;
    private final DatabaseConnection dbConnection;
    private final FirebaseFirestore  firestore;

    public SyncManager(Context context) {
        this.context      = context.getApplicationContext();
        this.dbConnection = DatabaseConnection.getInstance(context);
        this.firestore    = FirebaseFirestore.getInstance();
    }

    public boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return caps != null &&
                        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                //noinspection deprecation
                return cm.getActiveNetworkInfo() != null &&
                        cm.getActiveNetworkInfo().isConnected();
            }
        } catch (Exception e) {
            Log.e(TAG, "isOnline error: " + e.getMessage());
            return false;
        }
    }

    public void syncIfOnline() {
        if (!isOnline()) {
            Log.d(TAG, "Offline — sync skipped. Data saved locally, will sync when online.");
            return;
        }
        Log.d(TAG, "Online — starting full sync...");

        new Thread(() -> {
            try {
                syncRegisteredUsers();
                syncReportInfo();
                syncEstablishmentInfo();
                syncYearCoveredInfo();
                syncPurpose();
                syncComplianceStatus();
                syncMonitoringRecords();
            } catch (Exception e) {
                Log.e(TAG, "syncIfOnline crashed: " + e.getMessage(), e);
            }
        }).start();
    }


    private void syncRegisteredUsers() {
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        Cursor c = db.query("Register", null, "is_synced = 0", null, null, null, null);
        if (c == null) return;

        try {
            while (c.moveToNext()) {
                int    rowId      = safeInt(c, "id");
                String employeeId = safeStr(c, "Employee_Id");
                String firstName  = safeStr(c, "First_Name");
                String lastName   = safeStr(c, "Last_Name");
                String middleName = safeStr(c, "Middle_Name");
                String position   = safeStr(c, "Position");
                String username   = safeStr(c, "Username");
                String password   = safeStr(c, "Password");

                if (isEmpty(employeeId)) {
                    Log.w(TAG, "Register row " + rowId + ": no Employee_Id — skipped");
                    continue;
                }

                Map<String, Object> data = new HashMap<>();

                data.put("UserID",      employeeId);
                data.put("Employee_Id", employeeId);

                data.put("First_Name",  firstName  != null ? firstName  : "");
                data.put("Last_Name",   lastName   != null ? lastName   : "");
                data.put("Middle_Name", middleName != null ? middleName : "");
                data.put("Position",    position   != null ? position   : "");

                data.put("Username",    username   != null ? username   : "");
                data.put("Password",    password   != null ? password   : "");

                data.put("status", "completed");

                final int fId = rowId;
                firestore.collection(COL_REGISTERED_USER)
                        .document(employeeId)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(u -> {
                            markSynced("Register", fId);
                            Log.d(TAG, "Registered_User synced: " + employeeId);
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Register sync failed: " + e.getMessage()));
            }
        } finally {
            c.close();
        }
    }

    private void syncReportInfo() {
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        Cursor c = db.query("Report_Info", null, "is_synced = 0", null, null, null, null);
        if (c == null) return;

        try {
            while (c.moveToNext()) {
                int    rowId      = safeInt(c, "id");
                String employeeId = safeStr(c, "Employee_Id");
                if (isEmpty(employeeId)) continue;

                ensureParentDoc(COL_ESTABLISHMENT_MONITORING, employeeId);

                Map<String, Object> data = new HashMap<>();
                data.put("Employee_Id",        employeeId);
                data.put("Emb_Id",             safeStr(c, "Emb_Id"));
                data.put("Report_Control",     safeStr(c, "Report_Control"));
                data.put("Type_Monitoring",    safeStr(c, "Type_Monitoring"));
                data.put("Date_of_Inspection", safeStr(c, "Date_of_Inspection"));

                final int fId = rowId;
                firestore.collection(COL_ESTABLISHMENT_MONITORING)
                        .document(employeeId)
                        .collection("Report_Info")
                        .document(employeeId + "_report_" + rowId)
                        .set(data)
                        .addOnSuccessListener(u -> {
                            markSynced("Report_Info", fId);
                            Log.d(TAG, "Report_Info synced: " + fId);
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Report_Info failed: " + e.getMessage()));
            }
        } finally { c.close(); }
    }

    private void syncEstablishmentInfo() {
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        Cursor c = db.query("Establishment_Info", null, "is_synced = 0", null, null, null, null);
        if (c == null) return;

        try {
            while (c.moveToNext()) {
                int    rowId      = safeInt(c, "id");
                String employeeId = safeStr(c, "Employee_Id");
                if (isEmpty(employeeId)) continue;

                ensureParentDoc(COL_ESTABLISHMENT_MONITORING, employeeId);

                Map<String, Object> data = new HashMap<>();
                data.put("Employee_Id",            employeeId);
                data.put("Laws",                   safeStr(c, "Laws"));
                data.put("Name_of_Establishment",  safeStr(c, "Name_of_Establishment"));
                data.put("proponent",              safeStr(c, "proponent"));
                data.put("Mailing_Address",        safeStr(c, "Mailing_Address"));
                data.put("N",                      safeStr(c, "N"));
                data.put("E",                      safeStr(c, "E"));
                data.put("Project_Location",       safeStr(c, "Project_Location"));
                data.put("Nature_of_Business",     safeStr(c, "Nature_of_Business"));
                data.put("Year_Establish",         safeStr(c, "Year_Establish"));
                data.put("PSIC_Code",              safeStr(c, "PSIC_Code"));
                data.put("Op_hours_day",           safeStr(c, "Op_hours_day"));
                data.put("Op_day_week",            safeStr(c, "Op_day_week"));
                data.put("Op_day_year",            safeStr(c, "Op_day_year"));
                data.put("Male",                   safeStr(c, "Male"));
                data.put("Female",                 safeStr(c, "Female"));
                data.put("Number_of_Employee",     safeStr(c, "Number_of_Employee"));
                data.put("Product_Lines",          safeStr(c, "Product_Lines"));
                data.put("Production_Rate",        safeStr(c, "Production_Rate"));
                data.put("Actual_Production_Rate", safeStr(c, "Actual_Production_Rate"));
                data.put("Name_of_Managing_Head",  safeStr(c, "Name_of_Managing_Head"));
                data.put("Name_of_PCO",            safeStr(c, "Name_of_PCO"));
                data.put("PCO_Accreditation_No",   safeStr(c, "PCO_Accreditation_No"));
                data.put("Date_of_Effectivity",    safeStr(c, "Date_of_Effectivity"));
                data.put("Phone_Fax_No",           safeStr(c, "Phone_Fax_No"));
                data.put("Email_Address",          safeStr(c, "Email_Address"));

                final int fId = rowId;
                firestore.collection(COL_ESTABLISHMENT_MONITORING)
                        .document(employeeId)
                        .collection("Establishment_Info")
                        .document(employeeId + "_establishment_" + rowId)
                        .set(data)
                        .addOnSuccessListener(u -> {
                            markSynced("Establishment_Info", fId);
                            Log.d(TAG, "Establishment_Info synced: " + fId);
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Establishment_Info failed: " + e.getMessage()));
            }
        } finally { c.close(); }
    }

    private void syncYearCoveredInfo() {
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        Cursor c = db.query("YearCoverd_Info", null, "is_synced = 0", null, null, null, null);
        if (c == null) return;

        try {
            while (c.moveToNext()) {
                int    rowId      = safeInt(c, "id");
                String employeeId = safeStr(c, "Employee_Id");
                if (isEmpty(employeeId)) continue;

                ensureParentDoc(COL_ESTABLISHMENT_MONITORING, employeeId);

                Map<String, Object> data = new HashMap<>();
                data.put("Employee_Id", employeeId);
                data.put("Year_Coverd", safeStr(c, "Year_Coverd"));
                data.put("Vol_cu_m",    safeStr(c, "Vol_cu_m"));
                data.put("Total",       safeStr(c, "Total"));

                final int fId = rowId;
                firestore.collection(COL_ESTABLISHMENT_MONITORING)
                        .document(employeeId)
                        .collection("YearCoverd_Info")
                        .document(employeeId + "_year_" + rowId)
                        .set(data)
                        .addOnSuccessListener(u -> {
                            markSynced("YearCoverd_Info", fId);
                            Log.d(TAG, "YearCoverd_Info synced: " + fId);
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "YearCoverd_Info failed: " + e.getMessage()));
            }
        } finally { c.close(); }
    }

    private void syncPurpose() {
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        Cursor c = db.query("Purpose_Table", null, "is_synced = 0", null, null, null, null);
        if (c == null) {
            Log.d(TAG, "No Purpose data to sync.");
            return;
        }

        try {
            if (c.getCount() == 0) {
                Log.d(TAG, "No unsynced Purpose rows.");
                return;
            }

            while (c.moveToNext()) {
                int rowId = safeInt(c, "id");
                String employeeId = safeStr(c, "Employee_Id");
                if (isEmpty(employeeId)) {
                    Log.w(TAG, "Purpose row " + rowId + ": Employee_Id is null/empty — skipped.");
                    continue;
                }

                ensureParentDoc(COL_PURPOSE, employeeId);

                Map<String, Object> data = new HashMap<>();
                data.put("Employee_Id",          employeeId);
                data.put("verifyAccuracy",        safeInt(c, "verifyAccuracy") == 1);
                data.put("pmpinNew",              safeInt(c, "pmpinNew") == 1);
                data.put("pmpinRenewal",          safeInt(c, "pmpinRenewal") == 1);
                data.put("hwgidNew",              safeInt(c, "hwgidNew") == 1);
                data.put("hwgidRenewal",          safeInt(c, "hwgidRenewal") == 1);
                data.put("hwtrNew",               safeInt(c, "hwtrNew") == 1);
                data.put("hwtrRenewal",           safeInt(c, "hwtrRenewal") == 1);
                data.put("hwtsdNew",              safeInt(c, "hwtsdNew") == 1);
                data.put("hwtsdRenewal",          safeInt(c, "hwtsdRenewal") == 1);
                data.put("poapciNew",             safeInt(c, "poapciNew") == 1);
                data.put("poapciRenewal",         safeInt(c, "poapciRenewal") == 1);
                data.put("dpNew",                 safeInt(c, "dpNew") == 1);
                data.put("dpRenewal",             safeInt(c, "dpRenewal") == 1);
                data.put("otherPermit",           safeStr(c, "otherPermit"));
                data.put("othersPermitNew",       safeInt(c, "othersPermitNew") == 1);
                data.put("othersPermitRenewal",   safeInt(c, "othersPermitRenewal") == 1);
                data.put("determineCompliance",   safeInt(c, "determineCompliance") == 1);
                data.put("investigate",           safeInt(c, "investigate") == 1);
                data.put("survey",                safeInt(c, "survey") == 1);
                data.put("othersCEMCRR",          safeInt(c, "othersCEMCRR") == 1);
                data.put("otherSpecify",          safeStr(c, "otherSpecify"));
                data.put("contactName",           safeStr(c, "contactName"));
                data.put("position",              safeStr(c, "position"));
                data.put("syncedAt",              System.currentTimeMillis());

                final int fId = rowId;
                firestore.collection(COL_PURPOSE)
                        .document(employeeId)
                        .collection("Records")
                        .document(employeeId + "_purpose_" + rowId)
                        .set(data)
                        .addOnSuccessListener(u -> {
                            markSynced("Purpose_Table", fId);
                            Log.d(TAG, "Purpose synced: row=" + fId);
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Purpose sync failed: " + e.getMessage()));
            }
        } finally {
            if (!c.isClosed()) c.close();
        }
    }

    private void syncComplianceStatus() {
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        Cursor c = db.query("Compliance_Status", null, "is_synced = 0", null, null, null, null);
        if (c == null) { Log.d(TAG, "No Compliance data to sync."); return; }

        try {
            if (c.getCount() == 0) { Log.d(TAG, "No unsynced Compliance rows."); return; }

            while (c.moveToNext()) {
                int    rowId      = safeInt(c, "id");
                String employeeId = safeStr(c, "Employee_Id");
                if (isEmpty(employeeId)) { Log.w(TAG, "Compliance row " + rowId + ": no Employee_Id"); continue; }

                ensureParentDoc(COL_COMPLIANCE, employeeId);

                Map<String, Object> data = new HashMap<>();
                for (int i = 0; i < c.getColumnCount(); i++) {
                    String col = c.getColumnName(i);
                    if (!col.equals("id") && !col.equals("is_synced")) {
                        data.put(col, c.getString(i));
                    }
                }

                final int fId = rowId;
                firestore.collection(COL_COMPLIANCE)
                        .document(employeeId)
                        .collection("Records")
                        .document(employeeId + "_compliance_" + rowId)
                        .set(data)
                        .addOnSuccessListener(u -> {
                            markSynced("Compliance_Status", fId);
                            Log.d(TAG, "Compliance synced: row=" + fId);
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Compliance sync failed: " + e.getMessage()));
            }
        } finally { c.close(); }
    }

    private void syncMonitoringRecords() {
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        Cursor c = db.query("Monitoring_Records", null, "is_synced = 0", null, null, null, null);
        if (c == null) { Log.d(TAG, "No Monitoring_Records to sync."); return; }

        try {
            if (c.getCount() == 0) { Log.d(TAG, "No unsynced Monitoring_Records."); return; }

            while (c.moveToNext()) {
                int    rowId      = safeInt(c, "id");
                String employeeId = safeStr(c, "Employee_Id");
                if (isEmpty(employeeId)) { Log.w(TAG, "MonitoringRecord row " + rowId + ": no Employee_Id"); continue; }

                ensureParentDoc(COL_MONITORING_RECORDS, employeeId);

                Map<String, Object> data = new HashMap<>();
                for (int i = 0; i < c.getColumnCount(); i++) {
                    String col = c.getColumnName(i);
                    if (!col.equals("id") && !col.equals("is_synced")) {
                        data.put(col, c.getString(i));
                    }
                }

                final int fId = rowId;
                firestore.collection(COL_MONITORING_RECORDS)
                        .document(employeeId)
                        .collection("Records")
                        .document(employeeId + "_record_" + rowId)
                        .set(data)
                        .addOnSuccessListener(u -> {
                            markSynced("Monitoring_Records", fId);
                            Log.d(TAG, "Monitoring_Records synced: row=" + fId);
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Monitoring_Records sync failed: " + e.getMessage()));
            }
        } finally { c.close(); }
    }

    private void ensureParentDoc(String collection, String employeeId) {
        Map<String, Object> marker = new HashMap<>();
        marker.put("UserID", employeeId);
        firestore.collection(collection)
                .document(employeeId)
                .set(marker, SetOptions.merge())
                .addOnFailureListener(e ->
                        Log.e(TAG, "ensureParentDoc(" + collection + ") failed: " + e.getMessage()));
    }

    private void markSynced(String table, int rowId) {
        ContentValues cv = new ContentValues();
        cv.put("is_synced", 1);
        dbConnection.getWritableDatabase()
                .update(table, cv, "id = ?", new String[]{ String.valueOf(rowId) });
    }

    private String safeStr(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return i >= 0 ? c.getString(i) : null;
    }

    private int safeInt(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return i >= 0 ? c.getInt(i) : 0;
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}