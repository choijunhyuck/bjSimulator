package com.instabox.bjsimulator.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class VodDatabaseHelper(context: Context,
                        factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION
    ) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_THUMBNAIL + " TEXT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_TIME + " INTEGER,"+
                COLUMN_PLAY + " INTEGER," +
                COLUMN_RECOMMAND + " INTEGER," +
                COLUMN_DATE + " TEXT"+ ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addVod(thumbnail: String, title: String, time: Int, date: String) {

        val values = ContentValues()
        values.put(COLUMN_THUMBNAIL, thumbnail)
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_TIME, time)
        values.put(COLUMN_PLAY, 0)
        values.put(COLUMN_RECOMMAND, 0)
        values.put(COLUMN_DATE, date)
        val db = this.writableDatabase
        db.insert(
            TABLE_NAME,
            null,
            values
        );

        db.close()

    }

    fun deleteVod(date: String){

        val query =
            "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = \"$date\""

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            db.delete(TABLE_NAME, COLUMN_DATE + " = ?",
                arrayOf(date))
            cursor.close()
        }

        db.close()

    }

    fun updatePlayAndRecommand(play: Int, recommand: Int, date:String){

        val query =
            "UPDATE $TABLE_NAME SET $COLUMN_PLAY = \"$play\", $COLUMN_RECOMMAND = \"$recommand\" WHERE $COLUMN_DATE = \"$date\""

        val db = this.writableDatabase
        db.execSQL(query)

    }

    fun getVOD(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC", null)
    }

    fun getPlay(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT $COLUMN_PLAY FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC", null)
    }

    fun getRecommand(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT $COLUMN_RECOMMAND FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC", null)
    }

    fun deleteAll(){

        val db = this.writableDatabase
        var query = "DELETE FROM $TABLE_NAME"

        db.execSQL(query)
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "vod.db"
        val TABLE_NAME = "vod_table"
        val COLUMN_ID = "_id"
        val COLUMN_THUMBNAIL = "thumbnail"
        val COLUMN_TITLE = "title"
        val COLUMN_TIME = "time"
        val COLUMN_PLAY = "play"
        val COLUMN_RECOMMAND = "recommand"
        val COLUMN_DATE = "date"
    }
}