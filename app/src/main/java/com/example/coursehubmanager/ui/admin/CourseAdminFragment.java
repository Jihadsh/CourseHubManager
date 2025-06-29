package com.example.coursehubmanager.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.Course;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CourseAdminFragment extends Fragment {
    private AppDatabase db;
    private RecyclerView rvCourses;
    private AdminCourseAdapter adapter;
    private FloatingActionButton fabAddCourse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             ViewGroup ct, Bundle bs) {
        View v = inf.inflate(R.layout.fragment_admin_courses, ct, false);
        db = AppDatabase.getInstance(requireContext());

        rvCourses = v.findViewById(R.id.rvAdminCourses);
        rvCourses.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new AdminCourseAdapter(
                db.courseDao().getAllCourses(),
                course -> openLessonAdmin(course.getCourse_id()),   // ✅ عند النقر على الدورة
                this::showEditDialog,
                this::confirmDelete
        );

        rvCourses.setAdapter(adapter);

        fabAddCourse = v.findViewById(R.id.fabAddCourse);
        fabAddCourse.setOnClickListener(x -> showAddDialog());

        return v;
    }

    private void openLessonAdmin(int courseId) {
        Intent i = new Intent(requireContext(), AdminLessonActivity.class);
        i.putExtra("course_id", courseId);
        startActivity(i);
    }

    private void refresh() {
        adapter.setCourseList(db.courseDao().getAllCourses());
    }

    private void showAddDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText etTitle = makeField("Title");
        EditText etDesc = makeField("Description");
        EditText etInstructor = makeField("Instructor Name");
        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(etInstructor);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Course")
                .setView(layout)
                .setPositiveButton("Add", (d, i) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    String inst = etInstructor.getText().toString().trim();
                    if (title.isEmpty() || desc.isEmpty() || inst.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Course c = new Course(
                            title, desc, inst,
                            "", 0.0, 1, 1,
                            System.currentTimeMillis()
                    );
                    db.courseDao().insert(c);
                    Toast.makeText(requireContext(),
                            "Course added", Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(Course c) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText etTitle = makeField("Title");
        etTitle.setText(c.getTitle());
        EditText etDesc = makeField("Description");
        etDesc.setText(c.getDescription());
        EditText etInstructor = makeField("Instructor");
        etInstructor.setText(c.getInstructor_name());

        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(etInstructor);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Course")
                .setView(layout)
                .setPositiveButton("Save", (d, i) -> {
                    c.setTitle(etTitle.getText().toString().trim());
                    c.setDescription(etDesc.getText().toString().trim());
                    c.setInstructor_name(etInstructor.getText().toString().trim());
                    db.courseDao().update(c);
                    Toast.makeText(requireContext(),
                            "Course updated", Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(Course c) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete course?")
                .setMessage(c.getTitle())
                .setPositiveButton("Delete", (d, i) -> {
                    db.courseDao().delete(c);
                    Toast.makeText(requireContext(),
                            "Course deleted", Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private EditText makeField(String hint) {
        EditText e = new EditText(requireContext());
        e.setHint(hint);
        return e;
    }
}
