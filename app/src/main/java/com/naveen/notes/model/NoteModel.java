package com.naveen.notes.model;

import java.util.ArrayList;

public class NoteModel {
    private String title;
    private String info;
    private String category;
    private ArrayList<String> locations;
    private byte[] image;
    private String voice;
    private String time;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public ArrayList<String> getLocations() {
        return locations;
    }
    public void setLocations(ArrayList<String> locations) {
        this.locations = locations;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
    public String getVoice() {
        return voice;
    }
    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}
