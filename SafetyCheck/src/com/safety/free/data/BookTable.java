package com.safety.free.data;

import com.safety.free.model.Global;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

public class BookTable extends BaseTable {

    public final static String TABLE_NAME = "SafetyCheckBook";

    // 字段名称
    final static String FIELD_name = "name";
    final static String FIELD_isbn = "isbn";
    final static String FIELD_picture = "picture";// 书本的封面

    private BookElement element;

    public BookTable(Cursor c) {
        loadFromCursor(c);
    }

    /**
     * 返回创建表的sql语句
     */
    public static String getCreateSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append(" (");
        sb.append(FIELD_name).append(" TEXT, ");
        sb.append(FIELD_isbn).append(" TEXT, ");
        sb.append(FIELD_picture).append(" TEXT)");
        return sb.toString();
    }

    public BookElement getElement() {
        return element;
    }

    public void setElement(BookElement element) {
        this.element = element;
    }

    /**
     * 利用数据库记录信息对User进行初始化
     */
    @Override
    public void loadFromCursor(Cursor c) {

        try {
            element.setName(c.getString(c.getColumnIndex(FIELD_name)));
        } catch (Exception e) {
            element.setName("");
        }

        try {
            element.setISBN(c.getString(c.getColumnIndex(FIELD_isbn)));
        } catch (Exception e) {
            element.setISBN("");
        }

        try {
            String url = c.getString(c.getColumnIndex(FIELD_picture));
            if (!TextUtils.isEmpty(url))
                element.setPicture(url);
        } catch (Exception e) {
            element.setPicture("");
        }
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getKeyField() {
        return FIELD_isbn;
    }

    @Override
    protected String getKeyValue() {
        return element.getISBN();
    }

    /**
     * 把书本信息生成一个ContentValues结构，以便进行数据库操作
     */
    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(FIELD_name, element.getName());
        values.put(FIELD_isbn, element.getISBN());
        values.put(FIELD_picture, element.getPicture());
        return values;
    }

    /**
     * 返回isbn对应的数据库记录
     */
    public static Cursor getCursor(String isbn) {
        DBHelper dbhelper = Global.getDBHelper();
        if (dbhelper == null)
            return null;
        try {
            return dbhelper.query(TABLE_NAME, null, FIELD_isbn + "=?", new String[] { isbn });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据isbn取得书本的信息
     * 
     * @param isbn
     * @return
     */
    public static BookElement getBookElement(String isbn) {
        BookElement element = new BookElement();
        Cursor cursor = getCursor(isbn);
        if (cursor != null) {
            try {
                element.setName(cursor.getString(cursor.getColumnIndex(FIELD_name)));
            } catch (Exception e) {
                element.setName("");
            }

            try {
                element.setISBN(cursor.getString(cursor.getColumnIndex(FIELD_isbn)));
            } catch (Exception e) {
                element.setISBN("");
            }

            try {
                element.setPicture(cursor.getString(cursor.getColumnIndex(FIELD_picture)));
            } catch (Exception e) {
                element.setPicture("");
            }
            cursor.close();
        }
        return element;
    }
}
