package com.example.coursehubmanager.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.Course;
import com.example.coursehubmanager.room.entities.Enrollment;
import com.example.coursehubmanager.ui.auth.LoginActivity;

public class CourseDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE_ID = "course_id";

    private TextView tvTitle;
    private TextView tvInstructor;
    private TextView tvDescription;
    private Button btnEnroll;

    private AppDatabase db;
    private int courseId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        // bind views
        tvTitle       = findViewById(R.id.tvDetailTitle);
        tvInstructor  = findViewById(R.id.tvDetailInstructor);
        tvDescription = findViewById(R.id.tvDetailDescription);
        btnEnroll     = findViewById(R.id.btnEnroll);

        // init DB and get IDs
        db = AppDatabase.getInstance(this);
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        SharedPreferences prefs = getSharedPreferences("coursehub_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        // if not logged in, redirect to login screen
        if (userId < 0) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loadCourse();
        setupEnrollButton();
    }

    private void loadCourse() {
        Course c = db.courseDao().getById(courseId);
        if (c != null) {
            tvTitle.setText(c.getTitle());
            tvInstructor.setText(c.getInstructor_name());
            tvDescription.setText(c.getDescription());
        }
    }

    private void setupEnrollButton() {
        // initial state
        updateEnrollButton();

        btnEnroll.setOnClickListener(v -> {
            // recheck in case state changed
            boolean already = db.enrollmentDao().exists(userId, courseId) > 0;
            if (already) {
                Toast.makeText(this, "أنت مسجَّل مسبقًا في هذه الدورة", Toast.LENGTH_SHORT).show();
            } else {
                Enrollment e = new Enrollment(userId, courseId, System.currentTimeMillis());
                db.enrollmentDao().insert(e);
                Toast.makeText(this, "تم التسجيل في الدورة بنجاح", Toast.LENGTH_SHORT).show();
                updateEnrollButton();
            }
        });
    }

    private void updateEnrollButton() {
        boolean enrolled = db.enrollmentDao().exists(userId, courseId) > 0;
        btnEnroll.setText(enrolled ? "مسجل بالفعل" : "سجل في الدورة");
        btnEnroll.setEnabled(!enrolled);
    }

    public static void start(Context ctx, int courseId) {
        Intent i = new Intent(ctx, CourseDetailsActivity.class);
        i.putExtra(EXTRA_COURSE_ID, courseId);
        ctx.startActivity(i);
    }
}
