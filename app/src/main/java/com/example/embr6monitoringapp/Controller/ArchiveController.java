package com.example.embr6monitoringapp.Controller;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.embr6monitoringapp.Adapter.ArchiveAdapter;
import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.R;
import com.example.embr6monitoringapp.Service.ArchiveService;
import com.example.embr6monitoringapp.Service.ArchiveServiceImpl;
import com.example.embr6monitoringapp.Utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArchiveController extends AppCompatActivity {

    private static final String TAG = "ArchiveController";

    // UI Components
    private RecyclerView recyclerArchive;
    private TextView tvEmptyMessage;
    private TextView tvEmptySubtitle;
    private ImageView ivEmptyIcon;
    private TextView tvTitle;
    private CardView statsBar;
    private TextView tvTotalArchived;
    private TextView tvRecentlyArchived;
    private EditText etSearch;
    private ImageView btnClearSearch;
    private ProgressBar progressBar;
    private FloatingActionButton fabBatchAction;

    // Toolbar components
    private ImageButton btnBack;
    private ImageButton btnDelete;
    private TextView tvUnArchive;
    private TextView tvSelectAll;
    private LinearLayout layoutSelectionActions;
    private View emptyStateLayout;
    private TextView tvGoToCompleted;

    // Data & Adapter
    private ArchiveAdapter adapter;
    private List<MonitoringRecord> allRecords = new ArrayList<>();
    private List<MonitoringRecord> displayRecords = new ArrayList<>();

    // Services
    private ArchiveService archiveService;
    private ExecutorService executorService;
    private Handler mainHandler;

    // State
    private String employeeId;
    private String currentSearchQuery = "";
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        initServices();
        getEmployeeId();
        bindViews();
        setupRecyclerView();
        setupToolbarButtons();
        setupSearch();
        setupFAB();
        loadArchivedRecords();
        animateViews();
    }

    private void initServices() {
        archiveService = new ArchiveServiceImpl(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void getEmployeeId() {
        employeeId = getIntent().getStringExtra("EMPLOYEE_ID");
        if (employeeId == null || employeeId.isEmpty()) {
            employeeId = SessionManager.getInstance().getEmployeeId();
        }
        if (employeeId == null) employeeId = "";

        Log.d(TAG, "Employee ID: " + employeeId);
    }

    private void bindViews() {
        recyclerArchive = findViewById(R.id.recyclerArchive);
        tvTitle = findViewById(R.id.tvArchiveTitle);
        btnBack = findViewById(R.id.btnArchiveBack);
        btnDelete = findViewById(R.id.btnArchiveDelete);
        tvUnArchive = findViewById(R.id.tvUnArchive);
        tvSelectAll = findViewById(R.id.tvSelectAll);
        layoutSelectionActions = findViewById(R.id.layoutSelectionActions);
        statsBar = findViewById(R.id.statsBar);
        tvTotalArchived = findViewById(R.id.tvTotalArchived);
        tvRecentlyArchived = findViewById(R.id.tvRecentlyArchived);
        etSearch = findViewById(R.id.etSearchArchive);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        progressBar = findViewById(R.id.progressBar);
        fabBatchAction = findViewById(R.id.fabBatchAction);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);


        // Empty state views
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        tvEmptySubtitle = findViewById(R.id.tvEmptySubtitle);
        ivEmptyIcon = findViewById(R.id.ivEmptyIcon);
    }

    private void setupRecyclerView() {
        adapter = new ArchiveAdapter(
                displayRecords,
                this::onItemClick,
                this::onItemMenuClick,
                position -> onItemLongClick()
        );

        recyclerArchive.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerArchive.setAdapter(adapter);

        // Hide FAB on scroll
        recyclerArchive.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fabBatchAction.isShown()) {
                    fabBatchAction.hide();
                } else if (dy < 0 && !fabBatchAction.isShown() && !adapter.isSelectionMode()) {
                    fabBatchAction.show();
                }
            }
        });
    }

    private void onItemClick(MonitoringRecord record) {
        if (!adapter.isSelectionMode()) {
            Intent intent = new Intent(this, MonitoringDetailController.class);
            intent.putExtra("EMPLOYEE_ID", employeeId);
            intent.putExtra("RECORD_ID", record.getId());
            startActivity(intent);
        }
    }

    private void onItemMenuClick(MonitoringRecord record) {
        showItemMenu(record);
    }

    private void onItemLongClick() {
        updateSelectionUI();
    }

    private void showItemMenu(MonitoringRecord record) {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.ivMenu));
        popupMenu.getMenu().add(0, 1, 0, "Unarchive");
        popupMenu.getMenu().add(0, 2, 1, "Delete");
        popupMenu.getMenu().add(0, 3, 2, "View Details");

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    confirmSingleUnarchive(record);
                    return true;
                case 2:
                    confirmSingleDelete(record);
                    return true;
                case 3:
                    Intent intent = new Intent(this, MonitoringDetailController.class);
                    intent.putExtra("EMPLOYEE_ID", employeeId);
                    intent.putExtra("RECORD_ID", record.getId());
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }

    private void setupToolbarButtons() {
        btnBack.setOnClickListener(v -> {
            if (adapter.isSelectionMode()) {
                exitSelectionMode();
            } else {
                finish();
            }
        });

        btnDelete.setOnClickListener(v -> confirmBatchDelete());

        tvUnArchive.setOnClickListener(v -> confirmBatchUnarchive());

        tvSelectAll.setOnClickListener(v -> {
            if (adapter.getSelectedCount() == displayRecords.size()) {
                adapter.clearSelection();
            } else {
                adapter.selectAll();
            }
            updateSelectionUI();
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> performSearch(s.toString());
                searchHandler.postDelayed(searchRunnable, 300);

                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            performSearch("");
        });
    }

    private void performSearch(String query) {
        currentSearchQuery = query;
        showProgress(true);

        executorService.execute(() -> {
            List<MonitoringRecord> results;
            if (query.isEmpty()) {
                results = new ArrayList<>(allRecords);
            } else {
                results = archiveService.searchArchivedRecords(employeeId, query);
            }

            mainHandler.post(() -> {
                displayRecords.clear();
                displayRecords.addAll(results);
                adapter.notifyDataSetChanged();
                updateEmptyState(); // Update empty state based on results
                updateStats();
                showProgress(false);
            });
        });
    }

    private void setupFAB() {
        fabBatchAction.setOnClickListener(v -> {
            if (!adapter.isSelectionMode()) {
                adapter.setSelectionMode(true);
                adapter.selectAll();
                updateSelectionUI();
                animateSelectionBar();
            }
        });
    }

    private void animateSelectionBar() {
        if (layoutSelectionActions.getVisibility() != View.VISIBLE) {
            layoutSelectionActions.setVisibility(View.VISIBLE);
            layoutSelectionActions.setAlpha(0f);
            layoutSelectionActions.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();
        }
    }

    private void loadArchivedRecords() {
        showProgress(true);

        executorService.execute(() -> {
            List<MonitoringRecord> records = archiveService.getArchivedRecords(employeeId);

            mainHandler.post(() -> {
                allRecords.clear();
                allRecords.addAll(records);
                displayRecords.clear();
                displayRecords.addAll(records);
                adapter.notifyDataSetChanged();
                updateStats();
                updateEmptyState(); // Update empty state based on loaded data
                showProgress(false);
            });
        });
    }

    private void updateStats() {
        if (statsBar != null && !allRecords.isEmpty()) {
            statsBar.setVisibility(View.VISIBLE);
            tvTotalArchived.setText(String.valueOf(allRecords.size()));

            executorService.execute(() -> {
                int recentlyCount = archiveService.getRecentlyArchivedCount(employeeId);
                mainHandler.post(() -> tvRecentlyArchived.setText(String.valueOf(recentlyCount)));
            });
        } else if (statsBar != null) {
            statsBar.setVisibility(View.GONE);
        }
    }

    private void updateEmptyState() {
        boolean isEmpty = displayRecords.isEmpty();

        if (isEmpty) {
            // Show empty state with proper message
            recyclerArchive.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);

            // Get empty state message from service
            String message = archiveService.getEmptyStateMessage(currentSearchQuery);

            if (tvEmptyMessage != null) {
                if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
                    tvEmptyMessage.setText("No Results Found");
                    if (tvEmptySubtitle != null) {
                        tvEmptySubtitle.setText(message);
                    }
                } else {
                    tvEmptyMessage.setText("Archive is Empty");
                    if (tvEmptySubtitle != null) {
                        tvEmptySubtitle.setText("Records you archive will appear here");
                    }
                }
            }

            // Set empty icon
            if (ivEmptyIcon != null) {
                ivEmptyIcon.setImageResource(archiveService.getEmptyStateIcon());
            }

            // Hide stats bar when empty
            if (statsBar != null) {
                statsBar.setVisibility(View.GONE);
            }

            // Show FAB only if there are records (but there aren't, so hide it)
            fabBatchAction.setVisibility(View.GONE);

        } else {
            // Show recycler view with records
            recyclerArchive.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);

            // Show FAB for batch actions
            if (!adapter.isSelectionMode()) {
                fabBatchAction.setVisibility(View.VISIBLE);
            }

            // Show stats bar if there are records
            if (statsBar != null && !allRecords.isEmpty()) {
                statsBar.setVisibility(View.VISIBLE);
            }
        }
    }

    private void confirmSingleUnarchive(MonitoringRecord record) {
        new AlertDialog.Builder(this)
                .setTitle("Unarchive Record")
                .setMessage("Move \"" + record.getNameOfEstablishment() + "\" back to Monitoring Completed?")
                .setPositiveButton("Yes", (d, w) -> unarchiveSingle(record))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmSingleDelete(MonitoringRecord record) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Permanently delete \"" + record.getNameOfEstablishment() + "\"? This cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> deleteSingle(record))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmBatchUnarchive() {
        int count = adapter.getSelectedCount();
        if (count == 0) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Unarchive Records")
                .setMessage("Move " + count + " record(s) back to Monitoring Completed?")
                .setPositiveButton("Yes", (d, w) -> batchUnarchive())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmBatchDelete() {
        int count = adapter.getSelectedCount();
        if (count == 0) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Records")
                .setMessage("Permanently delete " + count + " record(s)? This cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> batchDelete())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void unarchiveSingle(MonitoringRecord record) {
        showProgress(true);

        executorService.execute(() -> {
            boolean success = archiveService.unarchiveRecord(record.getId());

            mainHandler.post(() -> {
                showProgress(false);
                if (success) {
                    Toast.makeText(this, "Record unarchived successfully", Toast.LENGTH_SHORT).show();
                    loadArchivedRecords();
                } else {
                    Toast.makeText(this, "Failed to unarchive record", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void deleteSingle(MonitoringRecord record) {
        showProgress(true);

        executorService.execute(() -> {
            boolean success = archiveService.deleteRecordPermanently(record.getId());

            mainHandler.post(() -> {
                showProgress(false);
                if (success) {
                    Toast.makeText(this, "Record deleted permanently", Toast.LENGTH_SHORT).show();
                    loadArchivedRecords();
                } else {
                    Toast.makeText(this, "Failed to delete record", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void batchUnarchive() {
        Set<Integer> positions = adapter.getSelectedPositions();
        if (positions.isEmpty()) return;

        List<Integer> recordIds = new ArrayList<>();
        for (int pos : positions) {
            if (pos >= 0 && pos < displayRecords.size()) {
                recordIds.add(displayRecords.get(pos).getId());
            }
        }

        showProgress(true);

        executorService.execute(() -> {
            int successCount = archiveService.batchUnarchiveRecords(recordIds);

            mainHandler.post(() -> {
                showProgress(false);
                Toast.makeText(this, successCount + " record(s) unarchived successfully", Toast.LENGTH_SHORT).show();
                exitSelectionMode();
                loadArchivedRecords();
            });
        });
    }

    private void batchDelete() {
        Set<Integer> positions = adapter.getSelectedPositions();
        if (positions.isEmpty()) return;

        List<Integer> recordIds = new ArrayList<>();
        for (int pos : positions) {
            if (pos >= 0 && pos < displayRecords.size()) {
                recordIds.add(displayRecords.get(pos).getId());
            }
        }

        showProgress(true);

        executorService.execute(() -> {
            int successCount = archiveService.batchDeleteRecordsPermanently(recordIds);

            mainHandler.post(() -> {
                showProgress(false);
                Toast.makeText(this, successCount + " record(s) deleted permanently", Toast.LENGTH_SHORT).show();
                exitSelectionMode();
                loadArchivedRecords();
            });
        });
    }

    private void exitSelectionMode() {
        adapter.setSelectionMode(false);
        updateSelectionUI();
        fabBatchAction.show();
    }

    private void updateSelectionUI() {
        boolean inSelection = adapter.isSelectionMode();
        int selectedCount = adapter.getSelectedCount();

        layoutSelectionActions.setVisibility(inSelection ? View.VISIBLE : View.GONE);
        fabBatchAction.setVisibility(inSelection ? View.GONE : View.VISIBLE);

        if (inSelection) {
            tvTitle.setText(selectedCount + " selected");
            tvSelectAll.setText((selectedCount == displayRecords.size() && displayRecords.size() > 0)
                    ? "Deselect all" : "Select all");
            btnBack.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        } else {
            tvTitle.setText("Archive");
            btnBack.setImageResource(R.drawable.ic_arrow_back);
        }
    }

    private void animateViews() {
        if (statsBar != null) {
            statsBar.setAlpha(0f);
            statsBar.setVisibility(View.VISIBLE);
            statsBar.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }
    }

    private void showProgress(boolean show) {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (adapter.isSelectionMode()) {
            exitSelectionMode();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        if (searchHandler != null) {
            searchHandler.removeCallbacksAndMessages(null);
        }
    }
}