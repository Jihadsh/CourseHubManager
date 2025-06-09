package com.example.coursehubmanager.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.ui.admin.AdminDashboardActivity;
import com.example.coursehubmanager.ui.home.HomeFragment;
import com.example.coursehubmanager.ui.mycourses.MyCoursesFragment;
import com.example.coursehubmanager.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selected = new HomeFragment();
            } else if (id == R.id.nav_my_courses) {
                selected = new MyCoursesFragment();
            } else if (id == R.id.nav_profile) {
                selected = new ProfileFragment();
            } else if (id == R.id.nav_admin_dashboard) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                return true;
            }

            if (selected != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
            }
            return true;
        });

        // عرض الصفحة الرئيسية بشكل افتراضي
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_home);
        }
    }
}
