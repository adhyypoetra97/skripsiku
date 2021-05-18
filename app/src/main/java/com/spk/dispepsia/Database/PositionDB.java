package com.spk.dispepsia.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PositionDB extends SQLiteOpenHelper {

    public static final String database_name = "DBspkddiispepsia.db";
    public static final String table_name = "posisi";
    public static final String row_kdRule = "kd_rules";
    public static final String row_posAlt = "alternatif";
    public static final String row_posKri = "kriteria";

    private SQLiteDatabase db;

    public PositionDB(Context context) {
        super(context, database_name, null, 2);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + table_name + "(" +  row_kdRule + " TEXT,"
                + row_posAlt + " TEXT," + row_posKri + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int x) {
        db.execSQL("DROP TABLE IF EXISTS " + table_name);
    }

    public Cursor oneData(String id){
        Cursor cur = db.rawQuery("SELECT * FROM " + table_name + " WHERE " + row_kdRule + "= ?", new String[] {id});
        return cur;
    }

    //Insert Data
    public void insertData(ContentValues values){
        db.insert(table_name, null, values);
    }

    //Update Data
    public void updateData(ContentValues values, String id){
        db.update(table_name, values, row_kdRule + "= ?", new String[] {id});
    }

    //Delete Data
    public void deleteData(String id){
        db.delete(table_name, row_kdRule+ "= ?", new String[] {id});
    }
}
