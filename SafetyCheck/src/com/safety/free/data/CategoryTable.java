package com.safety.free.data;

import java.util.ArrayList;

import com.safety.free.R.id;
import com.safety.free.model.Global;
import com.safety.free.utils.SCLog;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

public class CategoryTable extends BaseTable {

    public final static String TABLE_NAME = "SafetyCheckCategory";

    //字段名称
    final static String FIELD_id = "id";
    final static String FIELD_name = "name";
    final static String FIELD_assess = "assess";
    final static String FIELD_show = "show";
    final static String FIELD_evaluation = "evaluation";
    final static String FIELD_score = "score";
    final static String FIELD_score_zero = "score_zero";
    final static String FIELD_score_one = "score_one";
    final static String FIELD_score_five = "score_five";
    final static String FIELD_score_seven = "score_seven";
    final static String FIELD_time = "time";

    private CategoryElement element;
    
    public CategoryTable(){
        element = new CategoryElement();
    }
    
    public CategoryTable(String name, boolean isShow){
        setElement(new CategoryElement(name, isShow));
    }
    
    public CategoryTable(CategoryElement element){
        this.setElement(element);
    }

    public CategoryTable(Cursor c) {
        element = new CategoryElement();
        loadFromCursor(c);
    }

    public CategoryElement getElement() {
        return element;
    }

    public void setElement(CategoryElement element) {
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
        sb.append(FIELD_assess).append(" TEXT, ");
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
            element.setAssess(cursor.getString(cursor.getColumnIndex(FIELD_assess)));
        } catch (Exception e) {
            element.setAssess("");
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
            element.setScoreFive(cursor.getInt(cursor.getColumnIndex(FIELD_show)));
        } catch (Exception e) {
            element.setScoreFive(0);
        }
        
        try {
            element.setScoreSeven(cursor.getInt(cursor.getColumnIndex(FIELD_show)));
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
        return getElement().getId();
    }

    /**
     * 把当前用户信息生成一个ContentValues结构，以便进行数据库操作
     */
    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(FIELD_id, element.getId());
        values.put(FIELD_name, element.getName());
        values.put(FIELD_assess, element.getAssess());
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
    public synchronized static Cursor getCursor(String field, String id) {
        DBHelper dbhelper = Global.getDBHelper();
        if (dbhelper == null)
            return null;
        try {
            return dbhelper.query(TABLE_NAME, null, field + "=?", new String[] { id });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据name取得评估分类的信息
     * 
     * @param id
     * @return
     */
    public static CategoryElement getCategoryElement(String id) {
        CategoryElement element = new CategoryElement();
        Cursor cursor = getCursor(FIELD_id, id);
        if (cursor != null) {
            element = getCategoryElement(cursor);
            cursor.close();
        }
        return element;
    }

    /**
     * 根据评估名字assess取得全部评估分类的信息
     * 
     * @param assessid
     * @return
     */
    public static ArrayList<CategoryElement> getAllCategoryElements(String assessId) {
        ArrayList<CategoryElement> elements = new ArrayList<CategoryElement>();
        Cursor cursor = getCursor(FIELD_assess, assessId);
        if (cursor != null && cursor.moveToFirst()) {
            while (true) {
                CategoryElement element = getCategoryElement(cursor);
                elements.add(element);
                if (!cursor.moveToNext())
                    break;
            }
            cursor.close();
        }
        return elements;
    }
    
    public static ArrayList<CategoryElement> getCategoryElementsById(ArrayList<String> ids){
        ArrayList<CategoryElement> elements = new ArrayList<CategoryElement>();
//        String []selection = new String[ids.size()];
//        for(int i = 0; i < ids.size(); i++){
//            selection[i] = ids.get(i);
//        }
        String args = "";
        for(int i = 0; i < ids.size(); i++){
            args = args + "'" + ids.get(i) + "'" + ",";
        }
        args = args.substring(0, args.length() - 1);
        DBHelper dbhelper = Global.getDBHelper();
        Cursor cursor = null;
        if (dbhelper == null)
            return null;
        try {
            String sql = "select * from " + TABLE_NAME + " where " + FIELD_id + " in(" + args + ")";
            cursor = dbhelper.getReadableDatabase().rawQuery(sql, null);//.query(TABLE_NAME, null, FIELD_id + "=?", selection);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (cursor != null && cursor.moveToFirst()) {
            while (true) {
                CategoryElement element = getCategoryElement(cursor);
                elements.add(element);
                if (!cursor.moveToNext())
                    break;
            }
            cursor.close();
        }
        return elements;
    }
    
    /**
     * 获取用户没有删除的评估分类
     * @param assess
     * @return
     */
    public static ArrayList<CategoryElement> getVisiableCategoryElements(String assessId){
        ArrayList<CategoryElement> elements = new ArrayList<CategoryElement>();
        Cursor cursor = getCursor(FIELD_assess, assessId);
        if (cursor != null && cursor.moveToFirst()) {
            while (true) {
                CategoryElement element = getCategoryElement(cursor);
                if(element.isShow())
                    elements.add(element);
                if (!cursor.moveToNext())
                    break;
            }
            cursor.close();
        }
        return elements;
    }
    
    /**
     * 获取用户未评估分类
     * @param assess
     * @return
     */
    public static ArrayList<CategoryElement> getUnfinishedCategoryElements(String assessId){
        ArrayList<CategoryElement> elements = new ArrayList<CategoryElement>();
        Cursor cursor = getCursor(FIELD_assess, assessId);
        if (cursor != null && cursor.moveToFirst()) {
            while (true) {
                CategoryElement element = getCategoryElement(cursor);
                if(!element.isEvaluation())
                    elements.add(element);
                if (!cursor.moveToNext())
                    break;
            }
            cursor.close();
        }
        return elements;
    }
    
    /**
     * 获取用户已评估分类
     * @param assess
     * @return
     */
    public static ArrayList<CategoryElement> getFinishedCategoryElements(String assessId){
        ArrayList<CategoryElement> elements = new ArrayList<CategoryElement>();
        Cursor cursor = getCursor(FIELD_assess, assessId);
        if (cursor != null && cursor.moveToFirst()) {
            while (true) {
                CategoryElement element = getCategoryElement(cursor);
                if(element.isEvaluation())
                    elements.add(element);
                if (!cursor.moveToNext())
                    break;
            }
            cursor.close();
        }
        return elements;
    }
    
    /**
     * 是否应该显示
     * @param categoryId
     * @return
     */
    public static boolean isCategoryShow(String categoryId){
        boolean isShow = false;
        Cursor cursor = getCursor(FIELD_id, categoryId);
        if (cursor != null && cursor.moveToFirst()) {
            isShow = isShow(cursor);
        }
        cursor.close();
        return isShow;
    }
    
    /**
     * 将指定的评估项隐藏
     * @param categoryId
     */
    public static void hideCategory(String categoryId){
        DBHelper dbhelper = Global.getDBHelper();
        if (dbhelper == null)
            return;
        try {
            ContentValues values = new ContentValues();
            values.put(FIELD_show, 0);
            dbhelper.update(TABLE_NAME, values, FIELD_id + "=?", new String[] { categoryId }, null);//(TABLE_NAME, null, FIELD_id + "=?", new String[] { categoryId });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 显示指定的category
     * @param categoryId
     */
    public static void showCategory(String categoryId){
        DBHelper dbhelper = Global.getDBHelper();
        if (dbhelper == null)
            return;
        try {
            ContentValues values = new ContentValues();
            values.put(FIELD_show, 1);
            dbhelper.update(TABLE_NAME, values, FIELD_id + "=?", new String[] { categoryId }, null);//(TABLE_NAME, null, FIELD_id + "=?", new String[] { categoryId });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 是否全部category都进行了完整的评估
     * @param id
     * @return
     */
    public static boolean isAllVisibalCategoryFinished(String assessId){
        if(assessId == null)
            return false;
        boolean isFinished = true;
        Cursor cursor = getCursor(FIELD_assess, assessId);
        if (cursor != null && cursor.moveToFirst()) {
            while (true) {
                if(isShow(cursor)){
                    if(!isFinished(cursor)){
                        isFinished = false;
                        break;
                    }
                }
                if (!cursor.moveToNext())
                    break;
            }
            cursor.close();
        }
        return isFinished;
    }
    
    /**
     * 判断是否已经评估过
     * @param cursor
     * @return
     */
    private static boolean isFinished(Cursor cursor){
        boolean finished = false;
        try {
            finished = cursor.getInt(cursor.getColumnIndex(FIELD_evaluation)) > 0 ? true : false;
        } catch (Exception e) {
        }
        return finished;
    }
    
    /**
     * 判断是否显示
     * @param cursor
     * @return
     */
    private static boolean isShow(Cursor cursor){
        boolean isShow = false;
        try {
            isShow = cursor.getInt(cursor.getColumnIndex(FIELD_show)) > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isShow;
    }

    private static CategoryElement getCategoryElement(Cursor cursor) {
        CategoryElement element = new CategoryElement();
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
            element.setAssess(cursor.getString(cursor.getColumnIndex(FIELD_assess)));
        } catch (Exception e) {
            element.setAssess("");
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
        return element;
    }
    
    public void tempInsert(){
        String names[] = {"住宅公共区域","住宅区域用电","住宅区域用气","住宅区域用火","住宅区域危险品","住宅区域装修","住宅区域消防应急设备","住宅机动车","住宅区域消防不安全状态或行为"};
        for(int i = 0; i < names.length; i++){
            getElement().setId("0000" + i);
            getElement().setName(names[i]);
            getElement().setAssess("0000");
            getElement().setShow(true);
            getElement().setTime(String.valueOf(System.currentTimeMillis()));
            getElement().setEvaluation(false);
            save();
        }
    }
}
