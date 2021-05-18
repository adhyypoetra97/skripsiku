package com.spk.dispepsia.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class GejalaDB extends SQLiteOpenHelper {

    public static final String database_name = "DBspkdispepsiia.db";
    public static final String table_name = "gejala";
    public static final String row_id = "_id";
    public static final String row_kdGejala = "kd_gejala";
    public static final String row_nmGejala = "nm_gejala";
    public static final String row_bobot = "bobot";

    private SQLiteDatabase db;

    public GejalaDB(Context context) {
        super(context, database_name, null, 2);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + table_name + "(" + row_id + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + row_kdGejala + " TEXT,"
                + row_nmGejala + " TEXT," + row_bobot + " INTEGER)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int x) {
        db.execSQL("DROP TABLE IF EXISTS " + table_name);
    }

    public Cursor allData(){
        Cursor cur = db.rawQuery("SELECT * FROM " + table_name + " ORDER BY " + row_id + " ASC ", null);
        return cur;
    }

    public Cursor oneData(long id){
        Cursor cur = db.rawQuery("SELECT * FROM " + table_name + " WHERE " + row_id + "=" + id, null);
        return cur;
    }

    public Cursor byKode(String kode){
        Cursor cur = db.rawQuery("SELECT * FROM " + table_name + " WHERE " + row_kdGejala + "=" + kode, null);
        return cur;
    }

    //Insert Data
    public void insertData(ContentValues values){
        db.insert(table_name, null, values);
    }

    //Update Data
    public void updateData(ContentValues values, long id){
        db.update(table_name, values, row_id + "=" + id, null);
    }

    //Delete Data
    public void deleteData(long id){
        db.delete(table_name, row_id + "=" + id, null);
    }

    public Cursor kode(){
        Cursor cur = db.rawQuery("SELECT * FROM " + table_name + " ORDER BY " + row_kdGejala + " DESC", null);
        return cur;
    }

    public List<String> ambilData() {
        List<String> listData = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " ORDER BY " + row_id + " ASC ", null);
        if (cursor.moveToFirst()) {
            do {
                listData.add(cursor.getString(1) + " - " + cursor.getString(2) +
                            " (" + cursor.getString(cursor.getColumnIndex(row_bobot)) + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listData;
    }

    public List<Integer> kepentingan() {
        List<Integer> listData = new ArrayList<Integer>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " ORDER BY " + row_id + " ASC ", null);
        if (cursor.moveToFirst()) {
            do {
                listData.add(Integer.parseInt(cursor.getString(cursor.getColumnIndex(row_bobot))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listData;
    }
}
