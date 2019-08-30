package com.instabox.bjsimulator.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RankingDatabaseHelper(context: Context,
                        factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION
    ) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER + " TEXT," +
                COLUMN_COIN + " INTEGER"+ ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun isExistUser(user: String): Cursor? {

        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_USER = \"$user\"", null)

    }

    fun addUser(user: String, coin: Int) {

        var c = isExistUser(user)!!
        val db = this.writableDatabase

        if(c.moveToFirst()){

            //UPDATE COIN
            c.moveToFirst()
            var existedCoin = c.getInt(c.getColumnIndex(COLUMN_COIN))
            var totalCoin = coin + existedCoin

            var query = "UPDATE $TABLE_NAME SET $COLUMN_COIN = \"$totalCoin\" WHERE $COLUMN_USER = \"$user\""

            db.execSQL(query)

        } else {
            //ADD USER
            val values = ContentValues()
            values.put(COLUMN_USER, user)
            values.put(COLUMN_COIN, coin)
            db.insert(
                TABLE_NAME,
                null,
                values
            );

        }

        c.close()
        db.close()

    }

    fun deleteAll(){

        val db = this.writableDatabase
        var query = "DELETE FROM $TABLE_NAME"

        db.execSQL(query)
    }

    fun get10User(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_COIN DESC LIMIT 10", null)
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "ranking.db"
        val TABLE_NAME = "ranking_table"
        val COLUMN_ID = "_id"
        val COLUMN_USER = "user"
        val COLUMN_COIN = "coin"
    }
}