package com.example.chapterproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.format.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialogue.saveDateListener {

    private EditText editName,editTextCity,editTextStreetAddress,editTextState,editTextZipcode,
            editTextHomeNumber,editTextCellNumber,editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initContactButton();
        initMapButton();
        initSettingButton();
        initToggleButton();
        setForEdit(false);

        editName = findViewById(R.id.editName);
        editTextCity = findViewById(R.id.editTextCity);
        editTextStreetAddress = findViewById(R.id.editTextStreetAddress);
        editTextState = findViewById(R.id.editTextState);
        editTextZipcode = findViewById(R.id.editTextZipcode);
        editTextHomeNumber = findViewById(R.id.editTextHomeNumber);
        editTextCellNumber = findViewById(R.id.editTextCellNumber);
        editTextEmail = findViewById(R.id.editTextEmail);

    }
    private void initContactButton() {
        ImageButton ContactButton = findViewById(R.id.ContactButton);
        ContactButton.setOnClickListener(v -> {
            Intent listIntent = new Intent(MainActivity.this, ContactListActivity.class);
            listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(listIntent);
        });
    }
    private void initMapButton() {
        ImageButton MapButton = findViewById(R.id.MapButton);
        MapButton.setOnClickListener(v -> {
            Intent listIntent = new Intent(MainActivity.this, MapActivity.class);
            listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(listIntent);
        });
    }
    private void initSettingButton() {
        ImageButton SettingButton = findViewById(R.id.SettingButton);
        SettingButton.setOnClickListener(v -> {
            Intent listIntent = new Intent(MainActivity.this, SettingActivity.class);
            listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(listIntent);
        });
    }
    private void setForEdit(boolean enabled){
        editName = findViewById(R.id.editName);
        editTextCity = findViewById(R.id.editTextCity);
        editTextStreetAddress = findViewById(R.id.editTextStreetAddress);
        editTextState = findViewById(R.id.editTextState);
        editTextZipcode = findViewById(R.id.editTextZipcode);
        editTextHomeNumber = findViewById(R.id.editTextHomeNumber);
        editTextCellNumber = findViewById(R.id.editTextCellNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editName.setEnabled(enabled);
        editTextState.setEnabled(enabled);
        editTextStreetAddress.setEnabled(enabled);
        editTextCity.setEnabled(enabled);
        editTextHomeNumber.setEnabled(enabled);
        editTextZipcode.setEnabled(enabled);
        editTextCellNumber.setEnabled(enabled);
        editTextEmail.setEnabled(enabled);

        if (enabled){
            editName.requestFocus();
        }
    }
    private void initToggleButton(){
        ToggleButton toggleButton = findViewById(R.id.onOffButton);
        toggleButton.setOnClickListener(v ->{
            setForEdit(toggleButton.isChecked());
        });
    }

    @Override
    public void didFinishDatePickerDialogue(Calendar selectedDate) {
        TextView birthDay = findViewById(R.id.editTextDate);
        birthDay.setText(DateFormat.format("MM/dd/yyyy", selectedDate));
    }
}