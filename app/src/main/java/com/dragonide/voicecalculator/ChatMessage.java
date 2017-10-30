package com.dragonide.voicecalculator;

import android.text.format.DateFormat;

import java.util.concurrent.TimeUnit;

public class ChatMessage {
    private String message;
    private long timestamp;
    private Type type;
    private String processTime;

    public ChatMessage(String message, long timestamp, Type type, String processTime) {
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.processTime = processTime;
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public String getFormattedTime(){

        long oneDayInMillis = TimeUnit.DAYS.toMillis(1); // 24 * 60 * 60 * 1000;

        long timeDifference = System.currentTimeMillis() - timestamp;

        return timeDifference < oneDayInMillis
                ? DateFormat.format("hh:mm a", timestamp).toString()
                : DateFormat.format("dd MMM - hh:mm a", timestamp).toString();
    }

    public enum Type {
        SENT, RECEIVED
    }
}
