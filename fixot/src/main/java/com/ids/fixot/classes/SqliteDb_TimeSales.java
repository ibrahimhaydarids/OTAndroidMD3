package com.ids.fixot.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.ids.fixot.model.TimeSale;

import java.util.ArrayList;

public class SqliteDb_TimeSales {

    private static final String table_TimeSales = "TIMESALES";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    // constructor in it wec set the context
    public SqliteDb_TimeSales(Context ctx) {
        this.mCtx = ctx;
    }

    // when called the db is opened/created
    public SqliteDb_TimeSales open() throws android.database.SQLException {

        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    //<editor-fold desc="Client section">
    private TimeSale cursorToTimeSale(Cursor cursor) {

        TimeSale timeSale = new TimeSale();

        try {
            timeSale.setId(String.valueOf(cursor.getInt(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setStockSymbolAr(cursor.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setStockSymbolEn(cursor.getString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setTradeTime(cursor.getString(3));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setChange(cursor.getString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setQuantity(cursor.getString(5));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setPrice(cursor.getString(6));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setOrderTypeId(cursor.getString(7));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setInstrumentId(cursor.getString(8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setSecurityId(cursor.getString(9));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setChangeIndicator(cursor.getInt(10));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setStockID(cursor.getInt(11));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            timeSale.setOrderType(cursor.getInt(12));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeSale;
    }


    public int deleteTimeSales() {

        return mDb.delete(table_TimeSales, null, null);
    }


    public int deleteTimeSale(String id) {
        return mDb.delete(table_TimeSales, "id=" + id, null);
    }


    public TimeSale getTimeSale(String id) {

        String selectQuery = "SELECT * FROM " + table_TimeSales + " where id=" + id;

        Cursor cursor = mDb.rawQuery(selectQuery, null);
        TimeSale timeSale = new TimeSale();

        if (cursor.moveToFirst()) {
            timeSale = cursorToTimeSale(cursor);
        }

        cursor.close();
        //mDb.close();
        return timeSale;
    }


    public ArrayList<TimeSale> getAllTimeSales() {

        String query = "select * from " + table_TimeSales + " ORDER BY id desc";

        ArrayList<TimeSale> newsList = new ArrayList<>();
        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // fill news info
            TimeSale news = cursorToTimeSale(cursor);
            newsList.add(news);
            cursor.moveToNext();
        }
        cursor.close();
        return newsList;
    }


    public long insertTimeSale(TimeSale client) {

        long id;

        ContentValues values = new ContentValues();
        values.put("id", Integer.parseInt(client.getId()));
        values.put("StockSymbolAr", client.getStockSymbolAr());
        values.put("StockSymbolEn", client.getStockSymbolEn());
        values.put("TradeTime", client.getTradeTime());
        values.put("Change", client.getChange());
        values.put("Quantity", client.getQuantity());
        values.put("Price", client.getPrice());
        values.put("orderTypeId", client.getOrderTypeId());
        values.put("instrumentId", client.getInstrumentId());
        values.put("securityId", client.getSecurityId());
        values.put("ChangeIndicator", client.getChangeIndicator());
        values.put("StockID", client.getStockID());
        values.put("orderType", client.getOrderType());

        id = mDb.replace(table_TimeSales, null, values);

        return id;
    }


    public boolean insertTimeSalesList(ArrayList<TimeSale> all_item) {

        int length = all_item.size();
        Log.wtf("Client all_item size", "" + length);
        boolean insertStatus = true;
        String sql = "replace into " + table_TimeSales + "(id , StockSymbolAr , StockSymbolEn , TradeTime, Change, Quantity, Price, orderTypeId, instrumentId, " +
                "securityId, ChangeIndicator, StockID,  orderType) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        mDb.beginTransaction();
        SQLiteStatement stmt = mDb.compileStatement(sql);

        for (TimeSale anAll_item : all_item) {

            try {
                stmt.bindString(1, "" + anAll_item.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stmt.bindString(2, "" + anAll_item.getStockSymbolAr());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stmt.bindString(3, "" + anAll_item.getStockSymbolEn());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stmt.bindString(4, "" + anAll_item.getTradeTime());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stmt.bindString(5, "" + anAll_item.getChange());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stmt.bindString(6, "" + anAll_item.getQuantity());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stmt.bindString(7, "" + anAll_item.getPrice());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                stmt.bindString(8, "" + anAll_item.getOrderTypeId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stmt.bindString(9, "" + anAll_item.getInstrumentId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stmt.bindString(10, "" + anAll_item.getSecurityId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                stmt.bindString(11, "" + anAll_item.getChangeIndicator());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stmt.bindString(12, "" + anAll_item.getStockID());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stmt.bindString(13, "" + anAll_item.getOrderType());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                stmt.execute();
            } catch (SQLException e) {
                Log.wtf("  insert fail ", e.getMessage());
                mDb.endTransaction();
                insertStatus = false;

            }

            stmt.clearBindings();
        }

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return insertStatus;
    }
    //</editor-fold>


}