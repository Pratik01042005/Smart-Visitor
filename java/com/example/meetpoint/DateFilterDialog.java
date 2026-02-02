package com.example.meetpoint;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;

import java.util.Calendar;

public class DateFilterDialog {

    public interface OnFilterSelectedListener {
        void onDaySelected(long start, long end);
        void onMonthSelected(long start, long end);
        void onYearSelected(long start, long end);
    }

    Context context;
    OnFilterSelectedListener listener;

    public DateFilterDialog(Context context, OnFilterSelectedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show() {

        String[] options = {"Filter by Day", "Filter by Month", "Filter by Year"};

        new AlertDialog.Builder(context)
                .setTitle("Select Filter Type")
                .setItems(options, (d, i) -> {
                    if (i == 0) showDayPicker();
                    if (i == 1) showMonthPicker();
                    if (i == 2) showYearPicker();
                })
                .show();
    }

    // ================= DAY =================
    private void showDayPicker() {

        Calendar c = Calendar.getInstance();

        new DatePickerDialog(context, (v, y, m, d) -> {
            Calendar start = Calendar.getInstance();
            start.set(y, m, d, 0, 0, 0);

            Calendar end = Calendar.getInstance();
            end.set(y, m, d, 23, 59, 59);

            listener.onDaySelected(start.getTimeInMillis(), end.getTimeInMillis());

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ================= MONTH =================
    private void showMonthPicker() {

        Calendar c = Calendar.getInstance();

        new DatePickerDialog(context, (v, y, m, d) -> {

            Calendar start = Calendar.getInstance();
            start.set(y, m, 1, 0, 0, 0);

            Calendar end = Calendar.getInstance();
            end.set(y, m, start.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);

            listener.onMonthSelected(start.getTimeInMillis(), end.getTimeInMillis());

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1).show();
    }

    // ================= YEAR =================
    private void showYearPicker() {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);

        Calendar start = Calendar.getInstance();
        start.set(year, 0, 1, 0, 0, 0);

        Calendar end = Calendar.getInstance();
        end.set(year, 11, 31, 23, 59, 59);

        listener.onYearSelected(start.getTimeInMillis(), end.getTimeInMillis());
    }
}
