package ir.dornadev.encryptnote.Handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ir.dornadev.encryptnote.Model.DbHelper;


public class DbHandler {

    private DbHelper dbHelper;
    private SQLiteDatabase database;

    public DbHandler(Context context){
        dbHelper=new DbHelper(context);
    }
    public void open(){
        database=dbHelper.getWritableDatabase();
    }

    public  void close(){
        dbHelper.close();
    }

    public String display(int row,int field){
        Cursor cursor=database.query(DbHelper.tbltext,null,null,null,null,null,null,null);
        cursor.moveToPosition(row);
        String name=cursor.getString(field);
        return name;
    }
    public String display2(int id,int field){
        Cursor cursor=database.rawQuery("select * from tbltext where id="+id,null);
        cursor.moveToFirst();
        String title=cursor.getString(field);
        return title;
    }


    public void insert(String title,String text,String date){
        ContentValues contentValues=new ContentValues();
        contentValues.put(dbHelper.title,title);
        contentValues.put(dbHelper.text,text);
        contentValues.put(dbHelper.date,date);
        database.insert(dbHelper.tbltext,dbHelper.title,contentValues);
    }
    public Integer count(){
        Cursor cursor=database.query(dbHelper.tbltext,null,null,null,null,null,null);
        int s=cursor.getCount();
        return s;
    }
    public void delete(int id){
        database.delete(dbHelper.tbltext,"id="+id,null);
    }
    public void update(String title, String message, int id){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DbHelper.title,title);
        contentValues.put(DbHelper.text,message);
        database.update(dbHelper.tbltext,contentValues,"id="+id,null);
    }
}
