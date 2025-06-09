package com.example.coursehubmanager.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.User;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin_login);

        etEmail    = findViewById(R.id.etAdminEmail);
        etPassword = findViewById(R.id.etAdminPassword);
        btnLogin   = findViewById(R.id.btnAdminLogin);

        db = AppDatabase.getInstance(this);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass  = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "جميع الحقول مطلوبة", Toast.LENGTH_SHORT).show();
                return;
            }
            User admin = db.userDao().getByEmail(email);
            if (admin != null && admin.getPassword_hash().equals(pass) && admin.isIs_admin()) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "بيانات المسؤول غير صحيحة", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
