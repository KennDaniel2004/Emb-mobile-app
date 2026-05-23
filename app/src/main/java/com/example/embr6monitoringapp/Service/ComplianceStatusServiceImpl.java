package com.example.embr6monitoringapp.Service;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.example.embr6monitoringapp.DAO.ComplianceStatusDao;
import com.example.embr6monitoringapp.DAO.ComplianceStatusDaoImpl;
import com.example.embr6monitoringapp.Models.ComplianceStatusModel;
import com.example.embr6monitoringapp.Utils.SyncManager;


public class ComplianceStatusServiceImpl implements ComplianceStatusService {

    private static final String TAG = "ComplianceServiceImpl";

    private final Context              context;
    private final String               employeeId;
    private final ComplianceStatusDao  dao;
    private final SyncManager          syncManager;

    private final EditText etPd1586Ecc1, etPd1586Ecc1DateFrom, etPd1586Ecc1DateTo;
    private final EditText etPd1586Ecc2, etPd1586Ecc2DateFrom, etPd1586Ecc2DateTo;
    private final EditText etPd1586Ecc3, etPd1586Ecc3DateFrom, etPd1586Ecc3DateTo;
    private final EditText etRa6969DenrRegistry, etRa6969DenrDateFrom, etRa6969DenrDateTo;
    private final EditText etRa6969PclCert,      etRa6969PclDateFrom,  etRa6969PclDateTo;
    private final EditText etRa6969ImporterClearance, etRa6969ImporterDateFrom, etRa6969ImporterDateTo;
    private final EditText etRa6969CcoRegistry,  etRa6969CcoDateFrom,  etRa6969CcoDateTo;
    private final EditText etRa6969PermitTransport, etRa6969PermitDateFrom, etRa6969PermitDateTo;
    private final EditText etRa6969CotCopy,      etRa6969CotDateFrom,  etRa6969CotDateTo;
    private final EditText etRa8749PoNo, etRa8749PoDateFrom, etRa8749PoDateTo;
    private final EditText etRa9275DischargePermit, etRa9275DischargeDateFrom, etRa9275DischargeDateTo;

    private final EditText etRa9003MoaAgreement, etRa9003MoaDateFrom, etRa9003MoaDateTo;


    public ComplianceStatusServiceImpl(
            Context context, String employeeId,

            EditText etPd1586Ecc1,     EditText etPd1586Ecc1DateFrom, EditText etPd1586Ecc1DateTo,
            EditText etPd1586Ecc2,     EditText etPd1586Ecc2DateFrom, EditText etPd1586Ecc2DateTo,
            EditText etPd1586Ecc3,     EditText etPd1586Ecc3DateFrom, EditText etPd1586Ecc3DateTo,

            EditText etRa6969DenrRegistry, EditText etRa6969DenrDateFrom, EditText etRa6969DenrDateTo,
            EditText etRa6969PclCert,      EditText etRa6969PclDateFrom,  EditText etRa6969PclDateTo,
            EditText etRa6969ImporterClearance, EditText etRa6969ImporterDateFrom, EditText etRa6969ImporterDateTo,
            EditText etRa6969CcoRegistry,  EditText etRa6969CcoDateFrom,  EditText etRa6969CcoDateTo,
            EditText etRa6969PermitTransport, EditText etRa6969PermitDateFrom, EditText etRa6969PermitDateTo,
            EditText etRa6969CotCopy,      EditText etRa6969CotDateFrom,  EditText etRa6969CotDateTo,

            EditText etRa8749PoNo, EditText etRa8749PoDateFrom, EditText etRa8749PoDateTo,

            EditText etRa9275DischargePermit, EditText etRa9275DischargeDateFrom, EditText etRa9275DischargeDateTo,

            EditText etRa9003MoaAgreement, EditText etRa9003MoaDateFrom, EditText etRa9003MoaDateTo
    ) {
        this.context    = context;
        this.employeeId = employeeId;

        this.etPd1586Ecc1         = etPd1586Ecc1;
        this.etPd1586Ecc1DateFrom = etPd1586Ecc1DateFrom;
        this.etPd1586Ecc1DateTo   = etPd1586Ecc1DateTo;
        this.etPd1586Ecc2         = etPd1586Ecc2;
        this.etPd1586Ecc2DateFrom = etPd1586Ecc2DateFrom;
        this.etPd1586Ecc2DateTo   = etPd1586Ecc2DateTo;
        this.etPd1586Ecc3         = etPd1586Ecc3;
        this.etPd1586Ecc3DateFrom = etPd1586Ecc3DateFrom;
        this.etPd1586Ecc3DateTo   = etPd1586Ecc3DateTo;

        this.etRa6969DenrRegistry      = etRa6969DenrRegistry;
        this.etRa6969DenrDateFrom      = etRa6969DenrDateFrom;
        this.etRa6969DenrDateTo        = etRa6969DenrDateTo;
        this.etRa6969PclCert           = etRa6969PclCert;
        this.etRa6969PclDateFrom       = etRa6969PclDateFrom;
        this.etRa6969PclDateTo         = etRa6969PclDateTo;
        this.etRa6969ImporterClearance = etRa6969ImporterClearance;
        this.etRa6969ImporterDateFrom  = etRa6969ImporterDateFrom;
        this.etRa6969ImporterDateTo    = etRa6969ImporterDateTo;
        this.etRa6969CcoRegistry       = etRa6969CcoRegistry;
        this.etRa6969CcoDateFrom       = etRa6969CcoDateFrom;
        this.etRa6969CcoDateTo         = etRa6969CcoDateTo;
        this.etRa6969PermitTransport   = etRa6969PermitTransport;
        this.etRa6969PermitDateFrom    = etRa6969PermitDateFrom;
        this.etRa6969PermitDateTo      = etRa6969PermitDateTo;
        this.etRa6969CotCopy           = etRa6969CotCopy;
        this.etRa6969CotDateFrom       = etRa6969CotDateFrom;
        this.etRa6969CotDateTo         = etRa6969CotDateTo;

        this.etRa8749PoNo       = etRa8749PoNo;
        this.etRa8749PoDateFrom = etRa8749PoDateFrom;
        this.etRa8749PoDateTo   = etRa8749PoDateTo;

        this.etRa9275DischargePermit  = etRa9275DischargePermit;
        this.etRa9275DischargeDateFrom= etRa9275DischargeDateFrom;
        this.etRa9275DischargeDateTo  = etRa9275DischargeDateTo;

        this.etRa9003MoaAgreement = etRa9003MoaAgreement;
        this.etRa9003MoaDateFrom  = etRa9003MoaDateFrom;
        this.etRa9003MoaDateTo    = etRa9003MoaDateTo;


        this.dao         = new ComplianceStatusDaoImpl(context);
        this.syncManager = new SyncManager(context);
    }


    @Override
    public boolean submit() {
        ComplianceStatusModel model = new ComplianceStatusModel();
        model.setEmployeeId(employeeId);

        model.setPd1586Ecc1(g(etPd1586Ecc1));
        model.setPd1586Ecc1DateFrom(g(etPd1586Ecc1DateFrom));
        model.setPd1586Ecc1DateTo(g(etPd1586Ecc1DateTo));
        model.setPd1586Ecc2(g(etPd1586Ecc2));
        model.setPd1586Ecc2DateFrom(g(etPd1586Ecc2DateFrom));
        model.setPd1586Ecc2DateTo(g(etPd1586Ecc2DateTo));
        model.setPd1586Ecc3(g(etPd1586Ecc3));
        model.setPd1586Ecc3DateFrom(g(etPd1586Ecc3DateFrom));
        model.setPd1586Ecc3DateTo(g(etPd1586Ecc3DateTo));

        model.setRa6969DenrRegistry(g(etRa6969DenrRegistry));
        model.setRa6969DenrDateFrom(g(etRa6969DenrDateFrom));
        model.setRa6969DenrDateTo(g(etRa6969DenrDateTo));
        model.setRa6969PclCert(g(etRa6969PclCert));
        model.setRa6969PclDateFrom(g(etRa6969PclDateFrom));
        model.setRa6969PclDateTo(g(etRa6969PclDateTo));
        model.setRa6969ImporterClearance(g(etRa6969ImporterClearance));
        model.setRa6969ImporterDateFrom(g(etRa6969ImporterDateFrom));
        model.setRa6969ImporterDateTo(g(etRa6969ImporterDateTo));
        model.setRa6969CcoRegistry(g(etRa6969CcoRegistry));
        model.setRa6969CcoDateFrom(g(etRa6969CcoDateFrom));
        model.setRa6969CcoDateTo(g(etRa6969CcoDateTo));
        model.setRa6969PermitTransport(g(etRa6969PermitTransport));
        model.setRa6969PermitDateFrom(g(etRa6969PermitDateFrom));
        model.setRa6969PermitDateTo(g(etRa6969PermitDateTo));
        model.setRa6969CotCopy(g(etRa6969CotCopy));
        model.setRa6969CotDateFrom(g(etRa6969CotDateFrom));
        model.setRa6969CotDateTo(g(etRa6969CotDateTo));

        model.setRa8749PoNo(g(etRa8749PoNo));
        model.setRa8749PoDateFrom(g(etRa8749PoDateFrom));
        model.setRa8749PoDateTo(g(etRa8749PoDateTo));

        model.setRa9275DischargePermit(g(etRa9275DischargePermit));
        model.setRa9275DischargeDateFrom(g(etRa9275DischargeDateFrom));
        model.setRa9275DischargeDateTo(g(etRa9275DischargeDateTo));

        model.setRa9003MoaAgreement(g(etRa9003MoaAgreement));
        model.setRa9003MoaDateFrom(g(etRa9003MoaDateFrom));
        model.setRa9003MoaDateTo(g(etRa9003MoaDateTo));

        boolean result = dao.insert(model);
        if (result) syncManager.syncIfOnline();
        return result;
    }


    @Override
    public void resetForm() {
        EditText[] all = {
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
        };
        for (EditText et : all) et.setText("");
        Log.d(TAG, "resetForm() complete.");
    }

    private String g(EditText et) {
        return et != null ? et.getText().toString().trim() : "";
    }
}