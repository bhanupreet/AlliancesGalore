package com.alliancesgalore.alliancesgalore.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Event {
    private int Repetition;
    private int Color;
    private boolean AllDay;
    private long StartTime;
    private String Location;
    private String Title;
    private String Description;

    private String CreatedBy;

    public Event() {
    }


    public int getRepetition() {
        return Repetition;
    }

    public void setRepetition(int repetition) {
        Repetition = repetition;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public boolean isAllDay() {
        return AllDay;
    }

    public void setAllDay(boolean allDay) {
        AllDay = allDay;
    }

    public long getStartTime() {
        return StartTime;
    }

    public void setStartTime(long startTime) {
        StartTime = startTime;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    //Repetiton can have 3 values: 1 = everyday, 2 = every week, every month;
    //start time is time in milliseconds,
    public Event(String title, String description, String location, int color, long startTime, long endTime, boolean allDay, int Repetition, String CreatedBy) {
        this.Title = title;
        this.Description = description;
        this.Location = location;
        this.Color = color;
        this.StartTime = startTime;
        this.AllDay = allDay;
        this.Repetition = Repetition;
        this.CreatedBy = CreatedBy;
    }


}
