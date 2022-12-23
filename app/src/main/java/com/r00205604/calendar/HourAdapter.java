package com.r00205604.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourEvent> {

    private TextView event1, event2, event3;

    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents) {
        super(context, 0, hourEvents);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HourEvent event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);
        }
        setHour(convertView, event.time);
        setEvents(convertView, event.events);

        return convertView;
    }

    private void setHour(View convertView, LocalTime time) {
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        timeTV.setText(CalendarUtils.formattedShortTime(time));
    }

    private void setEvents(View convertView, ArrayList<com.r00205604.calendar.Event> events) {
        event1 = convertView.findViewById(R.id.event1);
        event2 = convertView.findViewById(R.id.event2);
        event3 = convertView.findViewById(R.id.event3);

        switch (events.size()) {
            case 0:
                hideEvent(event1);
                hideEvent(event2);
                hideEvent(event3);
                break;
            case 1:
                setEvent(event1, events.get(0));
                hideEvent(event2);
                hideEvent(event3);
                break;
            case 2:
                setEvent(event1, events.get(0));
                setEvent(event2, events.get(1));
                hideEvent(event3);
                break;
            case 3:
                setEvent(event1, events.get(0));
                setEvent(event2, events.get(1));
                setEvent(event3, events.get(2));
                break;
            default:
                setEvent(event1, events.get(0));
                setEvent(event2, events.get(1));
                event3.setVisibility(View.VISIBLE);
                String eventsNotShown = String.valueOf(events.size() - 2);
                eventsNotShown += "+ Events";
                event3.setText(eventsNotShown);
                break;
        }
    }

    private void setEvent(TextView textView, com.r00205604.calendar.Event event) {
        textView.setText(event.getName());
        textView.setVisibility(View.VISIBLE);
    }

    private void hideEvent(TextView textView) {
        textView.setVisibility(View.INVISIBLE);
    }

}













