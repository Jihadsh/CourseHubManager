package com.example.coursehubmanager.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.coursehubmanager.room.entities.Enrollment;

import java.util.List;

@Dao
public interface EnrollmentDao {
    @Insert
    long insert(Enrollment enrollment);

    @Update
    int update(Enrollment enrollment);

    @Query("SELECT * FROM Enrollment WHERE user_id = :userId")
    List<Enrollment> getByUser(int userId);

    @Query("SELECT COUNT(*) FROM Enrollment WHERE user_id = :userId AND course_id = :courseId")
    int exists(int userId, int courseId);


    @Query("UPDATE Enrollment " +
            "SET progress_percentage = :progress " +
            "WHERE user_id = :userId AND course_id = :courseId")
    void updateProgress(int userId, int courseId, int progress);
}
