package com.alliancesgalore.alliancesgalore.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {

    private String title;
    private boolean allDay;
    private long dateTime;
    private int repetition;
    private String description;
    private int notify;
    private String location;
    private String createdBy;

    public Event() {
    }
    //Repetiton can have 3 values: 1 = everyday, 2 = every week, every month;
    //start time is time in milliseconds,


    public Event(String title, boolean allDay, long dateTime, int repetition, String description, int notify, String location, String createdBy) {
        this.title = title;
        this.allDay = allDay;
        this.dateTime = dateTime;
        this.repetition = repetition;
        this.description = description;
        this.notify = notify;
        this.location = location;
        this.createdBy = createdBy;
    }

    protected Event(Parcel in) {
        title = in.readString();
        allDay = in.readByte() != 0;
        dateTime = in.readLong();
        repetition = in.readInt();
        description = in.readString();
        notify = in.readInt();
        location = in.readString();
        createdBy = in.readString();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNotify() {
        return notify;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeByte((byte) (allDay ? 1 : 0));
        parcel.writeLong(dateTime);
        parcel.writeInt(repetition);
        parcel.writeString(description);
        parcel.writeInt(notify);
        parcel.writeString(location);
        parcel.writeString(createdBy);
    }
}
