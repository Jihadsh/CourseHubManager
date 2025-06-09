package com.example.coursehubmanager.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.entities.Lesson;

import androidx.appcompat.app.AppCompatActivity;

public class LessonDetailActivity extends AppCompatActivity {
    public static final String EXTRA_LESSON = "lesson";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        Lesson lesson = (Lesson) getIntent().getSerializableExtra(EXTRA_LESSON);
        ((TextView)findViewById(R.id.tvLessonTitleDetail)).setText(lesson.getTitle());
        ((TextView)findViewById(R.id.tvLessonFullDesc)).setText(lesson.getDescription());
        TextView link = findViewById(R.id.tvYoutubeLink);
        link.setText("Watch on YouTube");
        link.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(lesson.getYoutube_link()));
            startActivity(i);
        });
    }

    public static void start(Context ctx, Lesson lesson) {
        Intent i = new Intent(ctx, LessonDetailActivity.class);
        i.putExtra(EXTRA_LESSON, lesson);
        ctx.startActivity(i);
    }
}