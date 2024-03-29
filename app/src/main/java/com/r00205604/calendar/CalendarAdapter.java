package com.r00205604.calendar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;
    private LocalDate date;

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if(days.size() > 15) {
            layoutParams.height = (int) (parent.getHeight() * 0.16);
        } else {
            layoutParams.height = (int) parent.getHeight();
        }
        return new CalendarViewHolder(view, onItemListener, days);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        date = days.get(position);

        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        if(date.equals(CalendarUtils.selectedDate)) {
            holder.parentView.setBackgroundColor(Color.LTGRAY);
        }

        if(!date.getMonth().equals(CalendarUtils.selectedDate.getMonth())) {
            holder.dayOfMonth.setTextColor(Color.parseColor("#6F94FF"));
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface  OnItemListener {
        void onItemClick(int position, LocalDate date);
    }
}
