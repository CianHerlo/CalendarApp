package com.r00205604.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class EventEditActivity extends AppCompatActivity {
    private EditText eventNameET, eventTimeET;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        eventNameET = findViewById(R.id.eventNameET);
        TextView eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeET = findViewById(R.id.eventTimeET);

        eventDateTV.setText("Selected Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
    }

    public void saveEventAction(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String eventName = eventNameET.getText().toString();
        eventTimeET = findViewById(R.id.eventTimeET);

        String time = eventTimeET.getText().toString();
        LocalTime convertTime = LocalTime.parse(time);

        if (eventName == "" || time == "") {
            if (eventName == "") {
                eventNameET.setError("Event Name Required");
            } else {
                eventTimeET.setError("Event Time Required");
            }
            return;
        }

        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, convertTime);
        Event.eventsList.add(newEvent);
        finish();
    }
}