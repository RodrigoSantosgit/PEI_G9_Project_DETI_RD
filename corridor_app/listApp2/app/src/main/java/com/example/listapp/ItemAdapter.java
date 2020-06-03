package com.example.listapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class ItemAdapter extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected MainActivity activity;

    public ItemAdapter(Context c) {
        activity = (MainActivity) c;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return MainActivity.roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.my_listview_detail, null);
        TextView eventoText = (TextView) v.findViewById(R.id.eventoText);
        String evento = MainActivity.roomList.get(i).getCurrentEvent().getName();
        if (evento.equals("Livre")) {
            eventoText.setTextColor(Color.parseColor(MainActivity.green));
        } else {
            eventoText.setTextColor(Color.parseColor("#000000"));
        }
        if (MainActivity.currentEventId == i) {
            if (evento.equals("Livre")) {
                eventoText.setBackgroundColor(Color.parseColor(MainActivity.green));
            } else {
                eventoText.setBackgroundColor(Color.parseColor(MainActivity.red));
            }
        } else {
            eventoText.setBackgroundColor(Color.parseColor("#00000000"));
        }
        eventoText.setText(MainActivity.roomList.get(i).getName());
        activity.updateView();
        return v;
    }


}
