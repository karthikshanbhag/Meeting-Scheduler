package com.example.madminiproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {
    private TabLayout tab;
    private ViewPager vp;
    ImageButton b1;
    DataBaseConn dbc;
    ArrayList<Integer> rec= new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tab=findViewById(R.id.tabs);
        vp=findViewById(R.id.view_pager);
        b1=findViewById(R.id.rm);
        dbc = new DataBaseConn(this);
        tab.addTab(tab.newTab().setText("Schedule Meeting"));
        tab.addTab(tab.newTab().setText("View Meeting"));
        vp.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(),FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        fragment1 frag1 = new fragment1();
                        return frag1;
                    case 1:
                        fragment2 frag2=new fragment2();
                        return frag2;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return tab.getTabCount();
            }
        });
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                Cursor c = dbc.getr();
                int count = c.getCount();
                boolean[] selreci;
                c.moveToFirst();
                String[] recip=new String[count];
                if (count > 0) {
                    int i=0;
                    do {
                        recip[i]=c.getString(c.getColumnIndex("name"));
                        i++;
                    } while (c.moveToNext());
                } else {
                    Toast.makeText(getApplicationContext(), "No recipients", Toast.LENGTH_LONG).show();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                selreci=new boolean[recip.length];
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

                builder.setPositiveButton("remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < rec.size(); j++) {
                            Boolean insert = dbc.del(recip[rec.get(j)]);
                            if (insert == true) {
                                Toast.makeText(getApplicationContext(), "items deleted", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getApplicationContext(), "items not deleted", Toast.LENGTH_SHORT).show();
                        }
//                            if (j != rec.size() - 1) {
//                                stringBuilder.append(",");
//                            }

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
                        }
                    }
                });
                builder.show();
            }
        });
    }
    public void add(View v){
        Intent i= new Intent(this,addrec.class);
        startActivity(i);
    }
}