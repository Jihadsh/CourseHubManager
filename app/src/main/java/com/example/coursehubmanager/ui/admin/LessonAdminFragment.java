package com.example.coursehubmanager.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.Course;
import com.example.coursehubmanager.room.entities.Lesson;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LessonAdminFragment extends Fragment {
    private Spinner spinnerCourses;
    private RecyclerView rvLessons;
    private FloatingActionButton fabAddLesson;
    private AppDatabase db;
    private List<Course> courseList;
    private int selectedCourseId;
    private AdminLessonAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             ViewGroup ct, Bundle bs) {
        View v = inf.inflate(R.layout.fragment_admin_lessons, ct, false);
        spinnerCourses = v.findViewById(R.id.spinnerCourses);
        rvLessons      = v.findViewById(R.id.rvAdminLessons);
        fabAddLesson   = v.findViewById(R.id.fabAddLesson);
        db             = AppDatabase.getInstance(requireContext());

        // 1) جلب كل الدورات من قاعدة البيانات
        courseList = db.courseDao().getAllCourses();

        // 2) بناء قائمة عناوين الدورات يدوياً
        List<String> titles = new ArrayList<>();
        for (Course c : courseList) {
            titles.add(c.getTitle());
        }

        // 3) تهيئة الـ Spinner بالعناوين
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                titles
        );
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(spAdapter);

        // 4) عند اختيار عنصر من الـ Spinner، حمّل الدروس
        spinnerCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                selectedCourseId = courseList.get(pos).getCourse_id();
                loadLessons();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 5) إعداد RecyclerView مع الـ Adapter
        rvLessons.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdminLessonAdapter(
                new ArrayList<>(),
                this::showEditDialog,
                this::confirmDelete
        );
        rvLessons.setAdapter(adapter);

        // 6) زرّ إضافة درس جديد
        fabAddLesson.setOnClickListener(x -> showAddDialog());

        return v;
    }

    private void loadLessons() {
        List<Lesson> lessons = db.lessonDao().getByCourse(selectedCourseId);
        adapter.setLessons(lessons);
    }

    private void showAddDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText etTitle = makeField("Title");
        EditText etDesc  = makeField("Description");
        EditText etLink  = makeField("YouTube Link");
        EditText etSeq   = makeField("Sequence Order");
        etSeq.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText etDur   = makeField("Duration (min)");
        etDur.setInputType(InputType.TYPE_CLASS_NUMBER);

        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(etLink);
        layout.addView(etSeq);
        layout.addView(etDur);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Lesson")
                .setView(layout)
                .setPositiveButton("Add", (d,i) -> {
                    try {
                        Lesson l = new Lesson(
                                etTitle.getText().toString().trim(),
                                etDesc.getText().toString().trim(),
                                etLink.getText().toString().trim(),
                                selectedCourseId,
                                Integer.parseInt(etSeq.getText().toString()),
                                Integer.valueOf(etDur.getText().toString()),
                                System.currentTimeMillis()
                        );
                        db.lessonDao().insert(l);
                        Toast.makeText(requireContext(), "Lesson added", Toast.LENGTH_SHORT).show();
                        loadLessons();
                    } catch (Exception ex) {
                        Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(Lesson lesson) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText etTitle = makeField("Title");
        etTitle.setText(lesson.getTitle());
        EditText etDesc  = makeField("Description");
        etDesc.setText(lesson.getDescription());
        EditText etLink  = makeField("YouTube Link");
        etLink.setText(lesson.getYoutube_link());
        EditText etSeq   = makeField("Sequence Order");
        etSeq.setInputType(InputType.TYPE_CLASS_NUMBER);
        etSeq.setText(String.valueOf(lesson.getSequence_order()));
        EditText etDur   = makeField("Duration (min)");
        etDur.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDur.setText(lesson.getDuration() != null ?
                lesson.getDuration().toString() : "");

        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(etLink);
        layout.addView(etSeq);
        layout.addView(etDur);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Lesson")
                .setView(layout)
                .setPositiveButton("Save", (d,i) -> {
                    try {
                        lesson.setTitle(etTitle.getText().toString().trim());
                        lesson.setDescription(etDesc.getText().toString().trim());
                        lesson.setYoutube_link(etLink.getText().toString().trim());
                        lesson.setSequence_order(Integer.parseInt(etSeq.getText().toString()));
                        lesson.setDuration(Integer.valueOf(etDur.getText().toString()));
                        db.lessonDao().update(lesson);
                        Toast.makeText(requireContext(), "Lesson updated", Toast.LENGTH_SHORT).show();
                        loadLessons();
                    } catch (Exception ex) {
                        Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(Lesson lesson) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm delete?")
                .setMessage(lesson.getTitle())
                .setPositiveButton("Delete", (d,i) -> {
                    db.lessonDao().delete(lesson);
                    Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    loadLessons();
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
