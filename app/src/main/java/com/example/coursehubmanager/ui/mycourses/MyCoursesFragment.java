package com.example.coursehubmanager.ui.mycourses;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.Course;
import com.example.coursehubmanager.room.entities.Enrollment;
import com.example.coursehubmanager.ui.home.CourseAdapter;
import com.example.coursehubmanager.ui.home.CourseContentActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyCoursesFragment extends Fragment {
    private RecyclerView rvOngoing, rvCompleted;
    private CourseAdapter ongoingAdapter, completedAdapter;
    private AppDatabase db;
    private int userId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater li, ViewGroup cv, Bundle bs) {
        View v = li.inflate(R.layout.fragment_my_courses, cv, false);

        db = AppDatabase.getInstance(requireContext());

        userId = getActivity()
                .getSharedPreferences("coursehub_prefs", Context.MODE_PRIVATE)
                .getInt("user_id", -1);

        rvOngoing = v.findViewById(R.id.rvOngoing);
        rvCompleted = v.findViewById(R.id.rvCompleted);
        TabLayout tabs = v.findViewById(R.id.tabLayoutStatus);

        rvOngoing.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCompleted.setLayoutManager(new LinearLayoutManager(getContext()));

        // ✅ تم التعديل هنا لفتح CourseContentActivity عند الضغط
        ongoingAdapter = new CourseAdapter(requireContext(), new ArrayList<>(), course ->
                CourseContentActivity.start(requireContext(), course.getCourse_id())
        );

        completedAdapter = new CourseAdapter(requireContext(), new ArrayList<>(), course ->
                CourseContentActivity.start(requireContext(), course.getCourse_id())
        );

        rvOngoing.setAdapter(ongoingAdapter);
        rvCompleted.setAdapter(completedAdapter);

        rvOngoing.setVisibility(View.VISIBLE);
        rvCompleted.setVisibility(View.GONE);

        if (userId == -1) {
            Toast.makeText(getContext(), "خطأ: المستخدم غير مسجل دخول!", Toast.LENGTH_SHORT).show();
            return v;
        }

        loadEnrollments();

        tabs.addTab(tabs.newTab().setText("Ongoing"));
        tabs.addTab(tabs.newTab().setText("Completed"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    rvOngoing.setVisibility(View.VISIBLE);
                    rvCompleted.setVisibility(View.GONE);
                } else {
                    rvOngoing.setVisibility(View.GONE);
                    rvCompleted.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return v;
    }

    private void loadEnrollments() {
        List<Enrollment> all = db.enrollmentDao().getByUser(userId);
        List<Course> ongoing = new ArrayList<>(), completed = new ArrayList<>();
        for (Enrollment e : all) {
            Course c = db.courseDao().getById(e.getCourse_id());
            if (c == null) continue;
            if ("completed".equalsIgnoreCase(e.getStatus())) completed.add(c);
            else ongoing.add(c);
        }
        ongoingAdapter.setCourseList(ongoing);
        completedAdapter.setCourseList(completed);
    }
}
