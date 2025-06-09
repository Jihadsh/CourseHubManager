package com.example.coursehubmanager.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.coursehubmanager.room.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Query("SELECT * FROM category ORDER BY name ASC")
    List<Category> getAllCategories();
}