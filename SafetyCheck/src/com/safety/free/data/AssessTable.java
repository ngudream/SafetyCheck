package com.safety.free.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.safety.free.model.Global;

public class AssessTable extends BaseTable {

    public final static String TABLE_NAME = "SafetyCheckAssess";

    //字段名称
    final static String FIELD_id = "id";
    final static String FIELD_name = "name";
    final static String FIELD_show = "show";
    final static String FIELD_evaluation = "evaluation";
    final static String FIELD_score = "score";
    final static String FIELD_score_zero = "score_zero";
    final static String FIELD_score_one = "score_one";
    final static String FIELD_score_five = "score_five";
    final static String FIELD_score_seven = "score_seven";
    final static String FIELD_time = "time";

//    private String name = "";
    private AssessElement element;
    
    public AssessTable(){
        this.element = new AssessElement();
    }

    public AssessTable(Cursor c) {
        this.element = new AssessElement();
        loadFromCursor(c);
    }

    public AssessElement getElement() {
        return element;
    }

    public void setElement(AssessElement element) {
        this.element = element;
    }

    /**
     * 返回创建loginUser表的sql语句
     */
    public static String getCreateSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append(" (");
        sb.append(FIELD_id).append(" TEXT, ");
        sb.append(FIELD_name).append(" TEXT, ");
        sb.append(FIELD_show).append(" INTEGER, ");
        sb.append(FIELD_evaluation).append(" INTEGER, ");
        sb.append(FIELD_score).append(" REAL, ");
        sb.append(FIELD_score_zero).append(" INTEGER, ");
        sb.append(FIELD_score_one).append(" INTEGER, ");
        sb.append(FIELD_score_five).append(" INTEGER, ");
        sb.append(FIELD_score_seven).append(" INTEGER, ");
        sb.append(FIELD_time).append(" TEXT)");
        return sb.toString();
    }

    /**
     * 利用数据库记录信息对User进行初始化
     */
    @Override
    public void loadFromCursor(Cursor cursor) {
        try {
            element.setId(cursor.getString(cursor.getColumnIndex(FIELD_id)));
        } catch (Exception e) {
            element.setId("");
        }

        try {
            element.setName(cursor.getString(cursor.getColumnIndex(FIELD_name)));
        } catch (Exception e) {
            element.setName("");
        }
        
        try {
            element.setShow(cursor.getInt(cursor.getColumnIndex(FIELD_show)) > 0 ? true : false);
        } catch (Exception e) {
            element.setShow(true);
        }
        
        try {
            element.setEvaluation(cursor.getInt(cursor.getColumnIndex(FIELD_evaluation)) > 0 ? true : false);
        } catch (Exception e) {
            element.setEvaluation(false);
        }
        
        try {
            element.setScore(cursor.getDouble(cursor.getColumnIndex(FIELD_score)));
        } catch (Exception e) {
            element.setScore(0);
        }
        
        try {
            element.setScoreZero(cursor.getInt(cursor.getColumnIndex(FIELD_score_zero)));
        } catch (Exception e) {
            element.setScoreZero(0);
        }
        
        try {
            element.setScoreOne(cursor.getInt(cursor.getColumnIndex(FIELD_score_one)));
        } catch (Exception e) {
            element.setScoreOne(0);
        }
        
        try {
            element.setScoreFive(cursor.getInt(cursor.getColumnIndex(FIELD_score_five)));
        } catch (Exception e) {
            element.setScoreFive(0);
        }
        
        try {
            element.setScoreSeven(cursor.getInt(cursor.getColumnIndex(FIELD_score_seven)));
        } catch (Exception e) {
            element.setScoreSeven(0);
        }
        
        try {
            element.setTime(cursor.getString(cursor.getColumnIndex(FIELD_time)));
        } catch (Exception e) {
            element.setTime(String.valueOf(System.currentTimeMillis()));
        }
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getKeyField() {
        return FIELD_id;
    }

    @Override
    protected String getKeyValue() {
        return element.getId();
    }

    /**
     * 把当前用户信息生成一个ContentValues结构，以便进行数据库操作
     */
    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(FIELD_id, element.getId());
        values.put(FIELD_name, element.getName());
        values.put(FIELD_show, element.isShow() ? 1 : 0);
        values.put(FIELD_evaluation, element.isEvaluation() ? 1 : 0);
        values.put(FIELD_score, element.getScore());
        values.put(FIELD_score_zero, element.getScoreZero());
        values.put(FIELD_score_one, element.getScoreOne());
        values.put(FIELD_score_five, element.getScoreFive());
        values.put(FIELD_score_seven, element.getScoreSeven());
        values.put(FIELD_time, element.getTime());
        return values;
    }

    /**
     * 返回id对应的数据库记录
     */
    public synchronized static Cursor getCursor(String id) {
        DBHelper dbhelper = Global.getDBHelper();
        if (dbhelper == null)
            return null;
        try {
            return dbhelper.query(TABLE_NAME, null, FIELD_id + "=?", new String[] { id });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据name取得评估标题的信息
     * 
     * @param id
     * @return
     */
    public static AssessElement getAssessElement(String id) {
        AssessElement element = new AssessElement();
        Cursor cursor = getCursor(id);
        if (cursor != null) {
            element = getAssessElement(cursor);
            cursor.close();
        }
        return element;
    }

    /**
     * 根据name取得全部评估标题的信息
     * 
     * @param id
     * @return
     */
    public static ArrayList<AssessElement> getAllAssessElements(String id) {
        ArrayList<AssessElement> elements = new ArrayList<AssessElement>();
        Cursor cursor = getCursor(id);
        if (cursor != null && cursor.moveToFirst()) {
            while (true) {
                AssessElement element = getAssessElement(cursor);
                elements.add(element);
                if (!cursor.moveToNext())
                    break;
            }
            cursor.close();
        }
        return elements;
    }
    
    private static AssessElement getAssessElement(Cursor cursor) {
        AssessElement element = new AssessElement();
        try {
            element.setId(cursor.getString(cursor.getColumnIndex(FIELD_id)));
        } catch (Exception e) {
            element.setId("");
        }

        try {
            element.setName(cursor.getString(cursor.getColumnIndex(FIELD_name)));
        } catch (Exception e) {
            element.setName("");
        }
        
        try {
            element.setShow(cursor.getInt(cursor.getColumnIndex(FIELD_show)) > 0 ? true : false);
        } catch (Exception e) {
            element.setShow(true);//默认显示
        }
        
        try {
            element.setEvaluation(cursor.getInt(cursor.getColumnIndex(FIELD_evaluation)) > 0 ? true : false);
        } catch (Exception e) {
            element.setEvaluation(false);//默认未评测
        }
        
        try {
            element.setScore(cursor.getInt(cursor.getColumnIndex(FIELD_score)));
        } catch (Exception e) {
            element.setScore(0);
        }
        
        try {
            element.setScoreZero(cursor.getInt(cursor.getColumnIndex(FIELD_score_zero)));
        } catch (Exception e) {
            element.setScoreZero(0);
        }
        
        try {
            element.setScoreOne(cursor.getInt(cursor.getColumnIndex(FIELD_score_one)));
        } catch (Exception e) {
            element.setScoreOne(0);
        }
        
        try {
            element.setScoreFive(cursor.getInt(cursor.getColumnIndex(FIELD_score_five)));
        } catch (Exception e) {
            element.setScoreFive(0);
        }
        
        try {
            element.setScoreSeven(cursor.getInt(cursor.getColumnIndex(FIELD_score_seven)));
        } catch (Exception e) {
            element.setScoreSeven(0);
        }
        
        try {
            element.setTime(cursor.getString(cursor.getColumnIndex(FIELD_time)));
        } catch (Exception e) {
            element.setTime(String.valueOf(System.currentTimeMillis()));
        }
        return element;
    }
    
    public void tempInsert(){
//        ContentValues values = new ContentValues();
//        values.put(FIELD_name, "家庭住宅防火检查内容");
        getElement().setId("0000");
        getElement().setName("家庭住宅防火检查内容");
        getElement().setTime(String.valueOf(System.currentTimeMillis()));
        getElement().setEvaluation(false);
        save();
//        DBHelper dbhelper = Global.getDBHelper();
//        dbhelper.insert(TABLE_NAME, null, values, null);
    }
}
