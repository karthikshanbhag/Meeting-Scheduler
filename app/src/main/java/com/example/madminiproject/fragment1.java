package com.example.madminiproject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class fragment1 extends Fragment {
    EditText date, time, agenda;
    Button btn;
    DataBaseConn dbc;
    RelativeLayout rel;
    TextView reci;
    ArrayList<Integer> rec = new ArrayList<Integer>();
    Calendar c = Calendar.getInstance();
    String[] req, emails;
    int mY = c.get(Calendar.YEAR);
    int mM = c.get(Calendar.MONTH);
    int mD = c.get(Calendar.DAY_OF_MONTH);

    // Alarm reminder variables
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);
        date = view.findViewById(R.id.txtDate);
        time = view.findViewById(R.id.txtTime);
        agenda = view.findViewById(R.id.txtAgenda);
        btn = view.findViewById(R.id.btn1);
        reci = view.findViewById(R.id.reci);
        rel = view.findViewById(R.id.rel);
        dbc = new DataBaseConn(getContext());
        date.setInputType(0);
        time.setInputType(0);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datedailog();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timedailog();
            }
        });

        //Intent intent = new Intent(getContext(), AlarmReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        // Initialize alarm manager and alarm intent
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(getContext(), AlarmReceiver.class), 0);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mdate, mTime, mAgenda;
                mdate = date.getText().toString();
                mTime = time.getText().toString();
                mAgenda = agenda.getText().toString();
                if (mdate.length() != 0 && mTime.length() != 0 && mAgenda.length() != 0 && reci.getText().toString().length() != 0) {
                    insert(mdate, mTime, mAgenda);
                    scheduleAlarmReminder(mdate, mTime, mAgenda);
                    mail(mAgenda,mTime);
                    rec.clear();
                } else {
                    Toast.makeText(getActivity(), "Fill all contents", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                Cursor c = dbc.getr();
                int count = c.getCount();
                boolean[] selreci;
                c.moveToFirst();
                String[] recip = new String[count];
                req = new String[count];
                emails = new String[count];
                if (count > 0) {
                    int k = 0;
                    do {
                        req[k] = recip[k] = c.getString(c.getColumnIndex("name"));
                        k++;
                    } while (c.moveToNext());
                } else {
                    Toast.makeText(getActivity(), "No recipients", Toast.LENGTH_LONG).show();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                selreci = new boolean[recip.length];
                // set title
                builder.setTitle("Select user");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(recip, selreci, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            rec.add(i);
                            // Sort array list
                            Collections.sort(rec);
                        } else {
                            rec.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < rec.size(); j++) {
                            stringBuilder.append(recip[rec.get(j)]);
                            if (j != rec.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        String noDuplicates = Arrays.asList(stringBuilder.toString().split(", "))
                                .stream()
                                .distinct()
                                .collect(Collectors.joining(", "));
                        reci.setText(noDuplicates);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selreci.length; j++) {
                            selreci[j] = false;
                            rec.clear();
                            reci.setText("");
                        }
                    }
                });
                builder.show();
            }
        });

        return view;
    }

    private void datedailog() {
        DatePickerDialog dt = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                date.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y));
            }
        }, mY, mM, mD);
        dt.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dt.show();
    }

    private void timedailog() {
        TimePickerDialog tm = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int h, int m) {
                int hour = h % 12;
                if (hour == 0) hour = 12;
                String _AM_PM = (h > 12) ? "PM" : "AM";
                time.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hour, m, _AM_PM));
            }
        }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, false);
        tm.show();
    }

    public void insert(String mdate, String mTime, String mAgenda) {
        Boolean insert = dbc.insertvalue(mdate, mTime, mAgenda, reci.getText().toString());
        if (insert) {
            Toast.makeText(getActivity(), "Data Inserted", Toast.LENGTH_SHORT).show();
            date.setText("");
            time.setText("");
            agenda.setText("");
            reci.setText("");
            reci.setHint("Select Recipient");
        } else {
            Toast.makeText(getActivity(), "Data NOT Inserted", Toast.LENGTH_SHORT).show();
        }
    }

    // Inside the scheduleAlarmReminder method
    private void scheduleAlarmReminder(String mdate, String mTime, String mAgenda) {
        long alarmTimeMillis = convertDateTimeToMillis(mdate, mTime);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("agenda", mAgenda); // Add agenda as extra data
        alarmIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, alarmIntent);
        Toast.makeText(getActivity(), "Alarm reminder set for the meeting", Toast.LENGTH_SHORT).show();
    }


    private long convertDateTimeToMillis(String date, String time) {
        try {
            String dateTimeString = date + " " + time;
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            Date dateTime = format.parse(dateTimeString);
            return dateTime.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void mail(String msg,String t){
        String uname = "myextest36@gmail.com";
        String pass = "igdinafbolmznfqa";
        final String[] email = new String[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int j = 0; j < req.length; j++) {
                        Cursor c = dbc.getemail(req[j]);
                        c.moveToFirst();
                        int count = c.getCount();
                        if (count > 0) {
                            email[0] = c.getString(c.getColumnIndex("email"));
                            emails[j] = email[0];
                        } else {
                            Toast.makeText(getActivity(), "No recipients", Toast.LENGTH_LONG).show();
                        }
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < emails.length; j++) {
                        stringBuilder.append(emails[j]);
                        if (j != emails.length - 1) {
                            stringBuilder.append(",");
                        }
                    }
                    email[0] = stringBuilder.toString();
                    Properties prop = new Properties();
                    prop.put("mail.smtp.auth", "true");
                    prop.put("mail.smtp.starttls.enable", "true");
                    prop.put("mail.smtp.host", "smtp.gmail.com");
                    prop.put("mail.smtp.port", "587");
                    Session s = Session.getInstance(prop, new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(uname, pass);
                        }
                    });

                    Message m = new MimeMessage(s);
                    m.setFrom(new InternetAddress(uname));
                    m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email[0]));
                    m.setSubject("Invitation for meeting");
                    m.setText("Hello,\nPlease attend the meeting regarding "+msg+" on "+t+".");
                    Transport.send(m);
                    //Toast.makeText(getContext(), "successful", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
