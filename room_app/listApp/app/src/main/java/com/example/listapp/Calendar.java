package com.example.listapp;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class Calendar {

    private LinkedList<Event> list;
    private int open;
    private int close;

    public Calendar() {
        list = new LinkedList<Event>();
        open = 800;
        close = 2000;
    }

    public Event get(int i) {
        return list.get(i);
    }

    public LinkedList<Event> getFullList() {
        LinkedList<Event> list2 = (LinkedList) list.clone();
        int begin = open;
        int end = 0;
        while (end < close) {
            Event e2 = null;
            Event e = getByBegin(begin);
            if (e != null) {
                begin = e.getEnd();
            }
            end = begin;
            while(end < close) {
                e2 = getByBegin(end);
                if (e2 != null) {
                    end = e2.getBegin();
                    break;
                }
                if (end % 100 == 0){
                    end = end + 30;
                } else {
                    end = (end/100 + 1) * 100;
                }
            }

            if (begin < close) {
                Event free = new Event(begin, end, "Livre");
                list2.add(free);
            }

            if (e2 != null) begin = e2.getEnd();
        }
        return (LinkedList<Event>) sortList(list2);
    }

    public Event getByBegin(int b) {
        int i = 0;
        while (i < list.size()) {
            if (b == list.get(i).getBegin()) {
                return list.get(i);
            }
            i++;
        }
        return null;
    }

    public int size() {
        return list.size();
    }

    public boolean addEvent(Event e) {
        if (!colides(e)) {
            list.add(e);
            return true;
        }
        return false;
    }

    public boolean clearCalendar() {
        list.clear();
        return list.isEmpty();
    }

    public boolean colides(Event e) {
        int i = 0;
        while(i < list.size()) {
            if (list.get(i).colides(e)) return true;
            i++;
        }
        return false;
    }

    private static List<Event> sortList(List<Event> l) {
        int min;
        int i = 0;
        int j;
        while(i < l.size()) {
            min = l.get(i).getBegin();
            j = i+1;
            while (j < l.size()) {
                if (l.get(j).getBegin() < min){
                    min = l.get(j).getBegin();
                    Event aux = l.get(j);
                    l.set(j, l.get(i));
                    l.set(i, aux);
                }
                j++;
            }
            i++;
        }
        return l;
    }

}
