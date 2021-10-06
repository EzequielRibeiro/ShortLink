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
                    "codeShort1" + " TEXT DEFAULT 'error'," +
                    "codeShort2" + " TEXT DEFAULT 'error'," +
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

    public long insertShortUrl1(String codeShort1, String date,String long_url){
        long newRowId = -1;
        ContentValues values = new ContentValues();
        values.put("codeShort1", codeShort1);
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

    public long insertShortUrl2(String codeShort2, String date,String long_url){
        long newRowId = -1;
        ContentValues values = new ContentValues();
        values.put("codeShort2", codeShort2);
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

    public long updateShortUrl1(String codeShort1, String date,String longUrl){

        SQLiteDatabase db = getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("codeShort1",codeShort1);
        cv.put("date", date);

        long i =  db.update(TABLE_NAME,cv,"long_url = ?",new String[]{longUrl});
        db.close();


     return i;
    }
    public long updateShortUrl2(String codeShort2, String date,String longUrl){

        SQLiteDatabase db = getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("codeShort2",codeShort2);
        cv.put("date", date);

        long i =  db.update(TABLE_NAME,cv,"long_url = ?",new String[]{longUrl});
        db.close();
        return i;
    }


    public int selectUrlExists(String url){

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE long_url = '"+url.trim()+"'", null);
        c.moveToFirst();
        int count = c.getCount();
        c.close();
        db.close();
        return count;
    }

    public ArrayList<ShortLink>  readData(){

        SQLiteDatabase db = getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                "id",
                "codeShort1",
                "codeShort2",
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

        ArrayList<ShortLink> itemIds = new ArrayList<>();
        ShortLink shortAddress;
        while(cursor.moveToNext()) {
            shortAddress = new ShortLink();
            shortAddress.setId(cursor.getString(
                    cursor.getColumnIndexOrThrow("id")));
            shortAddress.setCode1(cursor.getString(
                    cursor.getColumnIndexOrThrow("codeShort1")));
            shortAddress.setCode2(cursor.getString(
                    cursor.getColumnIndexOrThrow("codeShort2")));
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
