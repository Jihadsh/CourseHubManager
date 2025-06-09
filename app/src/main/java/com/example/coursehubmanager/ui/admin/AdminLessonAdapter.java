package com.example.coursehubmanager.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.entities.Lesson;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminLessonAdapter extends RecyclerView.Adapter<AdminLessonAdapter.Holder> {
    public interface OnEditClick { void onEdit(Lesson lesson); }
    public interface OnDeleteClick { void onDelete(Lesson lesson); }

    private List<Lesson> lessons;
    private OnEditClick editListener;
    private OnDeleteClick deleteListener;

    public AdminLessonAdapter(List<Lesson> lessons,
                              OnEditClick editListener,
                              OnDeleteClick deleteListener) {
        this.lessons = lessons;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Lesson l = lessons.get(pos);
        h.tvTitle.setText(l.getTitle());
        h.tvDesc.setText(l.getDescription());
        h.itemView.setOnClickListener(v -> {
            if (editListener != null) editListener.onEdit(l);
        });
        h.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(l);
            return true;
        });
    }

    @Override public int getItemCount() { return lessons.size(); }

    public void setLessons(List<Lesson> list) {
        this.lessons = list;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        Holder(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvLessonTitle);
            tvDesc  = v.findViewById(R.id.tvLessonDesc);
        }
    }
}
