package com.r00205604.calendar;

import static android.content.ContentValues.TAG;
import static com.r00205604.calendar.CalendarUtils.daysInMonthArray;
import static com.r00205604.calendar.CalendarUtils.monthYearFromDate;
import static com.r00205604.calendar.Event.eventsList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    FirebaseAuth fireAuth;
    FirebaseFirestore db;
    int userExists = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarRecyclerView = findViewById(R.id.calendarRV);
        monthYearText = findViewById(R.id.monthYearTV);
        db = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();

        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();

        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("email", fireAuth.getCurrentUser().getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String email = documentSnapshot.getString("email");

                        assert email != null;
                        if (email.equals(fireAuth.getCurrentUser().getEmail())) {
                            Log.d(TAG, "User Exists");
                            Toast.makeText(MainActivity.this, "Email Exists", Toast.LENGTH_SHORT).show();
                            userExists = 1;
                            break;
                        }
                    }

                    if (userExists != 1) {
                        Log.d(TAG, "New User");
                        Toast.makeText(MainActivity.this, "New User", Toast.LENGTH_SHORT).show();

                        // Create a new user with a first and last name
                        Map<String, Object> user = new HashMap<>();
                        user.put("email", fireAuth.getCurrentUser().getEmail());
                        user.put("events", new ArrayList<Event>());

                        // Add a new document with a generated ID
                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                    }
                }
            }
        });
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    public void lastMonth(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonth(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if(date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    public void weeklyAction(View view)
    {
        startActivity(new Intent(this, WeekViewActivity.class));
    }
}








