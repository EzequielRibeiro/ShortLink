package org.ezequiel.shortlink;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "url";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ShortUrl.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id" + " INTEGER PRIMARY KEY," +
                    "code" + " TEXT NOT NULL UNIQUE," +
                    "long_url" + " TEXT NOT NULL UNIQUE," +
                    "date" + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public long insert(String code, String date,String long_url){
        long newRowId = -1;
        ContentValues values = new ContentValues();
        values.put("code", code);
        values.put("date", date);
        values.put("long_url",long_url);

        SQLiteDatabase db = getReadableDatabase();
        try {
            newRowId = db.insert(TABLE_NAME, null, values);
            db.close();
        }catch(SQLiteConstraintException e){
            e.printStackTrace();
        }
        return newRowId;

    }

    public ArrayList<ShortAddress>  readData(){

        SQLiteDatabase db = getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                "id",
                "code",
                "date",
                "long_url"
        };

// Filter results WHERE "title" = 'My Title'
        String selection = "code" + " = ?";
        String[] selectionArgs = { "My Title" };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                "id" + " DESC";

        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause  -- selection
                null,          // The values for the WHERE clause  --selectionArgs
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        ArrayList<ShortAddress> itemIds = new ArrayList<>();
        ShortAddress shortAddress;
        while(cursor.moveToNext()) {
            shortAddress = new ShortAddress();
            shortAddress.setId(cursor.getLong(
                    cursor.getColumnIndexOrThrow("id")));
            shortAddress.setCode(cursor.getString(
                    cursor.getColumnIndexOrThrow("code")));
            shortAddress.setDate(cursor.getString(
                    cursor.getColumnIndexOrThrow("date")));
            shortAddress.setOriginal_link(cursor.getString(
                    cursor.getColumnIndexOrThrow("long_url")));

            itemIds.add(shortAddress);
        }
        cursor.close();
        db.close();
        return itemIds;
    }

    public int delete(String id){

        SQLiteDatabase db = getReadableDatabase();

        // Define 'where' part of query.
        String selection = "id" + " LIKE ?";
    // Specify arguments in placeholder order.
        String[] selectionArgs = { id };
    // Issue SQL statement.
        int deletedRows = db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
