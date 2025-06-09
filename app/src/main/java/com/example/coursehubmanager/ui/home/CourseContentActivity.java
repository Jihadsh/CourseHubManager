package com.example.coursehubmanager.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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

        // RecyclerView
        rvLessons = findViewById(R.id.rvLessons);
        rvLessons.setLayoutManager(new LinearLayoutManager(this));

        // قاعدة البيانات & المعرّفات
        db       = AppDatabase.getInstance(this);
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        SharedPreferences prefs = getSharedPreferences("coursehub_prefs", MODE_PRIVATE);
        userId   = prefs.getInt("user_id", -1);

        // جلب الدروس
        List<Lesson> list = db.lessonDao().getByCourse(courseId);

        // الـ DAO المسؤول عن حالة الدرس
        UserLessonStatusDao statusDao = db.userLessonStatusDao();

        // إنشاء الـ Adapter بالتوقيع الجديد:
        LessonAdapter adapter = new LessonAdapter(
                /*1*/ this,
                /*2*/ list,
                /*3*/ userId,
                /*4*/ statusDao,
                /*5*/ this::openLesson,
                /*6*/ () -> updateProgress(courseId, userId)
        );
        rvLessons.setAdapter(adapter);
    }

    // فتح شاشة تفاصيل الدرس
    private void openLesson(Lesson L) {
        LessonDetailActivity.start(this, L);
    }

    // حساب النسبة وتحديث الـ Enrollment
    private void updateProgress(int courseId, int userId) {
        int done  = db.userLessonStatusDao().countCompleted(userId, courseId);
        int total = db.lessonDao().getByCourse(courseId).size();
        int percent = total == 0 ? 0 : (done * 100) / total;
        db.enrollmentDao().updateProgress(userId, courseId, percent);
    }

    // طريقة مساعدة لإطلاق هذه الشاشة
    public static void start(Context ctx, int courseId) {
        Intent i = new Intent(ctx, CourseContentActivity.class);
        i.putExtra(EXTRA_COURSE_ID, courseId);
        ctx.startActivity(i);
    }
}
