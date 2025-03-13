package com.example.chapterproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.format.DateFormat;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerDialogue.saveDateListener {

    final int PERMISSION_REQUEST_PHONE = 102;
    final int PERMISSION_REQUEST_CAMERA = 103;
    final int PERMISSION_REQUEST_SMS = 104;
    final int CAMERA_REQUEST = 1888;

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
            initContact(extras.getInt("contactID"));
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
        initCallFunction();
        initSMSFunction();
        initImageButton();

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

    private void initCallFunction() {
        EditText editPhone = (EditText) findViewById(R.id.editTextHomeNumber);
        editPhone.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                checkPhonePermission(currentContact.getPhoneNumber());
                return false;
            }
        });


    }

    private void initSMSFunction(){
        EditText editCell = (EditText) findViewById(R.id.editTextCellNumber);
        editCell.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View arg0) {
                checkSMSPermission(currentContact.getCellNumber());
                return false;
            }
        });
    }
    private void checkPhonePermission(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.CALL_PHONE)) {
                    Snackbar.make(findViewById(R.id.main), "MyContactList requires this permission to place a call from the app." ,
                            Snackbar.LENGTH_INDEFINITE).setAction("OK", v -> {ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_PHONE);}).show();
                }
                else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_PHONE);
                }
            }
            else{
                callContact(phoneNumber);
            }
        }
        else{
            callContact(phoneNumber);
        }
    }
    private void checkSMSPermission(String cellNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        MainActivity.this,
                        Manifest.permission.SEND_SMS)) {
                    Snackbar.make(findViewById(R.id.main),
                                    "MyContactList requires this permission to send a text from the app.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                            Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
                }
            } else {
                SMSContact(cellNumber);
            }
        } else {
            SMSContact(cellNumber);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Permission", "Request triggered");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission to call granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permission to call not granted", Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(MainActivity.this, "You will not be able to save contact pictures from this app.", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSION_REQUEST_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "You may now send a sms from this app.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "You will not be able to send sms from this app.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
     private void callContact(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (Build.VERSION.SDK_INT >= 23 &&
        ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            startActivity(intent);
        }
     }
    private void SMSContact(String cellNumber) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + cellNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "No app sms.", Toast.LENGTH_SHORT).show();
        }
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
    private void initImageButton() {
        ImageButton ib = findViewById(R.id.imageContact);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED)  {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,android.Manifest.permission.CAMERA)){
                            Snackbar.make(findViewById(R.id.main), "The app needs permission to take pictures.", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                                        }
                                    })
                                    .show();
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                        }
                    } else {
                        takePhoto();
                    }
                }
                else {
                    takePhoto();
                }
            }
        });
    }
    public void takePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST){
            if(resultCode == RESULT_OK){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 100, 100, true);
                ImageButton imageButton = findViewById(R.id.imageContact);
                imageButton.setImageBitmap(scaledPhoto);
                currentContact.setPicture(scaledPhoto);
            }
        }
    }
    private void setForEdit(boolean enabled){
        EditText editName = findViewById(R.id.editName);
        EditText editTextCity = findViewById(R.id.editTextCity);
        EditText editTextStreetAddress = findViewById(R.id.editTextStreetAddress);
        EditText editTextState = findViewById(R.id.editTextState);
        EditText editTextZipcode = findViewById(R.id.editTextZipcode);
        EditText editTextHomeNumber = findViewById(R.id.editTextHomeNumber);
        EditText editTextCellNumber = findViewById(R.id.editTextCellNumber);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        editName.setEnabled(enabled);
        editTextState.setEnabled(enabled);
        editTextStreetAddress.setEnabled(enabled);
        editTextCity.setEnabled(enabled);
        editTextZipcode.setEnabled(enabled);
        editTextEmail.setEnabled(enabled);

        if (enabled){
            editName.requestFocus();
            editTextCellNumber.setInputType(InputType.TYPE_NULL);
            editTextHomeNumber.setInputType(InputType.TYPE_NULL);
        }
        else {
            editTextCellNumber.setInputType(InputType.TYPE_CLASS_PHONE);
            editTextHomeNumber.setInputType(InputType.TYPE_CLASS_PHONE);
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