package com.example.chapterproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerDialogue extends DialogFragment {

    Calendar selectedDate;

    public interface saveDateListener{
        void didFinishDatePickerDialogue(Calendar selectedDate);
    }
    public DatePickerDialogue(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_date_picker_dialogue,container);
        if (getDialog()!= null){
            getDialog().setTitle("Select Date");
        }
        selectedDate = Calendar.getInstance();
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((calendar,year,month,day)-> selectedDate.set(year,month,day));
        Button selectbutton = view.findViewById(R.id.selectButton);
        selectbutton.setOnClickListener(v -> saveItem(selectedDate));
        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v ->{
            if(getDialog()!=null){
                getDialog().dismiss();
            }
        });
        return view;
    }
    private void saveItem(Calendar selectedDate){
        saveDateListener activity = (saveDateListener) getActivity();
        if(activity !=null){
            activity.didFinishDatePickerDialogue(selectedDate);
            if(getDialog()!=null){
                getDialog().dismiss();
            }
        }
    }
}