package com.example.coursehubmanager.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.Course;
import com.example.coursehubmanager.room.entities.User;
import com.example.coursehubmanager.ui.admin.AdminCourseAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminDashboardActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "coursehub_prefs";

    private TextView tvWelcome;
    private TextView tvStudentCount;
    private TextView tvCourseCount;
    private RecyclerView rvRecentCourses;
    private FloatingActionButton fabAddCourse;

    private AppDatabase db;
    private List<Course> recentCourses;
    private AdminCourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvWelcome       = findViewById(R.id.tvWelcome);
        tvStudentCount  = findViewById(R.id.tvStudentCount);
        tvCourseCount   = findViewById(R.id.tvCourseCount);
        rvRecentCourses = findViewById(R.id.rvRecentCourses);
        fabAddCourse    = findViewById(R.id.fabAddCourse);

        db = AppDatabase.getInstance(this);

        setupWelcome();
        setupSummaryCards();
        setupRecentCoursesList();
        setupFab();
    }

    private void setupWelcome() {
        String name = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString("username", "admin");
        tvWelcome.setText("مرحباً، " + name + " 👋");
    }

    private void setupSummaryCards() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<User> allUsers = db.userDao().getAllUsers();
            List<Course> allCourses = db.courseDao().getAllCourses();

            runOnUiThread(() -> {
                tvStudentCount.setText(String.valueOf(allUsers.size()));
                tvCourseCount.setText(String.valueOf(allCourses.size()));
            });
        });
    }

    private void setupRecentCoursesList() {
        Executors.newSingleThreadExecutor().execute(() -> {
            recentCourses = db.courseDao().getAllCourses();

            runOnUiThread(() -> {
                if (adapter == null) {
                    rvRecentCourses.setLayoutManager(new LinearLayoutManager(this));
                    adapter = new AdminCourseAdapter(
                            recentCourses,
                            course -> {},
                            this::showEditCourseDialog,
                            this::confirmDeleteCourse
                            );
                    rvRecentCourses.setAdapter(adapter);
                } else {
                    adapter.setCourseList(recentCourses);
                }
            });
        });
    }

    private void setupFab() {
        fabAddCourse.setOnClickListener(v -> showAddCourseDialog());
    }

    private void showAddCourseDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText etTitle      = makeField("Title");
        EditText etDesc       = makeField("Description");
        EditText etInstructor = makeField("Instructor Name");
        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(etInstructor);

        new AlertDialog.Builder(this)
                .setTitle("Add Course")
                .setView(layout)
                .setPositiveButton("Add", (d, i) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc  = etDesc.getText().toString().trim();
                    String inst  = etInstructor.getText().toString().trim();
                    if (title.isEmpty() || desc.isEmpty() || inst.isEmpty()) {
                        Toast.makeText(this,
                                "جميع الحقول مطلوبة", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Course c = new Course(
                            title,
                            desc,
                            inst,
                            "",    // image_url placeholder
                            0.0,   // price placeholder
                            1,     // total_hours placeholder
                            1,     // default category_id
                            System.currentTimeMillis()
                    );

                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.courseDao().insert(c);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "تم إضافة الدورة", Toast.LENGTH_SHORT).show();
                            refreshDashboard();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditCourseDialog(Course c) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText etTitle      = makeField("Title");       etTitle.setText(c.getTitle());
        EditText etDesc       = makeField("Description"); etDesc.setText(c.getDescription());
        EditText etInstructor = makeField("Instructor");  etInstructor.setText(c.getInstructor_name());
        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(etInstructor);

        new AlertDialog.Builder(this)
                .setTitle("Edit Course")
                .setView(layout)
                .setPositiveButton("Save", (d, i) -> {
                    c.setTitle(etTitle.getText().toString().trim());
                    c.setDescription(etDesc.getText().toString().trim());
                    c.setInstructor_name(etInstructor.getText().toString().trim());

                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.courseDao().update(c);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "تم تعديل الدورة", Toast.LENGTH_SHORT).show();
                            refreshDashboard();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteCourse(Course c) {
        new AlertDialog.Builder(this)
                .setTitle("حذف الدورة؟")
                .setMessage(c.getTitle())
                .setPositiveButton("Delete", (d, i) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.courseDao().delete(c);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show();
                            refreshDashboard();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void refreshDashboard() {
        setupSummaryCards();
        setupRecentCoursesList();
    }

    private EditText makeField(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setInputType(InputType.TYPE_CLASS_TEXT);
        return e;
    }
}
