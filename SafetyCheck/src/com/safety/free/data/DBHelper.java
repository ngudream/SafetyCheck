/**
 * 
 */
package com.safety.free.data;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.safety.free.model.Global;

/**
 * @author Lee 数据库使用类。 可使用本类直接创建数据库，表和对数据进行操作
 */
public class DBHelper extends SQLiteOpenHelper {
    final static int DATABASE_VERSION = 1;
    private SQLiteDatabase databaseW, databaseR;
    private String userID;

    public DBHelper(String userID) {
        super(Global.getApplicationContext(), userID, null, DATABASE_VERSION);
        this.userID = userID;
        databaseW = getWritableDatabase();
        databaseR = getReadableDatabase();
    }

    public String getUserID() {
        return userID;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Note that numeric arguments in parentheses that following the type
        // name (ex: "VARCHAR(255)") are ignored by SQLite - SQLite does not
        // impose any length restrictions (other than the large global
        // SQLITE_MAX_LENGTH limit) on the length of strings, BLOBs or numeric
        // values.
        // ref:http://www.sqlite.org/datatype3.html

        db.beginTransaction();

        try {

            db.execSQL(User.getCreateSQL());
            db.execSQL(BookTable.getCreateSQL());
            db.execSQL(AssessTable.getCreateSQL());
            db.execSQL(CategoryTable.getCreateSQL());
            db.execSQL(RegularTable.getCreateSQL());
            
            db.setTransactionSuccessful();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 软件的第一个版本，先不考虑数据库的更新问题
    }

    @Override
    public synchronized void close() {
        if (databaseW != null) {
            databaseW.close();
            databaseW = null;
        }
        if (databaseR != null) {
            databaseR.close();
            databaseR = null;
        }
        super.close();
    }

    // CRUD 最后一个参数是用来控制数据库是否发出通知广播
    public synchronized long insert(String table, String nullColumnHack, ContentValues values, Intent intent) {
        if (databaseW == null) {
            return -1;
        }
        long result = databaseW.insert(table, nullColumnHack, values);
        if (result != -1) {
            // ================发出通知广播
            if (intent != null) {
                Global.getApplicationContext().sendBroadcast(intent);
            }
        }
        return result;
    }

    public synchronized int update(String table, ContentValues values, String whereClause, String[] whereArgs, Intent intent) {
        if (databaseW == null) {
            return -1;
        }
        int result = databaseW.update(table, values, whereClause, whereArgs);
        if (result > 0) {
            // ================发出通知广播
            if (intent != null) {
                Global.getApplicationContext().sendBroadcast(intent);
            }
        }
        return result;
    }

    public synchronized int delete(String table, String whereClause, String[] whereArgs, Intent intent) {
        if (databaseW == null) {
            return -1;
        }
        int result = databaseW.delete(table, whereClause, whereArgs);
        if (result > 0) {
            // ================发出通知广播
            if (intent != null) {
                Global.getApplicationContext().sendBroadcast(intent);
            }
        }
        return result;
    }

    public synchronized Cursor query(String sql, String[] selectionArgs) {
        if (databaseR == null) {
            return null;
        }
        Cursor cursor = databaseR.rawQuery(sql, selectionArgs);
        return cursor;
    }

    public synchronized Cursor query(String table, String[] columns, String selection, String[] selectionArgs) {
        if (databaseR == null) {
            return null;
        }
        Cursor cursor = databaseR.query(table, columns, selection, selectionArgs, null, null, null, null);
        return cursor;
    }

    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        if (databaseR == null) {
            return null;
        }
        Cursor cursor = databaseR.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

        return cursor;
    }

    public synchronized Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        if (databaseR == null) {
            return null;
        }
        Cursor cursor = databaseR.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, null);

        return cursor;
    }

    public synchronized Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        if (databaseR == null) {
            return null;
        }
        Cursor cursor = databaseR.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

        return cursor;
    }

}
