package com.example.embr6monitoringapp.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.embr6monitoringapp.Models.EstablishmentModel;
import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.Models.ReportInfoModel;
import com.example.embr6monitoringapp.Models.YearCoverdInfoModel;
import com.example.embr6monitoringapp.R;
import com.example.embr6monitoringapp.Service.GeneralInfoService;
import com.example.embr6monitoringapp.Service.GeneralInfoServiceImpl;
import com.example.embr6monitoringapp.Service.MonitoringProgressService;
import com.example.embr6monitoringapp.Service.MonitoringProgressServiceImpl;

public class GeneralinfoController extends AppCompatActivity {

    private static final String TAG = "GeneralinfoController";
    private String employeeId = "";

    private EditText etEmbIdNo, etReportControl, etDateInspection;
    private AutoCompleteTextView actTypeOfMonitoring;
    private CheckBox cbPd1586, cbRa9003;
    private CheckBox cbRa9275, cbRa9275Survey, cbRa9275Routine;
    private CheckBox cbRa6969, cbRa6969Survey, cbRa6969Routine;
    private CheckBox cbRa8749, cbRa8749Survey, cbRa8749Routine;
    private RadioGroup rgMcCovered;
    private EditText etNameOfEstablishment, etProponent, etMailingAddress;
    private EditText etGeoN, etGeoE, etProjectLocation, etNatureOfBusiness;
    private EditText etYearEstablished, etPsicCode;
    private EditText etOperatingHoursPerDay, etOperatingDaysPerWeek, etOperatingDaysPerYear;
    private EditText etMaleEmployees, etFemaleEmployees, etTotalEmployees;

    private EditText etProductLines, etProductionRate, etActualProductionRate;
    private EditText etYearCovered, etVolCuM;
    private TextView tvTableProductLines, tvTableProductionRate;
    private TextView tvSubYearCovered, tvSubVolCuM;

    private EditText etNameOfManagingHead, etNameOfPco, etPhoneFaxNo;
    private EditText etPcoAccreditationNo, etDateOfEffectivity, etEmailAddress;

    private Button btnSave, btnNext, btnAdd;

    private GeneralInfoService        service;
    private MonitoringProgressService monitoringService;

    private String tableProductLines   = "";
    private String tableProductionRate = "";
    private String tableActualRate     = "";
    private String tableYearCovered    = "";
    private String tableVolCuM         = "";

    private long lastSavedRecordId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generalinfo);

        employeeId = getIntent().getStringExtra("EMPLOYEE_ID");
        if (employeeId == null) employeeId = "";

        service           = new GeneralInfoServiceImpl(this);
        monitoringService = new MonitoringProgressServiceImpl(this);

        bindViews();
        setupMonitoringDropdown();
        setupMutuallyExclusiveCheckboxes();
        setupEmployeeTotalWatcher();
        setupAddButton();
        setupSaveButton();

    }

    private void bindViews() {
        etEmbIdNo = findViewById(R.id.etEmbIdNo);
        etReportControl = findViewById(R.id.etReportControl);
        etDateInspection = findViewById(R.id.etDateOfInspection);
        actTypeOfMonitoring = findViewById(R.id.etTypeOfMonitoring);

        cbPd1586 = findViewById(R.id.cbPd1586);
        cbRa9003 = findViewById(R.id.cbRa9003);
        cbRa9275 = findViewById(R.id.cbRa9275);
        cbRa9275Survey = findViewById(R.id.cbRa9275Survey);
        cbRa9275Routine = findViewById(R.id.cbRa9275Routine);
        cbRa6969 = findViewById(R.id.cbRa6969);
        cbRa6969Survey  = findViewById(R.id.cbRa6969Survey);
        cbRa6969Routine = findViewById(R.id.cbRa6969Routine);
        cbRa8749 = findViewById(R.id.cbRa8749);
        cbRa8749Survey = findViewById(R.id.cbRa8749Survey);
        cbRa8749Routine = findViewById(R.id.cbRa8749Routine);
        rgMcCovered = findViewById(R.id.rgMcCovered);

        etNameOfEstablishment  = findViewById(R.id.etNameOfEstablishment);
        etProponent            = findViewById(R.id.etProponent);
        etMailingAddress       = findViewById(R.id.etMailingAddress);
        etGeoN                 = findViewById(R.id.etGeoN);
        etGeoE                 = findViewById(R.id.etGeoE);
        etProjectLocation      = findViewById(R.id.etProjectLocation);
        etNatureOfBusiness     = findViewById(R.id.etNatureOfBusiness);
        etYearEstablished      = findViewById(R.id.etYearEstablished);
        etPsicCode             = findViewById(R.id.etPsicCode);
        etOperatingHoursPerDay = findViewById(R.id.etOperatingHoursPerDay);
        etOperatingDaysPerWeek = findViewById(R.id.etOperatingDaysPerWeek);
        etOperatingDaysPerYear = findViewById(R.id.etOperatingDaysPerYear);
        etMaleEmployees        = findViewById(R.id.etMaleEmployees);
        etFemaleEmployees      = findViewById(R.id.etFemaleEmployees);
        etTotalEmployees       = findViewById(R.id.etTotalEmployees);

        etProductLines         = findViewById(R.id.etProductLines);
        etProductionRate       = findViewById(R.id.etProductionRate);
        etActualProductionRate = findViewById(R.id.etActualProductionRate);
        etYearCovered          = findViewById(R.id.etYearCovered);
        etVolCuM               = findViewById(R.id.etVolCuM);

        tvTableProductLines   = findViewById(R.id.tvTableProductLines);
        tvTableProductionRate = findViewById(R.id.tvTableProductionRate);
        tvSubYearCovered      = findViewById(R.id.tvSubYearCovered);
        tvSubVolCuM           = findViewById(R.id.tvSubVolCuM);

        etNameOfManagingHead = findViewById(R.id.etNameOfManagingHead);
        etNameOfPco          = findViewById(R.id.etNameOfPco);
        etPhoneFaxNo         = findViewById(R.id.etPhoneFaxNo);
        etPcoAccreditationNo = findViewById(R.id.etPcoAccreditationNo);
        etDateOfEffectivity  = findViewById(R.id.etDateOfEffectivity);
        etEmailAddress       = findViewById(R.id.etEmailAddress);

        btnSave = findViewById(R.id.btnSave);

        btnAdd  = findViewById(R.id.btnAdd);
    }

    private void setupMonitoringDropdown() {
        String[] types = getResources().getStringArray(R.array.monitoring_types);
        actTypeOfMonitoring.setAdapter(
                new ArrayAdapter<>(this, R.layout.item_dropdown_12sp, types));
    }

    private void setupMutuallyExclusiveCheckboxes() {
        setMutuallyExclusive(cbRa9275Survey, cbRa9275Routine);
        setMutuallyExclusive(cbRa6969Survey, cbRa6969Routine);
        setMutuallyExclusive(cbRa8749Survey, cbRa8749Routine);
    }

    private void setMutuallyExclusive(CheckBox box1, CheckBox box2) {
        CompoundButton.OnCheckedChangeListener listener =
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton btn, boolean checked) {
                        if (checked) {
                            if (btn == box1) {
                                box2.setOnCheckedChangeListener(null);
                                box2.setChecked(false);
                                box2.setOnCheckedChangeListener(this);
                            } else {
                                box1.setOnCheckedChangeListener(null);
                                box1.setChecked(false);
                                box1.setOnCheckedChangeListener(this);
                            }
                        }
                    }
                };
        box1.setOnCheckedChangeListener(listener);
        box2.setOnCheckedChangeListener(listener);
    }

    private void setupEmployeeTotalWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                int male   = parseSafeInt(etMaleEmployees.getText().toString());
                int female = parseSafeInt(etFemaleEmployees.getText().toString());
                etTotalEmployees.setText(String.valueOf(male + female));
            }
        };
        etMaleEmployees.addTextChangedListener(watcher);
        etFemaleEmployees.addTextChangedListener(watcher);
    }

    private void setupAddButton() {
        btnAdd.setOnClickListener(v -> {
            String pl  = etProductLines.getText().toString().trim();
            String pr  = etProductionRate.getText().toString().trim();
            String apr = etActualProductionRate.getText().toString().trim();
            String yc  = etYearCovered.getText().toString().trim();
            String vol = etVolCuM.getText().toString().trim();

            if (pl.isEmpty() && pr.isEmpty() && apr.isEmpty()) {
                Toast.makeText(this,
                        "Please fill in at least Product Lines or Production Rate.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            tableProductLines   = pl;
            tableProductionRate = pr;
            tableActualRate     = apr;
            tableYearCovered    = yc;
            tableVolCuM         = vol;

            tvTableProductLines.setText(pl);
            tvTableProductionRate.setText(pr);
            tvSubYearCovered.setText(yc);
            tvSubVolCuM.setText(vol);

            Toast.makeText(this, "Row added to table.", Toast.LENGTH_SHORT).show();
        });
    }


    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            String error = validateAllFields();
            if (error != null) {
                showValidationDialog(error);
                return;
            }
            boolean saved = performSave();
            if (saved) {
                ((GeneralInfoServiceImpl) service).syncNow();
                Toast.makeText(this,
                        "General info saved! Please complete the Evidence section.",
                        Toast.LENGTH_LONG).show();
                navigateToDetail();
            } else {
                Toast.makeText(this,
                        "Failed to save data. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupNextButton() {
        btnNext.setOnClickListener(v -> {
            if (lastSavedRecordId == -1) {

                new AlertDialog.Builder(this)
                        .setTitle("Save Required")
                        .setMessage("Please fill in and save the form before proceeding.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            navigateToDetail();
        });
    }

    private void navigateToDetail() {
        Intent intent = new Intent(this, PurposeController.class);
        intent.putExtra("EMPLOYEE_ID", employeeId);
        intent.putExtra("RECORD_ID",   (int) lastSavedRecordId);
        startActivity(intent);
    }


    private String validateAllFields() {
        if (isEmpty(etEmbIdNo))               return "EMB ID No.";
        if (isEmpty(etReportControl))         return "Report Control No.";
        if (isEmpty(etDateInspection))        return "Date of Inspection";
        if (actTypeOfMonitoring.getText().toString().trim().isEmpty())
            return "Type of Monitoring";
        if (!cbPd1586.isChecked() && !cbRa9003.isChecked()
                && !cbRa9275.isChecked() && !cbRa6969.isChecked()
                && !cbRa8749.isChecked())     return "Laws Covered (check at least one)";

        if (isEmpty(etNameOfEstablishment))   return "Name of Establishment";
        if (isEmpty(etProponent))             return "Proponent";
        if (isEmpty(etMailingAddress))        return "Mailing Address";
        if (isEmpty(etGeoN))                  return "Geo N Coordinate";
        if (isEmpty(etGeoE))                  return "Geo E Coordinate";
        if (isEmpty(etProjectLocation))       return "Project Location";
        if (isEmpty(etNatureOfBusiness))      return "Nature of Business";
        if (isEmpty(etYearEstablished))       return "Year Established";
        if (isEmpty(etPsicCode))              return "PSIC Code";
        if (isEmpty(etOperatingHoursPerDay))  return "Operating Hours per Day";
        if (isEmpty(etOperatingDaysPerWeek))  return "Operating Days per Week";
        if (isEmpty(etOperatingDaysPerYear))  return "Operating Days per Year";
        if (isEmpty(etMaleEmployees))         return "No. of Male Employees";
        if (isEmpty(etFemaleEmployees))       return "No. of Female Employees";

        if (tableProductLines.isEmpty() && tableProductionRate.isEmpty())
            return "Product Lines / Production Rate (press Add)";
        if (tableYearCovered.isEmpty())       return "Year Covered (press Add)";
        if (tableVolCuM.isEmpty())            return "Volume Cu.M. (press Add)";

        if (isEmpty(etNameOfManagingHead))    return "Name of Managing Head";
        if (isEmpty(etNameOfPco))             return "Name of PCO";
        if (isEmpty(etPcoAccreditationNo))    return "PCO Accreditation No.";
        if (isEmpty(etDateOfEffectivity))     return "Date of Effectivity";
        if (isEmpty(etPhoneFaxNo))            return "Phone / Fax No.";
        if (isEmpty(etEmailAddress))          return "Email Address";

        return null;
    }

    private boolean isEmpty(EditText et) {
        return et == null || et.getText().toString().trim().isEmpty();
    }

    private void showValidationDialog(String field) {
        new AlertDialog.Builder(this)
                .setTitle("Incomplete Form")
                .setMessage("Please fill in the following field before saving:\n\n• " + field)
                .setPositiveButton("OK", null)
                .show();
    }
    private boolean performSave() {
        String embId      = etEmbIdNo.getText().toString().trim();
        String repCtrl    = etReportControl.getText().toString().trim();
        String typeMonit  = actTypeOfMonitoring.getText().toString().trim();
        String dateInsp   = etDateInspection.getText().toString().trim();
        String laws       = buildLawsString();
        String nameEst    = etNameOfEstablishment.getText().toString().trim();
        String proponent  = etProponent.getText().toString().trim();
        String mailing    = etMailingAddress.getText().toString().trim();
        String geoN       = etGeoN.getText().toString().trim();
        String geoE       = etGeoE.getText().toString().trim();
        String projLoc    = etProjectLocation.getText().toString().trim();
        String natBiz     = etNatureOfBusiness.getText().toString().trim();
        String yearEst    = etYearEstablished.getText().toString().trim();
        String psic       = etPsicCode.getText().toString().trim();
        String opHrs      = etOperatingHoursPerDay.getText().toString().trim();
        String opDw       = etOperatingDaysPerWeek.getText().toString().trim();
        String opDy       = etOperatingDaysPerYear.getText().toString().trim();
        String male       = etMaleEmployees.getText().toString().trim();
        String female     = etFemaleEmployees.getText().toString().trim();
        String total      = etTotalEmployees.getText().toString().trim();
        String mgHead     = etNameOfManagingHead.getText().toString().trim();
        String pco        = etNameOfPco.getText().toString().trim();
        String pcoAccred  = etPcoAccreditationNo.getText().toString().trim();
        String dateEff    = etDateOfEffectivity.getText().toString().trim();
        String phone      = etPhoneFaxNo.getText().toString().trim();
        String email      = etEmailAddress.getText().toString().trim();

        ReportInfoModel report = new ReportInfoModel();
        report.setEmployeeId(employeeId);
        report.setEmbId(embId);
        report.setReportControl(repCtrl);
        report.setTypeMonitoring(typeMonit);
        report.setDateOfInspection(dateInsp);
        if (!service.saveReportInfo(report)) { Log.e(TAG, "FAILED: saveReportInfo"); return false; }
        EstablishmentModel est = new EstablishmentModel();
        est.setEmployeeId(employeeId);
        est.setLaws(laws);
        est.setNameOfEstablishment(nameEst);
        est.setProponent(proponent);
        est.setMailingAddress(mailing);
        est.setGeoN(geoN); est.setGeoE(geoE);
        est.setProjectLocation(projLoc);
        est.setNatureOfBusiness(natBiz);
        est.setYearEstablish(yearEst);
        est.setPsicCode(psic);
        est.setOpHoursDay(opHrs); est.setOpDayWeek(opDw); est.setOpDayYear(opDy);
        est.setMale(male); est.setFemale(female); est.setNumberOfEmployee(total);
        est.setProductLines(tableProductLines);
        est.setProductionRate(tableProductionRate);
        est.setActualProductionRate(tableActualRate);
        est.setNameOfManagingHead(mgHead);
        est.setNameOfPCO(pco);
        est.setPcoAccreditationNo(pcoAccred);
        est.setDateOfEffectivity(dateEff);
        est.setPhoneFaxNo(phone);
        est.setEmailAddress(email);
        if (!service.saveEstablishmentInfo(est)) { Log.e(TAG, "FAILED: saveEstablishmentInfo"); return false; }

        YearCoverdInfoModel yr = new YearCoverdInfoModel();
        yr.setEmployeeId(employeeId);
        yr.setYearCovered(tableYearCovered);
        yr.setVolCuM(tableVolCuM);
        yr.setTotal(tableVolCuM);
        if (!service.saveYearCoveredInfo(yr)) { Log.e(TAG, "FAILED: saveYearCoveredInfo"); return false; }

        MonitoringRecord rec = new MonitoringRecord();
        rec.setEmployeeId(employeeId);
        rec.setEmbId(embId);
        rec.setReportControl(repCtrl);
        rec.setTypeMonitoring(typeMonit);
        rec.setDateOfInspection(dateInsp);
        rec.setLaws(laws);
        rec.setNameOfEstablishment(nameEst);
        rec.setProponent(proponent);
        rec.setMailingAddress(mailing);
        rec.setGeoN(geoN); rec.setGeoE(geoE);
        rec.setProjectLocation(projLoc);
        rec.setNatureOfBusiness(natBiz);
        rec.setYearEstablish(yearEst);
        rec.setPsicCode(psic);
        rec.setOpHoursDay(opHrs); rec.setOpDayWeek(opDw); rec.setOpDayYear(opDy);
        rec.setMale(male); rec.setFemale(female); rec.setNumberOfEmployee(total);
        rec.setProductLines(tableProductLines);
        rec.setProductionRate(tableProductionRate);
        rec.setActualProductionRate(tableActualRate);
        rec.setNameOfManagingHead(mgHead);
        rec.setNameOfPCO(pco);
        rec.setPcoAccreditationNo(pcoAccred);
        rec.setDateOfEffectivity(dateEff);
        rec.setPhoneFaxNo(phone);
        rec.setEmailAddress(email);
        rec.setYearCovered(tableYearCovered);
        rec.setVolCuM(tableVolCuM);
        rec.setTotal(tableVolCuM);
        rec.setIsComplete(0);

        lastSavedRecordId = monitoringService.saveRecord(rec);
        if (lastSavedRecordId == -1) {
            Log.e(TAG, "FAILED: insertMonitoringRecord");
            return false;
        }

        Log.d(TAG, "performSave OK — recordId=" + lastSavedRecordId);
        return true;
    }

    private String buildLawsString() {
        StringBuilder sb = new StringBuilder();
        if (cbPd1586.isChecked()) appendLaw(sb, "PD-1586");
        if (cbRa9003.isChecked()) appendLaw(sb, "RA-9003");
        if (cbRa9275.isChecked()) {
            String t = "RA-9275";
            if (cbRa9275Survey.isChecked())  t += "(Survey)";
            if (cbRa9275Routine.isChecked()) t += "(Routine)";
            appendLaw(sb, t);
        }
        if (cbRa6969.isChecked()) {
            String t = "RA-6969";
            if (cbRa6969Survey.isChecked())  t += "(Survey)";
            if (cbRa6969Routine.isChecked()) t += "(Routine)";
            appendLaw(sb, t);
        }
        if (cbRa8749.isChecked()) {
            String t = "RA-8749";
            if (cbRa8749Survey.isChecked())  t += "(Survey)";
            if (cbRa8749Routine.isChecked()) t += "(Routine)";
            appendLaw(sb, t);
        }
        return sb.toString();
    }

    private void appendLaw(StringBuilder sb, String law) {
        if (sb.length() > 0) sb.append(", ");
        sb.append(law);
    }
    private int parseSafeInt(String v) {
        try { return Integer.parseInt(v); } catch (Exception e) { return 0; }
    }
}