package com.example.embr6monitoringapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.embr6monitoringapp.Database.DatabaseConnection;
import com.example.embr6monitoringapp.Models.ComplianceStatusModel;

import java.util.ArrayList;
import java.util.List;

public class ComplianceStatusDaoImpl implements ComplianceStatusDao {

    private static final String TAG   = "ComplianceStatusDao";
    private static final String TABLE = "Compliance_Status";

    private final DatabaseConnection db;

    public ComplianceStatusDaoImpl(Context context) {
        db = DatabaseConnection.getInstance(context);
    }

    @Override
    public boolean insert(ComplianceStatusModel m) {
        try {
            ContentValues v = new ContentValues();
            v.put("Employee_Id",             m.getEmployeeId());
            // PD 1586
            v.put("Pd1586_Ecc1",             m.getPd1586Ecc1());
            v.put("Pd1586_Ecc1_DateFrom",    m.getPd1586Ecc1DateFrom());
            v.put("Pd1586_Ecc1_DateTo",      m.getPd1586Ecc1DateTo());
            v.put("Pd1586_Ecc2",             m.getPd1586Ecc2());
            v.put("Pd1586_Ecc2_DateFrom",    m.getPd1586Ecc2DateFrom());
            v.put("Pd1586_Ecc2_DateTo",      m.getPd1586Ecc2DateTo());
            v.put("Pd1586_Ecc3",             m.getPd1586Ecc3());
            v.put("Pd1586_Ecc3_DateFrom",    m.getPd1586Ecc3DateFrom());
            v.put("Pd1586_Ecc3_DateTo",      m.getPd1586Ecc3DateTo());
            // RA 6969
            v.put("Ra6969_Denr",             m.getRa6969DenrRegistry());
            v.put("Ra6969_Denr_DateFrom",    m.getRa6969DenrDateFrom());
            v.put("Ra6969_Denr_DateTo",      m.getRa6969DenrDateTo());
            v.put("Ra6969_Pcl",              m.getRa6969PclCert());
            v.put("Ra6969_Pcl_DateFrom",     m.getRa6969PclDateFrom());
            v.put("Ra6969_Pcl_DateTo",       m.getRa6969PclDateTo());
            v.put("Ra6969_Importer",         m.getRa6969ImporterClearance());
            v.put("Ra6969_Importer_DateFrom",m.getRa6969ImporterDateFrom());
            v.put("Ra6969_Importer_DateTo",  m.getRa6969ImporterDateTo());
            v.put("Ra6969_Cco",              m.getRa6969CcoRegistry());
            v.put("Ra6969_Cco_DateFrom",     m.getRa6969CcoDateFrom());
            v.put("Ra6969_Cco_DateTo",       m.getRa6969CcoDateTo());
            v.put("Ra6969_Permit",           m.getRa6969PermitTransport());
            v.put("Ra6969_Permit_DateFrom",  m.getRa6969PermitDateFrom());
            v.put("Ra6969_Permit_DateTo",    m.getRa6969PermitDateTo());
            v.put("Ra6969_Cot",              m.getRa6969CotCopy());
            v.put("Ra6969_Cot_DateFrom",     m.getRa6969CotDateFrom());
            v.put("Ra6969_Cot_DateTo",       m.getRa6969CotDateTo());
            // RA 8749
            v.put("Ra8749_PoNo",             m.getRa8749PoNo());
            v.put("Ra8749_DateFrom",         m.getRa8749PoDateFrom());
            v.put("Ra8749_DateTo",           m.getRa8749PoDateTo());
            // RA 9275
            v.put("Ra9275_Discharge",        m.getRa9275DischargePermit());
            v.put("Ra9275_DateFrom",         m.getRa9275DischargeDateFrom());
            v.put("Ra9275_DateTo",           m.getRa9275DischargeDateTo());
            // RA 9003
            v.put("Ra9003_Moa",              m.getRa9003MoaAgreement());
            v.put("Ra9003_DateFrom",         m.getRa9003MoaDateFrom());
            v.put("Ra9003_DateTo",           m.getRa9003MoaDateTo());
            v.put("is_synced",               0);

            long result = db.getWritableDatabase().insert(TABLE, null, v);
            Log.d(TAG, "insert → rowId=" + result);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "insert EXCEPTION: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<ComplianceStatusModel> getByEmployee(String employeeId) {
        List<ComplianceStatusModel> list = new ArrayList<>();
        try {
            Cursor cursor = db.getReadableDatabase().query(
                    TABLE, null, "Employee_Id = ?",
                    new String[]{employeeId}, null, null, "id DESC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ComplianceStatusModel m = new ComplianceStatusModel();
                    m.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    m.setEmployeeId(cursor.getString(cursor.getColumnIndexOrThrow("Employee_Id")));

                    list.add(m);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getByEmployee EXCEPTION: " + e.getMessage(), e);
        }
        return list;
    }
}