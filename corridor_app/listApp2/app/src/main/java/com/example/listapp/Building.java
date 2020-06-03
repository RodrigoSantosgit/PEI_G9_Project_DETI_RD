package com.example.listapp;

import java.util.LinkedList;

public class Building {

    private LinkedList<Room>[] floor;

    public Building(int n) {
        floor = new LinkedList[n];
        for (int i = 0; i < n; i++) {
            floor[i] = new LinkedList<>();
        }
    }

    public void addRoom(int i, Room e) {
        boolean b = true;
        for(int x = 0; x < floor[i].size(); x++) {
            if (floor[i].get(x).equals(e)) b = false;
        }
        if (b) floor[i].add(e);
    }

    public boolean clearFloor(int i) {
        floor[i].clear();
        return floor[i].isEmpty();
    }

    public LinkedList<Room> getFloor(int i) {
        return sortFloor(floor[i]);
    }

    public LinkedList<Room> sortFloor(LinkedList<Room> l) {
        int min;
        int i = 0;
        int j;
        while(i < l.size()) {
            min = l.get(i).getNumber();
            j = i+1;
            while (j < l.size()) {
                if (l.get(j).getNumber() < min){
                    min = l.get(j).getNumber();
                    Room aux = l.get(j);
                    l.set(j, l.get(i));
                    l.set(i, aux);
                }
                j++;
            }
            i++;
        }
        i = 0;
        j = 0;
        while(i < l.size()) {
            Room aux = l.get(i);
            if (aux.getCurrentEvent().getName().equals("Livre")){
                l.remove(i);
                l.add(j,aux);
                j++;
            }
            i++;
        }
        return l;
    }



}
