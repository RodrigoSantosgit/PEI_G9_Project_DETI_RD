package com.example.listapp;

import android.util.Log;

public class Room {

    private String name;
    private Event currentEvent;
    private int id;

    public Room(String n, Event e, int i) {
        name = n;
        currentEvent = e;
        id = i;
    }

    public int getId() {return id;}

    public int getNumber() {
        return Integer.parseInt(name.replace(".",""));
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Room r) {
        //Log.e("Time: ", "Aqui!!!!!!!!");
        return name.equals(r.getName());}

}
