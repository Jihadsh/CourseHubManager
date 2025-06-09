package com.example.coursehubmanager.room.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int category_id;

    @NonNull
    private String name;

    private long created_at;

    public Category(@NonNull String name, long created_at) {
        this.name = name;
        this.created_at = created_at;
    }

    // Getters and setters:
    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}