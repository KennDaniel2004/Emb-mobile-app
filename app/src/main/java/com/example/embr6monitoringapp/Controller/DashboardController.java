package com.example.embr6monitoringapp.Controller;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;

import com.example.embr6monitoringapp.Adapter.CompletedMonitoringAdapter;
import com.example.embr6monitoringapp.Database.DatabaseConnection;
import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.R;
import com.example.embr6monitoringapp.Service.DashboardService;
import com.example.embr6monitoringapp.Service.DashboardServiceImpl;
import com.example.embr6monitoringapp.Service.MonitoringProgressService;
import com.example.embr6monitoringapp.Service.MonitoringProgressServiceImpl;
import com.example.embr6monitoringapp.Service.ProfileService;
import com.example.embr6monitoringapp.Service.ProfileServiceImpl;
import com.example.embr6monitoringapp.Utils.BadgeManager;
import com.example.embr6monitoringapp.Utils.ExportDialogHelper;
import com.example.embr6monitoringapp.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class DashboardController extends AppCompatActivity {

    private static final String TAG                     = "DashboardController";
    private static final int    SCROLL_SPEED_PX_PER_SEC = 60;

    private TextView             tvEmployeeId;
    private TextView             tvEmployeeName;
    private TextView             tvPosition;
    private ShapeableImageView   profileImage;
    private ImageView            btnChangePhoto;
    private LinearLayout         cardAddMonitor;
    private LinearLayout         cardMonitoringProgress;
    private HorizontalScrollView marqueeScroll;
    private LinearLayout         marqueeTrack;
    private RecyclerView         recyclerCompleted;
    private LinearLayout         emptyStateContainer;
    private TextView             tvCompletedCount;
    private View                 btnArchiveContainer;
    private ImageView            btnArchive;
    private View                 badgeArchive;

    private DashboardService          dashboardService;
    private ProfileService            profileService;
    private MonitoringProgressService monitoringService;
    private DatabaseConnection        dbConn;
    private BadgeManager              badgeManager;

    private CompletedMonitoringAdapter   adapter;
    private final List<MonitoringRecord> completedRecords = new ArrayList<>();

    private String        employeeId = "";
    private ValueAnimator marqueeAnimator;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null
                                && result.getData().getData() != null) {

                            Uri uri = result.getData().getData();
                            getContentResolver().takePersistableUriPermission(
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            profileService.saveProfilePhoto(employeeId, uri);
                            applyProfilePhoto(uri.toString());
                            Toast.makeText(this, "Profile photo updated!", Toast.LENGTH_SHORT).show();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        SessionManager session = SessionManager.getInstance();
        employeeId = session.getEmployeeId();
        if (employeeId == null) employeeId = "";

        dashboardService  = new DashboardServiceImpl();
        profileService    = new ProfileServiceImpl(this);
        monitoringService = new MonitoringProgressServiceImpl(this);
        dbConn            = DatabaseConnection.getInstance(this);
        badgeManager      = new BadgeManager(this);

        bindViews();
        populateUserInfo(session);
        setupCardClicks();
        setupProfileButton();
        setupArchiveButton();
        restoreProfilePhoto();
        setupCompletedRecyclerView();
        loadCompletedRecords();
        checkForNewArchives();
        startMarquee();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (marqueeAnimator != null) marqueeAnimator.resume();
        loadCompletedRecords();
        checkForNewArchives();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (marqueeAnimator != null) marqueeAnimator.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (marqueeAnimator != null) marqueeAnimator.cancel();
    }

    private void bindViews() {
        tvEmployeeId           = findViewById(R.id.tvEmployeeId);
        tvEmployeeName         = findViewById(R.id.tvEmployeeName);
        tvPosition             = findViewById(R.id.tvPosition);
        profileImage           = findViewById(R.id.profileImage);
        btnChangePhoto         = findViewById(R.id.btnChangePhoto);
        cardAddMonitor         = findViewById(R.id.cardAddMonitor);
        cardMonitoringProgress = findViewById(R.id.cardMonitoringProgress);
        marqueeScroll          = findViewById(R.id.marqueeScroll);
        marqueeTrack           = findViewById(R.id.marqueeTrack);
        recyclerCompleted      = findViewById(R.id.recyclerCompleted);
        emptyStateContainer    = findViewById(R.id.emptyStateContainer);
        tvCompletedCount       = findViewById(R.id.tvCompletedCount);
        btnArchiveContainer    = findViewById(R.id.btnArchiveContainer);

        if (btnArchiveContainer != null) {
            btnArchive  = btnArchiveContainer.findViewById(R.id.btnArchive);
            badgeArchive = btnArchiveContainer.findViewById(R.id.badgeArchive);
        }
    }

    private void setupCompletedRecyclerView() {
        if (recyclerCompleted == null) return;

        recyclerCompleted.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerCompleted.setHasFixedSize(true);

        adapter = new CompletedMonitoringAdapter(
                completedRecords,
                record -> {
                    Intent intent = new Intent(this, MonitoringDetailController.class);
                    intent.putExtra("EMPLOYEE_ID", employeeId);
                    intent.putExtra("RECORD_ID", record.getId());
                    startActivity(intent);
                },
                record -> showExportDialog(record),
                record -> archiveRecord(record),
                record -> restoreRecord(record)  // Add restore callback
        );

        recyclerCompleted.setAdapter(adapter);
    }

    private void loadCompletedRecords() {
        new Thread(() -> {
            try {
                List<MonitoringRecord> all = monitoringService.getCompletedRecordsByEmployee(employeeId);

                List<MonitoringRecord> active = new ArrayList<>();
                for (MonitoringRecord r : all) {
                    if (r.getIsArchived() != 1) active.add(r);
                }

                runOnUiThread(() -> {
                    completedRecords.clear();
                    completedRecords.addAll(active);
                    if (adapter != null) {
                        adapter.updateRecords(completedRecords);
                    }
                    updateEmptyState();
                    updateCompletedCount();
                });

            } catch (Exception e) {
                Log.e(TAG, "Error loading completed records", e);
                runOnUiThread(() -> {
                    if (emptyStateContainer != null) {
                        emptyStateContainer.setVisibility(View.VISIBLE);
                        TextView emptyText = emptyStateContainer.findViewById(R.id.tvEmptyMessage);
                        if (emptyText != null) {
                            emptyText.setText("Error loading records");
                        }
                    }
                });
            }
        }).start();
    }

    private void updateEmptyState() {
        boolean empty = completedRecords.isEmpty();
        if (recyclerCompleted != null) {
            recyclerCompleted.setVisibility(empty ? View.GONE : View.VISIBLE);
        }
        if (emptyStateContainer != null) {
            emptyStateContainer.setVisibility(empty ? View.VISIBLE : View.GONE);
        }
    }

    private void updateCompletedCount() {
        if (tvCompletedCount != null) {
            int count = completedRecords.size();
            if (count > 0) {
                tvCompletedCount.setVisibility(View.VISIBLE);
                tvCompletedCount.setText(String.valueOf(count));
            } else {
                tvCompletedCount.setVisibility(View.GONE);
            }
        }
    }

    private void archiveRecord(MonitoringRecord record) {
        new Thread(() -> {
            SQLiteDatabase db = null;
            try {
                db = dbConn.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("is_archived", 1);
                int updated = db.update("Monitoring_Records", cv,
                        "id = ?", new String[]{String.valueOf(record.getId())});

                runOnUiThread(() -> {
                    if (updated > 0) {
                        Toast.makeText(this, "Archived Successfully", Toast.LENGTH_SHORT).show();
                        loadCompletedRecords();
                        checkForNewArchives();
                    } else {
                        Toast.makeText(this, "Archive failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Archive failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
                Log.e(TAG, "Archive error", e);
            } finally {
                if (db != null) db.close();
            }
        }).start();
    }

    private void restoreRecord(MonitoringRecord record) {
        new Thread(() -> {
            SQLiteDatabase db = null;
            try {
                db = dbConn.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("is_archived", 0);
                int updated = db.update("Monitoring_Records", cv,
                        "id = ?", new String[]{String.valueOf(record.getId())});

                runOnUiThread(() -> {
                    if (updated > 0) {
                        Toast.makeText(this, "Restored Successfully", Toast.LENGTH_SHORT).show();
                        loadCompletedRecords();
                        checkForNewArchives();
                    } else {
                        Toast.makeText(this, "Restore failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Restore failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
                Log.e(TAG, "Restore error", e);
            } finally {
                if (db != null) db.close();
            }
        }).start();
    }

    private void setupArchiveButton() {
        if (btnArchive == null) return;

        btnArchive.setOnClickListener(v -> {
            if (badgeArchive != null) {
                badgeArchive.setVisibility(View.GONE);
                badgeManager.clearBadge();
            }

            Intent intent = new Intent(this, ArchiveController.class);
            intent.putExtra("EMPLOYEE_ID", employeeId);
            startActivity(intent);
        });
    }

    private void checkForNewArchives() {
        new Thread(() -> {
            int currentArchiveCount = getArchiveCount();
            int lastArchiveCount = badgeManager.getLastArchiveCount();

            runOnUiThread(() -> {
                if (badgeArchive != null) {
                    if (lastArchiveCount != -1 && currentArchiveCount > lastArchiveCount) {
                        badgeArchive.setVisibility(View.VISIBLE);
                    } else {
                        badgeArchive.setVisibility(View.GONE);
                    }
                    badgeManager.saveLastArchiveCount(currentArchiveCount);
                }
            });
        }).start();
    }

    private int getArchiveCount() {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbConn.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM Monitoring_Records WHERE employee_id = ? AND is_archived = 1",
                    new String[]{employeeId});

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting archive count: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return count;
    }

    private void showExportDialog(MonitoringRecord record) {
        if (record == null) {
            Toast.makeText(this, "No record to export", Toast.LENGTH_SHORT).show();
            return;
        }
        ExportDialogHelper.show(this, record);
    }

    private void populateUserInfo(SessionManager session) {
        if (tvEmployeeId != null)
            tvEmployeeId.setText(session.getEmployeeId() != null ? session.getEmployeeId() : "");
        if (tvEmployeeName != null)
            tvEmployeeName.setText(session.getFullName() != null ? session.getFullName() : "");
        if (tvPosition != null)
            tvPosition.setText(session.getPosition() != null ? session.getPosition() : "");
    }

    private void setupCardClicks() {
        cardAddMonitor.setOnClickListener(v -> {
            try {
                Class<?> dest = Class.forName(dashboardService.getAddMonitorDestination());
                startActivity(new Intent(this, dest)
                        .putExtra("EMPLOYEE_ID", employeeId));
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "AddMonitor class not found", e);
                Toast.makeText(this, "Unable to open Monitor Form.", Toast.LENGTH_SHORT).show();
            }
        });

        cardMonitoringProgress.setOnClickListener(v -> {
            try {
                Class<?> dest = Class.forName(dashboardService.getMonitoringProgressDestination());
                startActivity(new Intent(this, dest)
                        .putExtra("EMPLOYEE_ID", employeeId));
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "MonitoringProgress class not found", e);
                Toast.makeText(this, "Unable to open Monitoring Progress.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupProfileButton() {
        if (btnChangePhoto != null) {
            btnChangePhoto.setOnClickListener(v -> showProfileSheet());
        }
    }

    private void showProfileSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this)
                .inflate(R.layout.fragment_profile_options, null);
        sheet.setContentView(sheetView);

        View optionViewPhoto = sheetView.findViewById(R.id.optionViewPhoto);
        View optionAddPhoto = sheetView.findViewById(R.id.optionAddPhoto);

        if (optionViewPhoto != null) {
            optionViewPhoto.setOnClickListener(v -> { sheet.dismiss(); showViewPhotoDialog(); });
        }
        if (optionAddPhoto != null) {
            optionAddPhoto.setOnClickListener(v -> { sheet.dismiss(); openImagePicker(); });
        }

        sheet.show();
    }

    private void showViewPhotoDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        dialog.setContentView(R.layout.dialog_view_photo);

        ShapeableImageView ivFull = dialog.findViewById(R.id.ivFullPhoto);
        String savedUri = profileService.getSavedProfilePhoto(employeeId);

        if (savedUri != null && !savedUri.isEmpty()) {
            Glide.with(this).load(Uri.parse(savedUri))
                    .circleCrop()
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(ivFull);
        } else {
            ivFull.setImageResource(R.drawable.profile);
        }

        View btnClose = dialog.findViewById(R.id.btnCloseView);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Profile Photo"));
    }

    private void applyProfilePhoto(String uriString) {
        if (uriString == null || uriString.isEmpty()) return;
        if (profileImage != null) {
            Glide.with(this).load(Uri.parse(uriString))
                    .circleCrop()
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(profileImage);
        }
    }

    private void restoreProfilePhoto() {
        String savedUri = profileService.getSavedProfilePhoto(employeeId);
        if (savedUri != null && !savedUri.isEmpty()) applyProfilePhoto(savedUri);
    }

    private void startMarquee() {
        if (marqueeTrack == null) return;

        marqueeTrack.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        marqueeTrack.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);

                        int oneSetWidth = marqueeTrack.getWidth() / 2;
                        if (oneSetWidth <= 0) return;

                        int durationMs = (int)
                                ((oneSetWidth / (float) SCROLL_SPEED_PX_PER_SEC) * 1000);

                        marqueeAnimator = ValueAnimator.ofInt(0, oneSetWidth);
                        marqueeAnimator.setDuration(durationMs);
                        marqueeAnimator.setInterpolator(new LinearInterpolator());
                        marqueeAnimator.setRepeatCount(ValueAnimator.INFINITE);
                        marqueeAnimator.setRepeatMode(ValueAnimator.RESTART);
                        marqueeAnimator.addUpdateListener(anim -> {
                            if (marqueeScroll != null)
                                marqueeScroll.scrollTo((int) anim.getAnimatedValue(), 0);
                        });
                        marqueeAnimator.start();
                    }
                });
    }
}