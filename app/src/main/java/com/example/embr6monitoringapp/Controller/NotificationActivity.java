package com.example.embr6monitoringapp.Controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.core.content.FileProvider;

import com.example.embr6monitoringapp.R;
import com.example.embr6monitoringapp.Utils.ExportManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView     tvEmpty;
    private ExportManager exportManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        exportManager = new ExportManager(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerNotifications);
        tvEmpty      = findViewById(R.id.tvNotificationEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadList();

        // Clear all button
        View btnClear = findViewById(R.id.btnClearNotifications);
        if (btnClear != null) {
            btnClear.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Clear all notifications?")
                        .setMessage("This will remove all export history entries " +
                                "(files on device are kept).")
                        .setPositiveButton("Clear", (d, w) -> {
                            exportManager.clearNotificationLinks();
                            loadList();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    private void loadList() {
        List<String> links = exportManager.getNotificationLinks();
        if (links.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setAdapter(new NotificationAdapter(links));
        }
    }

    private class NotificationAdapter
            extends RecyclerView.Adapter<NotificationAdapter.VH> {

        private final List<String> paths;

        NotificationAdapter(List<String> paths) { this.paths = paths; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            String path = paths.get(pos);
            File   file = new File(path);

            h.tvFileName.setText(file.getName());
            h.tvFilePath.setText(shortenPath(path));
            String ext = getExt(file.getName()).toUpperCase(Locale.getDefault());
            h.tvBadge.setText(ext);
            switch (ext) {
                case "PDF":
                    h.tvBadge.setBackgroundColor(0xFFD32F2F); break;
                case "DOCX":
                    h.tvBadge.setBackgroundColor(0xFF1565C0); break;
                case "XLSX":
                    h.tvBadge.setBackgroundColor(0xFF2E7D32); break;
                default:
                    h.tvBadge.setBackgroundColor(0xFF616161); break;
            }

            // File size / date
            if (file.exists()) {
                String date = new SimpleDateFormat("MMM dd, yyyy HH:mm",
                        Locale.getDefault()).format(new Date(file.lastModified()));
                h.tvFileDate.setText(date + "  •  " + formatSize(file.length()));
            } else {
                h.tvFileDate.setText("File not found");
                h.tvBadge.setBackgroundColor(0xFF9E9E9E);
            }

            // Tap → open
            h.itemView.setOnClickListener(v -> openFile(path, ext));

            // Long-press → share or delete
            h.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(NotificationActivity.this)
                        .setTitle(file.getName())
                        .setItems(new String[]{"Open", "Share", "Remove from list"},
                                (d, w) -> {
                                    switch (w) {
                                        case 0: openFile(path, ext);  break;
                                        case 1: shareFile(path, ext); break;
                                        case 2:
                                            paths.remove(pos);
                                            notifyItemRemoved(pos);
                                            rebuildPrefs();
                                            break;
                                    }
                                })
                        .show();
                return true;
            });
        }

        @Override public int getItemCount() { return paths.size(); }

        private void rebuildPrefs() {
            exportManager.clearNotificationLinks();
            for (String p : paths) exportManager.saveNotificationLink(p);
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvFileName, tvFilePath, tvBadge, tvFileDate;
            VH(View v) {
                super(v);
                tvFileName = v.findViewById(R.id.tvNotifFileName);
                tvFilePath = v.findViewById(R.id.tvNotifPath);
                tvBadge    = v.findViewById(R.id.tvNotifBadge);
                tvFileDate = v.findViewById(R.id.tvNotifDate);
            }
        }
    }

    private void openFile(String path, String ext) {
        File file = new File(path);
        if (!file.exists()) {
            new AlertDialog.Builder(this)
                    .setMessage("File not found: " + path)
                    .setPositiveButton("OK", null).show();
            return;
        }
        Uri uri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, getMime(ext));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Open with…"));
    }

    private void shareFile(String path, String ext) {
        File file = new File(path);
        if (!file.exists()) return;
        Uri uri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", file);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType(getMime(ext));
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, "Share via…"));
    }

    private String getMime(String ext) {
        switch (ext.toUpperCase(Locale.getDefault())) {
            case "PDF":  return "application/pdf";
            case "DOCX": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "XLSX": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default:     return "*/*";
        }
    }

    private String getExt(String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : "";
    }

    private String shortenPath(String path) {
        int idx = path.indexOf("EMBR6_Exports");
        return idx >= 0 ? "…/" + path.substring(idx) : path;
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024));
    }
}