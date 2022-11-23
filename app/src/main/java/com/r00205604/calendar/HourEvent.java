package com.r00205604.calendar;

import java.time.LocalTime;
import java.util.ArrayList;

class HourEvent {
    LocalTime time;
    ArrayList<com.r00205604.calendar.Event> events;

    public HourEvent(LocalTime time, ArrayList<com.r00205604.calendar.Event> events) {
        this.time = time;
        this.events = events;
    }

    public LocalTime getTime()
    {
        return time;
    }

    public void setTime(LocalTime time)
    {
        this.time = time;
    }

}
