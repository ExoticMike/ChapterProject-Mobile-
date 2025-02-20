package com.example.chapterproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initContactButton();
        initMapButton();
        initSettingButton();
        initSettings();
        initSortByClick();
        initSortOrderClick();

    }
    private void initContactButton() {
        ImageButton ContactButton = findViewById(R.id.ContactButton);
        ContactButton.setOnClickListener(v -> {
            Intent listIntent = new Intent(SettingActivity.this, ContactListActivity.class);
            listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(listIntent);
        });
    }
    private void initMapButton() {
        ImageButton MapButton = findViewById(R.id.MapButton);
        MapButton.setOnClickListener(v -> {
            Intent listIntent = new Intent(SettingActivity.this, MapActivity.class);
            listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(listIntent);
        });
    }
    private void initSettingButton() {
        ImageButton ibSettings = findViewById(R.id.SettingButton);
        ibSettings.setEnabled(false);
        }
    private void initSettings(){
        String sortBy = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortField","contactName");
        String sortOrder = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortOrder","ASC");

        RadioButton rbName = findViewById(R.id.radioName);
        RadioButton rbCity = findViewById(R.id.radioCity);
        RadioButton rbBirthday = findViewById(R.id.radioBirthday);
        if (sortBy.equalsIgnoreCase("contactName")) {
            rbName.setChecked(true);
        }
        else if (sortBy.equalsIgnoreCase("city")) {
            rbCity.setChecked(true);
        }
        else {
            rbBirthday.setChecked(true);
        }

        RadioButton rbAscending = findViewById(R.id.radioAscending);
        RadioButton rbDescending = findViewById(R.id.radioDescending);
        if (sortOrder.equalsIgnoreCase("ASC")) {
            rbAscending.setChecked(true);
        }
        else {
            rbDescending.setChecked(true);
        }
    }
    private void initSortByClick(){
        RadioGroup rgSortBy = findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rbName = findViewById(R.id.radioName);
            RadioButton rbCity = findViewById(R.id.radioCity);
            if (rbName.isChecked()) {
                getSharedPreferences("MyContactListPreferences",Context.MODE_PRIVATE)
                        .edit().putString("sortField","contactName").apply();
            }
            else if (rbCity.isChecked()) {
                getSharedPreferences("MyContactListPreferences",Context.MODE_PRIVATE)
                        .edit().putString("sortField","city").apply();
            }
            else {
                getSharedPreferences("MyContactListPreferences",Context.MODE_PRIVATE)
                        .edit().putString("sortField","birthday").apply();
            }
        });
    }
    private void initSortOrderClick(){
        RadioGroup rgSortOrder = findViewById(R.id.radioGroupSortBy);
        rgSortOrder.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rbAscending = findViewById(R.id.radioAscending);
            if (rbAscending.isChecked()) {
                getSharedPreferences("MyContactListPreferences",Context.MODE_PRIVATE)
                        .edit().putString("sortOrder","ASC").apply();
            }
            else {
                getSharedPreferences("MyContactListPreferences",Context.MODE_PRIVATE)
                        .edit().putString("sortOrder","DESC").apply();
            }
        });
    }
}