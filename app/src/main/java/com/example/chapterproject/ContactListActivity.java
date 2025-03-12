package com.example.chapterproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

   private ArrayList<Contact> contacts;
   ContactAdapter contactAdapter;

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            int contactId = contacts.get(position).getContactID();
            Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
            intent.putExtra("contactID", contactId);
            startActivity(intent);
        }
    };
    RecyclerView contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initContactButton();
        initMapButton();
        initSettingButton();
        initAddContactButton();
        initDeleteSwitch();

        String sortBy = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortField","contactName");
        String sortOrder = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortOrder","ASC");

        ContactDataSource ds = new ContactDataSource(this);


        try {
            ds.open();
            contacts = ds.getContacts(sortBy,sortOrder);
            ds.close();

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            contactList = findViewById(R.id.rvContacts);
            contactList.setLayoutManager(layoutManager);

            contactAdapter = new ContactAdapter(contacts, ContactListActivity.this);
            contactAdapter.setOnItemClickListener(onItemClickListener);
            contactList.setAdapter(contactAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving contacts", Toast.LENGTH_LONG).show();
        }
    }
    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            double levelScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
            int batteryPercent = (int) Math.floor(batteryLevel / levelScale * 100);
            TextView textBatteryState = (TextView) findViewById(R.id.textBatteryLevel);
            textBatteryState.setText(batteryPercent + "%");
        }
    };

    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(batteryReceiver, filter);
    }
    public void onResume(){
        super.onResume();

        String sortBy = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("sortfield", "contactname");
        String orderBy = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("sortorder", "ASC");



        ContactDataSource ContactDataSource = new ContactDataSource(this);

        //ArrayList<Contact> contacts;
        try {
            Log.d("DEBUG", "Attempting to open database...");
            ContactDataSource.open();

            Log.d("DEBUG", "Database opened successfully, retrieving contact names...");
            contacts = ContactDataSource.getContacts(sortBy, orderBy); // Get contact names

            ContactDataSource.close();
            Log.d("DEBUG", "Database closed successfully.");

            if (contacts.size() > 0) {


                if (contacts == null) {
                    Log.w("WARNING", "getContactName() returned null. Initializing empty list.");
                    contacts = new ArrayList<>(); // Prevent null crash
                }

                RecyclerView contactList = findViewById(R.id.rvContacts);
                if (contactList == null) {
                    Log.e("ERROR", "RecyclerView rvContacts not found in layout");
                    Toast.makeText(this, "RecyclerView not found", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d("DEBUG", "Setting up RecyclerView...");
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                contactList.setLayoutManager(layoutManager);

                contactAdapter = new ContactAdapter(contacts, this);
                contactAdapter.setOnItemClickListener(onItemClickListener);
                contactList.setAdapter(contactAdapter);
            } else {
                Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
                startActivity(intent);
            }


            Log.d("DEBUG", "Contacts loaded successfully");
        } catch (Exception e) {
            Log.e("ERROR", "Exception retrieving contacts", e);
            Toast.makeText(this, "Error retrieving contacts", Toast.LENGTH_LONG).show();
        }

    }
/*
    @Override
    public void onResume() {
        super.onResume();
        String sortBy = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortField","contactName");
        String sortOrder = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortOrder","ASC");
        ContactDataSource ds = new ContactDataSource(this);
        try {
            ds.open();
            contacts = ds.getContacts(sortBy,sortOrder);
            ds.close();

            contactList = findViewById(R.id.rvContacts);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            contactList.setLayoutManager(layoutManager);
            contactAdapter = new ContactAdapter(contacts, this);
            contactList.setAdapter(contactAdapter);
        }
        catch (Exception e){
            Toast.makeText(this,"Error retrieving contacts",Toast.LENGTH_LONG).show();
        }
    }*/

    private void initContactButton() {
        ImageButton ContactButton = findViewById(R.id.ContactButton);
        ContactButton.setOnClickListener(v -> {
            Intent listIntent = new Intent(ContactListActivity.this, ContactListActivity.class);
            listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(listIntent);
        });
    }

    private void initMapButton() {
        ImageButton MapButton = findViewById(R.id.MapButton);
        MapButton.setOnClickListener(v -> {
            Intent listIntent = new Intent(ContactListActivity.this, MapActivity.class);
            listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(listIntent);
        });
    }

    private void initSettingButton() {
        ImageButton SettingButton = findViewById(R.id.SettingButton);
        SettingButton.setOnClickListener(v -> {
            Intent listIntent = new Intent(ContactListActivity.this, SettingActivity.class);
            listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(listIntent);
        });
    }

    private void initAddContactButton() {
        Button newContact = findViewById(R.id.buttonAddContact);
        newContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initDeleteSwitch() {
        Switch s = findViewById(R.id.switchDelete);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged (CompoundButton compoundButton,boolean b){
                Boolean status = compoundButton.isChecked();
                contactAdapter.setDelete(status);;
                contactAdapter.notifyDataSetChanged();
            }
        });
    }
}