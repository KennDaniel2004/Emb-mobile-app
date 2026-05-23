package com.example.embr6monitoringapp.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.embr6monitoringapp.Adapter.MonitoringAdapter;
import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.R;
import com.example.embr6monitoringapp.Service.MonitoringProgressService;
import com.example.embr6monitoringapp.Service.MonitoringProgressServiceImpl;
import com.example.embr6monitoringapp.Utils.SessionManager;

import java.util.List;

public class MonitoringProgressController extends AppCompatActivity {

    private RecyclerView              recycler;
    private TextView                  tvEmpty;
    private TextView                  tvTotalCount;
    private TextView                  tvCompletedCount;
    private TextView                  tvInProgressCount;

    private MonitoringProgressService service;
    private String                    employeeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_progress);

        employeeId = getIntent().getStringExtra("EMPLOYEE_ID");
        if (employeeId == null || employeeId.isEmpty()) {
            employeeId = SessionManager.getInstance().getEmployeeId();
        }
        if (employeeId == null) employeeId = "";

        service = new MonitoringProgressServiceImpl(this);

        recycler          = findViewById(R.id.recyclerMonitoring);
        tvEmpty           = findViewById(R.id.tvEmpty);
        tvTotalCount      = findViewById(R.id.tvTotalCount);
        tvCompletedCount  = findViewById(R.id.tvCompletedCount);
        tvInProgressCount = findViewById(R.id.tvInProgressCount);

        recycler.setLayoutManager(new LinearLayoutManager(this));


        findViewById(R.id.btnBack).setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MonitoringProgressController.this, DashboardController.class);
                intent.putExtra("EMPLOYEE_ID", employeeId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecords();
    }

    private void loadRecords() {
        List<MonitoringRecord> records = service.getRecordsForEmployee(employeeId);

        if (tvTotalCount != null) {
            int total      = records.size();
            int completed  = 0;
            int inProgress = 0;
            for (MonitoringRecord r : records) {
                if (r.getIsComplete() == 1) completed++;
                else                        inProgress++;
            }
            tvTotalCount.setText(String.valueOf(total));
            tvCompletedCount.setText(String.valueOf(completed));
            tvInProgressCount.setText(String.valueOf(inProgress));
        }

        if (records.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }

        tvEmpty.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);

        MonitoringAdapter adapter = new MonitoringAdapter(
                this,
                records,
                record -> {
                    Intent intent = new Intent(this, MonitoringDetailController.class);
                    intent.putExtra("EMPLOYEE_ID", employeeId);
                    intent.putExtra("RECORD_ID",   record.getId());
                    startActivity(intent);
                }
        );
        recycler.setAdapter(adapter);
    }
}