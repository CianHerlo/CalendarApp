package com.r00205604.calendar;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventEditActivity extends AppCompatActivity {
    private EditText eventNameET, eventTimeET;
    FirebaseAuth fireAuth = FirebaseAuth.getInstance();

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

        if (eventName.equals("") || time.equals("")) {
            if (eventName.equals("")) {
                eventNameET.setError("Event Name Required");
            } else {
                eventTimeET.setError("Event Time Required");
            }
            return;
        }

        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, convertTime);
        Event.eventsList.add(newEvent);

        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("email", Objects.requireNonNull(fireAuth.getCurrentUser()).getEmail());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    String email = documentSnapshot.getString("email");

                    assert email != null;
                    if (email.equals(fireAuth.getCurrentUser().getEmail())) {
                        String userID = documentSnapshot.getId();
                        Toast.makeText(EventEditActivity.this, "Time: " + time, Toast.LENGTH_SHORT).show();

                        // Create a new user with a first and last name
                        Map<String, Object> addEvent = new HashMap<>();
                        addEvent.put("title", eventName);
                        addEvent.put("date", CalendarUtils.selectedDate.toString());
                        addEvent.put("time", time);

                        db.collection("users").document(userID).collection("events")
                                .add(addEvent)
                                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                    }
                }
            }
        });


        Intent intent_return = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent_return);
    }

}