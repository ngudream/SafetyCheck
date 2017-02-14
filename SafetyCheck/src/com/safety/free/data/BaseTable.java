package com.safety.free.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.safety.free.model.Global;

public abstract class BaseTable {

    protected abstract String getTableName();

    protected abstract String getKeyField();

    protected abstract String getKeyValue();

    abstract protected ContentValues getContentValues();

    abstract public void loadFromCursor(Cursor c);
    
    public synchronized void save() {

        DBHelper dbhelper = Global.getDBHelper();
        if (dbhelper == null)
            return;

        try {
            ContentValues values = getContentValues();
            String tableName = getTableName();
            String keyField = getKeyField();
            String keyValue = getKeyValue();

            /*
             * 更改数据库的记录
             */
            int affectedRows = dbhelper.update(tableName, values, keyField + "=?", new String[] { keyValue }, null);

            /*
             * 如果没有记录被更改，说明是新用户，添加新行
             */
            StringBuilder sb = new StringBuilder();
            if (affectedRows == 0) {
                dbhelper.insert(tableName, null, getContentValues(), null);
                sb.append("insert ");
            } else
                sb.append("update ");
            sb.append(tableName).append(" userID=").append(keyValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized int delete() {
        DBHelper dbhelper = Global.getDBHelper();
        if (dbhelper == null)
            return 0;
        try {
            return dbhelper.delete(getTableName(), getKeyField() + "=?", new String[] { getKeyValue() }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
