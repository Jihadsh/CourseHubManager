package com.example.coursehubmanager.room.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "Bookmark",
        foreignKeys = {
                @ForeignKey(
                        entity = com.example.coursehubmanager.room.entities.User.class,
                        parentColumns = "user_id",
                        childColumns = "user_id",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = com.example.coursehubmanager.room.entities.Course.class,
                        parentColumns = "course_id",
                        childColumns = "course_id",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = {"user_id", "course_id"}, unique = true),
                @Index("course_id")  // لرفع تحذير عدم وجود فهرس على المفتاح الخارجي
        }
)
public class Bookmark {
    @PrimaryKey(autoGenerate = true)
    private int bookmark_id;
    private int user_id;
    private int course_id;
    private long added_at;

    public Bookmark(int user_id, int course_id, long added_at) {
        this.user_id   = user_id;
        this.course_id = course_id;
        this.added_at  = added_at;
    }

    // getters & setters
    public int getBookmark_id() { return bookmark_id; }
    public void setBookmark_id(int bookmark_id) { this.bookmark_id = bookmark_id; }
    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }
    public int getCourse_id() { return course_id; }
    public void setCourse_id(int course_id) { this.course_id = course_id; }
    public long getAdded_at() { return added_at; }
    public void setAdded_at(long added_at) { this.added_at = added_at; }
}
