package com.example.coursehubmanager.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.Category;
import com.example.coursehubmanager.room.entities.Course;
import com.example.coursehubmanager.ui.home.CourseAdapter;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private TabLayout tabLayout;
    private RecyclerView rvCourses;
    private CourseAdapter adapter;
    private AppDatabase db;
    private List<Category> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.tabLayoutCategories);
        rvCourses = view.findViewById(R.id.rvCourses);
        db = AppDatabase.getInstance(requireContext());

        // إعداد RecyclerView
        adapter = new CourseAdapter(requireContext(), new ArrayList<>(), course ->
                CourseDetailsActivity.start(requireContext(), course.getCourse_id())
        );
        rvCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCourses.setAdapter(adapter);

        loadCategories();
    }

    private void loadCategories() {
        categoryList = db.categoryDao().getAllCategories();
        if (categoryList.isEmpty()) {
            // في حال لم توجد، ندخل بيانات حقيقية
            prepopulateRealData();
            categoryList = db.categoryDao().getAllCategories();
        }
        for (Category c : categoryList) {
            tabLayout.addTab(tabLayout.newTab().setText(c.getName()));
        }
        // عرض دفعة أولى
        if (!categoryList.isEmpty()) {
            loadCourses(categoryList.get(0).getCategory_id());
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                int catId = categoryList.get(tab.getPosition()).getCategory_id();
                loadCourses(catId);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadCourses(int categoryId) {
        List<Course> courses = db.courseDao().getCoursesForCategory(categoryId);
        adapter.setCourseList(courses != null ? courses : new ArrayList<>());
    }

    /**
     * Insert real data on first launch
     */
    private void prepopulateRealData() {
        long now = System.currentTimeMillis();
        long progId = db.categoryDao().insert(new Category("Programming", now));
        long designId = db.categoryDao().insert(new Category("Design", now));

        db.courseDao().insert(new Course(
                "Java Programming Masterclass - Beginner to Master",
                "Learn Java Basics to Advanced with Real Coding Examples. Become a Java coding master.",
                "Sara Academy",
                "https://upload.wikimedia.org/wikipedia/en/3/30/Java_programming_language_logo.svg",
                199.99,
                40,
                (int) progId,
                now
        ));
        db.courseDao().insert(new Course(
                "Android App Development Masterclass using Kotlin",
                "Learn Kotlin Android App Development and Become an Android Developer. Includes Kotlin tutorial and Android tutorial videos.",
                "Tim Buchalka, Jean-Paul Roberts",
                "https://upload.wikimedia.org/wikipedia/commons/3/33/Android_logo_2019_%28stacked%29.svg",
                22.99,
                62,
                (int) progId,
                now
        ));
        db.courseDao().insert(new Course(
                "Graphic Design Bootcamp: Create Projects Right Away!",
                "Use Photoshop, Illustrator, & InDesign for logo design, web design, poster design, and more.",
                "Derrick Mitchell",
                "https://upload.wikimedia.org/wikipedia/commons/4/4c/Design_basics_with_primary_colors.svg",
                29.99,
                17,
                (int) designId,
                now
        ));
    }
}
