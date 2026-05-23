package com.example.embr6monitoringapp.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseConnection extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseConnection";
    public static final String DATABASE_NAME = "EMBR6DB.db";
    private static final int DATABASE_VERSION = 14;

    private static DatabaseConnection instance;

    public static synchronized DatabaseConnection getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseConnection(context.getApplicationContext());
            Log.d(TAG, "Singleton instance created.");
        }
        return instance;
    }

    private DatabaseConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: creating all tables.");

        db.execSQL("CREATE TABLE Register (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Employee_Id TEXT UNIQUE, First_Name TEXT, Last_Name TEXT, Middle_Name TEXT, Position TEXT," +
                "Username TEXT UNIQUE, Password TEXT, is_synced INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE Report_Info (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Employee_Id TEXT, Emb_Id TEXT, Report_Control TEXT," +
                "Type_Monitoring TEXT, Date_of_Inspection TEXT, is_synced INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE Establishment_Info (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Employee_Id TEXT, Laws TEXT, Name_of_Establishment TEXT," +
                "proponent TEXT, Mailing_Address TEXT, N TEXT, E TEXT," +
                "Project_Location TEXT, Nature_of_Business TEXT, Year_Establish TEXT," +
                "PSIC_Code TEXT, Op_hours_day TEXT, Op_day_week TEXT, Op_day_year TEXT," +
                "Male TEXT, Female TEXT, Number_of_Employee TEXT," +
                "Product_Lines TEXT, Production_Rate TEXT, Actual_Production_Rate TEXT," +
                "Name_of_Managing_Head TEXT, Name_of_PCO TEXT, PCO_Accreditation_No TEXT," +
                "Date_of_Effectivity TEXT, Phone_Fax_No TEXT, Email_Address TEXT," +
                "is_synced INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE YearCoverd_Info (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Employee_Id TEXT, Year_Coverd TEXT, Vol_cu_m TEXT, Total TEXT," +
                "is_synced INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE Purpose_Table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Employee_Id TEXT," +
                "verifyAccuracy INTEGER DEFAULT 0," +
                "pmpinNew INTEGER DEFAULT 0, pmpinRenewal INTEGER DEFAULT 0," +
                "hwgidNew INTEGER DEFAULT 0, hwgidRenewal INTEGER DEFAULT 0," +
                "hwtrNew INTEGER DEFAULT 0, hwtrRenewal INTEGER DEFAULT 0," +
                "hwtsdNew INTEGER DEFAULT 0, hwtsdRenewal INTEGER DEFAULT 0," +
                "poapciNew INTEGER DEFAULT 0, poapciRenewal INTEGER DEFAULT 0," +
                "dpNew INTEGER DEFAULT 0, dpRenewal INTEGER DEFAULT 0," +
                "otherPermit TEXT," +
                "othersPermitNew INTEGER DEFAULT 0, othersPermitRenewal INTEGER DEFAULT 0," +
                "determineCompliance INTEGER DEFAULT 0," +
                "investigate INTEGER DEFAULT 0," +
                "survey INTEGER DEFAULT 0," +
                "othersCEMCRR INTEGER DEFAULT 0," +
                "otherSpecify TEXT," +
                "contactName TEXT," +
                "position TEXT," +
                "is_synced INTEGER DEFAULT 0)");

        db.execSQL(createMonitoringRecordsTable());
        db.execSQL(createComplianceTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: v" + oldVersion + " → v" + newVersion);

        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE Register ADD COLUMN is_synced INTEGER DEFAULT 0");
        }
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE Register ADD COLUMN First_Name TEXT");
            db.execSQL("ALTER TABLE Register ADD COLUMN Last_Name TEXT");
            db.execSQL("ALTER TABLE Register ADD COLUMN Middle_Name TEXT");
            db.execSQL("ALTER TABLE Register ADD COLUMN Position TEXT");
        }
        if (oldVersion < 8) {
            db.execSQL("DROP TABLE IF EXISTS Monitoring_Records");
            db.execSQL(createMonitoringRecordsTable());
        }
        if (oldVersion < 9) {
            db.execSQL(createComplianceTable());
        }
        if (oldVersion < 10) {
            String[] newCols = {
                    "S331_Status TEXT", "S331_Findings TEXT", "S331_Image1 TEXT", "S331_Image2 TEXT",
                    "S332_Status TEXT", "S332_Findings TEXT", "S332_Image1 TEXT", "S332_Image2 TEXT",
                    "S333_Status TEXT", "S333_Findings TEXT", "S333_Image1 TEXT", "S333_Image2 TEXT",
                    "S334_Status TEXT", "S334_Findings TEXT", "S334_Image1 TEXT", "S334_Image2 TEXT",
                    "S335_Status TEXT", "S335_Findings TEXT", "S335_Image1 TEXT", "S335_Image2 TEXT",
                    "S336_Status TEXT", "S336_Findings TEXT",
                    "S337_Status TEXT", "S337_Findings TEXT",
                    "Rec_Confirmatory INTEGER DEFAULT 0",
                    "Rec_RegularMonitoring INTEGER DEFAULT 0",
                    "Rec_IssuanceTempRenewal INTEGER DEFAULT 0",
                    "Rec_AccreditationPco INTEGER DEFAULT 0",
                    "Rec_SubmissionSmrCmr INTEGER DEFAULT 0",
                    "Rec_IssuanceNomTc INTEGER DEFAULT 0",
                    "Rec_IssuanceNov INTEGER DEFAULT 0",
                    "Rec_SuspensionEcc INTEGER DEFAULT 0",
                    "Rec_EndorsementPab INTEGER DEFAULT 0",
                    "Rec_Other TEXT",
                    "Submitted_By TEXT",
                    "Date_Submitted TEXT",
                    "Date_Travel_Concluded TEXT",
                    "Recommending1 TEXT", "Recommending1_Position TEXT",
                    "Recommending2 TEXT", "Recommending2_Position TEXT",
                    "Approved_By TEXT", "Approved_By_Position TEXT"
            };
            for (String col : newCols) {
                try {
                    db.execSQL("ALTER TABLE Monitoring_Records ADD COLUMN " + col);
                } catch (Exception e) {
                    Log.w(TAG, "Column may already exist: " + col);
                }
            }
            Log.d(TAG, "onUpgrade v10: Monitoring_Records extended");
        }
        if (oldVersion < 13) {
            upgradePurposeTableToIntegerStructure(db);
        }
        if (oldVersion < 14) {
            addIsArchivedColumn(db);
        }
    }

    private void addIsArchivedColumn(SQLiteDatabase db) {
        try {

            Cursor cursor = db.rawQuery("PRAGMA table_info(Monitoring_Records)", null);
            boolean hasArchivedColumn = false;

            if (cursor.moveToFirst()) {
                int nameColumnIndex = cursor.getColumnIndex("name");
                do {
                    String columnName = cursor.getString(nameColumnIndex);
                    if ("is_archived".equalsIgnoreCase(columnName)) {
                        hasArchivedColumn = true;
                        break;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();

            if (!hasArchivedColumn) {
                db.execSQL("ALTER TABLE Monitoring_Records ADD COLUMN is_archived INTEGER DEFAULT 0");
                Log.d(TAG, "Successfully added is_archived column to Monitoring_Records");
            } else {
                Log.d(TAG, "is_archived column already exists");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding is_archived column: " + e.getMessage(), e);
        }
    }

    private void upgradePurposeTableToIntegerStructure(SQLiteDatabase db) {
        try {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("PRAGMA table_info(Purpose_Table)", null);
                boolean hasOldStructure = false;
                boolean hasNewStructure = false;

                if (cursor != null && cursor.moveToFirst()) {
                    int nameColumnIndex = cursor.getColumnIndex("name");
                    if (nameColumnIndex >= 0) {
                        do {
                            String columnName = cursor.getString(nameColumnIndex);
                            if (columnName != null) {
                                if (columnName.equals("selected_purposes") || columnName.equals("selected_permit_types")) {
                                    hasOldStructure = true;
                                }
                                if (columnName.equals("verifyAccuracy") || columnName.equals("pmpinNew")) {
                                    hasNewStructure = true;
                                }
                            }
                        } while (cursor.moveToNext());
                    }
                }

                if (hasNewStructure) {
                    Log.d(TAG, "Purpose_Table already has new integer structure - skipping migration");
                    return;
                }

                if (hasOldStructure) {
                    Log.d(TAG, "Migrating Purpose_Table from string-based to integer-based structure");
                    db.execSQL("ALTER TABLE Purpose_Table RENAME TO Purpose_Table_Old");
                    db.execSQL("CREATE TABLE Purpose_Table (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "Employee_Id TEXT," +
                            "verifyAccuracy INTEGER DEFAULT 0," +
                            "pmpinNew INTEGER DEFAULT 0, pmpinRenewal INTEGER DEFAULT 0," +
                            "hwgidNew INTEGER DEFAULT 0, hwgidRenewal INTEGER DEFAULT 0," +
                            "hwtrNew INTEGER DEFAULT 0, hwtrRenewal INTEGER DEFAULT 0," +
                            "hwtsdNew INTEGER DEFAULT 0, hwtsdRenewal INTEGER DEFAULT 0," +
                            "poapciNew INTEGER DEFAULT 0, poapciRenewal INTEGER DEFAULT 0," +
                            "dpNew INTEGER DEFAULT 0, dpRenewal INTEGER DEFAULT 0," +
                            "otherPermit TEXT," +
                            "othersPermitNew INTEGER DEFAULT 0, othersPermitRenewal INTEGER DEFAULT 0," +
                            "determineCompliance INTEGER DEFAULT 0," +
                            "investigate INTEGER DEFAULT 0," +
                            "survey INTEGER DEFAULT 0," +
                            "othersCEMCRR INTEGER DEFAULT 0," +
                            "otherSpecify TEXT," +
                            "contactName TEXT," +
                            "position TEXT," +
                            "is_synced INTEGER DEFAULT 0)");
                    try {
                        db.execSQL("INSERT INTO Purpose_Table (id, Employee_Id, contactName, position, is_synced) " +
                                "SELECT id, Employee_Id, contact_name, position, is_synced FROM Purpose_Table_Old");
                    } catch (Exception e) {
                        Log.e(TAG, "Error copying data: " + e.getMessage());
                        db.execSQL("INSERT INTO Purpose_Table (id, Employee_Id, is_synced) " +
                                "SELECT id, Employee_Id, is_synced FROM Purpose_Table_Old");
                    }
                    db.execSQL("DROP TABLE Purpose_Table_Old");

                    Log.d(TAG, "Purpose_Table migration completed successfully. " +
                            "Old string data could not be converted - users may need to re-enter purpose data.");
                } else {
                    // Table doesn't exist or has unknown structure - create fresh
                    Log.d(TAG, "Purpose_Table doesn't exist or has unknown structure - creating fresh");
                    db.execSQL("CREATE TABLE IF NOT EXISTS Purpose_Table (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "Employee_Id TEXT," +
                            "verifyAccuracy INTEGER DEFAULT 0," +
                            "pmpinNew INTEGER DEFAULT 0, pmpinRenewal INTEGER DEFAULT 0," +
                            "hwgidNew INTEGER DEFAULT 0, hwgidRenewal INTEGER DEFAULT 0," +
                            "hwtrNew INTEGER DEFAULT 0, hwtrRenewal INTEGER DEFAULT 0," +
                            "hwtsdNew INTEGER DEFAULT 0, hwtsdRenewal INTEGER DEFAULT 0," +
                            "poapciNew INTEGER DEFAULT 0, poapciRenewal INTEGER DEFAULT 0," +
                            "dpNew INTEGER DEFAULT 0, dpRenewal INTEGER DEFAULT 0," +
                            "otherPermit TEXT," +
                            "othersPermitNew INTEGER DEFAULT 0, othersPermitRenewal INTEGER DEFAULT 0," +
                            "determineCompliance INTEGER DEFAULT 0," +
                            "investigate INTEGER DEFAULT 0," +
                            "survey INTEGER DEFAULT 0," +
                            "othersCEMCRR INTEGER DEFAULT 0," +
                            "otherSpecify TEXT," +
                            "contactName TEXT," +
                            "position TEXT," +
                            "is_synced INTEGER DEFAULT 0)");
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading Purpose_Table: " + e.getMessage(), e);
        }
    }

    private String createMonitoringRecordsTable() {
        return "CREATE TABLE IF NOT EXISTS Monitoring_Records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Employee_Id TEXT, Emb_Id TEXT, Report_Control TEXT," +
                "Type_Monitoring TEXT, Date_of_Inspection TEXT," +
                "Laws TEXT, Name_of_Establishment TEXT, Proponent TEXT," +
                "Mailing_Address TEXT, Geo_N TEXT, Geo_E TEXT," +
                "Project_Location TEXT, Nature_of_Business TEXT," +
                "Year_Establish TEXT, PSIC_Code TEXT," +
                "Op_Hours_Day TEXT, Op_Day_Week TEXT, Op_Day_Year TEXT," +
                "Male TEXT, Female TEXT, Number_of_Employee TEXT," +
                "Product_Lines TEXT, Production_Rate TEXT, Actual_Production_Rate TEXT," +
                "Name_of_Managing_Head TEXT, Name_of_PCO TEXT," +
                "PCO_Accreditation_No TEXT, Date_of_Effectivity TEXT," +
                "Phone_Fax_No TEXT, Email_Address TEXT," +
                "Year_Covered TEXT, Vol_cu_m TEXT, Total TEXT," +

                "S331_Status TEXT, S331_Findings TEXT, S331_Image1 TEXT, S331_Image2 TEXT," +
                "S332_Status TEXT, S332_Findings TEXT, S332_Image1 TEXT, S332_Image2 TEXT," +
                "S333_Status TEXT, S333_Findings TEXT, S333_Image1 TEXT, S333_Image2 TEXT," +
                "S334_Status TEXT, S334_Findings TEXT, S334_Image1 TEXT, S334_Image2 TEXT," +
                "S335_Status TEXT, S335_Findings TEXT, S335_Image1 TEXT, S335_Image2 TEXT," +
                "S336_Status TEXT, S336_Findings TEXT," +
                "S337_Status TEXT, S337_Findings TEXT," +

                "Rec_Confirmatory INTEGER DEFAULT 0," +
                "Rec_RegularMonitoring INTEGER DEFAULT 0," +
                "Rec_IssuanceTempRenewal INTEGER DEFAULT 0," +
                "Rec_AccreditationPco INTEGER DEFAULT 0," +
                "Rec_SubmissionSmrCmr INTEGER DEFAULT 0," +
                "Rec_IssuanceNomTc INTEGER DEFAULT 0," +
                "Rec_IssuanceNov INTEGER DEFAULT 0," +
                "Rec_SuspensionEcc INTEGER DEFAULT 0," +
                "Rec_EndorsementPab INTEGER DEFAULT 0," +
                "Rec_Other TEXT," +

                "Submitted_By TEXT, Date_Submitted TEXT, Date_Travel_Concluded TEXT," +
                "Recommending1 TEXT, Recommending1_Position TEXT," +
                "Recommending2 TEXT, Recommending2_Position TEXT," +
                "Approved_By TEXT, Approved_By_Position TEXT," +
                "is_complete INTEGER DEFAULT 0, " +
                "is_synced INTEGER DEFAULT 0, " +
                "is_archived INTEGER DEFAULT 0)"; // Added is_archived column
    }

    private String createComplianceTable() {
        return "CREATE TABLE IF NOT EXISTS Compliance_Status (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Employee_Id TEXT," +
                "Pd1586_Ecc1 TEXT, Pd1586_Ecc1_DateFrom TEXT, Pd1586_Ecc1_DateTo TEXT," +
                "Pd1586_Ecc2 TEXT, Pd1586_Ecc2_DateFrom TEXT, Pd1586_Ecc2_DateTo TEXT," +
                "Pd1586_Ecc3 TEXT, Pd1586_Ecc3_DateFrom TEXT, Pd1586_Ecc3_DateTo TEXT," +
                "Ra6969_Denr TEXT, Ra6969_Denr_DateFrom TEXT, Ra6969_Denr_DateTo TEXT," +
                "Ra6969_Pcl TEXT, Ra6969_Pcl_DateFrom TEXT, Ra6969_Pcl_DateTo TEXT," +
                "Ra6969_Importer TEXT, Ra6969_Importer_DateFrom TEXT, Ra6969_Importer_DateTo TEXT," +
                "Ra6969_Cco TEXT, Ra6969_Cco_DateFrom TEXT, Ra6969_Cco_DateTo TEXT," +
                "Ra6969_Permit TEXT, Ra6969_Permit_DateFrom TEXT, Ra6969_Permit_DateTo TEXT," +
                "Ra6969_Cot TEXT, Ra6969_Cot_DateFrom TEXT, Ra6969_Cot_DateTo TEXT," +
                "Ra8749_PoNo TEXT, Ra8749_DateFrom TEXT, Ra8749_DateTo TEXT," +
                "Ra9275_Discharge TEXT, Ra9275_DateFrom TEXT, Ra9275_DateTo TEXT," +
                "Ra9003_Moa TEXT, Ra9003_DateFrom TEXT, Ra9003_DateTo TEXT," +
                "is_synced INTEGER DEFAULT 0)";
    }
}