package com.example.coursehubmanager.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.coursehubmanager.room.entities.Course;
import java.util.List;

@Dao
public interface CourseDao {
    @Insert
    long insert(Course course);

    @Update
    int update(Course course);

    @Delete
    int delete(Course course);

    @Query("SELECT * FROM Course")
    List<Course> getAllCourses();

    @Query("SELECT * FROM Course WHERE course_id = :courseId")
    Course getById(int courseId);

    @Query("SELECT * FROM Course WHERE category_id = :categoryId")
    List<Course> getCoursesForCategory(int categoryId);
}
