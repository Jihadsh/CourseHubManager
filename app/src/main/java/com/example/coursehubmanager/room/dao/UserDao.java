package com.example.coursehubmanager.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import com.example.coursehubmanager.room.entities.User;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    User getByEmail(String email);

    @Query("SELECT * FROM user WHERE email = :email AND password_hash = :passwordHash LIMIT 1")
    User login(String email, String passwordHash);

    @Query("SELECT COUNT(*) FROM user WHERE email = :email")
    int existsByEmail(String email);


    @Query("SELECT * FROM User WHERE user_id = :id")
    User getById(int id);

    @Update
    void update(User user);

    @Query("SELECT * FROM User")
    List<User> getAllUsers();
}