package com.example.embr6monitoringapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.embr6monitoringapp.Database.DatabaseConnection;
import com.example.embr6monitoringapp.Models.MonitoringRecord;

import java.util.ArrayList;
import java.util.List;

public class MonitoringProgressDaoImpl implements MonitoringProgressDao {

    private static final String TAG   = "MonitoringProgressDao";
    private static final String TABLE = "Monitoring_Records";

    private final DatabaseConnection dbConn;

    public MonitoringProgressDaoImpl(Context context) {
        this.dbConn = DatabaseConnection.getInstance(context);
    }

    // =========================================================================
    // Existing methods — unchanged
    // =========================================================================

    @Override
    public long insertRecord(MonitoringRecord record) {
        SQLiteDatabase db = dbConn.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Employee_Id",            record.getEmployeeId());
        cv.put("Emb_Id",                 record.getEmbId());
        cv.put("Report_Control",         record.getReportControl());
        cv.put("Type_Monitoring",        record.getTypeMonitoring());
        cv.put("Date_of_Inspection",     record.getDateOfInspection());
        cv.put("Laws",                   record.getLaws());
        cv.put("Name_of_Establishment",  record.getNameOfEstablishment());
        cv.put("Proponent",              record.getProponent());
        cv.put("Mailing_Address",        record.getMailingAddress());
        cv.put("Geo_N",                  record.getGeoN());
        cv.put("Geo_E",                  record.getGeoE());
        cv.put("Project_Location",       record.getProjectLocation());
        cv.put("Nature_of_Business",     record.getNatureOfBusiness());
        cv.put("Year_Establish",         record.getYearEstablish());
        cv.put("PSIC_Code",              record.getPsicCode());
        cv.put("Op_Hours_Day",           record.getOpHoursDay());
        cv.put("Op_Day_Week",            record.getOpDayWeek());
        cv.put("Op_Day_Year",            record.getOpDayYear());
        cv.put("Male",                   record.getMale());
        cv.put("Female",                 record.getFemale());
        cv.put("Number_of_Employee",     record.getNumberOfEmployee());
        cv.put("Product_Lines",          record.getProductLines());
        cv.put("Production_Rate",        record.getProductionRate());
        cv.put("Actual_Production_Rate", record.getActualProductionRate());
        cv.put("Name_of_Managing_Head",  record.getNameOfManagingHead());
        cv.put("Name_of_PCO",            record.getNameOfPCO());
        cv.put("PCO_Accreditation_No",   record.getPcoAccreditationNo());
        cv.put("Date_of_Effectivity",    record.getDateOfEffectivity());
        cv.put("Phone_Fax_No",           record.getPhoneFaxNo());
        cv.put("Email_Address",          record.getEmailAddress());
        cv.put("Year_Covered",           record.getYearCovered());
        cv.put("Vol_cu_m",               record.getVolCuM());
        cv.put("Total",                  record.getTotal());
        cv.put("is_complete",            0);
        cv.put("is_synced",              0);
        cv.put("is_archived",            0);

        long id = db.insert(TABLE, null, cv);
        db.close();
        return id;
    }

    @Override
    public boolean updatePurposeSection(int recordId, String purposeStatus,
                                        String findings, String evidenceImageUri) {
        SQLiteDatabase db = dbConn.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("purpose_status",     purposeStatus);
        cv.put("findings",           findings);
        cv.put("evidence_image_uri", evidenceImageUri);

        int rows = db.update(TABLE, cv, "id = ?", new String[]{String.valueOf(recordId)});
        db.close();
        return rows > 0;
    }

    @Override
    public boolean updateFullRecord(MonitoringRecord record) {
        SQLiteDatabase db = dbConn.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("S331_Status",   record.getS331Status());
        cv.put("S331_Findings", record.getS331Findings());
        cv.put("S331_Image1",   record.getS331Image1());
        cv.put("S331_Image2",   record.getS331Image2());

        cv.put("S332_Status",   record.getS332Status());
        cv.put("S332_Findings", record.getS332Findings());
        cv.put("S332_Image1",   record.getS332Image1());
        cv.put("S332_Image2",   record.getS332Image2());

        cv.put("S333_Status",   record.getS333Status());
        cv.put("S333_Findings", record.getS333Findings());
        cv.put("S333_Image1",   record.getS333Image1());
        cv.put("S333_Image2",   record.getS333Image2());

        cv.put("S334_Status",   record.getS334Status());
        cv.put("S334_Findings", record.getS334Findings());
        cv.put("S334_Image1",   record.getS334Image1());
        cv.put("S334_Image2",   record.getS334Image2());

        cv.put("S335_Status",   record.getS335Status());
        cv.put("S335_Findings", record.getS335Findings());
        cv.put("S335_Image1",   record.getS335Image1());
        cv.put("S335_Image2",   record.getS335Image2());

        cv.put("S336_Status",   record.getS336Status());
        cv.put("S336_Findings", record.getS336Findings());

        cv.put("S337_Status",   record.getS337Status());
        cv.put("S337_Findings", record.getS337Findings());

        cv.put("Rec_Confirmatory",        record.getRecConfirmatorysampling());
        cv.put("Rec_RegularMonitoring",   record.getRecRegularMonitoring());
        cv.put("Rec_IssuanceTempRenewal", record.getRecIssuanceTempRenewalPoaDp());
        cv.put("Rec_AccreditationPco",    record.getRecAccreditationPco());
        cv.put("Rec_SubmissionSmrCmr",    record.getRecSubmissionSmrCmr());
        cv.put("Rec_IssuanceNomTc",       record.getRecIssuanceNomTc());
        cv.put("Rec_IssuanceNov",         record.getRecIssuanceNov());
        cv.put("Rec_SuspensionEcc",       record.getRecSuspensionEcc5DayCdo());
        cv.put("Rec_EndorsementPab",      record.getRecEndorsementPab());
        cv.put("Rec_Other",               record.getRecOther());

        cv.put("Submitted_By",             record.getSubmittedBy());
        cv.put("Date_Submitted",           record.getDateSubmitted());
        cv.put("Date_Travel_Concluded",    record.getDateTravelConcluded());
        cv.put("Recommending1",            record.getRecommendingApproval1());
        cv.put("Recommending1_Position",   record.getRecommendingApproval1Position());
        cv.put("Recommending2",            record.getRecommendingApproval2());
        cv.put("Recommending2_Position",   record.getRecommendingApproval2Position());
        cv.put("Approved_By",              record.getApprovedBy());
        cv.put("Approved_By_Position",     record.getApprovedByPosition());

        cv.put("is_complete",  record.getIsComplete());
        cv.put("is_synced",    record.getIsSynced());
        cv.put("is_archived",  record.getIsArchived());

        int rows = db.update(TABLE, cv, "id = ?", new String[]{String.valueOf(record.getId())});
        db.close();
        return rows > 0;
    }

    @Override
    public boolean markComplete(int recordId) {
        SQLiteDatabase db = dbConn.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_complete", 1);

        int rows = db.update(TABLE, cv, "id = ?", new String[]{String.valueOf(recordId)});
        db.close();
        return rows > 0;
    }

    @Override
    public List<MonitoringRecord> getRecordsByEmployee(String employeeId) {
        List<MonitoringRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbConn.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE +
                        " WHERE Employee_Id = ? AND (is_archived = 0 OR is_archived IS NULL)",
                new String[]{employeeId});

        if (cursor.moveToFirst()) {
            do { records.add(cursorToRecord(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    @Override
    public MonitoringRecord getRecordById(int recordId) {
        SQLiteDatabase db = dbConn.getReadableDatabase();
        MonitoringRecord record = null;

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE + " WHERE id = ?",
                new String[]{String.valueOf(recordId)});

        if (cursor.moveToFirst()) {
            record = cursorToRecord(cursor);
        }
        cursor.close();
        db.close();
        return record;
    }

    @Override
    public List<MonitoringRecord> getCompletedRecordsByEmployee(String employeeId) {
        List<MonitoringRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbConn.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE +
                        " WHERE Employee_Id = ? AND is_complete = 1" +
                        " AND (is_archived = 0 OR is_archived IS NULL)",
                new String[]{employeeId});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do { records.add(cursorToRecord(cursor)); } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return records;
    }

    // =========================================================================
    // New archive methods
    // =========================================================================

    @Override
    public List<MonitoringRecord> getArchivedRecordsByEmployee(String employeeId) {
        List<MonitoringRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbConn.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, Employee_Id, Emb_Id, Name_of_Establishment, " +
                        "Date_of_Inspection, Report_Control, Type_Monitoring, " +
                        "Project_Location, is_complete, is_archived " +
                        "FROM " + TABLE +
                        " WHERE Employee_Id = ? AND is_archived = 1" +
                        " ORDER BY Date_of_Inspection DESC",
                new String[]{employeeId});

        if (cursor.moveToFirst()) {
            do { records.add(cursorToRecord(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    @Override
    public boolean updateArchiveStatus(int recordId, int status) {
        SQLiteDatabase db = dbConn.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("is_archived", status);
            int rows = db.update(TABLE, cv, "id = ?", new String[]{String.valueOf(recordId)});
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "updateArchiveStatus failed for id=" + recordId, e);
            return false;
        } finally {
            db.close();
        }
    }

    @Override
    public boolean deleteRecord(int recordId) {
        SQLiteDatabase db = dbConn.getWritableDatabase();
        try {
            int rows = db.delete(TABLE, "id = ?", new String[]{String.valueOf(recordId)});
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "deleteRecord failed for id=" + recordId, e);
            return false;
        } finally {
            db.close();
        }
    }

    @Override
    public int batchUpdateArchiveStatus(List<Integer> recordIds, int status) {
        if (recordIds == null || recordIds.isEmpty()) return 0;

        SQLiteDatabase db = dbConn.getWritableDatabase();
        int successCount = 0;

        try {
            db.beginTransaction();
            ContentValues cv = new ContentValues();
            cv.put("is_archived", status);

            for (int id : recordIds) {
                int rows = db.update(TABLE, cv, "id = ?", new String[]{String.valueOf(id)});
                if (rows > 0) successCount++;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "batchUpdateArchiveStatus failed", e);
        } finally {
            db.endTransaction();
            db.close();
        }
        return successCount;
    }

    @Override
    public int batchDeleteRecords(List<Integer> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) return 0;

        SQLiteDatabase db = dbConn.getWritableDatabase();
        int successCount = 0;

        try {
            db.beginTransaction();

            for (int id : recordIds) {
                int rows = db.delete(TABLE, "id = ?", new String[]{String.valueOf(id)});
                if (rows > 0) successCount++;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "batchDeleteRecords failed", e);
        } finally {
            db.endTransaction();
            db.close();
        }
        return successCount;
    }

    // =========================================================================
    // Cursor mapping helpers — unchanged
    // =========================================================================

    private MonitoringRecord cursorToRecord(Cursor c) {
        MonitoringRecord r = new MonitoringRecord();
        try {
            r.setId(getInt(c, "id"));
            r.setEmployeeId(getString(c, "Employee_Id"));
            r.setEmbId(getString(c, "Emb_Id"));
            r.setReportControl(getString(c, "Report_Control"));
            r.setTypeMonitoring(getString(c, "Type_Monitoring"));
            r.setDateOfInspection(getString(c, "Date_of_Inspection"));
            r.setLaws(getString(c, "Laws"));
            r.setNameOfEstablishment(getString(c, "Name_of_Establishment"));
            r.setProponent(getString(c, "Proponent"));
            r.setMailingAddress(getString(c, "Mailing_Address"));
            r.setGeoN(getString(c, "Geo_N"));
            r.setGeoE(getString(c, "Geo_E"));
            r.setProjectLocation(getString(c, "Project_Location"));
            r.setNatureOfBusiness(getString(c, "Nature_of_Business"));
            r.setYearEstablish(getString(c, "Year_Establish"));
            r.setPsicCode(getString(c, "PSIC_Code"));
            r.setOpHoursDay(getString(c, "Op_Hours_Day"));
            r.setOpDayWeek(getString(c, "Op_Day_Week"));
            r.setOpDayYear(getString(c, "Op_Day_Year"));
            r.setMale(getString(c, "Male"));
            r.setFemale(getString(c, "Female"));
            r.setNumberOfEmployee(getString(c, "Number_of_Employee"));
            r.setProductLines(getString(c, "Product_Lines"));
            r.setProductionRate(getString(c, "Production_Rate"));
            r.setActualProductionRate(getString(c, "Actual_Production_Rate"));
            r.setNameOfManagingHead(getString(c, "Name_of_Managing_Head"));
            r.setNameOfPCO(getString(c, "Name_of_PCO"));
            r.setPcoAccreditationNo(getString(c, "PCO_Accreditation_No"));
            r.setDateOfEffectivity(getString(c, "Date_of_Effectivity"));
            r.setPhoneFaxNo(getString(c, "Phone_Fax_No"));
            r.setEmailAddress(getString(c, "Email_Address"));
            r.setYearCovered(getString(c, "Year_Covered"));
            r.setVolCuM(getString(c, "Vol_cu_m"));
            r.setTotal(getString(c, "Total"));

            r.setS331Status(getString(c, "S331_Status"));
            r.setS331Findings(getString(c, "S331_Findings"));
            r.setS331Image1(getString(c, "S331_Image1"));
            r.setS331Image2(getString(c, "S331_Image2"));

            r.setS332Status(getString(c, "S332_Status"));
            r.setS332Findings(getString(c, "S332_Findings"));
            r.setS332Image1(getString(c, "S332_Image1"));
            r.setS332Image2(getString(c, "S332_Image2"));

            r.setS333Status(getString(c, "S333_Status"));
            r.setS333Findings(getString(c, "S333_Findings"));
            r.setS333Image1(getString(c, "S333_Image1"));
            r.setS333Image2(getString(c, "S333_Image2"));

            r.setS334Status(getString(c, "S334_Status"));
            r.setS334Findings(getString(c, "S334_Findings"));
            r.setS334Image1(getString(c, "S334_Image1"));
            r.setS334Image2(getString(c, "S334_Image2"));

            r.setS335Status(getString(c, "S335_Status"));
            r.setS335Findings(getString(c, "S335_Findings"));
            r.setS335Image1(getString(c, "S335_Image1"));
            r.setS335Image2(getString(c, "S335_Image2"));

            r.setS336Status(getString(c, "S336_Status"));
            r.setS336Findings(getString(c, "S336_Findings"));

            r.setS337Status(getString(c, "S337_Status"));
            r.setS337Findings(getString(c, "S337_Findings"));

            r.setRecConfirmatorysampling(getInt(c, "Rec_Confirmatory"));
            r.setRecRegularMonitoring(getInt(c, "Rec_RegularMonitoring"));
            r.setRecIssuanceTempRenewalPoaDp(getInt(c, "Rec_IssuanceTempRenewal"));
            r.setRecAccreditationPco(getInt(c, "Rec_AccreditationPco"));
            r.setRecSubmissionSmrCmr(getInt(c, "Rec_SubmissionSmrCmr"));
            r.setRecIssuanceNomTc(getInt(c, "Rec_IssuanceNomTc"));
            r.setRecIssuanceNov(getInt(c, "Rec_IssuanceNov"));
            r.setRecSuspensionEcc5DayCdo(getInt(c, "Rec_SuspensionEcc"));
            r.setRecEndorsementPab(getInt(c, "Rec_EndorsementPab"));
            r.setRecOther(getString(c, "Rec_Other"));

            r.setSubmittedBy(getString(c, "Submitted_By"));
            r.setDateSubmitted(getString(c, "Date_Submitted"));
            r.setDateTravelConcluded(getString(c, "Date_Travel_Concluded"));
            r.setRecommendingApproval1(getString(c, "Recommending1"));
            r.setRecommendingApproval1Position(getString(c, "Recommending1_Position"));
            r.setRecommendingApproval2(getString(c, "Recommending2"));
            r.setRecommendingApproval2Position(getString(c, "Recommending2_Position"));
            r.setApprovedBy(getString(c, "Approved_By"));
            r.setApprovedByPosition(getString(c, "Approved_By_Position"));

            r.setIsComplete(getInt(c, "is_complete"));
            r.setIsSynced(getInt(c, "is_synced"));
            r.setIsArchived(getInt(c, "is_archived"));

        } catch (Exception e) {
            Log.e(TAG, "cursorToRecord error: " + e.getMessage(), e);
        }
        return r;
    }

    private String getString(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return i >= 0 ? c.getString(i) : null;
    }

    private int getInt(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return i >= 0 ? c.getInt(i) : 0;
    }
}