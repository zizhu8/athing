package com.github.ompc.athing.qatest.puppet.domain;

import java.util.Date;

public class Tick {

    private Date timestamp;
    private String localTime;

    public Tick(Date timestamp, String localTime) {
        this.timestamp = timestamp;
        this.localTime = localTime;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }
}
