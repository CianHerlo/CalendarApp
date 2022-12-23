package com.r00205604.calendar;

import static android.content.ContentValues.TAG;
import static com.r00205604.calendar.CalendarUtils.daysInMonthArray;
import static com.r00205604.calendar.CalendarUtils.monthYearFromDate;
import static com.r00205604.calendar.CalendarUtils.selectedDate;
import static com.r00205604.calendar.Event.eventsList;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
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
        Button logoutBTN = findViewById(R.id.logoutBTN);

        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();
        loadEvents();


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();

        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                Intent intent = new Intent(MainActivity.this, EventEditActivity.class);
                intent.putExtra("selectedDate", CalendarUtils.selectedDate.toString());
                startActivity(intent);
            }
        });


        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("email", Objects.requireNonNull(fireAuth.getCurrentUser()).getEmail());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    String email = documentSnapshot.getString("email");

                    assert email != null;
                    if (email.equals(fireAuth.getCurrentUser().getEmail())) {
                        Log.d(TAG, "Welcome Back");
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

                    // Add a new document with a generated ID
                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                            .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                }
            }
        });


        logoutBTN.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        });


    }

    private void loadEvents() {
        eventsList.clear();
        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("email", Objects.requireNonNull(fireAuth.getCurrentUser()).getEmail());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    String email = documentSnapshot.getString("email");

                    assert email != null;
                    if (email.equals(fireAuth.getCurrentUser().getEmail())) {
                        String userID = documentSnapshot.getId();

                        CollectionReference eventsRef = db.collection("users").document(userID).collection("events");
                        eventsRef.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot1 : task1.getResult()) {
                                    String eventTitle = documentSnapshot1.getString("title");
                                    LocalDate eventDate = LocalDate.parse(documentSnapshot1.getString("date"));
                                    LocalTime eventTime = LocalTime.parse(documentSnapshot1.getString("time"));

                                    Event event = new Event(eventTitle, eventDate, eventTime);
                                    eventsList.add(event);
                                }
                            }
                        });
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


    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > mShakeDetector.getThreshold()) {
                Context context = getBaseContext();
                mShakeDetector.onShake(context, selectedDate);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Register the ShakeDetector as a listener for the accelerometer sensor
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    protected void onPause() {
        // Unregister the ShakeDetector as a listener for the accelerometer sensor
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}

