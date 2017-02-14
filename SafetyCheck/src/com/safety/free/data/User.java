package com.safety.free.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.safety.free.model.Global;

public class User extends BaseTable {

    public final static String TABLE_NAME = "SafetyCheckUser";

    // loginUser的字段名称
    final static String FIELD_userID = "userID";
    final static String FIELD_username = "username";
    final static String FIELD_picture = "picture";

    private String userID = "";

    private String username = "";

    private String picture = "";

    public User(Cursor c) {
        loadFromCursor(c);
    }

    /**
     * 返回创建loginUser表的sql语句
     */
    public static String getCreateSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append(" (");
        sb.append(FIELD_userID).append(" TEXT, ");
        sb.append(FIELD_username).append(" TEXT, ");
        sb.append(FIELD_picture).append(" TEXT)");
        return sb.toString();
    }

    /**
     * 利用数据库记录信息对User进行初始化
     */
    @Override
    public void loadFromCursor(Cursor c) {

        try {
            this.userID = c.getString(c.getColumnIndex(FIELD_userID));
        } catch (Exception e) {
            this.userID = "";
        }

        try {
            this.username = c.getString(c.getColumnIndex(FIELD_username));
        } catch (Exception e) {
            this.username = "";
        }

        try {
            String url = c.getString(c.getColumnIndex(FIELD_picture));
            if (!TextUtils.isEmpty(url))
                this.picture = url;
        } catch (Exception e) {
            this.picture = "";
        }
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getKeyField() {
        return FIELD_userID;
    }

    @Override
    protected String getKeyValue() {
        return userID;
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(FIELD_userID, userID);
        values.put(FIELD_username, username);
        values.put(FIELD_picture, picture);
        return values;
    }

    /**
     * 返回userId对应的数据库记录
     */
    public static Cursor getCursor(String userID) {
        DBHelper dbhelper = Global.getDBHelper();
        if (dbhelper == null)
            return null;
        try {
            return dbhelper.query(TABLE_NAME, null, FIELD_userID + "=?", new String[] { userID });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
