package com.r00205604.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.time.LocalTime;

public class EventEditActivity extends AppCompatActivity
{
    private EditText eventNameET;

    private LocalTime time;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        eventNameET = findViewById(R.id.eventNameET);
        TextView eventDateTV = findViewById(R.id.eventDateTV);
        TextView eventTimeTV = findViewById(R.id.eventTimeTV);

        time = LocalTime.now();
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));
    }

    public void saveEventAction(View view)
    {
        String eventName = eventNameET.getText().toString();
        com.r00205604.calendar.Event newEvent = new com.r00205604.calendar.Event(eventName, CalendarUtils.selectedDate, time);
        com.r00205604.calendar.Event.eventsList.add(newEvent);
        finish();
    }
}