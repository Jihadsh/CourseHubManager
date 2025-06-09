package com.example.coursehubmanager.room.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int user_id;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String password_hash;

    private boolean is_admin = false;

    private long created_at;
    private long updated_at;

    // Constructors, getters and setters
    public User(@NonNull String username, @NonNull String email, @NonNull String password_hash, long created_at) {
        this.username = username;
        this.email = email;
        this.password_hash = password_hash;
        this.created_at = created_at;
        this.updated_at = created_at;
    }

    // Getters and setters:

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(@NonNull String password_hash) {
        this.password_hash = password_hash;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
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