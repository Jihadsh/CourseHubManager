package com.example.coursehubmanager.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.coursehubmanager.room.entities.Bookmark;
import com.example.coursehubmanager.room.entities.Course;

import java.util.List;

@Dao
public interface BookmarkDao {
    @Insert
    long insert(Bookmark bookmark);

    @Query("SELECT * FROM Bookmark WHERE user_id = :userId")
    List<Bookmark> getByUser(int userId);

    @Delete
    void delete(Bookmark bookmark);

    @Query("SELECT COUNT(*) FROM Bookmark WHERE user_id = :userId AND course_id = :courseId")
    int exists(int userId, int courseId);

    @Query("SELECT c.* FROM Course c INNER JOIN Bookmark b ON c.course_id = b.course_id WHERE b.user_id = :userId ORDER BY b.added_at DESC")
    List<Course> getBookmarkedCoursesByUser(int userId);

    @Query("DELETE FROM Bookmark WHERE user_id = :userId AND course_id = :courseId")
    void deleteByUserAndCourse(int userId, int courseId);

}