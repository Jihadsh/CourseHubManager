package com.example.coursehubmanager.ui.home;

import android.content.Context;
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

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Course course);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Course course);
    }

    public interface OnEditClickListener {
        void onEditClick(Course course);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Course course);
    }

    public interface OnBookmarkClickListener {
        void onBookmarkClick(Course course);
    }

    private Context context;
    private List<Course> courseList;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;
    private OnBookmarkClickListener bookmarkClickListener;

    public CourseAdapter(Context context, List<Course> courseList, OnItemClickListener clickListener) {
        this.context = context;
        this.courseList = courseList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.tvTitle.setText(course.getTitle());
        holder.tvInstructor.setText(course.getInstructor_name());
        holder.tvDescription.setText(course.getDescription() != null ? course.getDescription() : "");
        holder.tvPrice.setText(String.format("$%.2f", course.getPrice()));

        Glide.with(context)
                .load(course.getImage_url())
                .placeholder(R.drawable.ic_course_placeholder)
                .into(holder.ivCourseImage);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(course);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(course);
                return true;
            }
            return false;
        });

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

        holder.btnBookmark.setOnClickListener(v -> {
            if (bookmarkClickListener != null) {
                bookmarkClickListener.onBookmarkClick(course);
            }
        });

        // إذا تريد تغير لون الزر بناءً على حالة معينة للـ Bookmark مثلاً:
        // boolean isBookmarked = checkBookmarkStatus(course);
        // holder.btnBookmark.setColorFilter(isBookmarked ? ContextCompat.getColor(context, R.color.colorBookmarked) : ContextCompat.getColor(context, R.color.colorNotBookmarked));
    }

    @Override
    public int getItemCount() {
        return courseList != null ? courseList.size() : 0;
    }

    public void setCourseList(List<Course> newList) {
        this.courseList = newList;
        notifyDataSetChanged();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setOnBookmarkClickListener(OnBookmarkClickListener listener) {
        this.bookmarkClickListener = listener;
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCourseImage;
        TextView tvTitle, tvInstructor, tvDescription, tvPrice;
        ImageButton btnEdit, btnDelete, btnBookmark;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCourseImage = itemView.findViewById(R.id.ivCourseImage);
            tvTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvInstructor = itemView.findViewById(R.id.tvCourseInstructor);
            tvDescription = itemView.findViewById(R.id.tvCourseDescription);
            tvPrice = itemView.findViewById(R.id.tvCoursePrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
        }
    }
}
