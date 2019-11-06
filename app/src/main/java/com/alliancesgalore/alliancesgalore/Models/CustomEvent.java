package com.alliancesgalore.alliancesgalore.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class CustomEvent implements Parcelable {

    private String title;
    private boolean allDay;
    private long dateTime;
    private int repetition;
    private String description;
    private int notify;
    private String location;
    private String createdBy;
    private boolean repitionFlag = false;

    public CustomEvent(String title, boolean allDay, long dateTime, int repetition, String description, int notify, String location, String createdBy, boolean repitionFlag) {
        this.title = title;
        this.allDay = allDay;
        this.dateTime = dateTime;
        this.repetition = repetition;
        this.description = description;
        this.notify = notify;
        this.location = location;
        this.createdBy = createdBy;
        this.repitionFlag = repitionFlag;
    }

    public boolean isRepitionFlag() {
        return repitionFlag;
    }

    public void setRepitionFlag(boolean repitionFlag) {
        this.repitionFlag = repitionFlag;
    }

    public CustomEvent() {
    }
    //Repetiton can have 3 values: 1 = everyday, 2 = every week, every month;
    //start time is time in milliseconds,


    public CustomEvent(String title, boolean allDay, long dateTime, int repetition, String description, int notify, String location, String createdBy) {
        this.title = title;
        this.allDay = allDay;
        this.dateTime = dateTime;
        this.repetition = repetition;
        this.description = description;
        this.notify = notify;
        this.location = location;
        this.createdBy = createdBy;
    }

    public CustomEvent(Parcel in) {
        title = in.readString();
        allDay = in.readByte() != 0;
        dateTime = in.readLong();
        repetition = in.readInt();
        description = in.readString();
        notify = in.readInt();
        location = in.readString();
        createdBy = in.readString();
    }

    public static final Creator<CustomEvent> CREATOR = new Creator<CustomEvent>() {
        @Override
        public CustomEvent createFromParcel(Parcel in) {
            return new CustomEvent(in);
        }

        @Override
        public CustomEvent[] newArray(int size) {
            return new CustomEvent[size];
        }
    };

    public CustomEvent(CustomEvent myCustomEvent) {
        this.title = myCustomEvent.getTitle();
        this.allDay = myCustomEvent.isAllDay();
        this.dateTime = myCustomEvent.getDateTime();
        this.repetition = myCustomEvent.getRepetition();
        this.description = myCustomEvent.getDescription();
        this.notify = myCustomEvent.getNotify();
        this.location = myCustomEvent.getLocation();
        this.createdBy = myCustomEvent.getCreatedBy();
    }

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        CustomEvent o1 = (CustomEvent) obj;
        return o1.getTitle().equals(this.getTitle()) && o1.getDateTime() == this.getDateTime();
    }
}
