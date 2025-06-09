package com.example.coursehubmanager.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.Category;
import com.example.coursehubmanager.room.entities.Course;
import com.example.coursehubmanager.ui.auth.LoginActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView rvCourses;
    private CourseAdapter courseAdapter;
    private AppDatabase db;

    private List<Category> categoryList = new ArrayList<>();
    private List<Course> courseList = new ArrayList<>();

    private int userId; // نستخدمه إذا احتجنا لبيانات المستخدم لاحقًا

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ربط عناصر الواجهة
        tabLayout = findViewById(R.id.tabLayoutCategories);
        rvCourses = findViewById(R.id.rvCourses);

        // الحصول على user_id من شاشة الدخول (إذا تم تمريره)
        userId = getIntent().getIntExtra("user_id", -1);

        // تهيئة قاعدة البيانات
        db = AppDatabase.getInstance(this);

        // تهيئة RecyclerView مع Adapter فارغ أولاً
        setupRecyclerView();

        // جلب الفئات وإنشاء التبويبات (Tabs)
        loadCategoriesAndSetupTabs();
    }

    private void setupRecyclerView() {
        courseAdapter = new CourseAdapter(this, courseList, course -> {
            // عند النقر على دورة: ننتقل إلى شاشة التفاصيل (سننشئها لاحقًا)
            // حالياً نعرض Toast تجريبي
            Toast.makeText(HomeActivity.this,
                    "تم الضغط على: " + course.getTitle(),
                    Toast.LENGTH_SHORT).show();

            // مثال توجيهي لفتح CourseDetailsActivity:
            // Intent intent = new Intent(HomeActivity.this, CourseDetailsActivity.class);
            // intent.putExtra("course_id", course.getCourse_id());
            // startActivity(intent);
        });

        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(courseAdapter);
    }

    private void loadCategoriesAndSetupTabs() {
        // 1. جلب جميع الفئات من قاعدة البيانات عبر DAO
        categoryList = db.categoryDao().getAllCategories();

        // 2. إذا لم توجد فئات بعد (قائمة فارغة)، يمكننا إنشاء عيّنات تجريبية (Demo)
        if (categoryList.isEmpty()) {
            prepopulateDemoCategories();
            categoryList = db.categoryDao().getAllCategories();
        }

        // 3. إضافة تبويب (Tab) لكل فئة في TabLayout
        for (Category cat : categoryList) {
            tabLayout.addTab(tabLayout.newTab().setText(cat.getName()));
        }

        // 4. تحميل الدورات للبطاقة الأولى افتراضيًا (إذا كانت هناك فئات)
        if (!categoryList.isEmpty()) {
            int firstCatId = categoryList.get(0).getCategory_id();
            loadCoursesForCategory(firstCatId);
        }

        // 5. التعامل مع حدث تغيير التبويب
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                int selectedCatId = categoryList.get(position).getCategory_id();
                loadCoursesForCategory(selectedCatId);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // لا نحتاج فعل شيء هنا
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // في حال إعادة الضغط على نفس التبويب، يمكن تمرير فعل إضافي (مثل إعادة التمرير للأعلى)
            }
        });
    }

    private void loadCoursesForCategory(int categoryId) {
        // جلب جميع الدورات التي تنتمي إلى الفئة المحددة
        List<Course> courses = db.courseDao().getCoursesForCategory(categoryId);
        if (courses == null) {
            courses = new ArrayList<>();
        }
        courseAdapter.setCourseList(courses);
    }

    private void prepopulateDemoCategories() {
        long now = System.currentTimeMillis();

        // إضافة فئات تجريبية
        long idProg = db.categoryDao().insert(new Category("Programming", now));
        long idDesign = db.categoryDao().insert(new Category("Design", now));
        long idBusiness = db.categoryDao().insert(new Category("Business", now));

        // إضافة دورات تجريبية لكل فئة
        db.courseDao().insert(new Course(
                "Java للمتقدمين",
                "تعلم مفاهيم Java المتقدمة خطوة بخطوة.",
                "محمد علي",
                "",
                0.0,
                30,
                (int) idProg,
                now
        ));
        db.courseDao().insert(new Course(
                "تطوير Android",
                "بناء تطبيقات Android احترافية باستخدام Java.",
                "سلمى حسين",
                "",
                0.0,
                25,
                (int) idProg,
                now
        ));
        db.courseDao().insert(new Course(
                "أساسيات التصميم الجرافيكي",
                "مقدمة شاملة حول مبادئ التصميم وأدواته.",
                "أحمد جمال",
                "",
                0.0,
                20,
                (int) idDesign,
                now
        ));
        db.courseDao().insert(new Course(
                "إدارة المشاريع",
                "مبادئ وإستراتيجيات في إدارة المشاريع الناجحة.",
                "ليلى سمير",
                "",
                0.0,
                15,
                (int) idBusiness,
                now
        ));
    }
}