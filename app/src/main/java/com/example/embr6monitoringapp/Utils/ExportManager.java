package com.example.embr6monitoringapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.R;

import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportManager {

    private static final String TAG        = "ExportManager";
    private static final String PREFS_NAME = "ExportNotifications";
    private static final String KEY_LINKS  = "notification_links";
    private static final int    GREEN     = Color.parseColor("#1a6b2d");
    private static final String GREEN_HEX = "1a6b2d";

    private static final int PAGE_W   = 612;
    private static final int PAGE_H   = 792;
    private static final int MARGIN_L = 54;
    private static final int MARGIN_R = 558;

    private final Context context;
    private Bitmap headerBitmap;

    public ExportManager(Context context) {
        this.context = context.getApplicationContext();
        try {
            int resId = context.getResources().getIdentifier(
                    "header", "drawable", context.getPackageName());
            if (resId != 0) {
                headerBitmap = BitmapFactory.decodeResource(
                        context.getResources(), resId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load header image", e);
            headerBitmap = null;
        }
    }

    public interface ExportCallback {
        void onSuccess(String format, File file, Uri shareUri);
        void onError(String format, String error);
    }

    public void exportAll(MonitoringRecord record, ExportCallback cb) {
        new Thread(() -> {
            exportPdf(record, cb);
            exportDocx(record, cb);
        }).start();
    }



    // ==================== PDF EXPORT ====================

    public void exportPdf(MonitoringRecord rec, ExportCallback cb) {
        try {
            File file = buildFile(rec.getEmployeeId(), rec.getId(), "pdf");
            PdfDocument doc = new PdfDocument();

            PdfDocument.PageInfo pi1 =
                    new PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create();
            PdfDocument.Page p1 = doc.startPage(pi1);
            Canvas c1 = p1.getCanvas();
            int y = drawPdfHeader(c1);
            y = drawPdfTitle(c1, rec, y);
            y = drawPdfGeneralInfo(c1, rec, y);
            y = drawPdfPurpose(c1, rec, y);
            drawPdfFooter(c1, 1);
            doc.finishPage(p1);

            PdfDocument.PageInfo pi2 =
                    new PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 2).create();
            PdfDocument.Page p2 = doc.startPage(pi2);
            Canvas c2 = p2.getCanvas();
            int y2 = MARGIN_L;
            y2 = drawPdfCompliancePermits(c2, rec, y2);
            y2 = drawPdfRecommendations(c2, rec, y2);
            drawPdfSignatures(c2, rec, y2);
            drawPdfFooter(c2, 2);
            doc.finishPage(p2);

            FileOutputStream fos = new FileOutputStream(file);
            doc.writeTo(fos);
            doc.close();
            fos.close();

            saveNotificationLink(file.getAbsolutePath());
            Uri uri = FileProvider.getUriForFile(
                    context, context.getPackageName() + ".fileprovider", file);
            cb.onSuccess("PDF", file, uri);
        } catch (Exception e) {
            Log.e(TAG, "PDF error", e);
            cb.onError("PDF", e.getMessage());
        }
    }

    private int drawPdfHeader(Canvas c) {
        int y = MARGIN_L;
        if (headerBitmap != null) {
            int finalH = 80;
            int finalW = (int) (headerBitmap.getWidth()
                    * (finalH / (float) headerBitmap.getHeight()));
            int startX = MARGIN_L + (500 - finalW) / 2;
            Rect src = new Rect(0, 0, headerBitmap.getWidth(), headerBitmap.getHeight());
            Rect dst = new Rect(startX, y, startX + finalW, y + finalH);
            c.drawBitmap(headerBitmap, src, dst, null);
            y += finalH + 8;
        }
        Paint rule = new Paint();
        rule.setColor(GREEN);
        rule.setStrokeWidth(2.5f);
        c.drawLine(MARGIN_L, y, MARGIN_R, y, rule);
        rule.setStrokeWidth(1f);
        c.drawLine(MARGIN_L, y + 4, MARGIN_R, y + 4, rule);
        return y + 14;
    }

    private int drawPdfTitle(Canvas c, MonitoringRecord r, int y) {
        Paint center = bold(13, Color.BLACK);
        center.setTextAlign(Paint.Align.CENTER);
        c.drawText("INTEGRATED COMPLIANCE INSPECTION REPORT", PAGE_W / 2f, y, center);
        y += 18;

        Paint norm = normal(9, Color.BLACK);
        norm.setTextAlign(Paint.Align.CENTER);
        c.drawText("EMB ID No.: " + s(r.getEmbId())
                        + "     Report Control: "
                        + (s(r.getReportControl()).isEmpty()
                        ? "__________________" : s(r.getReportControl())),
                PAGE_W / 2f, y, norm);
        y += 15;

        String onsite = "Onsite Monitoring".equals(r.getTypeMonitoring()) ? "☒" : "☐";
        String tblMon = "Table Monitoring".equals(r.getTypeMonitoring())  ? "☒" : "☐";
        c.drawText("Type of Monitoring: " + onsite
                        + " Onsite Monitoring   " + tblMon + " Table Monitoring",
                PAGE_W / 2f, y, norm);
        y += 15;

        c.drawText("Date of Inspection: " + s(r.getDateOfInspection()),
                PAGE_W / 2f, y, norm);
        y += 22;
        return y;
    }





    //             PDF GENERAL INFORMATION

    private int drawPdfGeneralInfo(Canvas c, MonitoringRecord r, int y) {
        Paint secBg = new Paint();
        secBg.setColor(GREEN);
        c.drawRect(MARGIN_L, y - 2, MARGIN_R, y + 14, secBg);
        Paint secTxt = bold(9, Color.WHITE);
        c.drawText("1. GENERAL INFORMATION", MARGIN_L + 4, y + 10, secTxt);
        y += 18;

        Paint box = new Paint();
        box.setColor(Color.LTGRAY);
        box.setStyle(Paint.Style.STROKE);
        box.setStrokeWidth(0.8f);

        // Environmental Laws
        int lawBoxH = 80;
        c.drawRect(MARGIN_L, y - lawBoxH + 2, MARGIN_R, y + 2, box);

        String laws  = s(r.getLaws());
        String pd    = laws.contains("PD-1586") ? "☒" : "☐";
        String ra8   = laws.contains("RA-8749") ? "☒" : "☐";
        String ra6   = laws.contains("RA-6969") ? "☒" : "☐";
        String ra9275 = laws.contains("RA-9275") ? "☒" : "☐";
        String ra9003 = laws.contains("RA-9003") ? "☒" : "☐";

        Paint lawLabel = bold(8, Color.BLACK);
        c.drawText("Applicable Environmental Laws: (Pls. check box)",
                MARGIN_L + 4, y - lawBoxH + 14, lawLabel);

        int mid = MARGIN_L + 252;
        Paint lawText = normal(8, Color.BLACK);
        // Left column
        c.drawText(pd   + " PD-1586",                         MARGIN_L + 4, y - lawBoxH + 30, lawText);
        c.drawText(ra6  + " RA-6969 (\u2610Survey \u2612Routine)", MARGIN_L + 4, y - lawBoxH + 44, lawText);
        c.drawText(ra9275 + " RA-9275 (\u2610Survey \u2612Routine)", MARGIN_L + 4, y - lawBoxH + 58, lawText);
        // Right column
        c.drawText(ra8  + " RA-8749 (\u2610Survey \u2612Routine)", mid, y - lawBoxH + 30, lawText);
        c.drawText("Covered by MC 2022-003?  \u2610YES   \u2612NO", mid, y - lawBoxH + 44, lawText);
        c.drawText(ra9003 + " RA-9003",                        mid, y - lawBoxH + 58, lawText);

        y += 10;

        y = drawPdfTableRow2Cols(c, "Name of Establishment:", s(r.getNameOfEstablishment()),
                "Geo-Coordinates:", s(r.getGeoN()) + " N / " + s(r.getGeoE()) + " E", y);
        y = drawPdfTableRowSingle(c, "Proponent:", s(r.getProponent()), y);
        y = drawPdfTableRowSingle(c, "Project Location:", s(r.getProjectLocation()), y);
        y = drawPdfTableRowSingle(c, "Nature of Business:", s(r.getNatureOfBusiness()), y);
        y = drawPdfTableRow2Cols(c, "Year Established:", s(r.getYearEstablish()),
                "PSIC Code:", s(r.getPsicCode()), y);
        y = drawPdfTableRow3Cols(c,
                "Operating hours/day:", s(r.getOpHoursDay()),
                "Operating days/week:", s(r.getOpDayWeek()),
                "Operating days/year:", s(r.getOpDayYear()), y);
        y = drawPdfTableRow3Cols(c,
                "No. of employees:", s(r.getNumberOfEmployee()),
                "Male:", s(r.getMale()),
                "Female:", s(r.getFemale()), y);

        y += 4;

        int col1 = MARGIN_L, col2 = MARGIN_L + 160, col3 = MARGIN_L + 380;
        drawPdfBorderedCell(c, "Product Lines", col1, col2, y, true);
        drawPdfBorderedCell(c,
                "Production Rate as Declared in the ECC (Unit/day)", col2, col3, y, true);
        drawPdfBorderedCell(c, "Actual Production Rate (Unit/day)", col3, MARGIN_R, y, true);
        y += 15;
        drawPdfBorderedCell(c, orBlank(r.getProductLines()),           col1, col2, y, false);
        drawPdfBorderedCell(c, orBlank(r.getProductionRate()),         col2, col3, y, false);
        drawPdfBorderedCell(c, orBlank(r.getActualProductionRate()),   col3, MARGIN_R, y, false);
        y += 18;

        y = drawPdfTableRowSingle(c, "Name of Managing Head:", s(r.getNameOfManagingHead()), y);
        y = drawPdfTableRow2Cols(c, "Name of PCO:", s(r.getNameOfPCO()),
                "PCO Accreditation No.:", s(r.getPcoAccreditationNo()), y);
        y = drawPdfTableRow2Cols(c, "Date of Effectivity:", s(r.getDateOfEffectivity()),
                "Phone/Fax No.:", s(r.getPhoneFaxNo()), y);
        y = drawPdfTableRowSingle(c, "Email Address:", s(r.getEmailAddress()), y);

        y += 6;
        return y;
    }

    private int drawPdfTableRowSingle(Canvas c, String label, String value, int y) {
        Paint box = new Paint();
        box.setColor(Color.LTGRAY);
        box.setStyle(Paint.Style.STROKE);
        box.setStrokeWidth(0.5f);

        int rowH = 16;
        c.drawRect(MARGIN_L, y - rowH + 2, MARGIN_R, y + 2, box);
        c.drawText(label, MARGIN_L + 4, y, bold(8, Color.BLACK));
        c.drawText(value.isEmpty() ? "_________________________" : value,
                MARGIN_L + 140, y, normal(8, Color.BLACK));
        return y + rowH;
    }

    private int drawPdfTableRow2Cols(Canvas c,
                                     String label1, String val1,
                                     String label2, String val2, int y) {
        Paint box = new Paint();
        box.setColor(Color.LTGRAY);
        box.setStyle(Paint.Style.STROKE);
        box.setStrokeWidth(0.5f);

        int rowH = 16;
        int mid  = MARGIN_L + 280;
        c.drawRect(MARGIN_L, y - rowH + 2, mid,     y + 2, box);
        c.drawRect(mid,       y - rowH + 2, MARGIN_R, y + 2, box);

        c.drawText(label1, MARGIN_L + 4,   y, bold(8, Color.BLACK));
        c.drawText(val1.isEmpty() ? "_________________________" : val1,
                MARGIN_L + 140, y, normal(8, Color.BLACK));
        c.drawText(label2, mid + 4,         y, bold(8, Color.BLACK));
        c.drawText(val2.isEmpty() ? "_________________________" : val2,
                mid + 130, y, normal(8, Color.BLACK));
        return y + rowH;
    }

    private int drawPdfTableRow3Cols(Canvas c,
                                     String label1, String val1,
                                     String label2, String val2,
                                     String label3, String val3, int y) {
        Paint box = new Paint();
        box.setColor(Color.LTGRAY);
        box.setStyle(Paint.Style.STROKE);
        box.setStrokeWidth(0.5f);

        int rowH   = 16;
        int col1End = MARGIN_L + 168;
        int col2End = MARGIN_L + 336;

        c.drawRect(MARGIN_L,  y - rowH + 2, col1End,  y + 2, box);
        c.drawRect(col1End,   y - rowH + 2, col2End,  y + 2, box);
        c.drawRect(col2End,   y - rowH + 2, MARGIN_R, y + 2, box);

        c.drawText(label1, MARGIN_L + 4,    y, bold(8, Color.BLACK));
        c.drawText(val1.isEmpty() ? "________" : val1,
                MARGIN_L + 120, y, normal(8, Color.BLACK));

        c.drawText(label2, col1End + 4,     y, bold(8, Color.BLACK));
        c.drawText(val2.isEmpty() ? "________" : val2,
                col1End + 110, y, normal(8, Color.BLACK));

        c.drawText(label3, col2End + 4,     y, bold(8, Color.BLACK));
        c.drawText(val3.isEmpty() ? "________" : val3,
                col2End + 110, y, normal(8, Color.BLACK));

        return y + rowH;
    }

    private void drawPdfBorderedCell(Canvas c, String text,
                                     int x1, int x2, int y, boolean isHeader) {
        int rowH = 14;
        Paint box = new Paint();
        box.setColor(Color.LTGRAY);
        box.setStyle(Paint.Style.STROKE);
        box.setStrokeWidth(0.5f);
        c.drawRect(x1, y - rowH + 2, x2, y + 2, box);

        if (isHeader) {
            Paint bg = new Paint();
            bg.setColor(Color.parseColor("#e8f5e9"));
            c.drawRect(x1, y - rowH + 2, x2, y + 2, bg);
        }

        Paint txt = isHeader ? bold(7, Color.BLACK) : normal(7, Color.BLACK);
        String display = (text == null || text.isEmpty()) ? " " : text;
        c.drawText(display, x1 + 3, y - 2, txt);
    }

    private int drawPdfPurpose(Canvas c, MonitoringRecord r, int y) {
        Paint secBg = new Paint();
        secBg.setColor(GREEN);
        c.drawRect(MARGIN_L, y - 2, MARGIN_R, y + 14, secBg);
        Paint secTxt = bold(9, Color.WHITE);
        c.drawText("2. PURPOSE OF INSPECTION", MARGIN_L + 4, y + 10, secTxt);
        y += 18;

        Paint norm = normal(8, Color.BLACK);
        String va = r.getVerifyAccuracy() == 1 ? "☒" : "☐";
        c.drawText(va + " Verify accuracy of information submitted by the establishment.",
                MARGIN_L + 4, y, norm);
        y += 15;

        int tCol0 = MARGIN_L + 12, tCol1 = MARGIN_L + 300, tCol2 = MARGIN_L + 380;
        drawPdfBorderedCell(c, "Permit Type", tCol0, tCol1, y, true);
        drawPdfBorderedCell(c, "New",         tCol1, tCol2, y, true);
        drawPdfBorderedCell(c, "Renewal",     tCol2, MARGIN_R, y, true);
        y += 14;

        String[][] permits = {
                {"PMPIN Application",
                        yn(r.getPmpinNew()),   yn(r.getPmpinRenewal())},
                {"Hazardous Waste Generator ID Registration",
                        yn(r.getHwgidNew()),   yn(r.getHwgidRenewal())},
                {"Hazardous Waste Transporter Registration",
                        yn(r.getHwtrNew()),    yn(r.getHwtrRenewal())},
                {"Hazardous Waste TSD Registration",
                        yn(r.getHwtsdNew()),   yn(r.getHwtsdRenewal())},
                {"Permit to Operate Air Pollution Control Installation",
                        yn(r.getPoapciNew()),  yn(r.getPoapciRenewal())},
                {"Discharge Permit",
                        yn(r.getDpNew()),      yn(r.getDpRenewal())},
                {"Others: " + s(r.getOtherPermit()), "", ""},
        };

        for (String[] row : permits) {
            drawPdfBorderedCell(c, row[0], tCol0, tCol1, y, false);
            drawPdfBorderedCell(c, row[1], tCol1, tCol2, y, false);
            drawPdfBorderedCell(c, row[2], tCol2, MARGIN_R, y, false);
            y += 13;
        }
        y += 8;

        String dc  = r.getDetermineCompliance() == 1 ? "☒" : "☐";
        String inv = r.getInvestigate()          == 1 ? "☒" : "☐";
        String sv  = r.getSurvey()               == 1 ? "☒" : "☐";
        c.drawText(dc  + " Determine compliance status with environmental regulations.",
                MARGIN_L + 4, y, norm); y += 14;
        c.drawText(inv + " Investigate community complaints.",
                MARGIN_L + 4, y, norm); y += 14;
        c.drawText(sv  + " Survey",
                MARGIN_L + 4, y, norm); y += 14;
        c.drawText("\u2610 Others: " + s(r.getOtherSpecify()),
                MARGIN_L + 4, y, norm); y += 18;

        y = drawPdfTableRow2Cols(c,
                "Name of Contact Person:", s(r.getContactName()),
                "Position / Designation:", s(r.getPosition()), y);
        return y + 4;
    }

    private int drawPdfCompliancePermits(Canvas c, MonitoringRecord r, int y) {
        Paint secBg = new Paint();
        secBg.setColor(GREEN);
        c.drawRect(MARGIN_L, y - 2, MARGIN_R, y + 14, secBg);
        Paint secTxt = bold(9, Color.WHITE);
        c.drawText("3. COMPLIANCE STATUS", MARGIN_L + 4, y + 10, secTxt);
        y += 18;

        c.drawText("3.1 DENR Permits/Licenses/Clearances",
                MARGIN_L + 4, y, bold(9, Color.BLACK));
        y += 16;

        int[] cols = {MARGIN_L, MARGIN_L + 100, MARGIN_L + 300, MARGIN_L + 420, MARGIN_R};
        y = drawPdfTableRow4(c, new String[]{"Environmental Law","Permits","Date of Issue","Expiry Date"}, cols, y, true);

        y = drawPdfTableRow4(c, new String[]{"PD 1586","ECC 1: "+s(r.getPd1586Ecc1()),s(r.getPd1586Ecc1DateFrom()),s(r.getPd1586Ecc1DateTo())}, cols, y, false);
        y = drawPdfTableRow4(c, new String[]{"","ECC 2: "+s(r.getPd1586Ecc2()),s(r.getPd1586Ecc2DateFrom()),s(r.getPd1586Ecc2DateTo())}, cols, y, false);
        y = drawPdfTableRow4(c, new String[]{"","ECC 3: "+s(r.getPd1586Ecc3()),s(r.getPd1586Ecc3DateFrom()),s(r.getPd1586Ecc3DateTo())}, cols, y, false);

        y = drawPdfTableRow4(c, new String[]{"RA 6969","DENR Registry ID: "+s(r.getRa6969DenrRegistry()),s(r.getRa6969DenrDateFrom()),s(r.getRa6969DenrDateTo())}, cols, y, false);
        y = drawPdfTableRow4(c, new String[]{"","PCL Compliance Certificate","",""}, cols, y, false);
        y = drawPdfTableRow4(c, new String[]{"","Importer Clearance No.","",""}, cols, y, false);
        y = drawPdfTableRow4(c, new String[]{"","CCO Registry","",""}, cols, y, false);
        y = drawPdfTableRow4(c, new String[]{"","Permit to Transport","",""}, cols, y, false);
        y = drawPdfTableRow4(c, new String[]{"","Copy of COT issued by licensed TSD Facility","",""}, cols, y, false);

        y = drawPdfTableRow4(c, new String[]{"RA 8749","PO No.: "+s(r.getRa8749PoNo()),s(r.getRa8749PoDateFrom()),s(r.getRa8749PoDateTo())}, cols, y, false);
        y = drawPdfTableRow4(c, new String[]{"RA 9275","Discharge Permit No.: "+s(r.getRa9275DischargePermit()),s(r.getRa9275DischargeDateFrom()),s(r.getRa9275DischargeDateTo())}, cols, y, false);

        y += 10;
        return y;
    }

    private int drawPdfRecommendations(Canvas c, MonitoringRecord r, int y) {
        Paint secBg = new Paint();
        secBg.setColor(GREEN);
        c.drawRect(MARGIN_L, y - 2, MARGIN_R, y + 14, secBg);
        Paint secTxt = bold(9, Color.WHITE);
        c.drawText("4. RECOMMENDATIONS", MARGIN_L + 4, y + 10, secTxt);
        y += 18;

        Paint norm = normal(8, Color.BLACK);
        Object[][] recs = {
                {cb(r.getRecConfirmatory()),        "For confirmatory sampling"},
                {cb(r.getRecRegularMonitoring()),    "For regular monitoring"},
                {cb(r.getRecIssuanceTempRenewal()),  "For issuance of Temporary/Renewal of Permit to Operate (POA) and/or Renewal of Discharge Permit (DP)"},
                {cb(r.getRecAccreditationPco()),     "For accreditation of Pollution Control Officer (PCO)"},
                {cb(r.getRecSubmissionSmrCmr()),     "For submission of Self-Monitoring Report (SMR) / Compliance Monitoring Report (CMR)"},
                {cb(r.getRecIssuanceNomTc()),        "For issuance of Notice of Meeting (NOM) / Technical Conference (TC)"},
                {cb(r.getRecIssuanceNov()),          "For issuance of Notice of Violation (NOV)"},
                {cb(r.getRecSuspensionEcc()),        "For issuance of suspension of ECC / 5-day CDO"},
                {cb(r.getRecEndorsementPab()),       "For endorsement to Pollution Adjudication Board (PAB)"},
                {"\u2610",                           "Other recommendations: " + s(r.getRecOther())},
        };
        for (Object[] rec : recs) {
            c.drawText(rec[0] + " " + rec[1], MARGIN_L + 4, y, norm);
            y += 14;
        }
        return y + 6;
    }

    private int drawPdfSignatures(Canvas c, MonitoringRecord r, int y) {
        Paint secBg = new Paint();
        secBg.setColor(GREEN);
        c.drawRect(MARGIN_L, y - 2, MARGIN_R, y + 14, secBg);
        Paint secTxt = bold(9, Color.WHITE);
        c.drawText("SIGNATURES", MARGIN_L + 4, y + 10, secTxt);
        y += 18;

        Paint lbl = bold(8, Color.BLACK);
        Paint val = normal(8, Color.BLACK);

        c.drawText("Submitted by:",       MARGIN_L + 4, y, lbl);
        c.drawText(s(r.getSubmittedBy()), MARGIN_L + 90, y, val);
        y += 14;
        c.drawText("EMS II/EMS II",       MARGIN_L + 90, y, val);
        y += 14;

        c.drawText("Date Submitted:",           MARGIN_L + 4,   y, lbl);
        c.drawText(orBlank(r.getDateSubmitted()), MARGIN_L + 110, y, val);
        c.drawText("Date Travel Concluded:",    MARGIN_L + 310, y, lbl);
        c.drawText(orBlank(r.getDateTravelConcluded()), MARGIN_L + 450, y, val);
        y += 22;

        c.drawText("Recommending Approval/s:", MARGIN_L + 4, y, lbl);
        y += 16;
        c.drawText(s(r.getRecommendingApproval1()), MARGIN_L + 4,   y, val);
        c.drawText(s(r.getRecommendingApproval2()), MARGIN_L + 260, y, val);
        y += 14;
        c.drawText(s(r.getRecommendingApproval1Position()), MARGIN_L + 4,   y, val);
        c.drawText(s(r.getRecommendingApproval2Position()), MARGIN_L + 260, y, val);
        y += 24;

        c.drawText("Approved by:", MARGIN_L + 4, y, lbl);
        y += 16;
        c.drawText(s(r.getApprovedBy()),         MARGIN_L + 4, y, val);
        y += 14;
        c.drawText(s(r.getApprovedByPosition()), MARGIN_L + 4, y, val);
        return y;
    }

    private int drawPdfTableRow4(Canvas c, String[] cells, int[] cols, int y, boolean hdr) {
        for (int i = 0; i < 4; i++) {
            drawPdfBorderedCell(c, cells[i], cols[i], cols[i + 1], y, hdr);
        }
        return y + 14;
    }

    private void drawPdfFooter(Canvas c, int pageNum) {
        Paint rule = new Paint();
        rule.setColor(Color.LTGRAY);
        rule.setStrokeWidth(0.5f);
        c.drawLine(MARGIN_L, 758, MARGIN_R, 758, rule);
        Paint fn = normal(7, Color.GRAY);
        c.drawText("Document No. : FM-EMED-MON-01", MARGIN_L,       770, fn);
        c.drawText("Eff. Date : 01-12-26",          MARGIN_L + 200, 770, fn);
        c.drawText("Revision No. : 00",             MARGIN_L + 310, 770, fn);
        c.drawText("Page : " + pageNum + " of 2",  MARGIN_L + 420, 770, fn);
    }

    // ==================== DOCX EXPORT ====================

    public void exportDocx(MonitoringRecord rec, ExportCallback cb) {
        try {
            File file = buildFile(rec.getEmployeeId(), rec.getId(), "docx");
            XWPFDocument doc = new XWPFDocument();

            addDocxHeader(doc);
            addDocxTitle(doc, rec);
            addDocxGeneralInfo(doc, rec);
            addDocxPurpose(doc, rec);
            addDocxCompliance(doc, rec);
            addDocxRecommendations(doc, rec);
            addDocxSignatures(doc, rec);
            addDocxDocumentFooter(doc);

            FileOutputStream fos = new FileOutputStream(file);
            doc.write(fos);
            fos.close();
            doc.close();

            saveNotificationLink(file.getAbsolutePath());
            Uri uri = FileProvider.getUriForFile(
                    context, context.getPackageName() + ".fileprovider", file);
            cb.onSuccess("DOCX", file, uri);
        } catch (Exception e) {
            Log.e(TAG, "DOCX error", e);
            cb.onError("DOCX", e.getMessage());
        }
    }

    private void addDocxHeader(XWPFDocument doc) {
        try {
            int resId = context.getResources().getIdentifier(
                    "header", "drawable", context.getPackageName());
            if (resId != 0) {
                InputStream is = context.getResources().openRawResource(resId);
                XWPFParagraph hp = doc.createParagraph();
                hp.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun hr = hp.createRun();
                hr.addPicture(is, Document.PICTURE_TYPE_PNG, "header.png",
                        org.apache.poi.util.Units.toEMU(450),
                        org.apache.poi.util.Units.toEMU(80));
                is.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Header image error", e);
        }
        XWPFParagraph lp = doc.createParagraph();
        lp.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun lr = lp.createRun();
        lr.setColor(GREEN_HEX);
        lr.setFontSize(6);
        lr.setText("___________________________________________________________________________");
    }

    private void addDocxTitle(XWPFDocument doc, MonitoringRecord r) {
        XWPFParagraph tp = doc.createParagraph();
        tp.setAlignment(ParagraphAlignment.CENTER);
        tp.setSpacingBefore(100);
        XWPFRun tr = tp.createRun();
        tr.setText("INTEGRATED COMPLIANCE INSPECTION REPORT");
        tr.setBold(true);
        tr.setFontSize(14);

        XWPFParagraph ep = doc.createParagraph();
        ep.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun er = ep.createRun();
        er.setText("EMB ID No.: " + s(r.getEmbId()));
        er.setFontSize(10);

        XWPFParagraph rcp = doc.createParagraph();
        rcp.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun rcr = rcp.createRun();
        rcr.setText("Report Control: "
                + (s(r.getReportControl()).isEmpty()
                ? "__________________" : s(r.getReportControl())));
        rcr.setFontSize(10);

        String onsite = "Onsite Monitoring".equals(r.getTypeMonitoring()) ? "☒" : "☐";
        String tblMon = "Table Monitoring".equals(r.getTypeMonitoring())  ? "☒" : "☐";
        XWPFParagraph mp = doc.createParagraph();
        mp.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun mr = mp.createRun();
        mr.setText("Type of Monitoring: " + onsite
                + " Onsite Monitoring   " + tblMon + " Table Monitoring");
        mr.setFontSize(10);

        XWPFParagraph dp = doc.createParagraph();
        dp.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun dr = dp.createRun();
        dr.setText("Date of Inspection: " + s(r.getDateOfInspection()));
        dr.setFontSize(10);
    }

    // ==================== DOCX GENERAL INFORMATION ====================

    private void addDocxGeneralInfo(XWPFDocument doc, MonitoringRecord r) {

        addDocxSectionHeader(doc, "1. GENERAL INFORMATION");

        String laws   = s(r.getLaws());
        String pd1586 = laws.contains("PD-1586") ? "☒" : "☐";
        String ra6969 = laws.contains("RA-6969") ? "☒" : "☐";
        String ra9275 = laws.contains("RA-9275") ? "☒" : "☐";
        String ra8749 = laws.contains("RA-8749") ? "☒" : "☐";
        String ra9003 = laws.contains("RA-9003") ? "☒" : "☐";

        // ── Environmental Laws: header row (merged) + 2-column content row ──
        XWPFTable lawsTable = doc.createTable(2, 2);
        setTableWidth(lawsTable, 9500);

        // Header row – merge both columns
        mergeCellsHorizontal(lawsTable, 0, 0, 1);
        XWPFTableCell hdrCell = lawsTable.getRow(0).getCell(0);
        hdrCell.removeParagraph(0);
        XWPFParagraph hdrP = hdrCell.addParagraph();
        hdrP.setSpacingAfter(0);
        XWPFRun hdrR = hdrP.createRun();
        hdrR.setBold(true);
        hdrR.setFontSize(9);
        hdrR.setFontFamily("Times New Roman");
        hdrR.setText("Applicable Environmental Laws: (Pls. check box)");

        // Left column: PD-1586, RA-6969, RA-9275
        XWPFTableCell leftCell = lawsTable.getRow(1).getCell(0);
        leftCell.removeParagraph(0);
        addCellLine(leftCell, pd1586 + " PD-1586", false, 9);
        addCellLine(leftCell, ra6969 + " RA-6969 (\u2610Survey  \u2612Routine)", false, 9);
        addCellLine(leftCell, ra9275 + " RA-9275 (\u2610Survey  \u2612Routine)", false, 9);

        // Right column: RA-8749, MC 2022-003, RA-9003
        XWPFTableCell rightCell = lawsTable.getRow(1).getCell(1);
        rightCell.removeParagraph(0);
        addCellLine(rightCell, ra8749 + " RA-8749 (\u2610Survey  \u2612Routine)", false, 9);
        addCellLine(rightCell, "Covered by MC 2022-003?  \u2610YES   \u2612NO", false, 9);
        addCellLine(rightCell, ra9003 + " RA-9003", false, 9);

        styleLawsTable(lawsTable);

        // ── Main info table (4 columns) ──────────────────────────────────
        XWPFTable t = doc.createTable(7, 4);
        setTableWidth(t, 9500);

        // Row 0 – Name of Establishment | value | Geo-Coordinates | N\nE
        setCellText(t, 0, 0, "Name of Establishment:", false);
        setCellText(t, 0, 1, s(r.getNameOfEstablishment()), true);
        setCellText(t, 0, 2, "Geo-Coordinates:", false);
        XWPFTableCell geoCell = t.getRow(0).getCell(3);
        geoCell.removeParagraph(0);
        XWPFParagraph geoPara = geoCell.addParagraph();
        geoPara.setSpacingAfter(0);
        geoPara.setSpacingBefore(0);
        XWPFRun geoN = geoPara.createRun();
        geoN.setFontFamily("Times New Roman");
        geoN.setFontSize(9);
        geoN.setBold(true);
        geoN.setText(s(r.getGeoN()) + " N");
        geoN.addBreak();
        XWPFRun geoE = geoPara.createRun();
        geoE.setFontFamily("Times New Roman");
        geoE.setFontSize(9);
        geoE.setBold(true);
        geoE.setText(s(r.getGeoE()) + " E");

        // Row 1 – Proponent | value (cols 1–3 merged)
        setCellText(t, 1, 0, "Proponent", false);
        mergeCellsHorizontal(t, 1, 1, 3);
        setCellText(t, 1, 1, s(r.getProponent()), true);

        // Row 2 – Project Location | value (cols 1–3 merged)
        setCellText(t, 2, 0, "Project Location", false);
        mergeCellsHorizontal(t, 2, 1, 3);
        setCellText(t, 2, 1, s(r.getProjectLocation()), true);

        // Row 3 – Nature of Business | value (cols 1–3 merged)
        setCellText(t, 3, 0, "Nature of Business:", false);
        mergeCellsHorizontal(t, 3, 1, 3);
        setCellText(t, 3, 1, s(r.getNatureOfBusiness()), true);

        // Row 4 – Year Established | value | PSIC Code | value
        setCellText(t, 4, 0, "Year Established:", false);
        setCellText(t, 4, 1, s(r.getYearEstablish()), true);
        setCellText(t, 4, 2, "PSIC Code", false);
        setCellText(t, 4, 3, s(r.getPsicCode()), true);

        // Row 5 – Operating hrs/day (label\nvalue) | days/week | days/year (cols 2–3 merged)
        setCellTwoLine(t, 5, 0, "Operating hours/day:",  s(r.getOpHoursDay()));
        setCellTwoLine(t, 5, 1, "Operating days/week:",  s(r.getOpDayWeek()));
        mergeCellsHorizontal(t, 5, 2, 3);
        setCellTwoLine(t, 5, 2, "Operating days/year:",  s(r.getOpDayYear()));

        // Row 6 – No. of employees: value | Male: value | Female: value (cols 2–3 merged)
        setCellText(t, 6, 0, "No. of employees: " + s(r.getNumberOfEmployee()), false);
        setCellText(t, 6, 1, "Male: " + s(r.getMale()), false);
        mergeCellsHorizontal(t, 6, 2, 3);
        setCellText(t, 6, 2, "Female: " + s(r.getFemale()), false);

        styleNoTopBorder(t);

        // ── Product Lines table (separate, 3 columns) ─────────────────────
        doc.createParagraph();
        XWPFTable prodTable = doc.createTable(2, 3);
        setTableWidth(prodTable, 9500);

        setCellText(prodTable, 0, 0, "Product Lines", true);
        setCellText(prodTable, 0, 1,
                "Production Rate as Declared in the ECC (Unit/day)", true);
        setCellText(prodTable, 0, 2,
                "Actual Production Rate (Unit/day)", true);

        setCellText(prodTable, 1, 0, orBlank(r.getProductLines()),          false);
        setCellText(prodTable, 1, 1, orBlank(r.getProductionRate()),        false);
        setCellText(prodTable, 1, 2, orBlank(r.getActualProductionRate()),  false);

        styleWholeTable(prodTable);

        // ── Managing Head / PCO table (separate, 4 columns) ───────────────
        doc.createParagraph();
        XWPFTable mgmtTable = doc.createTable(4, 4);
        setTableWidth(mgmtTable, 9500);

        // Name of Managing Head
        setCellText(mgmtTable, 0, 0, "Name of Managing\nHead:", false);
        mergeCellsHorizontal(mgmtTable, 0, 1, 3);
        setCellText(mgmtTable, 0, 1, s(r.getNameOfManagingHead()), true);

        // Name of PCO
        setCellText(mgmtTable, 1, 0, "Name of PCO:", false);
        mergeCellsHorizontal(mgmtTable, 1, 1, 3);
        setCellText(mgmtTable, 1, 1, s(r.getNameOfPCO()), true);

        // PCO Accreditation No. | value | Date of Effectivity | value
        setCellText(mgmtTable, 2, 0, "PCO Accreditation\nNo.:", false);
        setCellText(mgmtTable, 2, 1, s(r.getPcoAccreditationNo()), true);
        setCellText(mgmtTable, 2, 2, "Date of Effectivity:", false);
        setCellText(mgmtTable, 2, 3, s(r.getDateOfEffectivity()), true);

        // Phone/Fax No. | value | Email Address | value
        setCellText(mgmtTable, 3, 0, "Phone/Fax No.:", false);
        setCellText(mgmtTable, 3, 1, s(r.getPhoneFaxNo()), true);
        setCellText(mgmtTable, 3, 2, "Email Address:", false);
        setCellText(mgmtTable, 3, 3, s(r.getEmailAddress()), true);

        styleWholeTable(mgmtTable);
    }

    // ==================== PURPOSE (DOCX) ====================

    private void addDocxPurpose(XWPFDocument doc, MonitoringRecord r) {
        addDocxSectionHeader(doc, "2. PURPOSE OF INSPECTION");

        String va = r.getVerifyAccuracy() == 1 ? "☒" : "☐";
        addDocxPara(doc, va + " Verify accuracy of information submitted by the establishment.", 10);

        XWPFTable pt = doc.createTable(8, 3);
        setTableWidth(pt, 9500);
        setCellText(pt, 0, 0, "Permit Type", true);
        setCellText(pt, 0, 1, "New",         true);
        setCellText(pt, 0, 2, "Renewal",     true);

        String[][] permits = {
                {"PMPIN Application",                                    yn(r.getPmpinNew()),  yn(r.getPmpinRenewal())},
                {"Hazardous Waste Generator ID Registration",            yn(r.getHwgidNew()),  yn(r.getHwgidRenewal())},
                {"Hazardous Waste Transporter Registration",             yn(r.getHwtrNew()),   yn(r.getHwtrRenewal())},
                {"Hazardous Waste TSD Registration",                     yn(r.getHwtsdNew()),  yn(r.getHwtsdRenewal())},
                {"Permit to Operate Air Pollution Control Installation", yn(r.getPoapciNew()), yn(r.getPoapciRenewal())},
                {"Discharge Permit",                                     yn(r.getDpNew()),     yn(r.getDpRenewal())},
                {"Others: " + s(r.getOtherPermit()),                    "",                   ""},
        };
        for (int i = 0; i < permits.length; i++) {
            setCellText(pt, i + 1, 0, permits[i][0], false);
            setCellText(pt, i + 1, 1, permits[i][1], false);
            setCellText(pt, i + 1, 2, permits[i][2], false);
        }
        styleWholeTable(pt);

        String dc  = r.getDetermineCompliance() == 1 ? "☒" : "☐";
        String inv = r.getInvestigate()          == 1 ? "☒" : "☐";
        String sv  = r.getSurvey()               == 1 ? "☒" : "☐";
        addDocxPara(doc, dc  + " Determine compliance status with environmental regulations.", 10);
        addDocxPara(doc, inv + " Investigate community complaints.", 10);
        addDocxPara(doc, sv  + " Survey", 10);
        addDocxPara(doc, "\u2610 Others: " + s(r.getOtherSpecify()), 10);

        XWPFTable ct = doc.createTable(1, 4);
        setTableWidth(ct, 9500);
        setCellText(ct, 0, 0, "Name of Contact Person:", false);
        setCellText(ct, 0, 1, s(r.getContactName()), true);
        setCellText(ct, 0, 2, "Position / Designation:", false);
        setCellText(ct, 0, 3, s(r.getPosition()), true);
        styleWholeTable(ct);
    }

    // ==================== COMPLIANCE (DOCX) ====================

    private void addDocxCompliance(XWPFDocument doc, MonitoringRecord r) {
        addDocxSectionHeader(doc, "3. COMPLIANCE STATUS");

        XWPFParagraph permitsTitle = doc.createParagraph();
        XWPFRun permRun = permitsTitle.createRun();
        permRun.setText("3.1 DENR Permits/Licenses/Clearances");
        permRun.setBold(true);
        permRun.setFontSize(10);

        XWPFTable pt = doc.createTable(13, 4);
        setTableWidth(pt, 9500);

        setCellText(pt, 0, 0, "Environmental Law", true);
        setCellText(pt, 0, 1, "Permits",           true);
        setCellText(pt, 0, 2, "Date of Issue",     true);
        setCellText(pt, 0, 3, "Expiry Date",       true);

        setCellText(pt, 1, 0, "PD 1586",                                       true);
        setCellText(pt, 1, 1, "ECC 1: " + s(r.getPd1586Ecc1()),                false);
        setCellText(pt, 1, 2, s(r.getPd1586Ecc1DateFrom()),                    false);
        setCellText(pt, 1, 3, s(r.getPd1586Ecc1DateTo()),                      false);

        setCellText(pt, 2, 1, "ECC 2: " + s(r.getPd1586Ecc2()),                false);
        setCellText(pt, 2, 2, s(r.getPd1586Ecc2DateFrom()),                    false);
        setCellText(pt, 2, 3, s(r.getPd1586Ecc2DateTo()),                      false);

        setCellText(pt, 3, 1, "ECC 3: " + s(r.getPd1586Ecc3()),                false);
        setCellText(pt, 3, 2, s(r.getPd1586Ecc3DateFrom()),                    false);
        setCellText(pt, 3, 3, s(r.getPd1586Ecc3DateTo()),                      false);

        setCellText(pt, 4, 0, "RA 6969",                                       true);
        setCellText(pt, 4, 1, "DENR Registry ID: " + s(r.getRa6969DenrRegistry()), false);
        setCellText(pt, 4, 2, s(r.getRa6969DenrDateFrom()),                    false);
        setCellText(pt, 4, 3, s(r.getRa6969DenrDateTo()),                      false);

        setCellText(pt, 5,  1, "PCL Compliance Certificate",                   false);
        setCellText(pt, 6,  1, "Importer Clearance No.",                       false);
        setCellText(pt, 7,  1, "CCO Registry",                                 false);
        setCellText(pt, 8,  1, "Permit to Transport",                          false);
        setCellText(pt, 9,  1, "Copy of COT issued by licensed TSD Facility",  false);

        setCellText(pt, 10, 0, "RA 8749",                                      true);
        setCellText(pt, 10, 1, "PO No.: " + s(r.getRa8749PoNo()),              false);
        setCellText(pt, 10, 2, s(r.getRa8749PoDateFrom()),                     false);
        setCellText(pt, 10, 3, s(r.getRa8749PoDateTo()),                       false);

        setCellText(pt, 11, 0, "RA 9275",                                      true);
        setCellText(pt, 11, 1, "Discharge Permit No.: " + s(r.getRa9275DischargePermit()), false);
        setCellText(pt, 11, 2, s(r.getRa9275DischargeDateFrom()),              false);
        setCellText(pt, 11, 3, s(r.getRa9275DischargeDateTo()),                false);

        setCellText(pt, 12, 1,
                "With MOA/Agreement for residuals disposed to a SLF w/ ECC: "
                        + s(r.getRa9003MoaAgreement()), false);

        styleWholeTable(pt);
    }

    // ==================== RECOMMENDATIONS (DOCX) ====================

    private void addDocxRecommendations(XWPFDocument doc, MonitoringRecord r) {
        addDocxSectionHeader(doc, "4. RECOMMENDATIONS");

        Object[][] recs = {
                {cb(r.getRecConfirmatory()),       "For confirmatory sampling"},
                {cb(r.getRecRegularMonitoring()),   "For regular monitoring"},
                {cb(r.getRecIssuanceTempRenewal()), "For issuance of Temporary/Renewal of Permit to Operate (POA) and/or Renewal of Discharge Permit (DP)"},
                {cb(r.getRecAccreditationPco()),    "For accreditation of Pollution Control Officer (PCO)"},
                {cb(r.getRecSubmissionSmrCmr()),    "For submission of Self-Monitoring Report (SMR) / Compliance Monitoring Report (CMR)"},
                {cb(r.getRecIssuanceNomTc()),       "For issuance of Notice of Meeting (NOM) / Technical Conference (TC)"},
                {cb(r.getRecIssuanceNov()),         "For issuance of Notice of Violation (NOV)"},
                {cb(r.getRecSuspensionEcc()),       "For issuance of suspension of ECC / 5-day CDO"},
                {cb(r.getRecEndorsementPab()),      "For endorsement to Pollution Adjudication Board (PAB)"},
                {"\u2610",                          "Other recommendations: " + s(r.getRecOther())},
        };
        for (Object[] rec : recs) {
            XWPFParagraph p = doc.createParagraph();
            XWPFRun run = p.createRun();
            run.setText(rec[0] + " " + rec[1]);
            run.setFontSize(10);
        }
    }

    // ==================== SIGNATURES (DOCX) ====================

    private void addDocxSignatures(XWPFDocument doc, MonitoringRecord r) {
        addDocxSectionHeader(doc, "SIGNATURES");

        XWPFTable st = doc.createTable(3, 2);
        setTableWidth(st, 9500);

        setCellText(st, 0, 0, "Submitted by:", true);
        setCellText(st, 0, 1, s(r.getSubmittedBy())
                + "  EMS II/EMS II\nDate Submitted: "
                + orBlank(r.getDateSubmitted())
                + "     Date Travel Concluded: "
                + orBlank(r.getDateTravelConcluded()), false);

        setCellText(st, 1, 0, "Recommending Approval/s:", true);
        setCellText(st, 1, 1,
                s(r.getRecommendingApproval1()) + "  "
                        + s(r.getRecommendingApproval1Position()) + "\n"
                        + s(r.getRecommendingApproval2()) + "  "
                        + s(r.getRecommendingApproval2Position()), false);

        setCellText(st, 2, 0, "Approved by:", true);
        setCellText(st, 2, 1,
                s(r.getApprovedBy()) + "\n" + s(r.getApprovedByPosition()), false);

        styleWholeTable(st);
    }

    // ==================== DOCUMENT FOOTER (DOCX) ====================

    private void addDocxDocumentFooter(XWPFDocument doc) {
        XWPFTable ft = doc.createTable(1, 6);
        setTableWidth(ft, 9500);
        setCellText(ft, 0, 0, "Document No.",   true);
        setCellText(ft, 0, 1, "FM-EMED-MON-01", false);
        setCellText(ft, 0, 2, "Eff. Date",      true);
        setCellText(ft, 0, 3, "01-12-26",        false);
        setCellText(ft, 0, 4, "Revision No.",    true);
        setCellText(ft, 0, 5, "00",              false);
        styleWholeTable(ft);
    }

    // ==================== UTILITY METHODS ====================

    private void addDocxSectionHeader(XWPFDocument doc, String title) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(200);
        XWPFRun r = p.createRun();
        r.setText(title);
        r.setBold(true);
        r.setFontSize(12);
        r.setColor(GREEN_HEX);
    }

    private XWPFParagraph addDocxPara(XWPFDocument doc, String text, int fontSize) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setText(text);
        r.setFontSize(fontSize);
        return p;
    }

    /**
     * Sets a single cell's text with the given weight.
     * Guarantees the cell has exactly one paragraph with one run.
     */
    private void setCellText(XWPFTable table, int row, int col,
                             String text, boolean bold) {
        while (table.getNumberOfRows() <= row) table.createRow();
        XWPFTableRow tr = table.getRow(row);
        while (tr.getTableCells().size() <= col) tr.createCell();
        XWPFTableCell cell = tr.getCell(col);
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        p.setSpacingAfter(0);
        p.setSpacingBefore(0);
        XWPFRun run = p.createRun();
        run.setFontFamily("Times New Roman");
        run.setFontSize(9);
        run.setBold(bold);
        run.setText(text == null || text.isEmpty() ? " " : text);
    }

    /**
     * Writes a cell whose content shows a plain label on the first line
     * and a bold value on the second line — e.g. "Operating hours/day:\n12 hours".
     */
    private void setCellTwoLine(XWPFTable table, int row, int col,
                                String label, String value) {
        XWPFTableCell cell = table.getRow(row).getCell(col);
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        p.setSpacingAfter(0);
        p.setSpacingBefore(0);

        XWPFRun labelRun = p.createRun();
        labelRun.setFontFamily("Times New Roman");
        labelRun.setFontSize(9);
        labelRun.setBold(false);
        labelRun.setText(label == null ? "" : label);
        labelRun.addBreak();

        XWPFRun valueRun = p.createRun();
        valueRun.setFontFamily("Times New Roman");
        valueRun.setFontSize(9);
        valueRun.setBold(true);
        valueRun.setText(value == null || value.isEmpty() ? " " : value);
    }

    /**
     * Appends a new paragraph (one text line) inside a table cell.
     * Used to build multi-line cells without needing addBreak().
     */
    private void addCellLine(XWPFTableCell cell, String text,
                             boolean bold, int fontSize) {
        XWPFParagraph p = cell.addParagraph();
        p.setSpacingAfter(0);
        p.setSpacingBefore(0);
        XWPFRun run = p.createRun();
        run.setFontFamily("Times New Roman");
        run.setFontSize(fontSize);
        run.setBold(bold);
        run.setText(text == null ? "" : text);
    }

    private void mergeCellsHorizontal(XWPFTable table, int row,
                                      int fromCol, int toCol) {
        for (int col = fromCol; col <= toCol; col++) {
            if (col == fromCol) {
                table.getRow(row).getCell(col)
                        .getCTTc().addNewTcPr()
                        .addNewHMerge().setVal(STMerge.RESTART);
            } else {
                table.getRow(row).getCell(col)
                        .getCTTc().addNewTcPr()
                        .addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * Styles the environmental-laws table: outer border visible on all sides,
     * internal horizontal border visible (separates header from content row),
     * but NO internal vertical border (the two law columns share no dividing line).
     */
    private void styleLawsTable(XWPFTable table) {
        // No bottom border — the main info table top border serves as the shared line
        table.setTopBorder   (XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setBottomBorder(XWPFTable.XWPFBorderType.NONE,   0, 0, "auto");
        table.setLeftBorder  (XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setRightBorder (XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        // Internal horizontal border (between header row and content row)
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        // No internal vertical border — left and right columns share no dividing line
        table.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "auto");
        table.setCellMargins(40, 40, 40, 40);
    }

    /** Like styleWholeTable but without a top border — used for tables that
     *  sit immediately below the laws table so there is only one shared line. */
    private void styleNoTopBorder(XWPFTable table) {
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setTopBorder   (XWPFTable.XWPFBorderType.NONE,   0, 0, "auto");
        table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setLeftBorder  (XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setRightBorder (XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setCellMargins(40, 40, 40, 40);
    }

    private void styleWholeTable(XWPFTable table) {
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setTopBorder   (XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setLeftBorder  (XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setRightBorder (XWPFTable.XWPFBorderType.SINGLE, 1, 0, "000000");
        table.setCellMargins(40, 40, 40, 40);
    }

    private void setTableWidth(XWPFTable table, int widthTwips) {
        CTTblWidth tblWidth = table.getCTTbl().getTblPr().addNewTblW();
        tblWidth.setType(STTblWidth.DXA);
        tblWidth.setW(BigInteger.valueOf(widthTwips));
    }

    // ==================== NOTIFICATION LINKS ====================

    public void saveNotificationLink(String filePath) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String existing = prefs.getString(KEY_LINKS, "");
        String updated  = existing.isEmpty() ? filePath : existing + "|" + filePath;
        prefs.edit().putString(KEY_LINKS, updated).apply();
    }

    public List<String> getNotificationLinks() {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String raw  = prefs.getString(KEY_LINKS, "");
        List<String> list = new ArrayList<>();
        if (!raw.isEmpty()) {
            for (String p : raw.split("\\|")) {
                if (!p.isEmpty()) list.add(p);
            }
        }
        return list;
    }

    public void clearNotificationLinks() {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().remove(KEY_LINKS).apply();
    }

    // ==================== MISC HELPERS ====================

    private File buildFile(String empId, int recId, String ext) throws IOException {
        String ts  = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String dir = "EMBR6_Exports/" + (empId != null ? empId : "unknown");
        File folder = new File(context.getExternalFilesDir(null), dir);
        if (!folder.exists() && !folder.mkdirs())
            throw new IOException("Cannot create export dir: " + folder);
        return new File(folder, "ICIR_" + recId + "_" + ts + "." + ext);
    }

    private Paint bold(float size, int color) {
        Paint p = new Paint();
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextSize(size);
        p.setColor(color);
        p.setAntiAlias(true);
        return p;
    }

    private Paint normal(float size, int color) {
        Paint p = new Paint();
        p.setTypeface(Typeface.DEFAULT);
        p.setTextSize(size);
        p.setColor(color);
        p.setAntiAlias(true);
        return p;
    }

    private String s(String v)       { return v != null ? v : ""; }
    private String orBlank(String v) { return (v == null || v.isEmpty()) ? "_______" : v; }
    private String yn(int v)         { return v == 1 ? "☒" : "☐"; }
    private String cb(int v)         { return v == 1 ? "☒" : "☐"; }
}