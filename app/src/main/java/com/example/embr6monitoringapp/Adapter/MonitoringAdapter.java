package com.example.embr6monitoringapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.R;

import java.util.List;

public class MonitoringAdapter extends RecyclerView.Adapter<MonitoringAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MonitoringRecord record);
    }

    private final Context                context;
    private final List<MonitoringRecord> records;
    private final OnItemClickListener    listener;

    public MonitoringAdapter(Context context,
                             List<MonitoringRecord> records,
                             OnItemClickListener listener) {
        this.context  = context;
        this.records  = records;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_monitoring_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        MonitoringRecord r = records.get(position);

        h.tvEmbId.setText("EMB ID: " + safe(r.getEmbId()));
        h.tvEstablishment.setText("Establishment: " + safe(r.getNameOfEstablishment()));
        h.tvType.setText("Type: " + safe(r.getTypeMonitoring()));
        h.tvLocation.setText("Location: " + safe(r.getProjectLocation()));
        h.tvDate.setText(safe(r.getDateOfInspection()));


        // Status badge

        if (r.getIsComplete() == 1) {
            h.tvStatus.setText("Completed");
            h.tvStatus.setBackgroundResource(R.drawable.badge_bg_green);
        } else {
            h.tvStatus.setText("In Progress");
            h.tvStatus.setBackgroundResource(R.drawable.badge_bg_orange);
        }

        String imgUri = r.getEvidenceImageUri();
        if (r.getIsComplete() == 1 && imgUri != null && !imgUri.isEmpty()) {
            h.ivEvidenceThumb.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(Uri.parse(imgUri))
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(h.ivEvidenceThumb);
        } else {
            h.ivEvidenceThumb.setVisibility(View.GONE);
            Glide.with(context).clear(h.ivEvidenceThumb);
        }

        // Findings preview — only for completed records that have findings text
        String findings = r.getFindings();
        if (r.getIsComplete() == 1 && findings != null && !findings.isEmpty()) {
            h.tvFindingsPreview.setVisibility(View.VISIBLE);
            String preview = findings.length() > 80
                    ? findings.substring(0, 80) + "…"
                    : findings;
            h.tvFindingsPreview.setText("Findings: " + preview);
        } else {
            h.tvFindingsPreview.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> listener.onItemClick(r));
    }

    @Override
    public int getItemCount() { return records.size(); }

    private String safe(String s) { return s != null ? s : "--"; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  tvStatus;
        TextView  tvDate;
        TextView  tvEmbId;
        TextView  tvEstablishment;
        TextView  tvType;
        TextView  tvLocation;
        TextView  tvFindingsPreview;
        ImageView ivEvidenceThumb;

        ViewHolder(@NonNull View v) {
            super(v);
            tvStatus          = v.findViewById(R.id.tvStatusBadge);
            tvDate            = v.findViewById(R.id.tvCardDate);
            tvEmbId           = v.findViewById(R.id.tvCardEmbId);
            tvEstablishment   = v.findViewById(R.id.tvCardEstablishment);
            tvType            = v.findViewById(R.id.tvCardType);
            tvLocation        = v.findViewById(R.id.tvCardLocation);
            tvFindingsPreview = v.findViewById(R.id.tvCardFindingsPreview);
            ivEvidenceThumb   = v.findViewById(R.id.ivCardEvidenceThumb);
        }
    }
}