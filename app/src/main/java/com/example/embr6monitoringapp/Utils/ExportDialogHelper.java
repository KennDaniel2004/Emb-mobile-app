package com.example.embr6monitoringapp.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.R;

import java.io.File;

public class ExportDialogHelper {

    public static void show(Activity activity, MonitoringRecord record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Export Report");

        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_export, null);
        builder.setView(v);
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        ExportManager mgr = new ExportManager(activity);
        Handler uiHandler = new Handler(Looper.getMainLooper());

        ProgressBar progress = v.findViewById(R.id.exportProgress);
        TextView status = v.findViewById(R.id.tvExportStatus);

        ExportManager.ExportCallback cb = new ExportManager.ExportCallback() {
            @Override
            public void onSuccess(String format, File file, Uri shareUri) {
                uiHandler.post(() -> {
                    progress.setVisibility(View.GONE);
                    status.setVisibility(View.VISIBLE);
                    status.setText(format + " export complete: " + file.getName());
                    Toast.makeText(activity, format + " saved to device storage", Toast.LENGTH_LONG).show();
                    showOpenShareDialog(activity, format, file, shareUri);
                });
            }

            @Override
            public void onError(String format, String error) {
                uiHandler.post(() -> {
                    progress.setVisibility(View.GONE);
                    status.setVisibility(View.VISIBLE);
                    status.setText(format + " failed: " + error);
                    Toast.makeText(activity, format + " export failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        };

        v.findViewById(R.id.btnExportAll).setOnClickListener(btn -> {
            progress.setVisibility(View.VISIBLE);
            status.setVisibility(View.GONE);
            mgr.exportAll(record, cb);
        });

        v.findViewById(R.id.btnExportPdf).setOnClickListener(btn -> {
            progress.setVisibility(View.VISIBLE);
            status.setVisibility(View.GONE);
            new Thread(() -> mgr.exportPdf(record, cb)).start();
        });

        v.findViewById(R.id.btnExportWord).setOnClickListener(btn -> {
            progress.setVisibility(View.VISIBLE);
            status.setVisibility(View.GONE);
            new Thread(() -> mgr.exportDocx(record, cb)).start();
        });


    }

    private static void showOpenShareDialog(Activity activity, String format, File file, Uri shareUri) {
        String mime = format.equals("PDF") ? "application/pdf" : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        new AlertDialog.Builder(activity)
                .setTitle(format + " Export Ready")
                .setMessage("File saved to:\n" + file.getAbsolutePath())
                .setPositiveButton("Open", (d, w) -> {
                    Intent open = new Intent(Intent.ACTION_VIEW);
                    open.setDataAndType(shareUri, mime);
                    open.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(Intent.createChooser(open, "Open with…"));
                })
                .setNeutralButton("Share", (d, w) -> {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType(mime);
                    share.putExtra(Intent.EXTRA_STREAM, shareUri);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(Intent.createChooser(share, "Share via…"));
                })
                .setNegativeButton("Done", null)
                .show();
    }
}