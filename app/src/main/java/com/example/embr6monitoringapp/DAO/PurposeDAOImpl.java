package com.example.embr6monitoringapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.embr6monitoringapp.Database.DatabaseConnection;
import com.example.embr6monitoringapp.Models.PurposeModel;
import java.util.ArrayList;
import java.util.List;

public class PurposeDAOImpl implements PurposeDAO {

    private static final String TAG = "PurposeDAO";
    private final SQLiteDatabase db;

    public PurposeDAOImpl(Context context) {
        db = DatabaseConnection.getInstance(context).getWritableDatabase();
    }

    @Override
    public long insertPurpose(PurposeModel m) {
        long result = -1;
        try {
            ContentValues v = new ContentValues();
            v.put("Employee_Id", m.getEmployeeId());
            v.put("verifyAccuracy", m.getVerifyAccuracy());
            v.put("pmpinNew", m.getPmpinNew());
            v.put("pmpinRenewal", m.getPmpinRenewal());
            v.put("hwgidNew", m.getHwgidNew());
            v.put("hwgidRenewal", m.getHwgidRenewal());
            v.put("hwtrNew", m.getHwtrNew());
            v.put("hwtrRenewal", m.getHwtrRenewal());
            v.put("hwtsdNew", m.getHwtsdNew());
            v.put("hwtsdRenewal", m.getHwtsdRenewal());
            v.put("poapciNew", m.getPoapciNew());
            v.put("poapciRenewal", m.getPoapciRenewal());
            v.put("dpNew", m.getDpNew());
            v.put("dpRenewal", m.getDpRenewal());
            v.put("otherPermit", m.getOtherPermit());
            v.put("othersPermitNew", m.getOthersPermitNew());
            v.put("othersPermitRenewal", m.getOthersPermitRenewal());
            v.put("determineCompliance", m.getDetermineCompliance());
            v.put("investigate", m.getInvestigate());
            v.put("survey", m.getSurvey());
            v.put("othersCEMCRR", m.getOthersCEMCRR());
            v.put("otherSpecify", m.getOtherSpecify());
            v.put("contactName", m.getContactName());
            v.put("position", m.getPosition());
            v.put("is_synced", m.getIsSynced());

            result = db.insert("Purpose_Table", null, v);

            if (result == -1) {
                Log.e(TAG, "insertPurpose: DB insert returned -1");
            } else {
                Log.d(TAG, "insertPurpose: success, rowId=" + result);
            }
        } catch (Exception e) {
            Log.e(TAG, "insertPurpose: exception", e);
        }
        return result;
    }

    @Override
    public List<PurposeModel> getUnsyncedPurpose() {
        List<PurposeModel> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM Purpose_Table WHERE is_synced = 0", null);
            while (c.moveToNext()) {
                PurposeModel m = new PurposeModel();
                m.setId(safeInt(c, "id"));
                m.setEmployeeId(safeStr(c, "Employee_Id"));
                m.setVerifyAccuracy(safeInt(c, "verifyAccuracy"));
                m.setPmpinNew(safeInt(c, "pmpinNew"));
                m.setPmpinRenewal(safeInt(c, "pmpinRenewal"));
                m.setHwgidNew(safeInt(c, "hwgidNew"));
                m.setHwgidRenewal(safeInt(c, "hwgidRenewal"));
                m.setHwtrNew(safeInt(c, "hwtrNew"));
                m.setHwtrRenewal(safeInt(c, "hwtrRenewal"));
                m.setHwtsdNew(safeInt(c, "hwtsdNew"));
                m.setHwtsdRenewal(safeInt(c, "hwtsdRenewal"));
                m.setPoapciNew(safeInt(c, "poapciNew"));
                m.setPoapciRenewal(safeInt(c, "poapciRenewal"));
                m.setDpNew(safeInt(c, "dpNew"));
                m.setDpRenewal(safeInt(c, "dpRenewal"));
                m.setOtherPermit(safeStr(c, "otherPermit"));
                m.setOthersPermitNew(safeInt(c, "othersPermitNew"));
                m.setOthersPermitRenewal(safeInt(c, "othersPermitRenewal"));
                m.setDetermineCompliance(safeInt(c, "determineCompliance"));
                m.setInvestigate(safeInt(c, "investigate"));
                m.setSurvey(safeInt(c, "survey"));
                m.setOthersCEMCRR(safeInt(c, "othersCEMCRR"));
                m.setOtherSpecify(safeStr(c, "otherSpecify"));
                m.setContactName(safeStr(c, "contactName"));
                m.setPosition(safeStr(c, "position"));
                m.setIsSynced(safeInt(c, "is_synced"));
                list.add(m);
            }
        } catch (Exception e) {
            Log.e(TAG, "getUnsyncedPurpose: exception", e);
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    @Override
    public int updateSyncStatus(int id) {
        ContentValues v = new ContentValues();
        v.put("is_synced", 1);
        return db.update("Purpose_Table", v, "id=?", new String[]{String.valueOf(id)});
    }

    @Override
    public PurposeModel getPurposeByEmployeeId(String employeeId) {
        PurposeModel m = null;
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM Purpose_Table WHERE Employee_Id = ? ORDER BY id DESC LIMIT 1",
                    new String[]{employeeId});
            if (c.moveToFirst()) {
                m = new PurposeModel();
                m.setId(safeInt(c, "id"));
                m.setEmployeeId(safeStr(c, "Employee_Id"));
                m.setVerifyAccuracy(safeInt(c, "verifyAccuracy"));
                m.setPmpinNew(safeInt(c, "pmpinNew"));
                m.setPmpinRenewal(safeInt(c, "pmpinRenewal"));
                m.setHwgidNew(safeInt(c, "hwgidNew"));
                m.setHwgidRenewal(safeInt(c, "hwgidRenewal"));
                m.setHwtrNew(safeInt(c, "hwtrNew"));
                m.setHwtrRenewal(safeInt(c, "hwtrRenewal"));
                m.setHwtsdNew(safeInt(c, "hwtsdNew"));
                m.setHwtsdRenewal(safeInt(c, "hwtsdRenewal"));
                m.setPoapciNew(safeInt(c, "poapciNew"));
                m.setPoapciRenewal(safeInt(c, "poapciRenewal"));
                m.setDpNew(safeInt(c, "dpNew"));
                m.setDpRenewal(safeInt(c, "dpRenewal"));
                m.setOtherPermit(safeStr(c, "otherPermit"));
                m.setOthersPermitNew(safeInt(c, "othersPermitNew"));
                m.setOthersPermitRenewal(safeInt(c, "othersPermitRenewal"));
                m.setDetermineCompliance(safeInt(c, "determineCompliance"));
                m.setInvestigate(safeInt(c, "investigate"));
                m.setSurvey(safeInt(c, "survey"));
                m.setOthersCEMCRR(safeInt(c, "othersCEMCRR"));
                m.setOtherSpecify(safeStr(c, "otherSpecify"));
                m.setContactName(safeStr(c, "contactName"));
                m.setPosition(safeStr(c, "position"));
                m.setIsSynced(safeInt(c, "is_synced"));
            }
        } catch (Exception e) {
            Log.e(TAG, "getPurposeByEmployeeId: exception", e);
        } finally {
            if (c != null) c.close();
        }
        return m;
    }

    private String safeStr(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return (i >= 0 && !c.isNull(i)) ? c.getString(i) : "";
    }

    private int safeInt(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return i >= 0 ? c.getInt(i) : 0;
    }
}