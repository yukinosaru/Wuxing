package com.dkcy.wuxing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Zodiac zodiac = new Zodiac();
    private DataBaseHelper myHelper = new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        /* Set up the date picker */
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

    }

    public void displayZodiac(){
        String[] sign = zodiac.getZodiac();
        String outcome = "You're a " + sign[0] + " " + sign[3];
        final TextView textZodiac = (TextView) findViewById(R.id.zodiac_sign);
        final TextView textAnimal = (TextView) findViewById(R.id.animal);
        final TextView textElement = (TextView) findViewById(R.id.element);
        textZodiac.setText(outcome);
        textAnimal.setText(sign[5]);
        textElement.setText(sign[2]);
    }

}
