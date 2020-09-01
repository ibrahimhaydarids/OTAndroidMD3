package com.ids.fixot.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LIBRA_DB"; // database name
    private static final int DATABASE_VERSION = 2; // database version

    private static final String table_TimeSales = "TIMESALES";

    DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static void createTimeSalesTable(SQLiteDatabase db) {

        String timeSalesQuery = ("CREATE TABLE " + table_TimeSales) +
                "(id INTEGER PRIMARY KEY, " +
                "StockSymbolAr TEXT, " +
                "StockSymbolEn TEXT, " +
                "TradeTime TEXT, " +
                "Change TEXT, " +
                "Quantity TEXT, " +
                "Price TEXT, " +
                "orderTypeId TEXT, " +
                "instrumentId TEXT, " +
                "securityId TEXT, " +
                "ChangeIndicator INTEGER, " +
                "StockID INTEGER, " +
                "orderType INTEGER)";
        db.execSQL(timeSalesQuery);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createAllTables(db);

        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    private void createAllTables(SQLiteDatabase db) {

        createTimeSalesTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        newVersion = DATABASE_VERSION;

        if (newVersion > oldVersion) {

            Log.wtf("UPDATING", "DATABASE");

            db.execSQL("DROP TABLE IF EXISTS " + table_TimeSales);
            createTimeSalesTable(db);
        }
    }


}