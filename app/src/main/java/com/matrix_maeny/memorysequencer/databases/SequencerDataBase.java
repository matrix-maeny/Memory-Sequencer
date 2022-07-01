package com.matrix_maeny.memorysequencer.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SequencerDataBase extends SQLiteOpenHelper {


    public SequencerDataBase(@Nullable Context context) {
        super(context, "Sequencer.db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL("Create Table Sequencer(name TEXT primary key,keys TEXT,keyValue TEXT)");
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("Drop Table if exists Sequencer");
    }

    public boolean insertData(String name, String keys, String keyValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("keys", keys);
        cv.put("keyValue", keyValue);

        long result = db.insert("Sequencer", null, cv);

        return result != -1;

    }

//    public boolean insertSequenceTitles(String name) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//
//        cv.put("name", name);
//
//        long result = db.insert("SequencerTitles", null, cv);
//
//        return result != -1;
//    }


    public boolean updateData(String name, String keys, String keyValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("keys", keys);
        cv.put("keyValue", keyValue);

        long result = db.update("Sequencer", cv, "name=?", new String[]{name});

        return result != -1;
    }


    public boolean deleteData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete("Sequencer", "name=?", new String[]{name});

        return result != -1;
    }

    public boolean deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete("Sequencer", "1", null);

        return result > 0;

    }




    public Cursor getData() {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("Select * from Sequencer", null);
    }



}
