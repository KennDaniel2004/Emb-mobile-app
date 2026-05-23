package com.example.embr6monitoringapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.embr6monitoringapp.Database.DatabaseConnection;
import com.example.embr6monitoringapp.Models.EstablishmentModel;
import com.example.embr6monitoringapp.Models.ReportInfoModel;
import com.example.embr6monitoringapp.Models.YearCoverdInfoModel;

import java.util.ArrayList;
import java.util.List;

public class GeneralInfoDaoImpl implements GeneralInfoDao {

    private static final String TAG = "GeneralInfoDaoImpl";

    private final DatabaseConnection dbConnection;

    public GeneralInfoDaoImpl(Context context) {
        dbConnection = DatabaseConnection.getInstance(context);
    }

    @Override
    public boolean insertReportInfo(ReportInfoModel model) {
        try {
            SQLiteDatabase db = dbConnection.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("Employee_Id",        model.getEmployeeId());
            values.put("Emb_Id",             model.getEmbId());
            values.put("Report_Control",     model.getReportControl());
            values.put("Type_Monitoring",    model.getTypeMonitoring());
            values.put("Date_of_Inspection", model.getDateOfInspection());

            long result = db.insert("Report_Info", null, values);
            Log.d(TAG, "insertReportInfo → rowId=" + result);
            return result != -1;

        } catch (Exception e) {
            Log.e(TAG, "insertReportInfo EXCEPTION: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean insertEstablishmentInfo(EstablishmentModel model) {
        try {
            SQLiteDatabase db = dbConnection.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("Employee_Id",            model.getEmployeeId());
            values.put("Laws",                   model.getLaws());
            values.put("Name_of_Establishment",  model.getNameOfEstablishment());
            values.put("proponent",              model.getProponent());
            values.put("Mailing_Address",        model.getMailingAddress());
            values.put("N",                      model.getGeoN());
            values.put("E",                      model.getGeoE());
            values.put("Project_Location",       model.getProjectLocation());
            values.put("Nature_of_Business",     model.getNatureOfBusiness());
            values.put("Year_Establish",         model.getYearEstablish());
            values.put("PSIC_Code",              model.getPsicCode());
            values.put("Op_hours_day",           model.getOpHoursDay());
            values.put("Op_day_week",            model.getOpDayWeek());
            values.put("Op_day_year",            model.getOpDayYear());
            values.put("Male",                   model.getMale());
            values.put("Female",                 model.getFemale());
            values.put("Number_of_Employee",     model.getNumberOfEmployee());
            values.put("Product_Lines",          model.getProductLines());
            values.put("Production_Rate",        model.getProductionRate());
            values.put("Actual_Production_Rate", model.getActualProductionRate());
            values.put("Name_of_Managing_Head",  model.getNameOfManagingHead());
            values.put("Name_of_PCO",            model.getNameOfPCO());
            values.put("PCO_Accreditation_No",   model.getPcoAccreditationNo());
            values.put("Date_of_Effectivity",    model.getDateOfEffectivity());
            values.put("Phone_Fax_No",           model.getPhoneFaxNo());
            values.put("Email_Address",          model.getEmailAddress());

            long result = db.insert("Establishment_Info", null, values);
            Log.d(TAG, "insertEstablishmentInfo → rowId=" + result);
            return result != -1;

        } catch (Exception e) {
            Log.e(TAG, "insertEstablishmentInfo EXCEPTION: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean insertYearCoveredInfo(YearCoverdInfoModel model) {
        try {
            SQLiteDatabase db = dbConnection.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("Employee_Id", model.getEmployeeId());
            values.put("Year_Coverd", model.getYearCovered());
            values.put("Vol_cu_m",    model.getVolCuM());
            values.put("Total",       model.getTotal());

            long result = db.insert("YearCoverd_Info", null, values);
            Log.d(TAG, "insertYearCoveredInfo → rowId=" + result);
            return result != -1;

        } catch (Exception e) {
            Log.e(TAG, "insertYearCoveredInfo EXCEPTION: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<EstablishmentModel> getAllEstablishments() {
        List<EstablishmentModel> list = new ArrayList<>();
        try {
            SQLiteDatabase db     = dbConnection.getReadableDatabase();
            Cursor         cursor = db.query(
                    "Establishment_Info",
                    null, null, null, null, null, "id DESC"
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    EstablishmentModel model = new EstablishmentModel();
                    model.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    model.setEmployeeId(cursor.getString(cursor.getColumnIndexOrThrow("Employee_Id")));
                    model.setLaws(cursor.getString(cursor.getColumnIndexOrThrow("Laws")));
                    model.setNameOfEstablishment(cursor.getString(cursor.getColumnIndexOrThrow("Name_of_Establishment")));
                    model.setProponent(cursor.getString(cursor.getColumnIndexOrThrow("proponent")));
                    model.setMailingAddress(cursor.getString(cursor.getColumnIndexOrThrow("Mailing_Address")));
                    model.setGeoN(cursor.getString(cursor.getColumnIndexOrThrow("N")));
                    model.setGeoE(cursor.getString(cursor.getColumnIndexOrThrow("E")));
                    model.setProjectLocation(cursor.getString(cursor.getColumnIndexOrThrow("Project_Location")));
                    model.setNatureOfBusiness(cursor.getString(cursor.getColumnIndexOrThrow("Nature_of_Business")));
                    model.setYearEstablish(cursor.getString(cursor.getColumnIndexOrThrow("Year_Establish")));
                    model.setPsicCode(cursor.getString(cursor.getColumnIndexOrThrow("PSIC_Code")));
                    model.setOpHoursDay(cursor.getString(cursor.getColumnIndexOrThrow("Op_hours_day")));
                    model.setOpDayWeek(cursor.getString(cursor.getColumnIndexOrThrow("Op_day_week")));
                    model.setOpDayYear(cursor.getString(cursor.getColumnIndexOrThrow("Op_day_year")));
                    model.setMale(cursor.getString(cursor.getColumnIndexOrThrow("Male")));
                    model.setFemale(cursor.getString(cursor.getColumnIndexOrThrow("Female")));
                    model.setNumberOfEmployee(cursor.getString(cursor.getColumnIndexOrThrow("Number_of_Employee")));
                    model.setProductLines(cursor.getString(cursor.getColumnIndexOrThrow("Product_Lines")));
                    model.setProductionRate(cursor.getString(cursor.getColumnIndexOrThrow("Production_Rate")));
                    model.setActualProductionRate(cursor.getString(cursor.getColumnIndexOrThrow("Actual_Production_Rate")));
                    model.setNameOfManagingHead(cursor.getString(cursor.getColumnIndexOrThrow("Name_of_Managing_Head")));
                    model.setNameOfPCO(cursor.getString(cursor.getColumnIndexOrThrow("Name_of_PCO")));
                    model.setPcoAccreditationNo(cursor.getString(cursor.getColumnIndexOrThrow("PCO_Accreditation_No")));
                    model.setDateOfEffectivity(cursor.getString(cursor.getColumnIndexOrThrow("Date_of_Effectivity")));
                    model.setPhoneFaxNo(cursor.getString(cursor.getColumnIndexOrThrow("Phone_Fax_No")));
                    model.setEmailAddress(cursor.getString(cursor.getColumnIndexOrThrow("Email_Address")));
                    list.add(model);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllEstablishments EXCEPTION: " + e.getMessage(), e);
        }
        return list;
    }
}