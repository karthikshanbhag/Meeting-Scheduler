package com.example.madminiproject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class fragment2 extends Fragment {
    EditText date;
    Button btn1;
    DataBaseConn dbc;
    CalendarView cal;
    TextView cont;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment2, container, false);
        date = view.findViewById(R.id.edittextDate);
        btn1 = view.findViewById(R.id.btn2);
        dbc = new DataBaseConn(getContext());
        cal=view.findViewById(R.id.cal);
        cont=view.findViewById(R.id.cont);
        date.setInputType(0);
        cal.setVisibility(view.GONE);
        cont.setVisibility(view.GONE);

//        Cursor c1 = dbc.fetch(date.getText().toString());
//        int count1 = c1.getCount();
//        c1.moveToFirst();
//        if (count1 > 0) {
//            do {
//
//
//            } while (c1.moveToNext());
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.setVisibility(view.VISIBLE);
                cont.setVisibility(view.GONE);
            }

        });
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month+1,year));
            }        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                String d1 = date.getText().toString();
                StringBuffer res = new StringBuffer();
                Cursor c = dbc.fetch(d1);
                int count = c.getCount();
                c.moveToFirst();
                if (count > 0) {
                    do {
                        res.append("\nAgenda : "+c.getString(c.getColumnIndex("agenda")) + "\nTime: " + c.getString(c.getColumnIndex("time"))+"\nParticipants: "+c.getString(c.getColumnIndex("partic"))+"\n");
                        res.append("\n");
                    } while (c.moveToNext());
                    cal.setVisibility(view.GONE);
                    cont.setVisibility(view.VISIBLE);
                    cont.setMovementMethod(new ScrollingMovementMethod());
                    cont.setText(res);
                } else {
                    Toast.makeText(getActivity(), "No Meeting on This Day....", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }
}