package com.example.embr6monitoringapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MonitoringRecord record);
    }

    public interface OnItemMenuClickListener {
        void onMenuClick(MonitoringRecord record);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    private final List<MonitoringRecord> records;
    private final OnItemClickListener clickListener;
    private final OnItemMenuClickListener menuClickListener;
    private final OnItemLongClickListener longClickListener;

    private boolean selectionMode = false;
    private final Set<Integer> selectedPositions = new HashSet<>();

    public ArchiveAdapter(List<MonitoringRecord> records,
                          OnItemClickListener clickListener,
                          OnItemMenuClickListener menuClickListener,
                          OnItemLongClickListener longClickListener) {
        this.records = records;
        this.clickListener = clickListener;
        this.menuClickListener = menuClickListener;
        this.longClickListener = longClickListener;
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean enabled) {
        selectionMode = enabled;
        if (!enabled) {
            selectedPositions.clear();
        }
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position);
        } else {
            selectedPositions.add(position);
        }
        notifyItemChanged(position);
    }

    public void selectAll() {
        selectedPositions.clear();
        for (int i = 0; i < records.size(); i++) {
            selectedPositions.add(i);
        }
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedPositions.clear();
        notifyDataSetChanged();
    }

    public Set<Integer> getSelectedPositions() {
        return new HashSet<>(selectedPositions);
    }

    public int getSelectedCount() {
        return selectedPositions.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_archive_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonitoringRecord record = records.get(position);

        // Set text data
        String name = record.getNameOfEstablishment();
        holder.tvName.setText((name != null && !name.isEmpty()) ? name : "No Name");

        String date = record.getDateOfInspection();
        holder.tvDate.setText((date != null && !date.isEmpty()) ? date : "--");

        String embId = record.getEmbId();
        if (embId != null && !embId.isEmpty()) {
            holder.tvEmbIdValue.setText(embId);
        } else {
            holder.tvEmbIdValue.setText("No ID");
        }

        // Handle selection mode
        if (selectionMode) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(selectedPositions.contains(position));
            holder.ivMenu.setVisibility(View.GONE);

            if (selectedPositions.contains(position)) {
                holder.itemView.setAlpha(0.7f);
            } else {
                holder.itemView.setAlpha(1.0f);
            }
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.ivMenu.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (selectionMode) {
                toggleSelection(holder.getAdapterPosition());
            } else {
                if (clickListener != null) {
                    clickListener.onItemClick(record);
                }
            }
        });

        // Menu click listener
        holder.ivMenu.setOnClickListener(v -> {
            if (!selectionMode && menuClickListener != null) {
                menuClickListener.onMenuClick(record);
            }
        });

        // Long press listener
        holder.itemView.setOnLongClickListener(v -> {
            if (!selectionMode) {
                setSelectionMode(true);
                toggleSelection(holder.getAdapterPosition());
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(holder.getAdapterPosition());
                }
                return true;
            }
            return false;
        });

        // Checkbox click
        holder.checkbox.setOnClickListener(v -> {
            toggleSelection(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvEmbIdValue;
        CheckBox checkbox;
        ImageView ivMenu;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvArchiveName);
            tvDate = itemView.findViewById(R.id.tvArchiveDate);
            tvEmbIdValue = itemView.findViewById(R.id.tvEmbIdValue);
            checkbox = itemView.findViewById(R.id.cbArchiveSelect);
            ivMenu = itemView.findViewById(R.id.ivMenu);
        }
    }
}