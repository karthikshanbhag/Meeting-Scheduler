package com.example.madminiproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DataBaseConn extends SQLiteOpenHelper {

    public DataBaseConn(Context context) {
        super(context,"MeetingDB.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table meetingTbl(date TEXT,time TEXT, agenda TEXT,partic TEXT)");
        db.execSQL("create Table reci(name TEXT,email TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists meetingTbl");
        db.execSQL("drop Table if exists reci");}
    public boolean insertvalue(String d, String t, String agd,String p){
        SQLiteDatabase DB=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date",d);
        cv.put("time",t);
        cv.put("agenda",agd);
        cv.put("partic",p);
        long res=DB.insert("meetingTbl",null,cv);
        if(res==-1){
            return  false;
        }
        else
            return true;
    }
    public Cursor fetch(String d) {
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor c = DB.rawQuery("Select time,agenda,partic from meetingTbl where date='" + d + "' ", null);
        return c;
    }
    public Cursor getemail(String d) {
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor c = DB.rawQuery("Select email from reci where name ='" + d + "' ", null);
        return c;
    }
    public Cursor getr() {
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor c = DB.rawQuery("Select name from reci ", null);
        return c;
    }
//    public ArrayList<String> getRecords(String[] st){
//        ArrayList<String> data=new ArrayList<String>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        //Cursor cursor = db.query(reci, new String[]{"column names"},null, null, null, null, null);
//        String fieldToAdd=null;
//        for(int i=0;i<st.length;i++){
//            Cursor c = db.rawQuery("Select email from reci where name ='" + st[i] + "' ", null);
//            data.add(c.getString(1));
//        }
//          // dont forget to close the cursor after operation done
//        return data;
//    }
    public boolean ins(String stn,String em){
        SQLiteDatabase DB=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name",stn);
        cv.put("email",em);
        //cv.put("agenda",agd);
        long res=DB.insert("reci",null,cv);
        if(res==-1){
            return  false;
        }
        else
            return true;
    }
    public boolean del(String name) {

        // on below line we are creating
        // a variable to write our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        long res=db.delete("reci", "name=?", new String[]{name});
        db.close();
        if(res==-1){
            return  false;
        }
        else
            return true;
    }

}
