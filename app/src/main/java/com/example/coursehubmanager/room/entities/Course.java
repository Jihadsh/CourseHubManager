package com.example.coursehubmanager.room.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "course",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "category_id",
                childColumns = "category_id",
                onDelete = ForeignKey.CASCADE

        ), indices = {@Index(value = "category_id")}

)
public class Course {
    @PrimaryKey(autoGenerate = true)
    private int course_id;

    @NonNull
    private String title;

    private String description;
    private String instructor_name;
    private String image_url;
    private double price;
    private int total_hours;

    private int category_id;
    private long created_at;
    private long updated_at;

    public Course(@NonNull String title, String description, String instructor_name,
                  String image_url, double price, int total_hours, int category_id, long created_at) {
        this.title = title;
        this.description = description;
        this.instructor_name = instructor_name;
        this.image_url = image_url;
        this.price = price;
        this.total_hours = total_hours;
        this.category_id = category_id;
        this.created_at = created_at;
        this.updated_at = created_at;
    }

    // Getters and setters:
    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructor_name() {
        return instructor_name;
    }

    public void setInstructor_name(String instructor_name) {
        this.instructor_name = instructor_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTotal_hours() {
        return total_hours;
    }

    public void setTotal_hours(int total_hours) {
        this.total_hours = total_hours;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }
}