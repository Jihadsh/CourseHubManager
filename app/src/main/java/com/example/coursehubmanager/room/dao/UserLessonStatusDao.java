package com.example.coursehubmanager.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.coursehubmanager.room.entities.UserLessonStatus;

@Dao
public interface UserLessonStatusDao {
    /** إضافة أو تحديث حالة الدرس (conflict -> replace) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(UserLessonStatus status);

    /** احصل على حالة الدرس لمستخدم */
    @Query("SELECT is_completed FROM UserLessonStatus WHERE user_id=:userId AND lesson_id=:lessonId")
    Boolean isCompleted(int userId, int lessonId);

    /** عدد الدروس المكتملة للمستخدم في دورة معينة */
    @Query(
      "SELECT COUNT(*) FROM UserLessonStatus\n" +
              "      WHERE user_id=:userId \n" +
              "        AND is_completed=1 \n" +
              "        AND lesson_id IN (\n" +
              "          SELECT lesson_id FROM Lesson WHERE course_id=:courseId\n" +
              "        )"
    )
    int countCompleted(int userId, int courseId);
}