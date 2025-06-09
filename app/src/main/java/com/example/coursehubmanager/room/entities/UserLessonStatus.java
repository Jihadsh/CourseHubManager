package com.example.coursehubmanager.room.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "UserLessonStatus",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_id",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = Lesson.class,
                        parentColumns = "lesson_id",
                        childColumns = "lesson_id",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = {"user_id", "lesson_id"}, unique = true),
                @Index("lesson_id")
        }
)
public class UserLessonStatus {
    @PrimaryKey(autoGenerate = true)
    public int status_id;
    public int user_id;
    public int lesson_id;
    public boolean is_completed;
    public long completed_at;

    public UserLessonStatus(int user_id, int lesson_id, boolean is_completed, long completed_at) {
        this.user_id = user_id;
        this.lesson_id = lesson_id;
        this.is_completed = is_completed;
        this.completed_at = completed_at;
    }
}