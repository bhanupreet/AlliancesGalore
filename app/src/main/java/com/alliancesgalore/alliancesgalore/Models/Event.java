package com.alliancesgalore.alliancesgalore.Models;

public class Event {
    private int Color;
    private boolean AllDay;
    private long StartTime;
    private long EndTime;
    private String Location;
    private String Title;
    private String Description;

    public Event() {
    }


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

    /**
     * Initializes the event
     *  @param title       The title of the event.
     * @param description The description of the event.
     * @param location    The location of the event.
     * @param color       The color of the event (for display in the app).
     * @param startTime   The start time of the event.
     * @param endTime     The end time of the event.
     * @param allDay      Indicates if the event lasts the whole day.
     */


    public Event(String title, String description, String location, int color, long startTime, long endTime, boolean allDay) {
        this.Title = title;
        this.Description = description;
        this.Location = location;
        this.Color = color;
        this.StartTime = startTime;
        this.EndTime = endTime;
        this.AllDay = allDay;
    }
}
