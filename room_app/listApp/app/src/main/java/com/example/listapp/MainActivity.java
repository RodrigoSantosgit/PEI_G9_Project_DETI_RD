package com.example.listapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import static java.lang.Character.isDigit;

public class MainActivity extends AppCompatActivity {

    protected static ListView myListView;
    protected static Calendar calendar;
    protected static int currentEventId;
    protected static Loop looperThread;
    protected static LinkedList<Event> fullList;
    protected static ItemAdapter itemAdapter;
    protected static TextView currentEventTextView;
    protected static TextView currentEventTopTextView;
    protected static TextView nextEventTextView;
    protected static TextView nextEventTopTextView;
    protected static TextView currentEventDescrTextView;
    protected static TextView livreTextView;
    protected static TextView timeTextView;
    protected static View currentEventView;
    protected static View nextEventView;
    protected static final String lightGreen = "#8053AE6D";
    protected static final String green = "#53AE6D";
    //protected static final String red = "#DE5334";
    protected static final String red = "#FA6D56";
    //protected static final String red = "#EA5454";
    protected static final String lightRed = "#80FA6D56";
    protected static int currentTime;
    protected static RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myListView = (ListView) findViewById(R.id.myListView);
        calendar = new Calendar();
        looperThread = new Loop();
        currentEventTextView = findViewById(R.id.currentEventTextView);
        nextEventTextView = findViewById(R.id.nextEventTextView);
        currentEventTopTextView = findViewById(R.id.currentEventTopTextView);
        nextEventTopTextView = findViewById(R.id.nextEventTopTextView);
        currentEventDescrTextView = findViewById(R.id.currentEventDescrTextView);
        currentEventView = findViewById(R.id.currentEventView);
        livreTextView = findViewById(R.id.livreTextView);
        nextEventView = findViewById(R.id.nextEventView);
        timeTextView = findViewById(R.id.timeTextView);

        mQueue = Volley.newRequestQueue(this);
        myListView.setDivider(null);
        String currentTimeStr = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
        currentTime = Integer.parseInt(currentTimeStr);
        //jsonParse();



        fullList = calendar.getFullList();
        itemAdapter = new ItemAdapter(this);
        myListView.setAdapter(itemAdapter);

        new Thread() {
            public void run() {
                loop();
            }
        }.start();
    }

    public void addEvent(Event e) {
        //Log.e("Time: ", "Testeeeeee");
        calendar.addEvent(e);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fullList = calendar.getFullList();
                itemAdapter.notifyDataSetChanged();
            }
        });
        //updateView();
    }

    public void clearCalendar() {
        calendar.clearCalendar();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fullList = calendar.getFullList();
                itemAdapter.notifyDataSetChanged();
            }
        });
        //currentEventId = 0;
        //updateView();
    }

    public void loop() {
        boolean flag = false;
        int i = -1;
        int t = 800;

        Event e = new Event(900, 1030, "ALGA", "Álgebra Linear e Geometria Analítica\nProfessor: José Sousa\nCurso: ECT");
        //addEvent(e);
        Event e2 = new Event(1130, 1300, "AC1", "Arquitetura de Computadores 1\nProfessor: Pedro Lavrador\nCurso: EET");
        //addEvent(e2);
        Event e3 = new Event(1300, 1400, "LFA", "Linguagens Formais e Autómatos\nProfessor: Artur Pereira\nCurso: ECT");
        //addEvent(e3);


        while(true) {

            String currentTimeStr = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
            //currentTime = Integer.parseInt(currentTimeStr);
            currentTime = t;

            jsonParse();
            for (i = 0; i < fullList.size(); i++) {
                if (fullList.get(i).isCurrent(currentTime)) {
                    //Log.e("Current: ", i + "");
                    break;
                }
            }

            if (i >= fullList.size()) i = -1;
            currentEventId = i;
            updateView();
            if(i == -1) i = 0;
            myListView.smoothScrollToPosition(i);
            SystemClock.sleep(400);
            updateView();
            SystemClock.sleep(4000);
            t = t + 100;
            //i++;
            if (i == 3 && flag) {
                flag = false;
                e = new Event(1500, 1630, "P3", "Programação 3\n Professor: Carlos Costa\nCurso: ECT");
                clearCalendar();
                addEvent(e);

                Event e4 = new Event(1900, 1930, "P1", "Programação 1\nProfessor: João Costa\nCurso: Matemática");
                addEvent(e4);
                jsonParse();
            }
            if (i >= fullList.size()) i = 0;
            if (t >= 2100) t = 800;
        }
    }

    protected static void updateView(){

        if (currentEventId == -1) {
            currentEventView.setBackgroundColor(Color.parseColor("#00000000"));
            nextEventView.setBackgroundColor(Color.parseColor("#00000000"));
            timeTextView.setText("");
            currentEventTextView.setText("");
            nextEventTextView.setText("");
            livreTextView.setText("");
            nextEventTopTextView.setText("");
            currentEventTopTextView.setText("");
            for(int index = 0; index < fullList.size(); index++) {
                View v = myListView.getChildAt(index - myListView.getFirstVisiblePosition());
                if (v != null) {
                    TextView eventoText = (TextView) v.findViewById(R.id.eventoText);
                    String evento = fullList.get(index).getName();
                    if (evento.equals("Livre")) {
                        eventoText.setTextColor(Color.parseColor(green));
                    }
                    eventoText.setBackgroundColor(Color.parseColor("#00000000"));
                }
            }
        } else {
            nextEventTopTextView.setText("Depois");
            currentEventTopTextView.setText("Agora");
            Event currentEvent = fullList.get(currentEventId);
            String text = currentEvent.getName();
            timeTextView.setText(currentEvent.getTime());
            if (currentEvent.getDescription() != null) {
                currentEventDescrTextView.setText(currentEvent.getDescription());
            } else {
                currentEventDescrTextView.setText("");
            }
            if (text.equals("Livre")) {
                currentEventView.setBackgroundColor(Color.parseColor(green));
                //timeTextView.setText("");
                currentEventTextView.setText("");
                livreTextView.setText("Livre");
                //livreTextView.setTextSize(100);
            } else if ((currentEventDescrTextView.getText()+"") != ""){
                currentEventView.setBackgroundColor(Color.parseColor(red));
                //timeTextView.setText(currentEvent.getTime());
                livreTextView.setText("");
                currentEventTextView.setText(text);
                //currentEventTextView.setTextSize(60);
                //if (text.length() > 20) currentEventTextView.setTextSize(30);
            } else {
                currentEventView.setBackgroundColor(Color.parseColor(red));
                //timeTextView.setText(currentEvent.getTime());
                currentEventTextView.setText("");
                livreTextView.setText(text);
                //livreTextView.setTextSize(70);
                //if (text.length() > 30) livreTextView.setTextSize(50);
            }
            if (currentEventId + 1 >= fullList.size()) {
                nextEventTextView.setText("");
                nextEventView.setBackgroundColor(Color.parseColor("#00000000"));
                nextEventTopTextView.setTextColor(Color.parseColor("#00000000"));
            } else {
                String text2 = fullList.get(currentEventId + 1).getName();
                nextEventTextView.setText(text2);
                nextEventTopTextView.setTextColor(Color.parseColor("#000000"));
                if (text2.equals("Livre")) {
                    nextEventView.setBackgroundColor(Color.parseColor(lightGreen));
                } else {
                    nextEventView.setBackgroundColor(Color.parseColor(lightRed));
                }
            }

            for (int index = 0; index < fullList.size(); index++) {
                View v = myListView.getChildAt(index - myListView.getFirstVisiblePosition());
                if (v != null) {
                    TextView eventoText = (TextView) v.findViewById(R.id.eventoText);
                    String evento = fullList.get(index).getName();
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
        //String url = "http://192.168.160.82:5000/mobile/room?room=04.1.01";
        String url = "http://192.168.160.82:5000/mobile/room?room=04.1.28";
        //String url = "http://www.json-generator.com/api/json/get/cedGpfmvkO?indent=2";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("events");
                            clearCalendar();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject evento = jsonArray.getJSONObject(i);
                                //Log.e("Time: ", evento.getString("begin"));
                                int begin = Integer.parseInt(dateConverter(evento.getString("begin")));
                                int end = Integer.parseInt(dateConverter(evento.getString("end")));
                                //int begin = evento.getInt("begin");
                                //int end = evento.getInt("end");
                                //String title = evento.getString("title");
                                String title = evento.getString("name");
                                Event e;
                                if (evento.has("tipo")) {
                                    String description = evento.getString("tipo");
                                    if (description.equals("Aula")) {
                                        title = title.split("#")[0];
                                        String[] ar = title.split("-");
                                        if(ar.length>2) {
                                            if (ar[ar.length - 2].equals("P"))
                                                description += " Prática";
                                            else if (ar[ar.length - 2].equals("TP"))
                                                description += " Teórico-Prática";
                                            else if (ar[ar.length - 2].equals("T"))
                                                description += " Teórica";
                                            title = "";
                                            for (int j = 1; j < ar.length - 2; j++) {
                                                if (j != 1) title += "-";
                                                title += ar[j];
                                            }
                                            if (isDigit(ar[0].charAt(0))) {
                                                description += "\nCódigo: " + ar[0];
                                            } else {
                                                title = ar[0] + title;
                                            }
                                            description += "\nTurma: " + ar[ar.length - 1];
                                        }
                                        title = title.replace("-turmas", " ");
                                        Log.e("Erro: ", "("+title+")");
                                    }
                                    e = new Event(begin, end, title, "Tipo: "+description);
                                } else {
                                    e = new Event(begin, end, title);
                                }
                                addEvent(e);
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

    private String dateConverter(String date) {
        String[] ar = date.split(" ");
        if (ar.length > 1) ar = ar[1].split(":");
        else ar = ar[0].split(":");
        //Log.e("Time: ", ar[0]+ar[1]);
        return ar[0] + ar[1];
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
Log.e("Erro: ", "Aquiiiiiiiiiiiiiiii");


 */


