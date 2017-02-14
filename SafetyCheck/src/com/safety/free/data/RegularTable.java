package com.safety.free.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.safety.free.model.Global;

public class RegularTable extends BaseTable {

    public final static String TABLE_NAME = "SafetyCheckRegular";

    // 字段名称
    final static String FIELD_id = "id";
    final static String FIELD_name = "name";
    final static String FIELD_source = "source";
    final static String FIELD_show = "show";
    final static String FIELD_evaluation = "evaluation";
    final static String FIELD_score = "score";
    final static String FIELD_punishable_score = "punishable_score";
    final static String FIELD_category = "category";

    private RegularElement element;
    public RegularTable(){
        element = new RegularElement();
    }

    public RegularTable(Cursor c) {
        element = new RegularElement();
        loadFromCursor(c);
    }
    
    public RegularTable(String name, boolean isShow){
        setElement(new RegularElement(name, isShow));
    }
    
    public RegularTable(RegularElement element){
        this.element = element;
    }

    public RegularElement getElement() {
        return element;
    }

    public void setElement(RegularElement element) {
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
        sb.append(FIELD_source).append(" TEXT, ");
        sb.append(FIELD_show).append(" INTEGER, ");
        sb.append(FIELD_evaluation).append(" INTEGER, ");
        sb.append(FIELD_punishable_score).append(" TEXT, ");
        sb.append(FIELD_score).append(" INTEGER, ");
        sb.append(FIELD_category).append(" TEXT)");
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
            element.setSource(cursor.getString(cursor.getColumnIndex(FIELD_source)));
        } catch (Exception e) {
            element.setSource("");
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
            element.setScore(cursor.getInt(cursor.getColumnIndex(FIELD_score)));
        } catch (Exception e) {
            element.setScore(0);
        }

        try {
            element.setPunishableScore(cursor.getString(cursor.getColumnIndex(FIELD_punishable_score)));
        } catch (Exception e) {
            element.setPunishableScore("");
        }

        try {
            element.setCategory(cursor.getString(cursor.getColumnIndex(FIELD_category)));
        } catch (Exception e) {
            element.setCategory("");
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
     * 把当前评估细则信息生成一个ContentValues结构，以便进行数据库操作
     */
    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(FIELD_id, element.getId());
        values.put(FIELD_name, element.getName());
        values.put(FIELD_source, element.getSource());
        values.put(FIELD_show, element.isShow() ? 1 : 0);
        values.put(FIELD_evaluation, element.isEvaluation() ? 1 : 0);
        values.put(FIELD_score, element.getScore());
        values.put(FIELD_punishable_score, element.getPunishableScore());
        values.put(FIELD_category, element.getCategory());
        return values;
    }

    /**
     * 返回name对应的数据库记录
     */
    public synchronized static Cursor getCursor(String field, String name) {
        DBHelper dbhelper = Global.getDBHelper();
        if (dbhelper == null)
            return null;
        try {
            return dbhelper.query(TABLE_NAME, null, field + "=?", new String[] { name });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据name取得评估分类细则的信息
     * 
     * @param id
     * @return
     */
    public static RegularElement getRegularElement(String id) {
        RegularElement element = new RegularElement();
        Cursor cursor = getCursor(FIELD_name, id);
        if (cursor != null) {
            element = getRegularElement(cursor);
            cursor.close();
        }
        return element;
    }

    /**
     * 根据评估分类的名字取得全部评估细则的信息
     * 
     * @param categoryId
     * @return
     */
    public static ArrayList<RegularElement> getAllRegularElements(String categoryId) {
        if(categoryId == null && categoryId.length() <= 0)
            return null;
        ArrayList<RegularElement> elements = new ArrayList<RegularElement>();
        Cursor cursor = getCursor(FIELD_category, categoryId);
        if (cursor != null && cursor.moveToFirst()) {
            while (true) {
                RegularElement element = getRegularElement(cursor);
                elements.add(element);
                if (!cursor.moveToNext())
                    break;
            }
            cursor.close();
        }
        return elements;
    }
    
    private static RegularElement getRegularElement(Cursor cursor) {
        RegularElement element = new RegularElement();
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
            element.setSource(cursor.getString(cursor.getColumnIndex(FIELD_source)));
        } catch (Exception e) {
            element.setSource("");
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
            element.setScore(cursor.getInt(cursor.getColumnIndex(FIELD_score)));
        } catch (Exception e) {
            element.setScore(0);
        }

        try {
            element.setPunishableScore(cursor.getString(cursor.getColumnIndex(FIELD_punishable_score)));
        } catch (Exception e) {
            element.setPunishableScore("");
        }

        try {
            element.setCategory(cursor.getString(cursor.getColumnIndex(FIELD_category)));
        } catch (Exception e) {
            element.setCategory("");
        }
        return element;
    }
    
    public void tempInsert(){
        String []categories = {"住宅公共区域","住宅区域用电","住宅区域用气","住宅区域用火","住宅区域危险品","住宅区域装修","住宅区域消防应急设备","住宅机动车","住宅区域消防不安全状态或行为"};
        String [][]regulars = {{"周边高危场所",
            "楼层高危底商",
            "防火门",
            "疏散指示标志",
            "应急照明",
            "火灾探测器",
            "喷淋头",
            "声光警报器",
            "手动报警按钮",
            "室内消火栓箱",
            "灭火器",
            "通道",
            "楼梯间",
            "电梯",
            "合用前室",
            "消防控制室"}, 
            {"电线支路",
                "普通线径",
                "空调线径",
                "固定电源插座",
                "移动电源插座",
                "漏电保护装置",
                "照明灯具",
                "空调器",
                "电暖器",
                "电视机"
}, {"燃气管线接口",
    "燃气灶具",
    "油炸食物"}, {"吸烟",
        "蚊香",
        "蜡烛",
        "火柴",
        "打火机"}, {"汽油",
            "酒精",
            "稀料",
            "烟花炮竹"}, {"吊顶",
                "隔墙",
                "地面",
                "家具",
                "窗帘"}, {"灭火器",
                    "逃生梯",
                    "缓降器",
                    "防烟呼吸器",
                    "住宅建筑窗户"}, {"机动车停放",
                        "机动车保养",
                        "机动车内物品",
                        "机动车灭火器"}, {"住宅窗户",
                            "住宅凉台",
                            "外出",
                            "杂物",
                            "灭火器",
                            "火警电话",
                            "防烟逃生",
                            "未成年接触火具等"}};
        for (int j = 0; j < regulars.length; j++) {
            String[] names = regulars[j];
            for (int k = 0; k < names.length; k++) {
                element.setId("0000" + j + k);
                element.setName(names[k]);
                element.setScore(-1);
                element.setSource("公安部");
                element.setPunishableScore("0-1-5-7");
                element.setCategory("0000" + j);
                element.setShow(true);
                element.setEvaluation(false);
                save();
            }
        }
    }
}
