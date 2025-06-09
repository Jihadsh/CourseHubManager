package com.example.coursehubmanager.room.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "Lesson",
        foreignKeys = @ForeignKey(
                entity = Course.class,
                parentColumns = "course_id",
                childColumns = "course_id",
                onDelete = CASCADE
        ),
        indices = @Index("course_id")
)
public class Lesson implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int lesson_id;
    private String title;
    private String description;
    private String youtube_link;
    private int course_id;
    private int sequence_order;
    private Integer duration;      // دقائق (اختياري)
    private long created_at;

    public Lesson(String title, String description, String youtube_link,
                  int course_id, int sequence_order, Integer duration, long created_at) {
        this.title = title;
        this.description = description;
        this.youtube_link = youtube_link;
        this.course_id = course_id;
        this.sequence_order = sequence_order;
        this.duration = duration;
        this.created_at = created_at;
    }

    // getters & setters
    public int getLesson_id() { return lesson_id; }
    public void setLesson_id(int lesson_id) { this.lesson_id = lesson_id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getYoutube_link() { return youtube_link; }
    public void setYoutube_link(String youtube_link) { this.youtube_link = youtube_link; }
    public int getCourse_id() { return course_id; }
    public void setCourse_id(int course_id) { this.course_id = course_id; }
    public int getSequence_order() { return sequence_order; }
    public void setSequence_order(int sequence_order) { this.sequence_order = sequence_order; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public long getCreated_at() { return created_at; }
    public void setCreated_at(long created_at) { this.created_at = created_at; }
}