package com.example.coursehubmanager.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.Bookmark;
import com.example.coursehubmanager.room.entities.Course;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookmarkActivity extends AppCompatActivity {
    private RecyclerView rvBookmarks;
    private CourseAdapter adapter;
    private AppDatabase db;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        rvBookmarks = findViewById(R.id.rvBookmarks);
        rvBookmarks.setLayoutManager(new LinearLayoutManager(this));
        db = AppDatabase.getInstance(this);

        SharedPreferences prefs = getSharedPreferences("coursehub_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        adapter = new CourseAdapter(this, new ArrayList<>(), course -> {
            Toast.makeText(this, "Long press to remove", Toast.LENGTH_SHORT).show();
        });
        rvBookmarks.setAdapter(adapter);

        // حذف عند الضغط الطويل
        adapter.setOnItemLongClickListener(course -> {
            db.bookmarkDao().deleteByUserAndCourse(userId, course.getCourse_id());
            loadBookmarks();
            Toast.makeText(this, "Removed from bookmarks", Toast.LENGTH_SHORT).show();
        });

        loadBookmarks();
    }

    private void loadBookmarks() {
        List<Bookmark> list = db.bookmarkDao().getByUser(userId);
        List<Course> courses = new ArrayList<>();
        for (Bookmark b : list) {
            Course c = db.courseDao().getById(b.getCourse_id());
            if (c != null) courses.add(c);
        }
        adapter.setCourseList(courses);
    }
}
