package com.example.madminiproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class addrec extends AppCompatActivity {
    EditText usr,em;
    Button btn1;
    DataBaseConn dbc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrec);
        usr=findViewById(R.id.uid);
        em=findViewById(R.id.email);
        btn1=findViewById(R.id.btn2);
        dbc = new DataBaseConn(getApplicationContext());
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=usr.getText().toString(),n=em.getText().toString();
                if(s.length()!=0 &&n.length()!=0) {
                    Boolean insert = dbc.ins(s, n);
                    if (insert == true) {
                        Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Data NOT Inserted", Toast.LENGTH_SHORT).show();
                    usr.setText("");
                    em.setText("");
                }
                else if(s.length()==0)
                    Toast.makeText(getApplicationContext(), "Enter username", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void back(View v){
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }
}