package com.example.mohamedabdelaziz.newsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Mohamed Abd ELaziz on 1/11/2017.
 */

public class database extends SQLiteOpenHelper {
    final static String name ="dataabase" ;
    final static int version = 1 ;
    public database(Context context) {
        super(context, name, null , version);
      }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
         sqLiteDatabase.execSQL("CREATE TABLE MYTABLE (url VARCHAR(100),title VARCHAR(50) ,author VARCHR(50) ,description VARCHAR(150) ,pageurl VARCHAR(10) ,date VARCHAR(50) ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
         sqLiteDatabase.execSQL("DROP TABLE IF EXISTS MYTABLE");
        onCreate(sqLiteDatabase);
    }

    public boolean insert_data(datatype dt) {
        SQLiteDatabase sqLitewrite = this.getWritableDatabase() ;
        ContentValues contentValues = new ContentValues() ;
        contentValues.put("url",dt.url) ;
        contentValues.put("title",dt.title) ;
        contentValues.put("author",dt.author) ;
        contentValues.put("description",dt.description) ;
        contentValues.put("pageurl",dt.pageurl) ;
        contentValues.put("date",dt.date) ;
        sqLitewrite.insert("MYTABLE",null,contentValues);
        sqLitewrite.close();
          return true;
    }
    public boolean  is_exists(String title)
    {
        SQLiteDatabase sqLiteread = this.getReadableDatabase() ;
        Cursor cursor = sqLiteread.rawQuery("SELECT * FROM MYTABLE",null);
        cursor.moveToFirst();
        if(cursor.isAfterLast()){
              return false;
        }
        else {
            String check;
            cursor.moveToFirst();
            while (cursor.isAfterLast()!= true) {
                check = cursor.getString(1).toString() ;
                if (check.equalsIgnoreCase(title)) {
                    return true;
                }
                     cursor.moveToNext();
            }
        }
          return false;
    }
    public int delete_it(String title)
    {
        SQLiteDatabase sqLiteread = this.getReadableDatabase() ;
        String []array={title};
        return sqLiteread.delete("MYTABLE","title = ? ",array);
    }
    public ArrayList restore_data()
    {
        ArrayList<datatype> array_values = new ArrayList<>() ;
        SQLiteDatabase sql = this.getReadableDatabase() ;
        Cursor cursor = sql.rawQuery("SELECT * FROM "+"MYTABLE",null) ;
        cursor.moveToFirst();
        while(cursor.isAfterLast()==false)
        {
            datatype dt =new datatype();
            dt.url=cursor.getString(cursor.getColumnIndex("url"));
            dt.title=cursor.getString(cursor.getColumnIndex("title"));
            dt.author=cursor.getString(cursor.getColumnIndex("author"));
            dt.description=cursor.getString(cursor.getColumnIndex("description"));
            dt.pageurl=cursor.getString(cursor.getColumnIndex("pageurl"));
            dt.date=cursor.getString(cursor.getColumnIndex("date"));
            array_values.add(dt);
            cursor.moveToNext();
        }
        return array_values;
    }
}
