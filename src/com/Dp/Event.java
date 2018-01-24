package com.Dp;

public class Event {
    private String type;
    private float endTime;

    Event(String type,Segment seg , int speed, float currentTime)
    {
        endTime = seg.getSize()/speed  + currentTime;
        this.type = type;
    }

    Event(String type, float endTime){
        this.type = type;
        this.endTime = endTime;
    }


    public String getType() {
        return type;
    }

    public float getEndTime(){return endTime;}
}
