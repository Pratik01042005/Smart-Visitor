package com.example.meetpoint;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class FilterDialog extends Dialog {

    public interface OnFilterSelected {
        void onFilter(long startTime, long endTime);
    }

    public FilterDialog(@NonNull Context context, OnFilterSelected listener) {
        super(context);
        setContentView(R.layout.activity_filter_dialog);

        findViewById(R.id.filterCategory).setOnClickListener(v -> {
            dismiss();
        });

        findViewById(R.id.filterYear).setOnClickListener(v -> {
            showYearPicker(listener);
            dismiss();
        });

        findViewById(R.id.filterMonth).setOnClickListener(v -> {
            showMonthPicker(context, listener);
            dismiss();
        });

        findViewById(R.id.filterCustom).setOnClickListener(v -> {
            showDayPicker(context, listener);
            dismiss();
        });
    }

    private void showYearPicker(OnFilterSelected listener) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);

        Calendar start = Calendar.getInstance();
        start.set(year, 0, 1, 0, 0, 0);

        Calendar end = Calendar.getInstance();
        end.set(year, 11, 31, 23, 59, 59);

        listener.onFilter(start.getTimeInMillis(), end.getTimeInMillis());
    }

    private void showMonthPicker(Context context, OnFilterSelected listener) {

        Calendar c = Calendar.getInstance();

        new DatePickerDialog(context, (v, y, m, d) -> {

            Calendar start = Calendar.getInstance();
            start.set(y, m, 1, 0, 0, 0);

            Calendar end = Calendar.getInstance();
            end.set(y, m,
                    start.getActualMaximum(Calendar.DAY_OF_MONTH),
                    23, 59, 59);

            listener.onFilter(start.getTimeInMillis(), end.getTimeInMillis());

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1).show();
    }

    private void showDayPicker(Context context, OnFilterSelected listener) {

        Calendar c = Calendar.getInstance();

        new DatePickerDialog(context, (v, y, m, d) -> {

            Calendar start = Calendar.getInstance();
            start.set(y, m, d, 0, 0, 0);

            Calendar end = Calendar.getInstance();
            end.set(y, m, d, 23, 59, 59);

            listener.onFilter(start.getTimeInMillis(), end.getTimeInMillis());

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}
