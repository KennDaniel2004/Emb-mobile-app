package com.example.embr6monitoringapp.Utils;

import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.embr6monitoringapp.R;

public class FindingsSection {

    public final TextView   tvTitle;
    public final RadioGroup rgStatus;
    public final RadioButton rbCompliant, rbNonCompliant, rbNotApplicable;
    public final EditText   etFindings;

    public final FrameLayout frameImage1, frameImage2;
    public final ImageView   ivImage1, ivImage2;
    public final ImageView   ivUploadIcon1, ivUploadIcon2;
    public final EditText    etCaption1, etCaption2;

    private String imageUri1 = null;
    private String imageUri2 = null;

    public FindingsSection(View root, String title, boolean hasImages) {
        tvTitle         = root.findViewById(R.id.tvSectionTitle);
        rgStatus        = root.findViewById(R.id.rgStatus);
        rbCompliant     = root.findViewById(R.id.rbCompliant);
        rbNonCompliant  = root.findViewById(R.id.rbNonCompliant);
        rbNotApplicable = root.findViewById(R.id.rbNotApplicable);
        etFindings      = root.findViewById(R.id.etFindings);

        if (hasImages) {
            frameImage1   = root.findViewById(R.id.frameImage1);
            frameImage2   = root.findViewById(R.id.frameImage2);
            ivImage1      = root.findViewById(R.id.ivImage1);
            ivImage2      = root.findViewById(R.id.ivImage2);
            ivUploadIcon1 = root.findViewById(R.id.ivUploadIcon1);
            ivUploadIcon2 = root.findViewById(R.id.ivUploadIcon2);
            etCaption1    = root.findViewById(R.id.etCaption1);
            etCaption2    = root.findViewById(R.id.etCaption2);
        } else {
            frameImage1 = frameImage2 = null;
            ivImage1 = ivImage2 = null;
            ivUploadIcon1 = ivUploadIcon2 = null;
            etCaption1 = etCaption2 = null;
        }

        if (tvTitle != null) tvTitle.setText(title);
    }


    public String getStatus() {
        if (rbCompliant != null    && rbCompliant.isChecked())    return "Compliant";
        if (rbNonCompliant != null && rbNonCompliant.isChecked()) return "Non-compliant";
        if (rbNotApplicable != null && rbNotApplicable.isChecked()) return "Not applicable";
        return "";
    }

    public void setStatus(String status) {
        if (status == null) return;
        switch (status) {
            case "Compliant":      if (rbCompliant    != null) rbCompliant.setChecked(true);    break;
            case "Non-compliant":  if (rbNonCompliant != null) rbNonCompliant.setChecked(true); break;
            case "Not applicable": if (rbNotApplicable != null) rbNotApplicable.setChecked(true); break;
        }
    }


    public String getFindings() {
        return etFindings != null ? etFindings.getText().toString().trim() : "";
    }


    public String getImageUri1() { return imageUri1; }
    public void   setImageUri1(String uri) { this.imageUri1 = uri; }

    public String getImageUri2() { return imageUri2; }
    public void   setImageUri2(String uri) { this.imageUri2 = uri; }
}