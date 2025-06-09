package com.example.coursehubmanager.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.AppDatabase;
import com.example.coursehubmanager.room.entities.User;
import com.example.coursehubmanager.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {

    private EditText etName, etEmail;
    private Button btnSave;
    private AppDatabase db;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        etName  = v.findViewById(R.id.etProfileName);
        etEmail = v.findViewById(R.id.etProfileEmail);
        btnSave = v.findViewById(R.id.btnSaveProfile);

        db = AppDatabase.getInstance(requireContext());
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("coursehub_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        // إذا لم يكن هناك userId صالح، أعد التوجيه لشاشة تسجيل الدخول
        if (userId < 0) {
            Intent i = new Intent(requireContext(), LoginActivity.class);
            startActivity(i);
            requireActivity().finish();
            return v;
        }

        loadUser();
        btnSave.setOnClickListener(view -> saveProfile());
        return v;
    }

    private void loadUser() {
        User u = db.userDao().getById(userId);
        if (u != null) {
            etName.setText(u.getUsername());
            etEmail.setText(u.getEmail());
        }
    }

    private void saveProfile() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email= etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            etName.setError("الاسم مطلوب");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("البريد مطلوب");
            etEmail.requestFocus();
            return;
        }

        User u = db.userDao().getById(userId);
        if (u != null) {
            u.setUsername(name);
            u.setEmail(email);
            db.userDao().update(u);
            Toast.makeText(getContext(),
                    "تم حفظ البيانات بنجاح",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
