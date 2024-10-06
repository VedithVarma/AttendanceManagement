package com.example.myattendance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MyCalendar extends DialogFragment {
    Calendar  calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private  int day = calendar.get(Calendar.DAY_OF_MONTH);
    private int month = calendar.get(Calendar.MONTH);


    public interface OnCalendarOkClickListener{
        void onClick(int year, int month, int day);
    }
    public OnCalendarOkClickListener onCalendarOkClickListener;

    public void setOnCalendarOkClickListener(OnCalendarOkClickListener onCalendarOkClickListener){
        this.onCalendarOkClickListener = onCalendarOkClickListener;
        onCalendarOkClickListener.onClick(year,month,day);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), ((view, year, month, dayOfMonth) -> {
            // Set the selected date to the calendar
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // If a listener is set, invoke its onClick method with the selected date
            if (onCalendarOkClickListener != null) {
                onCalendarOkClickListener.onClick(year, month, dayOfMonth);
            }
        }), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setBackgroundResource(R.drawable.darkblue_gradient);

        // Return the created DatePickerDialog
        return datePickerDialog;

    }

    void setDate(int year, int month,int day){
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
    }

    String getDate(){
        return DateFormat.format("dd.MM.yyyy",calendar).toString();
    }
}
