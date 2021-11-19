package com.naveen.notes.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.naveen.notes.application.App;
import com.naveen.notes.model.NoteModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DBController {

    private SQLiteDatabase database;
    private static DBController mInstance;
    private DatabaseManager dbManager = DatabaseManager.shared(App.context);

    public static DBController shared() {
        if (mInstance == null) {
            synchronized (DBController.class) {
                if (mInstance == null) {
                    mInstance = new DBController();
                }
            }
        }
        return mInstance;
    }

    public void deleteAllData() {
        database = dbManager.getWritableDatabase();
        database.delete(DatabaseManager.NoteTable, null, null);
    }

    //---------------------- note ----------------------------//
    public int insertNote(NoteModel data) {
        database = DatabaseManager.shared(App.context).getWritableDatabase();
        ContentValues values = new ContentValues();

        Gson gson = new Gson();
        values.put(DatabaseManager.KEY_noteTitle, data.getTitle());
        values.put(DatabaseManager.KEY_noteInfo, data.getInfo());
        values.put(DatabaseManager.KEY_noteCategory, data.getCategory());
        values.put(DatabaseManager.KEY_noteLocation, gson.toJson(data.getLocations()));
        values.put(DatabaseManager.KEY_noteImage, data.getImage());
        values.put(DatabaseManager.KEY_noteVoice, data.getVoice());
        values.put(DatabaseManager.KEY_noteTime, data.getTime());

        long insertedID = database.insert(DatabaseManager.NoteTable, null, values);
        return (int) insertedID;
    }

    public void deleteNote(NoteModel noteModel) {
        database = dbManager.getWritableDatabase();
        database.delete(DatabaseManager.NoteTable, DatabaseManager.KEY_noteTime + " = ?", new String[]{noteModel.getTime()});
    }

    public NoteModel getNote(String noteTime) {
        database = dbManager.getWritableDatabase();

        Cursor cursor = database.query(DatabaseManager.NoteTable, DatabaseManager.columnsNote,
                DatabaseManager.KEY_noteTime + "= ?", new String[]{noteTime},
                null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

        NoteModel data = new NoteModel();
        Gson gson = new Gson();
        Type locationType = new TypeToken<List<String>>(){}.getType();
        data.setTitle(cursor.getString(0));
        data.setInfo(cursor.getString(1));
        data.setCategory(cursor.getString(2));
        data.setLocations(gson.fromJson(cursor.getString(3), locationType));
        data.setImage(cursor.getBlob(4));
        data.setVoice(cursor.getString(5));
        data.setTime(cursor.getString(6));
        cursor.close();
        // to free up memory of cursor
        return data;
    }

    public void updateNote(NoteModel data) {
        ContentValues values = new ContentValues();

        Gson gson = new Gson();
        values.put(DatabaseManager.KEY_noteTitle, data.getTitle());
        values.put(DatabaseManager.KEY_noteInfo, data.getInfo());
        values.put(DatabaseManager.KEY_noteCategory, data.getCategory());
        values.put(DatabaseManager.KEY_noteLocation, gson.toJson(data.getLocations()));
        values.put(DatabaseManager.KEY_noteImage,data.getImage());
        values.put(DatabaseManager.KEY_noteVoice, data.getVoice());
        values.put(DatabaseManager.KEY_noteTime, data.getTime());

        String where = DatabaseManager.KEY_noteTime + " = ?";
        database = dbManager.getReadableDatabase();
        database.update(DatabaseManager.NoteTable, values, where, new String[]{data.getTime()});
    }

    public ArrayList<NoteModel> getAllNote() {
        ArrayList<NoteModel> results = new ArrayList<>();
        database = dbManager.getWritableDatabase();

        Cursor cursor = database.rawQuery("select * from " + DatabaseManager.NoteTable, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    NoteModel data = new NoteModel();
                    Gson gson = new Gson();
                    Type locationType = new TypeToken<List<String>>(){}.getType();
                    data.setTitle(cursor.getString(1));
                    data.setInfo(cursor.getString(2));
                    data.setCategory(cursor.getString(3));
                    data.setLocations(gson.fromJson(cursor.getString(4), locationType));
                    data.setImage(cursor.getBlob(5));
                    data.setVoice(cursor.getString(6));
                    data.setTime(cursor.getString(7));
                    results.add(data);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } else {
            if (cursor != null) {
                cursor.close();
            }
        }       // to free up memory of cursor

        return results;
    }
    public ArrayList<NoteModel> getAllNoteWithCategory(String category) {
        ArrayList<NoteModel> results = new ArrayList<>();
        database = dbManager.getWritableDatabase();

        String where = DatabaseManager.KEY_noteCategory + " = ?";
        Cursor cursor = database.query(DatabaseManager.NoteTable, DatabaseManager.columnsNote,
                where, new String[]{category},
                null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    NoteModel data = new NoteModel();
                    Gson gson = new Gson();
                    Type locationType = new TypeToken<List<String>>(){}.getType();
                    data.setTitle(cursor.getString(0));
                    data.setInfo(cursor.getString(1));
                    data.setCategory(cursor.getString(2));
                    data.setLocations(gson.fromJson(cursor.getString(3), locationType));
                    data.setImage(cursor.getBlob(4));
                    data.setVoice(cursor.getString(5));
                    data.setTime(cursor.getString(6));
                    results.add(data);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } else {
            if (cursor != null) {
                cursor.close();
            }
        }       // to free up memory of cursor

        return results;
    }
    //---------------------- note ----------------------------//
}
