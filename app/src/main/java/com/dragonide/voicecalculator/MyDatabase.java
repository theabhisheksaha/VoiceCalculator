package com.dragonide.voicecalculator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "symbols.db";
    private static final int DATABASE_VERSION = 1;
    private static final String ID="s_id";
    private static final String TEXT="s_text";
    private static final String SYMBOL="s_symbol";

    private static final String POSES_TABLE="Symbol";


    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ArrayList<Poses> getPoses(){
        SQLiteDatabase db=getWritableDatabase();
        String[] columns={MyDatabase.ID,MyDatabase.TEXT,MyDatabase.SYMBOL};
//        String[] selectionArgs={categoryId+"",subjectId+"",yearId+""};
        Cursor cursor=db.query(MyDatabase.POSES_TABLE, columns, null, null, null, null, null);
//        Cursor cursor=db.query(MyDatabase.TABLE_NAME, columns, null,null, null, null, null);
        ArrayList<Poses> questionsArrayList=new ArrayList<>();

        while(cursor.moveToNext()){



            Poses questions=new Poses();
            questions.s_id=cursor.getInt(cursor.getColumnIndex(MyDatabase.ID));
            questions.s_text=cursor.getString(cursor.getColumnIndex(MyDatabase.TEXT));
            questions.s_symbol=cursor.getString(cursor.getColumnIndex(MyDatabase.SYMBOL));

            questionsArrayList.add(questions);

        }
        return questionsArrayList;
    }



}