package com.example.embr6monitoringapp.Controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.embr6monitoringapp.R;
import com.example.embr6monitoringapp.Service.ComplianceStatusService;
import com.example.embr6monitoringapp.Service.ComplianceStatusServiceImpl;

import java.util.Calendar;


public class ComplianceStatusController extends AppCompatActivity {

    TextView tabPd1586, tabRa6969, tabRa8749, tabRa9275, tabRa9003;
    View anchorPd1586, anchorRa6969, anchorRa8749, anchorRa9275, anchorRa9003;
    ScrollView mainScrollView;
    EditText etPd1586Ecc1, etPd1586Ecc1DateFrom, etPd1586Ecc1DateTo;
    EditText etPd1586Ecc2, etPd1586Ecc2DateFrom, etPd1586Ecc2DateTo;
    EditText etPd1586Ecc3, etPd1586Ecc3DateFrom, etPd1586Ecc3DateTo;
    EditText etRa6969DenrRegistry,    etRa6969DenrDateFrom,     etRa6969DenrDateTo;
    EditText etRa6969PclCert,         etRa6969PclDateFrom,      etRa6969PclDateTo;
    EditText etRa6969ImporterClearance, etRa6969ImporterDateFrom, etRa6969ImporterDateTo;
    EditText etRa6969CcoRegistry,     etRa6969CcoDateFrom,      etRa6969CcoDateTo;
    EditText etRa6969PermitTransport, etRa6969PermitDateFrom,   etRa6969PermitDateTo;
    EditText etRa6969CotCopy,         etRa6969CotDateFrom,      etRa6969CotDateTo;
    EditText etRa8749PoNo, etRa8749PoDateFrom, etRa8749PoDateTo;
    EditText etRa9275DischargePermit, etRa9275DischargeDateFrom, etRa9275DischargeDateTo;
    EditText etRa9003MoaAgreement, etRa9003MoaDateFrom, etRa9003MoaDateTo;
    Button btnSubmitCompliance;
    ComplianceStatusService complianceService;
    private String  employeeId          = "";
    private int     recordId            = -1;
    private boolean isSavedSuccessfully = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compliance_status);

        employeeId = getIntent().getStringExtra("EMPLOYEE_ID");
        if (employeeId == null) employeeId = "";
        recordId = getIntent().getIntExtra("RECORD_ID", -1);

        bindViews();

        complianceService = new ComplianceStatusServiceImpl(
                this, employeeId,
                etPd1586Ecc1, etPd1586Ecc1DateFrom, etPd1586Ecc1DateTo,
                etPd1586Ecc2, etPd1586Ecc2DateFrom, etPd1586Ecc2DateTo,
                etPd1586Ecc3, etPd1586Ecc3DateFrom, etPd1586Ecc3DateTo,
                etRa6969DenrRegistry, etRa6969DenrDateFrom, etRa6969DenrDateTo,
                etRa6969PclCert, etRa6969PclDateFrom, etRa6969PclDateTo,
                etRa6969ImporterClearance, etRa6969ImporterDateFrom, etRa6969ImporterDateTo,
                etRa6969CcoRegistry, etRa6969CcoDateFrom, etRa6969CcoDateTo,
                etRa6969PermitTransport, etRa6969PermitDateFrom, etRa6969PermitDateTo,
                etRa6969CotCopy, etRa6969CotDateFrom, etRa6969CotDateTo,
                etRa8749PoNo, etRa8749PoDateFrom, etRa8749PoDateTo,
                etRa9275DischargePermit, etRa9275DischargeDateFrom, etRa9275DischargeDateTo,
                etRa9003MoaAgreement, etRa9003MoaDateFrom, etRa9003MoaDateTo
        );

        buttonListeners(new ButtonClick());
        setupDatePickers();
        setActiveTab(tabPd1586);
    }

    private void buttonListeners(ButtonClick listener) {

        tabPd1586.setOnClickListener(listener);
        tabRa6969.setOnClickListener(listener);
        tabRa8749.setOnClickListener(listener);
        tabRa9275.setOnClickListener(listener);
        tabRa9003.setOnClickListener(listener);
        btnSubmitCompliance.setOnClickListener(listener);
    }

    class ButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if      (id == R.id.btnBack)   finish();
            else if (id == R.id.tabPd1586) { setActiveTab(tabPd1586); scrollToAnchor(anchorPd1586); }
            else if (id == R.id.tabRa6969) { setActiveTab(tabRa6969); scrollToAnchor(anchorRa6969); }
            else if (id == R.id.tabRa8749) { setActiveTab(tabRa8749); scrollToAnchor(anchorRa8749); }
            else if (id == R.id.tabRa9275) { setActiveTab(tabRa9275); scrollToAnchor(anchorRa9275); }
            else if (id == R.id.tabRa9003) { setActiveTab(tabRa9003); scrollToAnchor(anchorRa9003); }

            else if (id == R.id.btnSubmitCompliance) {
                boolean saved = complianceService.submit();
                if (saved) {
                    isSavedSuccessfully = true;
                    Toast.makeText(ComplianceStatusController.this,
                            "Compliance status saved!", Toast.LENGTH_SHORT).show();
                    complianceService.resetForm();
                    navigateToMonitoringDetail();
                } else {
                    Toast.makeText(ComplianceStatusController.this,
                            "Failed to save compliance. Please try again.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void navigateToMonitoringDetail() {
        Intent intent = new Intent(this, MonitoringProgressController.class);
        intent.putExtra("EMPLOYEE_ID", employeeId);
        intent.putExtra("RECORD_ID",   recordId);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_left);
    }


    private void setActiveTab(TextView activeTab) {
        TextView[] allTabs = { tabPd1586, tabRa6969, tabRa8749, tabRa9275, tabRa9003 };
        for (TextView tab : allTabs) {
            tab.setBackgroundResource(android.R.color.transparent);
            tab.setTextColor(0xFFFFFFFF);
        }
        activeTab.setBackgroundResource(R.drawable.tab_active_bg);
        activeTab.setTextColor(0xFFFFFFFF);
    }

    private void scrollToAnchor(View anchor) {
        mainScrollView.post(() -> {
            int top = 0;
            View current = anchor;
            while (current != null && current != mainScrollView) {
                top += current.getTop();
                if (current.getParent() instanceof View)
                    current = (View) current.getParent();
                else break;
            }
            mainScrollView.smoothScrollTo(0, top);
        });
    }

    private void setupDatePickers() {
        EditText[] dateFields = {
                etPd1586Ecc1DateFrom, etPd1586Ecc1DateTo,
                etPd1586Ecc2DateFrom, etPd1586Ecc2DateTo,
                etPd1586Ecc3DateFrom, etPd1586Ecc3DateTo,
                etRa6969DenrDateFrom, etRa6969DenrDateTo,
                etRa6969PclDateFrom,  etRa6969PclDateTo,
                etRa6969ImporterDateFrom, etRa6969ImporterDateTo,
                etRa6969CcoDateFrom,  etRa6969CcoDateTo,
                etRa6969PermitDateFrom, etRa6969PermitDateTo,
                etRa6969CotDateFrom,  etRa6969CotDateTo,
                etRa8749PoDateFrom,   etRa8749PoDateTo,
                etRa9275DischargeDateFrom, etRa9275DischargeDateTo,
                etRa9003MoaDateFrom,  etRa9003MoaDateTo
        };
        for (EditText field : dateFields) {
            field.setOnClickListener(v -> {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(this,
                        (view, year, month, day) ->
                                field.setText(String.format("%04d-%02d-%02d", year, month + 1, day)),
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                ).show();
            });
        }
    }

    private void bindViews() {

        mainScrollView = findViewById(R.id.mainScrollView);

        tabPd1586 = findViewById(R.id.tabPd1586);
        tabRa6969 = findViewById(R.id.tabRa6969);
        tabRa8749 = findViewById(R.id.tabRa8749);
        tabRa9275 = findViewById(R.id.tabRa9275);
        tabRa9003 = findViewById(R.id.tabRa9003);

        anchorPd1586 = findViewById(R.id.anchorPd1586);
        anchorRa6969 = findViewById(R.id.anchorRa6969);
        anchorRa8749 = findViewById(R.id.anchorRa8749);
        anchorRa9275 = findViewById(R.id.anchorRa9275);
        anchorRa9003 = findViewById(R.id.anchorRa9003);

        etPd1586Ecc1         = findViewById(R.id.etPd1586Ecc1);
        etPd1586Ecc1DateFrom = findViewById(R.id.etPd1586Ecc1DateFrom);
        etPd1586Ecc1DateTo   = findViewById(R.id.etPd1586Ecc1DateTo);
        etPd1586Ecc2         = findViewById(R.id.etPd1586Ecc2);
        etPd1586Ecc2DateFrom = findViewById(R.id.etPd1586Ecc2DateFrom);
        etPd1586Ecc2DateTo   = findViewById(R.id.etPd1586Ecc2DateTo);
        etPd1586Ecc3         = findViewById(R.id.etPd1586Ecc3);
        etPd1586Ecc3DateFrom = findViewById(R.id.etPd1586Ecc3DateFrom);
        etPd1586Ecc3DateTo   = findViewById(R.id.etPd1586Ecc3DateTo);

        etRa6969DenrRegistry      = findViewById(R.id.etRa6969DenrRegistry);
        etRa6969DenrDateFrom      = findViewById(R.id.etRa6969DenrDateFrom);
        etRa6969DenrDateTo        = findViewById(R.id.etRa6969DenrDateTo);
        etRa6969PclCert           = findViewById(R.id.etRa6969PclCert);
        etRa6969PclDateFrom       = findViewById(R.id.etRa6969PclDateFrom);
        etRa6969PclDateTo         = findViewById(R.id.etRa6969PclDateTo);
        etRa6969ImporterClearance = findViewById(R.id.etRa6969ImporterClearance);
        etRa6969ImporterDateFrom  = findViewById(R.id.etRa6969ImporterDateFrom);
        etRa6969ImporterDateTo    = findViewById(R.id.etRa6969ImporterDateTo);
        etRa6969CcoRegistry       = findViewById(R.id.etRa6969CcoRegistry);
        etRa6969CcoDateFrom       = findViewById(R.id.etRa6969CcoDateFrom);
        etRa6969CcoDateTo         = findViewById(R.id.etRa6969CcoDateTo);
        etRa6969PermitTransport   = findViewById(R.id.etRa6969PermitTransport);
        etRa6969PermitDateFrom    = findViewById(R.id.etRa6969PermitDateFrom);
        etRa6969PermitDateTo      = findViewById(R.id.etRa6969PermitDateTo);
        etRa6969CotCopy           = findViewById(R.id.etRa6969CotCopy);
        etRa6969CotDateFrom       = findViewById(R.id.etRa6969CotDateFrom);
        etRa6969CotDateTo         = findViewById(R.id.etRa6969CotDateTo);

        etRa8749PoNo              = findViewById(R.id.etRa8749PoNo);
        etRa8749PoDateFrom        = findViewById(R.id.etRa8749PoDateFrom);
        etRa8749PoDateTo          = findViewById(R.id.etRa8749PoDateTo);

        etRa9275DischargePermit   = findViewById(R.id.etRa9275DischargePermit);
        etRa9275DischargeDateFrom = findViewById(R.id.etRa9275DischargeDateFrom);
        etRa9275DischargeDateTo   = findViewById(R.id.etRa9275DischargeDateTo);

        etRa9003MoaAgreement      = findViewById(R.id.etRa9003MoaAgreement);
        etRa9003MoaDateFrom       = findViewById(R.id.etRa9003MoaDateFrom);
        etRa9003MoaDateTo         = findViewById(R.id.etRa9003MoaDateTo);

        btnSubmitCompliance = findViewById(R.id.btnSubmitCompliance);
    }
}