package com.dkcy.wuxing;

import android.annotation.TargetApi;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Zodiac zodiac = new Zodiac();
    private DataBaseHelper myHelper = new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets toolbar as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Set up the database helper for accessing data
        try {
            myHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myHelper.openDataBase();
        } catch (Exception e) {
            throw new Error("Error opening database");
        }

        myHelper.close();

        /* Set up the date picker - original approach of a visible DatePicker as card.
        DatePicker datePicker = (DatePicker) findViewById(R.id.date_of_birth);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year,month,dayOfMonth,new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                myHelper.openDataBase();
                String[] sign = myHelper.lookupDateOfBirth(year, month, dayOfMonth);
                zodiac.setZodiac(sign);
                displayZodiac();
                myHelper.close();
            }
        });
*/

        /* Experimenting with input toolbar
        // Set up the input toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), agones.class);
                //startActivityForResult(myIntent, 0);
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create(); //Read Update
                alertDialog.setTitle("hi");
                alertDialog.setMessage("this is my app");

                alertDialog.setButton("Continue..", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                    }
                });

                alertDialog.show();  //<-- See This!
            }
        });*/

    }

    // Set up actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Only has one item at the mo...
        int id = item.getItemId();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int now_year = calendar.get(Calendar.YEAR);
        int now_month = calendar.get(Calendar.MONTH);
        int now_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Creates DatePickerDialog (simpler method in API24...)
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.AppTheme,new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                myHelper.openDataBase();
                String[] sign = myHelper.lookupDateOfBirth(year, month, dayOfMonth);
                zodiac.setZodiac(sign);
                displayZodiac();
                myHelper.close();
            }
        },now_year,now_month,now_dayOfMonth);

        datePickerDialog.setTitle("Enter date of birth");

        datePickerDialog.show();

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void displayZodiac(){
        String[] sign = zodiac.getZodiac();
        String outcome = "You're a " + sign[0] + " " + sign[3];
        final TextView textZodiac = (TextView) findViewById(R.id.zodiac_sign);
        textZodiac.setText(outcome);

        final TextView textAnimal = (TextView) findViewById(R.id.animal);
        final TextView textElement = (TextView) findViewById(R.id.element);

        textAnimal.setText(sign[5]);
        textElement.setText(sign[2]);
    }
}
