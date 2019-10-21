package com.alliancesgalore.alliancesgalore.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private int Repetition;
    private int Color;
    private boolean AllDay;
    private long StartTime;
    private long EndTime;
    private String Location;
    private String Title;
    private String Description;

    public Event() {
    }

    //Repetiton can have 3 values: 1 = everyday, 2 = every week, every month;
    //start time is time in milliseconds,

    public Event(String title, String description, String location, int color, long startTime, long endTime, boolean allDay, int Repetition) {
        this.Title = title;
        this.Description = description;
        this.Location = location;
        this.Color = color;
        this.StartTime = startTime;
        this.EndTime = endTime;
        this.AllDay = allDay;
        this.Repetition = Repetition;
    }


    protected Event(Parcel in) {
        Repetition = in.readInt();
        Color = in.readInt();
        AllDay = in.readByte() != 0;
        StartTime = in.readLong();
        EndTime = in.readLong();
        Location = in.readString();
        Title = in.readString();
        Description = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public long getStartTime() {
        return StartTime;
    }

    public void setStartTime(long startTime) {
        StartTime = startTime;
    }

    public boolean isAllDay() {
        return AllDay;
    }

    public void setAllDay(boolean allDay) {
        AllDay = allDay;
    }

    public long getEndTime() {
        return EndTime;
    }

    public void setEndTime(long endTime) {
        EndTime = endTime;
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

    public int getRepetition() {
        return Repetition;
    }

    public void setRepetition(int repetition) {
        Repetition = repetition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Repetition);
        parcel.writeInt(Color);
        parcel.writeByte((byte) (AllDay ? 1 : 0));
        parcel.writeLong(StartTime);
        parcel.writeLong(EndTime);
        parcel.writeString(Location);
        parcel.writeString(Title);
        parcel.writeString(Description);
    }
}
