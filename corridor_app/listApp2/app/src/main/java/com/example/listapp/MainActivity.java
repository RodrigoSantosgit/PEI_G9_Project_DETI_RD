package com.example.listapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    protected Activity activity = this;
    protected static ListView myListView;
    protected static Building building;
    protected static int currentEventId;
    protected static LinkedList<Room> roomList;
    protected static ItemAdapter itemAdapter;
    //protected static TextView currentEventTextView;
    //protected static TextView currentEventDescrTextView;
    //protected static TextView livreTextView;
    protected static TextView codeTextView;
    protected static Button floorButton1;
    protected static Button floorButton2;
    protected static Button floorButton3;
    protected static ImageView mapImageView;
    protected static final String lightGreen = "#8053AE6D";
    protected static final String green = "#53AE6D";
    //protected static final String red = "#DE5334";
    protected static final String red = "#FA6D56";
    //protected static final String red = "#EA5454";
    protected static final String lightRed = "#80FA6D56";
    protected static final String blue = "#328BF5";
    protected static final String grey = "#DADADA";
    protected static int currentTime;
    protected static RequestQueue mQueue;
    protected static int floor;
    protected static int clicks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        myListView = (ListView) findViewById(R.id.myListView);
        building = new Building(3);
        codeTextView = findViewById(R.id.codeTextView);
        //currentEventTextView = findViewById(R.id.currentEventTextView);
        //currentEventDescrTextView = findViewById(R.id.currentEventDescrTextView);
        //livreTextView = findViewById(R.id.livreTextView);
        floorButton1 = findViewById(R.id.floorButton1);
        floorButton2 = findViewById(R.id.floorButton2);
        floorButton3 = findViewById(R.id.floorButton3);
        mapImageView = findViewById(R.id.mapImageView);

        floorButton1.setBackgroundColor(Color.parseColor(blue));
        floorButton2.setBackgroundColor(Color.parseColor(grey));
        floorButton3.setBackgroundColor(Color.parseColor(grey));

        mQueue = Volley.newRequestQueue(this);
        //myListView.setDivider(null);
        String currentTimeStr = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
        currentTime = Integer.parseInt(currentTimeStr);
        //jsonParse();

        floor = 0;
        currentEventId = -1;
        roomList = building.getFloor(floor);

        itemAdapter = new ItemAdapter(this);
        myListView.setAdapter(itemAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentEventId = position;
                updateView();
                new Thread() {
                    public void run() {
                        loop2();
                    }
                }.start();
            }
        });

        floorButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFloor(0);
                floorButton1.setBackgroundColor(Color.parseColor(blue));
                floorButton2.setBackgroundColor(Color.parseColor(grey));
                floorButton3.setBackgroundColor(Color.parseColor(grey));
                currentEventId = -1;
                updateView();
                new Thread() {
                    public void run() {
                        loop2();
                    }
                }.start();
            }
        });

        floorButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFloor(1);
                floorButton2.setBackgroundColor(Color.parseColor(blue));
                floorButton1.setBackgroundColor(Color.parseColor(grey));
                floorButton3.setBackgroundColor(Color.parseColor(grey));
                currentEventId = -1;
                updateView();
                new Thread() {
                    public void run() {
                        loop2();
                    }
                }.start();
            }
        });

        floorButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFloor(2);
                floorButton3.setBackgroundColor(Color.parseColor(blue));
                floorButton2.setBackgroundColor(Color.parseColor(grey));
                floorButton1.setBackgroundColor(Color.parseColor(grey));
                currentEventId = -1;
                updateView();
                new Thread() {
                    public void run() {
                        loop2();
                    }
                }.start();
            }
        });

        //changeCode("http://192.168.160.82/home/departments_book/11/");

        Event e = new Event(1130, 1300, "AC1", "Arquitetura de Computadores 1\nProfessor: Pedro Lavrador\nCurso: EET");
        Room r = new Room("4.2.7", e, 0);
        addRoom(1, r);
        e = new Event(1300, 1400, "LFA", "Linguagens Formais e Autómatos\nProfessor: Artur Pereira\nCurso: ECT");
        r = new Room("4.2.3", e, 0);
        addRoom(1, r);
        e = new Event(1900, 1930, "P1", "Programação 1\nProfessor: João Costa\nCurso: Matemática");
        r = new Room("4.2.11", e, 0);
        addRoom(1, r);
        e = new Event(1130, 1300, "Livre", null);
        r = new Room("4.2.17", e, 0);
        addRoom(1,r);
        e = new Event(1130, 1300, "Livre", null);
        r = new Room("4.2.15", e, 0);
        addRoom(1,r);

        e = new Event(1130, 1300, "AC1", "Arquitetura de Computadores 1\nProfessor: Pedro Lavrador\nCurso: EET");
        r = new Room("4.1.2", e, 0);
        addRoom(0, r);
        e = new Event(1130, 1300, "Livre", null);
        r = new Room("4.1.6", e, 0);
        addRoom(0,r);
        e = new Event(1130, 1300, "Livre", null);
        r = new Room("4.1.19", e, 0);
        addRoom(0,r);

        //Log.e("Teste ", "Teste");
        //Log.e("Event: ", roomList.get(0).getCurrentEvent() + "");

        new Thread() {
            public void run() {
                loop();
            }
        }.start();

    }

    public void addRoom(final int i, Room r) {
        building.addRoom(i,r);
        if (floor == i)
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roomList = building.getFloor(i);
                itemAdapter.notifyDataSetChanged();
            }
        });
        //updateView();
    }

    public void addRoom(Room r) {
        //Log.e("Teste ", r.getName());
        int i = Integer.parseInt(r.getName().split("\\.")[1]) - 1;
        addRoom(i, r);
    }

    public void clearFloor(final int i) {
        building.clearFloor(i);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roomList = building.getFloor(i);
                itemAdapter.notifyDataSetChanged();
            }
        });
        //currentEventId = 0;
        //updateView();
    }

    public void clearFloor() {
        building.clearFloor(0);
        building.clearFloor(1);
        building.clearFloor(2);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roomList = building.getFloor(floor);
                itemAdapter.notifyDataSetChanged();
            }
        });
        //currentEventId = 0;
        //updateView();
    }

    public void changeFloor(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roomList = building.getFloor(i);
                floor = i;
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    public static String getMapName(String n) {
        return "r" + n.replace(".", "_");
    }

    public void loop() {
        while(true) {

            String currentTimeStr = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
            currentTime = Integer.parseInt(currentTimeStr);
            //currentTime = t;
            jsonParse();
            updateView();
            SystemClock.sleep(300);
            updateView();
            SystemClock.sleep(10000);
        }
    }

    public void loop2() {
        clicks++;
        int x = clicks;
        for(int i = 0; i < 8; i++) {
            SystemClock.sleep(1000);
        }
        //Log.e("Teste ", "Teste");
        if (clicks == x) {
            changeFloor(0);
            floorButton1.setBackgroundColor(Color.parseColor(blue));
            floorButton2.setBackgroundColor(Color.parseColor(grey));
            floorButton3.setBackgroundColor(Color.parseColor(grey));
            currentEventId = -1;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateView();
            }
        });
    }

    protected void updateView(){

        if (currentEventId == -1) {
            for(int index = 0; index < roomList.size(); index++) {
                codeTextView.setText("Web App");
                View v = myListView.getChildAt(index - myListView.getFirstVisiblePosition());
                if (v != null) {
                    TextView eventoText = (TextView) v.findViewById(R.id.eventoText);
                    String evento = roomList.get(index).getCurrentEvent().getName();
                    if (evento.equals("Livre")) {
                        eventoText.setTextColor(Color.parseColor(green));
                    }
                    eventoText.setBackgroundColor(Color.parseColor("#00000000"));
                }
            }
            if (roomList.size() != 0)
            mapImageView.setImageResource(activity.getResources().getIdentifier("r4_"+(floor+1)+"_0", "drawable", getPackageName()));
            //changeCode("http://192.168.160.82/home/departments_book/11/");
            ((ImageView) findViewById(R.id.QRCode)).setImageResource(activity.getResources().getIdentifier("c4", "drawable", getPackageName()));
        } else {
            codeTextView.setText("Book Room");
            Event currentEvent = roomList.get(currentEventId).getCurrentEvent();
            String text = currentEvent.getName();
            //currentEventTextView.setText(text);
            Log.e("Time: ", getMapName(roomList.get(currentEventId).getName()));
            mapImageView.setImageResource(activity.getResources().getIdentifier(getMapName(roomList.get(currentEventId).getName()), "drawable", getPackageName()));
            //changeCode("http://192.168.160.82/home/departments_book/11/rooms/"+roomList.get(currentEventId).getId());
            changeCode("http://ieeta-iot.web.ua.pt/booking/home/departments_book/11/rooms/"+roomList.get(currentEventId).getId());
            if (currentEvent.getDescription() != null) {
                //currentEventDescrTextView.setText(currentEvent.getDescription());
            } else {
                //currentEventDescrTextView.setText("");
            }
            /*
            if (text.equals("Livre")) {
                currentEventView.setBackgroundColor(Color.parseColor(green));
                timeTextView.setText("");
                currentEventTextView.setText("");
                livreTextView.setText("Livre");
            } else {
                currentEventView.setBackgroundColor(Color.parseColor(red));
                timeTextView.setText(currentEvent.getTime());
                livreTextView.setText("");
            }
            */
            for (int index = 0; index < roomList.size(); index++) {
                View v = myListView.getChildAt(index - myListView.getFirstVisiblePosition());
                if (v != null) {
                    TextView eventoText = (TextView) v.findViewById(R.id.eventoText);
                    String evento = roomList.get(index).getCurrentEvent().getName();
                    if (currentEventId == index) {
                        if (evento.equals("Livre")) {
                            eventoText.setBackgroundColor(Color.parseColor(green));
                            eventoText.setTextColor(Color.parseColor("#000000"));
                        } else {
                            eventoText.setBackgroundColor(Color.parseColor(red));
                        }
                    } else {
                        if (evento.equals("Livre")) {
                            eventoText.setTextColor(Color.parseColor(green));
                        }
                        eventoText.setBackgroundColor(Color.parseColor("#00000000"));
                    }
                }
            }
        }

    }

    private void jsonParse() {
        //String url = "http://www.json-generator.com/api/json/get/bZNXtHyvJu?indent=2";
        //String url = "http://192.168.160.82:5000/mobile/corridor";
        String url[] = {"http://192.168.160.82:5000/mobile/corridor?floor=04.1", "http://192.168.160.82:5000/mobile/corridor?floor=04.2", "http://192.168.160.82:5000/mobile/corridor?floor=04.3"};
        final boolean change[] = {false};
        for (int i = 0; i < url.length; i++) {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url[i], null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("free");
                                if(!change[0]) {
                                    clearFloor();
                                    change[0] = true;
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject evento = jsonArray.getJSONObject(i);
                                    String name = evento.getString("name").replace(".0", ".").replace("04", "4");
                                    int id = evento.getInt("id");
                                    //int begin = evento.getInt("begin");
                                    //int end = evento.getInt("end");
                                    //String title = evento.getString("title");
                                    //String description = evento.getString("description");
                                    Event e = new Event(0, 0, "Livre", null);
                                    Room r = new Room(name, e, id);
                                    addRoom(r);
                                }
                                jsonArray = response.getJSONArray("occupied");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject evento = jsonArray.getJSONObject(i);
                                    String name = evento.getString("name").replace(".0", ".").replace("04", "4");
                                    int id = evento.getInt("id");
                                    //int begin = evento.getInt("begin");
                                    //int end = evento.getInt("end");
                                    //String title = evento.getString("title");
                                    //String description = evento.getString("description");
                                    Event e = new Event(0, 0, "", null);
                                    Room r = new Room(name, e, id);
                                    addRoom(r);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            mQueue.add(request);
        }
    }

    private void changeCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.QRCode)).setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}

/*

String url = "http://www.json-generator.com/api/json/get/bVgYYlSAqG?indent=2";
Event e = new Event(900, 1030, "ALGA", "Álgebra Linear e Geometria Analítica\nProfessor: José Sousa\nCurso: ECT");
calendar.addEvent(e);
Event e2 = new Event(1130, 1300, "AC1", "Arquitetura de Computadores 1\nProfessor: Pedro Lavrador\nCurso: EET");
calendar.addEvent(e2);
Event e3 = new Event(1300, 1400, "LFA", "Linguagens Formais e Autómatos\nProfessor: Artur Pereira\nCurso: ECT");
calendar.addEvent(e3);
Event e = new Event(1900, 1930, "P1", "Programação 1\nProfessor: João Costa\nCurso: Matemática");
addEvent(e);
Log.e("Time: ", "Aqui!!!!!!!!");


 */


