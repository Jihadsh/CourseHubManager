package com.example.coursehubmanager.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private RecyclerView rvAdminLessons;
    private FloatingActionButton fabAddLesson;

    private AppDatabase db;
    private List<Course> courseList;
    private int selectedCourseId = -1;
    private AdminLessonAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_lessons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        spinnerCourses   = v.findViewById(R.id.spinnerCourses);
        rvAdminLessons   = v.findViewById(R.id.rvAdminLessons);
        fabAddLesson     = v.findViewById(R.id.fabAddLesson);

        db = AppDatabase.getInstance(requireContext());

        setupSpinner();
        setupRecycler();
        fabAddLesson.setOnClickListener(x -> showAddLessonDialog());
    }

    private void setupSpinner() {
        courseList = db.courseDao().getAllCourses();
        List<String> titles = new ArrayList<>();
        for (Course c : courseList) {
            titles.add(c.getTitle());
        }

        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                titles
        );
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(spAdapter);

        spinnerCourses.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent,
                                                 View view,
                                                 int pos,
                                                 long id) {
                selectedCourseId = courseList.get(pos).getCourse_id();
                loadLessons();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    private void setupRecycler() {
        rvAdminLessons.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadLessons();
    }

    private void loadLessons() {
        List<Lesson> lessons = (selectedCourseId < 0)
                ? new ArrayList<>()
                : db.lessonDao().getByCourse(selectedCourseId);

        if (adapter == null) {
            adapter = new AdminLessonAdapter(
                    lessons,
                    this::showEditLessonDialog,
                    this::confirmDeleteLesson
            );
            rvAdminLessons.setAdapter(adapter);
        } else {
            adapter.setLessons(lessons);
        }
    }

    private void showAddLessonDialog() {
        if (selectedCourseId < 0) {
            Toast.makeText(requireContext(), "اختر كورس أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText etTitle    = makeField("Lesson Title");
        EditText etDesc     = makeField("Description");
        EditText etYoutube  = makeField("YouTube URL");
        EditText etOrder    = makeField("Sequence Order");
        etOrder.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText etDuration = makeField("Duration (min)");
        etDuration.setInputType(InputType.TYPE_CLASS_NUMBER);

        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(etYoutube);
        layout.addView(etOrder);
        layout.addView(etDuration);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Lesson")
                .setView(layout)
                .setPositiveButton("Add", (d,i) -> {
                    try {
                        Lesson newLesson = new Lesson(
                                etTitle.getText().toString().trim(),
                                etDesc.getText().toString().trim(),
                                etYoutube.getText().toString().trim(),
                                selectedCourseId,
                                Integer.parseInt(etOrder.getText().toString().trim()),
                                Integer.parseInt(etDuration.getText().toString().trim()),
                                System.currentTimeMillis()
                        );
                        db.lessonDao().insert(newLesson);
                        Toast.makeText(requireContext(), "Lesson added", Toast.LENGTH_SHORT).show();
                        loadLessons();
                    } catch (Exception ex) {
                        Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditLessonDialog(Lesson lesson) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText etTitle    = makeField("Lesson Title");    etTitle.setText(lesson.getTitle());
        EditText etDesc     = makeField("Description");     etDesc.setText(lesson.getDescription());
        EditText etYoutube  = makeField("YouTube URL");     etYoutube.setText(lesson.getYoutube_url());
        EditText etOrder    = makeField("Sequence Order");  etOrder.setText(String.valueOf(lesson.getSequence_order()));
        etOrder.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText etDuration = makeField("Duration (min)");  etDuration.setText(String.valueOf(lesson.getDuration()));
        etDuration.setInputType(InputType.TYPE_CLASS_NUMBER);

        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(etYoutube);
        layout.addView(etOrder);
        layout.addView(etDuration);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Lesson")
                .setView(layout)
                .setPositiveButton("Save", (d,i) -> {
                    try {
                        lesson.setTitle(etTitle.getText().toString().trim());
                        lesson.setDescription(etDesc.getText().toString().trim());
                        lesson.setYoutube_url(etYoutube.getText().toString().trim());
                        lesson.setSequence_order(Integer.parseInt(etOrder.getText().toString().trim()));
                        lesson.setDuration(Integer.parseInt(etDuration.getText().toString().trim()));
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

    private void confirmDeleteLesson(Lesson lesson) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Lesson?")
                .setMessage(lesson.getTitle())
                .setPositiveButton("Delete", (d,i) -> {
                    db.lessonDao().delete(lesson);
                    Toast.makeText(requireContext(), "Lesson deleted", Toast.LENGTH_SHORT).show();
                    loadLessons();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private EditText makeField(String hint) {
        EditText e = new EditText(requireContext());
        e.setHint(hint);
        e.setInputType(InputType.TYPE_CLASS_TEXT);
        return e;
    }
}
