package com.example.embr6monitoringapp.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.embr6monitoringapp.Models.MonitoringRecord;
import com.example.embr6monitoringapp.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CompletedMonitoringAdapter extends RecyclerView.Adapter<CompletedMonitoringAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MonitoringRecord record);
    }

    public interface OnExportClickListener {
        void onExportClick(MonitoringRecord record);
    }

    public interface OnArchiveClickListener {
        void onArchiveClick(MonitoringRecord record);
    }

    public interface OnRestoreClickListener {
        void onRestoreClick(MonitoringRecord record);
    }

    private List<MonitoringRecord> records;
    private final OnItemClickListener onItemClickListener;
    private final OnExportClickListener onExportClickListener;
    private final OnArchiveClickListener onArchiveClickListener;
    private final OnRestoreClickListener onRestoreClickListener;
    private boolean isArchiveMode = false;
    private int lastPosition = -1;

    public CompletedMonitoringAdapter(
            List<MonitoringRecord> records,
            OnItemClickListener onItemClickListener,
            OnExportClickListener onExportClickListener,
            OnArchiveClickListener onArchiveClickListener,
            OnRestoreClickListener onRestoreClickListener) {
        this.records = records != null ? records : new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
        this.onExportClickListener = onExportClickListener;
        this.onArchiveClickListener = onArchiveClickListener;
        this.onRestoreClickListener = onRestoreClickListener;
    }

    public void setArchiveMode(boolean isArchiveMode) {
        this.isArchiveMode = isArchiveMode;
        notifyDataSetChanged();
    }

    public void updateRecords(List<MonitoringRecord> newRecords) {
        if (newRecords == null) return;

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return records.size();
            }

            @Override
            public int getNewListSize() {
                return newRecords.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return records.get(oldItemPosition).getId() == newRecords.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                MonitoringRecord oldRecord = records.get(oldItemPosition);
                MonitoringRecord newRecord = newRecords.get(newItemPosition);
                return oldRecord.getIsArchived() == newRecord.getIsArchived();
            }
        });

        records = newRecords;
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_completed_monitoring, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonitoringRecord record = records.get(position);
        Context ctx = holder.itemView.getContext();

        // Set data with null safety
        String name = record.getNameOfEstablishment();
        holder.tvEstablishment.setText(name != null && !name.isEmpty() ? name : "No Name");

        String date = record.getDateOfInspection();
        holder.tvDate.setText(date != null && !date.isEmpty() ? date : "No Date");

        String embId = record.getEmbId();
        holder.tvEmbId.setText(embId != null && !embId.isEmpty() ? embId : "No ID");

        // Set status badge color based on date or completion status
        if (isRecentlyCompleted(date)) {
            holder.statusBadge.setBackgroundResource(R.drawable.status_badge_green);
        } else {
            holder.statusBadge.setBackgroundResource(R.drawable.status_badge_blue);
        }

        // Setup click listeners
        holder.contentLayout.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                animateClick(v);
                onItemClickListener.onItemClick(record);
            }
        });

        // Setup popup menu with different options for archive mode
        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ctx, holder.btnMore);
            if (isArchiveMode) {
                popup.getMenu().add(0, 3, 0, "Restore");
                popup.getMenu().add(0, 4, 1, "Delete Permanently");
            } else {
                popup.getMenu().add(0, 1, 0, "Export");
                popup.getMenu().add(0, 2, 1, "Archive");
            }

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1 && onExportClickListener != null) {
                    onExportClickListener.onExportClick(record);
                    animatePopupAction(holder.itemView);
                    return true;
                } else if (item.getItemId() == 2 && onArchiveClickListener != null) {
                    onArchiveClickListener.onArchiveClick(record);
                    animateArchive(holder.itemView, position);
                    return true;
                } else if (item.getItemId() == 3 && onRestoreClickListener != null) {
                    onRestoreClickListener.onRestoreClick(record);
                    animateRestore(holder.itemView, position);
                    return true;
                } else if (item.getItemId() == 4) {
                    showDeleteConfirmDialog(ctx, record, position);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // Apply entrance animation
        applyEntranceAnimation(holder.itemView, position);
    }

    private boolean isRecentlyCompleted(String date) {
        // Check if completed within last 7 days
        // Implement your logic here
        return true; // Placeholder
    }

    private void animateClick(View view) {
        view.animate()
                .scaleX(0.98f)
                .scaleY(0.98f)
                .setDuration(100)
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start())
                .start();
    }

    private void animateArchive(View view, int position) {
        view.animate()
                .alpha(0f)
                .translationX(view.getWidth())
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(1f);
                        view.setTranslationX(0f);
                        notifyItemRemoved(position);
                    }
                })
                .start();
    }

    private void animateRestore(View view, int position) {
        view.setAlpha(0f);
        view.setScaleX(0.8f);
        view.setScaleY(0.8f);
        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .start();
        notifyItemChanged(position);
    }

    private void animatePopupAction(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0, 5, -5, 3, -3, 0);
        shake.setDuration(300);
        shake.start();
    }

    private void applyEntranceAnimation(View view, int position) {
        if (position > lastPosition) {
            view.setAlpha(0f);
            view.setTranslationY(50f);
            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay(position * 50L)
                    .start();
            lastPosition = position;
        }
    }

    private void showDeleteConfirmDialog(Context ctx, MonitoringRecord record, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(ctx)
                .setTitle("Delete Permanently")
                .setMessage("Are you sure you want to permanently delete this record? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (onArchiveClickListener != null) {
                        onArchiveClickListener.onArchiveClick(record);
                        notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEstablishment, tvDate, tvEmbId;
        ImageButton btnMore;
        View statusBadge;
        ViewGroup contentLayout;
        MaterialCardView cardView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEstablishment = itemView.findViewById(R.id.tvEstablishment);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvEmbId = itemView.findViewById(R.id.tvEmbId);
            btnMore = itemView.findViewById(R.id.btnMore);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            contentLayout = itemView.findViewById(R.id.contentLayout);
            cardView = (MaterialCardView) itemView;
        }
    }
}