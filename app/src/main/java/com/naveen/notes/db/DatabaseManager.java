package com.naveen.notes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseManager extends SQLiteOpenHelper {

    private static DatabaseManager mInstance;

    public static DatabaseManager shared(Context context) {
        if (mInstance == null){
            mInstance = new DatabaseManager(context);
        }
        return mInstance;
    }
    private static final int DB_version = 1;
    private static final String DB_name = "note.db";

    static final String NoteTable = "Table_Note";
//------------------------------------- note --------------------------------------------------//
    public static final String KEY_id = "note_id";
    public static final String KEY_noteTitle = "note_title";
    public static final String KEY_noteInfo = "note_info";
    public static final String KEY_noteCategory = "note_category";
    public static final String KEY_noteLocation = "note_location";
    public static final String KEY_noteImage = "note_image";
    public static final String KEY_noteVoice = "note_voice";
    public static final String KEY_noteTime = "note_time";
//------------------------------------- note --------------------------------------------------//

    private static final String CREATE_NOTE_TABLE = "CREATE TABLE " + NoteTable +
            "(" + KEY_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_noteTitle + " TEXT,"
            + KEY_noteInfo + " TEXT,"
            + KEY_noteCategory + " TEXT,"
            + KEY_noteLocation + " TEXT,"
            + KEY_noteImage + " TEXT,"
            + KEY_noteVoice + " TEXT,"
            + KEY_noteTime + " TEXT"
            + ")";
    //---------------------------------------------------------------------------------------------//


    static final String[] columnsNote = new String[] {
            DatabaseManager.KEY_noteTitle,
            DatabaseManager.KEY_noteInfo,
            DatabaseManager.KEY_noteCategory,
            DatabaseManager.KEY_noteLocation,
            DatabaseManager.KEY_noteImage,
            DatabaseManager.KEY_noteVoice,
            DatabaseManager.KEY_noteTime
    };
    DatabaseManager(@Nullable Context context) {
        super(context, DB_name, null, DB_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("db", "update");
        db.execSQL("DROP TABLE IF EXISTS '" + CREATE_NOTE_TABLE + "'");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        //enable foreign key constraints like ON UPDATE CASCADE, ON DELETE CASCADE
        db.execSQL("PRAGMA foregin_keys=ON;");
    }
}
