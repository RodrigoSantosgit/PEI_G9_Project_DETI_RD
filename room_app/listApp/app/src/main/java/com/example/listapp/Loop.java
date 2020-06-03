package com.example.listapp;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

public class Loop extends Thread {

    public Handler handler;

    public void run() {
        boolean flag = false;
        int i = 0;
        Event e = new Event(1900, 1930, "ALGA");
        //MainActivity.addEvent(e);

        while(true) {

            MainActivity.currentEventId = i;
            String text = MainActivity.fullList.get(i).getName();
            MainActivity.currentEventTextView.setText(text);
            if (text.equals("Livre")) {
                MainActivity.currentEventView.setBackgroundColor(Color.parseColor("#53AE6D"));
            } else {
                MainActivity.currentEventView.setBackgroundColor(Color.parseColor("#DE5334"));
            }
            MainActivity.updateView();
            SystemClock.sleep(2000);
            i++;
            if (i >= MainActivity.fullList.size()) i = 0;

            if (i == 3 && flag) {
                flag = false;
                Event e2 = new Event(1500, 1630, "P1");
                //MainActivity.addEvent(e);
            }

        }
    }
}
