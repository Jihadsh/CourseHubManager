package com.example.coursehubmanager.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.coursehubmanager.R;
import com.example.coursehubmanager.room.dao.UserLessonStatusDao;
import com.example.coursehubmanager.room.entities.Lesson;
import com.example.coursehubmanager.room.entities.UserLessonStatus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.Holder> {
    /** يتم استدعاؤها عند ضغط على العنصر */
    public interface OnClick {
        void onClick(Lesson lesson);
    }
    /** يتم استدعاؤها بعد تغيير حالة الإكمال لعسح النسبة */
    public interface OnStatusChange {
        void onStatusChanged();
    }

    private final Context ctx;
    private final List<Lesson> list;
    private final OnClick clickListener;
    private final OnStatusChange statusListener;
    private final UserLessonStatusDao statusDao;
    private final int userId;

    public LessonAdapter(Context ctx,
                         List<Lesson> list,
                         int userId,
                         UserLessonStatusDao statusDao,
                         OnClick clickListener,
                         OnStatusChange statusListener) {
        this.ctx = ctx;
        this.list = list;
        this.userId = userId;
        this.statusDao = statusDao;
        this.clickListener = clickListener;
        this.statusListener = statusListener;
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx)
                .inflate(R.layout.item_lesson, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Lesson L = list.get(pos);

        // ضبط النصوص
        h.tvTitle.setText(L.getTitle());
        h.tvDesc.setText(L.getDescription());

        // جلب الحالة من القاعدة (قد يكون null إذا لم يُسجّل بعد)
        Boolean done = statusDao.isCompleted(userId, L.getLesson_id());
        h.chkCompleted.setOnCheckedChangeListener(null);
        h.chkCompleted.setChecked(done != null && done);

        // عند النقر على العنصر كله
        h.itemView.setOnClickListener(v -> clickListener.onClick(L));

        // عند تعديل الـ CheckBox
        h.chkCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // حفظ أو تحديث الحالة باستخدام upsert
            statusDao.upsert(new UserLessonStatus(
                    userId,
                    L.getLesson_id(),
                    isChecked,
                    System.currentTimeMillis()
            ));
            // إعلام المكوّن الحاوي ليعيد حساب النسبة
            statusListener.onStatusChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        CheckBox chkCompleted;
        Holder(@NonNull View v) {
            super(v);
            tvTitle      = v.findViewById(R.id.tvLessonTitle);
            tvDesc       = v.findViewById(R.id.tvLessonDesc);
            chkCompleted = v.findViewById(R.id.chkCompleted);
        }
    }
}
