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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.User;
import com.example.coursehubmanager.ui.MainActivity;

import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private TextView tvGoToRegister;
    private TextView tvForgotPassword;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "coursehub_prefs";
    private static final String KEY_REMEMBER = "remember_me";
    private static final String KEY_EMAIL = "saved_email";
    private static final String KEY_PASSWORD = "saved_password";

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        loginButton = findViewById(R.id.loginButton);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        db = AppDatabase.getInstance(this);

        if (prefs.getBoolean(KEY_REMEMBER, false)) {
            emailInput.setText(prefs.getString(KEY_EMAIL, ""));
            passwordInput.setText(prefs.getString(KEY_PASSWORD, ""));
            rememberMeCheckBox.setChecked(true);
        }

        passwordInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >=
                        (passwordInput.getRight() - passwordInput.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    if (passwordInput.getTransformationMethod() instanceof PasswordTransformationMethod) {
                        passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    passwordInput.setSelection(passwordInput.getText().length());
                    return true;
                }
            }
            return false;
        });

        loginButton.setOnClickListener(v -> attemptLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        tvForgotPassword.setOnClickListener(v -> showResetPasswordDialog());
    }

    private void attemptLogin() {
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("حقل البريد مطلوب");
            emailInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("حقل كلمة المرور مطلوب");
            passwordInput.requestFocus();
            return;
        }

        String hashed = hashPassword(password);
        User user = db.userDao().login(email, hashed);

        if (user == null) {
            Toast.makeText(this,
                    "خطأ: البريد أو كلمة المرور غير صحيحة",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        if (rememberMeCheckBox.isChecked()) {
            editor.putBoolean(KEY_REMEMBER, true)
                    .putString(KEY_EMAIL, email)
                    .putString(KEY_PASSWORD, password);
        } else {
            editor.putBoolean(KEY_REMEMBER, false)
                    .remove(KEY_EMAIL)
                    .remove(KEY_PASSWORD);
        }
        editor.putInt("user_id", user.getUser_id());
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_id", user.getUser_id());
        startActivity(intent);
        finish();
    }

    private void showResetPasswordDialog() {
        EditText input = new EditText(this);
        input.setHint("أدخل بريدك الإلكتروني");

        new AlertDialog.Builder(this)
                .setTitle("إعادة تعيين كلمة المرور")
                .setView(input)
                .setPositiveButton("إرسال", (dialog, which) -> {
                    String email = input.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(this, "البريد مطلوب", Toast.LENGTH_SHORT).show();
                    } else if (db.userDao().existsByEmail(email) > 0) {
                        Toast.makeText(this,
                                "تم إرسال رابط إعادة التعيين إلى بريدك الإلكتروني",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,
                                "هذا البريد غير مسجل", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    // ✅ دالة التشفير SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
