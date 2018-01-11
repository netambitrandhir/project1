package com.sanganan.app.model;

import java.util.ArrayList;

/**
 * Created by root on 3/9/16.
 */
public class GeneralNotification {
    String ID;
    String RWAID;
    String Severity;
    String Title;
    String Text;
    String IsCalendarEvent;
    String Datecreated;
    String CreatedBy;
    String EventStartDate;
    String EventEndDateTime;
    String Status;
    ArrayList<String> ImageList;

    public ArrayList<String> getImageList() {
        return ImageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        ImageList = imageList;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }


    public String getRWAID() {
        return RWAID;
    }

    public void setRWAID(String RWAID) {
        this.RWAID = RWAID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSeverity() {
        return Severity;
    }

    public void setSeverity(String severity) {
        Severity = severity;
    }

    public String getIsCalendarEvent() {
        return IsCalendarEvent;
    }

    public void setIsCalendarEvent(String isCalendarEvent) {
        IsCalendarEvent = isCalendarEvent;
    }

    public String getDatecreated() {
        return Datecreated;
    }

    public void setDatecreated(String datecreated) {
        Datecreated = datecreated;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getEventStartDate() {
        return EventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        EventStartDate = eventStartDate;
    }

    public String getEventEndDateTime() {
        return EventEndDateTime;
    }

    public void setEventEndDateTime(String eventEndDateTime) {
        EventEndDateTime = eventEndDateTime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

}
