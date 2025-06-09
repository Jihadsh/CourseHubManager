package com.example.coursehubmanager.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.coursehubmanager.room.entities.Lesson;

import java.util.List;

@Dao
public interface LessonDao {
    @Insert
    long insert(Lesson lesson);

    @Update
    int update(Lesson lesson);

    @Delete
    int delete (Lesson lesson);


    @Query("SELECT * FROM Lesson WHERE course_id = :courseId ORDER BY sequence_order ASC")
    List<Lesson> getByCourse(int courseId);
}