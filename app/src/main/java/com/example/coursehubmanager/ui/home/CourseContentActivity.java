package com.example.coursehubmanager.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.dao.UserLessonStatusDao;
import com.example.coursehubmanager.room.entities.Lesson;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CourseContentActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE_ID = "course_id";

    private RecyclerView rvLessons;
    private AppDatabase db;
    private int courseId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_content);

        rvLessons = findViewById(R.id.rvLessons);
        rvLessons.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getInstance(this);
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        SharedPreferences prefs = getSharedPreferences("coursehub_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        // ✅ تحقق من صحة معرف الدورة
        if (courseId == -1) {
            Toast.makeText(this, "خطأ: لم يتم تحديد الدورة", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        List<Lesson> list = db.lessonDao().getByCourse(courseId);
        UserLessonStatusDao statusDao = db.userLessonStatusDao();

        // ✅ إذا لم توجد دروس، إظهار تنبيه للمستخدم
        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "لا توجد دروس في هذه الدورة حتى الآن", Toast.LENGTH_LONG).show();
            return;
        }

        LessonAdapter adapter = new LessonAdapter(
                this,
                list,
                userId,
                statusDao,
                this::openLesson,
                () -> updateProgress(courseId, userId)
        );
        rvLessons.setAdapter(adapter);
    }

    private void openLesson(Lesson lesson) {
        LessonDetailActivity.start(this, lesson);
    }

    private void updateProgress(int courseId, int userId) {
        int done = db.userLessonStatusDao().countCompleted(userId, courseId);
        int total = db.lessonDao().getByCourse(courseId).size();
        int percent = total == 0 ? 0 : (done * 100) / total;

        db.enrollmentDao().updateProgress(userId, courseId, percent);

        if (percent == 100) {
            db.enrollmentDao().markAsCompleted(userId, courseId);
        }
    }

    public static void start(Context ctx, int courseId) {
        Intent i = new Intent(ctx, CourseContentActivity.class);
        i.putExtra(EXTRA_COURSE_ID, courseId);
        ctx.startActivity(i);
    }
}
