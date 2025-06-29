package com.example.coursehubmanager.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.User;
import com.example.coursehubmanager.ui.MainActivity;

import java.security.MessageDigest;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etRegEmail, etRegPassword, etConfirmPassword;
    private Button btnCreateAccount;
    private TextView tvGoToLogin;

    private AppDatabase db;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "coursehub_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName        = findViewById(R.id.etFullName);
        etRegEmail        = findViewById(R.id.etRegEmail);
        etRegPassword     = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount  = findViewById(R.id.btnCreateAccount);
        tvGoToLogin       = findViewById(R.id.tvGoToLogin);

        db = AppDatabase.getInstance(this);
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        setupPasswordToggle(etRegPassword);
        setupPasswordToggle(etConfirmPassword);

        btnCreateAccount.setOnClickListener(v -> attemptRegister());
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupPasswordToggle(EditText passwordField) {
        passwordField.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >=
                        (passwordField.getRight() -
                                passwordField.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    if (passwordField.getTransformationMethod() instanceof PasswordTransformationMethod) {
                        passwordField.setTransformationMethod(
                                HideReturnsTransformationMethod.getInstance());
                    } else {
                        passwordField.setTransformationMethod(
                                PasswordTransformationMethod.getInstance());
                    }
                    passwordField.setSelection(passwordField.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private void attemptRegister() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email    = etRegEmail.getText() != null ? etRegEmail.getText().toString().trim() : "";
        String password = etRegPassword.getText() != null ? etRegPassword.getText().toString().trim() : "";
        String confirm  = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("حقل الاسم الكامل مطلوب");
            etFullName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etRegEmail.setError("حقل البريد مطلوب");
            etRegEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("حقل كلمة المرور مطلوب");
            etRegPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirm)) {
            etConfirmPassword.setError("حقل تأكيد كلمة المرور مطلوب");
            etConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirm)) {
            etConfirmPassword.setError("كلمتا المرور غير متطابقتين");
            etConfirmPassword.requestFocus();
            return;
        }
        if (db.userDao().existsByEmail(email) > 0) {
            etRegEmail.setError("هذا البريد مسجّل بالفعل");
            etRegEmail.requestFocus();
            return;
        }

        long now = System.currentTimeMillis();
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {
            Toast.makeText(this, "حدث خطأ أثناء التشفير.", Toast.LENGTH_SHORT).show();
            return;
        }

        // إنشاء المستخدم
        User newUser = new User(fullName, email, hashedPassword, now);
        newUser.setIs_admin(false);  // ← تأكيد أنه مستخدم عادي
        newUser.setUpdated_at(now); // ← لا تتركه null

        long id = db.userDao().insert(newUser);
        if (id > 0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("user_id", (int) id);
            editor.apply();

            Toast.makeText(this, "تم إنشاء الحساب بنجاح!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.putExtra("user_id", (int) id);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "فشل إنشاء الحساب. حاول مرة أخرى.", Toast.LENGTH_SHORT).show();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
