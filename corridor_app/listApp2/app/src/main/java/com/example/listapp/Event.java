package com.example.listapp;

import java.util.Scanner;

public class Event implements Comparable<Event>{

    private int begin;
    private int end;
    private String name;
    private String time;
    private String description;

    public Event(int b, int e, String n) {
        begin = b;
        end = e;
        name = n;
        int bh = b/100;
        int bm = (b-bh*100)/10;
        int eh = e/100;
        int em = (e-eh*100)/10;
        time = bh + ":" + bm + "0" + "-" + eh + ":" + em + "0";
    }

    public Event(int b, int e, String n, String d) {
        this(b, e, n);
        description = d;
    }

    public Event(String str, String n) {
        name = n;
        time = str;
        str = str.replace(':',' ').replace('-',' ');
        Scanner sc = new Scanner(str);
        begin = 100 * sc.nextInt() + sc.nextInt();
        end = 100 * sc.nextInt() + sc.nextInt();
    }

    public boolean colides(Event e) {
        return ((e.begin > begin && e.begin < end) || (e.end > begin && e.end < end) || (begin > e.begin && begin < e.end) || (end > e.begin && end < e.end) || (begin == e.begin && end == e.end));
    }

    public boolean isCurrent(int currentTime) {
        return (currentTime >= begin && currentTime < end);
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return time + "\t" + name;
    }

    public int compareTo(Event e) {
        if (begin > e.getBegin()) return 1;
        else return 0;
    }

}
