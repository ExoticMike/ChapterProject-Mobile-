package com.example.chapterproject;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.format.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerDialogue.saveDateListener {

    private EditText editName,editTextCity,editTextStreetAddress,editTextState,editTextZipcode,
            editTextHomeNumber,editTextCellNumber,editTextEmail;

    private Contact currentContact;

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
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initContact(extras.getInt("contactid"));
        }
        else {
            currentContact = new Contact();
        }

        initContactButton();
        initMapButton();
        initSettingButton();
        initToggleButton();
        setForEdit(false);
        initTextChangedEvents();
        initSaveButton();
        initChangeButton();

        editName = findViewById(R.id.editName);
        editTextCity = findViewById(R.id.editTextCity);
        editTextStreetAddress = findViewById(R.id.editTextStreetAddress);
        editTextState = findViewById(R.id.editTextState);
        editTextZipcode = findViewById(R.id.editTextZipcode);
        editTextHomeNumber = findViewById(R.id.editTextHomeNumber);
        editTextCellNumber = findViewById(R.id.editTextCellNumber);
        editTextEmail = findViewById(R.id.editTextEmail);

    }
    private void initSaveButton() {
        Button saveButton = findViewById((R.id.buttonSave));
        saveButton.setOnClickListener(s -> {
            boolean wasSuccessful;
            ContactDataSource ds = new ContactDataSource(MainActivity.this);
            try {
                ds.open();

                if (currentContact.getContactID() == -1) {
                    wasSuccessful = ds.insertContact(currentContact);
                }
                else {
                    wasSuccessful = ds.updateContact(currentContact);
                }
                ds.close();
            }
            catch (Exception e) {
                wasSuccessful = false;
            }

            if (wasSuccessful) {
                ToggleButton editToggle = findViewById(R.id.onOffButton);
                editToggle.toggle();
                setForEdit(false);
            }
        } );
    }
    private void initChangeButton(){
        Button changeButton = findViewById(R.id.ChangeButton);
        changeButton.setOnClickListener(m -> {
            DatePickerDialogue datePickerDialogue = new DatePickerDialogue();
            datePickerDialogue.show(getSupportFragmentManager(), "date picker");
        });
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

        currentContact.setBirthday(selectedDate);
    }

    private void initTextChangedEvents(){
        final EditText etContactName = findViewById(R.id.editName);
        etContactName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setContactName(etContactName.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
        });

        final EditText etStreetAddress = findViewById(R.id.editTextStreetAddress);
        etStreetAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(etStreetAddress.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        final EditText etCity = findViewById(R.id.editTextCity);
        etCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCity(etCity.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        final EditText etState = findViewById(R.id.editTextState);
        etState.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setState(etState.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        final EditText etZipCode = findViewById(R.id.editTextZipcode);
        etZipCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setZipCode(etZipCode.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        final EditText etCellNumber = findViewById(R.id.editTextCellNumber);
        etCellNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String formattedNumber = PhoneNumberUtils.formatNumber(s.toString(), Locale.getDefault().getCountry());
                currentContact.setCellNumber(formattedNumber);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        final EditText etHomeNumber = findViewById(R.id.editTextHomeNumber);
        etHomeNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String formattedNumber = PhoneNumberUtils.formatNumber(s.toString(), Locale.getDefault().getCountry());
                currentContact.setHomeNumber(formattedNumber);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        final EditText etEmail = findViewById(R.id.editTextEmail);
        etEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setEmail(etEmail.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }
    private void initContact(int id) {
        ContactDataSource ds = new ContactDataSource(MainActivity.this);
        try {
            ds.open();
            currentContact = ds.getSpecificContact(id);
            ds.close();
        }
        catch (Exception e) {
            Toast.makeText(this, "Load Contact Failed", Toast.LENGTH_LONG).show();
        }

        EditText editName = findViewById(R.id.editName);
        EditText editAddress = findViewById(R.id.editTextStreetAddress);
        EditText editCity = findViewById(R.id.editTextCity);
        EditText editState = findViewById(R.id.editTextState);
        EditText editZipCode = findViewById(R.id.editTextZipcode);
        EditText editPhone = findViewById(R.id.editTextHomeNumber);
        EditText editCell = findViewById(R.id.editTextCellNumber);
        EditText editEmail = findViewById(R.id.editTextEmail);
        EditText editBirthday = findViewById(R.id.editTextDate);

        editName.setText(currentContact.getContactName());
        editAddress.setText(currentContact.getStreetAddress());
        editCity.setText(currentContact.getCity());
        editState.setText(currentContact.getState());
        editZipCode.setText(currentContact.getZipCode());
        editPhone.setText(currentContact.getPhoneNumber());
        editCell.setText(currentContact.getCellNumber());
        editEmail.setText(currentContact.getEmail());
        editBirthday.setText(DateFormat.format("MM/dd/yyyy",
                currentContact.getBirthday().getTimeInMillis()).toString());
    }
}