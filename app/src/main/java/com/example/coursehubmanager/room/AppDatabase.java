package com.example.coursehubmanager.room;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.coursehubmanager.room.dao.BookmarkDao;
import com.example.coursehubmanager.room.dao.CategoryDao;
import com.example.coursehubmanager.room.dao.CourseDao;
import com.example.coursehubmanager.room.dao.LessonDao;
import com.example.coursehubmanager.room.dao.UserDao;
import com.example.coursehubmanager.room.dao.EnrollmentDao;
import com.example.coursehubmanager.room.dao.UserLessonStatusDao;
import com.example.coursehubmanager.room.entities.Category;
import com.example.coursehubmanager.room.entities.Course;
import com.example.coursehubmanager.room.entities.Lesson;
import com.example.coursehubmanager.room.entities.User;
import com.example.coursehubmanager.room.entities.Enrollment;
import com.example.coursehubmanager.room.entities.Bookmark;
import com.example.coursehubmanager.room.entities.UserLessonStatus;

@Database(
        entities = {
                User.class,
                Category.class,
                Course.class,
                Enrollment.class,
                Lesson.class,
                Bookmark.class,
                UserLessonStatus.class
        },
        version = 7,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract CourseDao courseDao();
    public abstract EnrollmentDao enrollmentDao();
    public abstract LessonDao lessonDao();
    public abstract BookmarkDao bookmarkDao();

    public abstract UserLessonStatusDao userLessonStatusDao();

    private static final String DB_NAME = "coursehub_db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
