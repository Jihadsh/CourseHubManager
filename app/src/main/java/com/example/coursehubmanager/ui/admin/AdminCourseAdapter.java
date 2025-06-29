package com.example.coursehubmanager.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.entities.Course;

import java.util.List;

public class AdminCourseAdapter extends RecyclerView.Adapter<AdminCourseAdapter.CourseViewHolder> {

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public interface OnEditClickListener {
        void onEditClick(Course course);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Course course);
    }

    private List<Course> courseList;
    private OnCourseClickListener courseClickListener;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;

    public AdminCourseAdapter(List<Course> courseList,
                              OnCourseClickListener courseClickListener,
                              OnEditClickListener editClickListener,
                              OnDeleteClickListener deleteClickListener) {
        this.courseList = courseList;
        this.courseClickListener = courseClickListener;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.tvTitle.setText(course.getTitle());
        holder.tvInstructor.setText(course.getInstructor_name());
        holder.tvDescription.setText(course.getDescription());
        holder.tvPrice.setText(String.format("$%.2f", course.getPrice()));

        Glide.with(holder.itemView.getContext())
                .load(course.getImage_url())
                .placeholder(R.drawable.ic_course_placeholder)
                .into(holder.ivCourseImage);

        holder.btnEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(course);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(course);
            }
        });

        // ✅ حدث الضغط على الدورة نفسها
        holder.itemView.setOnClickListener(v -> {
            if (courseClickListener != null) {
                courseClickListener.onCourseClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList != null ? courseList.size() : 0;
    }

    public void setCourseList(List<Course> newList) {
        this.courseList = newList;
        notifyDataSetChanged();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCourseImage;
        TextView tvTitle, tvInstructor, tvDescription, tvPrice;
        ImageButton btnEdit, btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCourseImage = itemView.findViewById(R.id.ivCourseImage);
            tvTitle       = itemView.findViewById(R.id.tvCourseTitle);
            tvInstructor  = itemView.findViewById(R.id.tvCourseInstructor);
            tvDescription = itemView.findViewById(R.id.tvCourseDescription);
            tvPrice       = itemView.findViewById(R.id.tvCoursePrice);
            btnEdit       = itemView.findViewById(R.id.btnEdit);
            btnDelete     = itemView.findViewById(R.id.btnDelete);
        }
    }
}
