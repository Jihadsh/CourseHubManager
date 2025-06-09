package com.example.coursehubmanager.room.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "Enrollment",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_id",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = Course.class,
                        parentColumns = "course_id",
                        childColumns = "course_id",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = {"user_id", "course_id"}, unique = true)
        }
)
public class Enrollment {
    @PrimaryKey(autoGenerate = true)
    private int enrollment_id;
    private int user_id;
    private int course_id;
    private long enrollment_date;
    private String status;            // "ongoing" or "completed"
    private int progress_percentage;  // 0–100

    public Enrollment(int user_id, int course_id, long enrollment_date) {
        this.user_id = user_id;
        this.course_id = course_id;
        this.enrollment_date = enrollment_date;
        this.status = "ongoing";
        this.progress_percentage = 0;
    }

    // getters & setters...
    public int getEnrollment_id() { return enrollment_id; }
    public void setEnrollment_id(int id) { this.enrollment_id = id; }

    public int getUser_id() { return user_id; }
    public int getCourse_id() { return course_id; }

    public long getEnrollment_date() { return enrollment_date; }
    public void setEnrollment_date(long d) { this.enrollment_date = d; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getProgress_percentage() { return progress_percentage; }
    public void setProgress_percentage(int p) { this.progress_percentage = p; }
}