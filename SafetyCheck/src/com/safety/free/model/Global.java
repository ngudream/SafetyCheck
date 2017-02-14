package com.safety.free.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.DisplayMetrics;

import com.safety.free.R;
import com.safety.free.data.AssessTable;
import com.safety.free.data.CategoryTable;
import com.safety.free.data.DBHelper;
import com.safety.free.data.RegularTable;
import com.safety.free.data.User;
import com.safety.free.ui.BaseActivity;
import com.safety.free.ui.MainActivity;
import com.safety.free.ui.StartActivity;
import com.safety.free.utils.SCLog;

/**
 * 主要处理一些全局变量，函数
 * 
 */
public class Global {

    private static Activity  mActivity;
    private static Context mAppContext = null;
    private static DisplayMetrics dm = null;
    private static DBHelper dbHelper = null;
    public static User loginUser = null;
    /**
     * 是否应该执行数据插入数据库
     */
    private static final String INSERT_BOOK_DATA = "should_insert_data";
    /**
     * 是否应该显示start activity
     */
    private static final String SHOULD_SHOW_START_ACTIVITY = "should_show_start";
    
    /**
     * 执行数据库存储的单线程
     */
    private static ExecutorService mDatabaseExecutorService = null;
    /**
     * 柱状图的柱子的颜色
     */
    public static int []mColors = new int[]{Color.BLUE, Color.YELLOW, Color.GREEN, Color.RED, Color.CYAN, 
        Color.BLACK, Color.DKGRAY, Color.GRAY, Color.LTGRAY, Color.MAGENTA, Color.WHITE, Color.parseColor("#CCFF99"), 
        Color.parseColor("#FF9900"), Color.parseColor("#66CCCC")};

    public static void initApplication(Activity context) {
        mActivity = context;
        setApplicationContext(context);
        initDisplayLayout();
        createDBHelper();
        SCLog.setDebug(true);//发布前一定要设置为false
    }

    public static Context getApplicationContext() {
        return mAppContext;
    }

    public static void setApplicationContext(Context context) {
        mAppContext = context;
    }

    /**
     * 获得屏幕分辨率，及布局类型
     */
    public static void initDisplayLayout() {
        dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    /**
     * 获取屏幕矩阵
     * 
     * @return
     */
    public static DisplayMetrics getDisplayMetrics() {
        return dm;
    }

    private static void createDBHelper() {
        if (dbHelper == null)
            dbHelper = new DBHelper("safety");// loginUser.getUserID());
    }

    public static DBHelper getDBHelper() {
        return dbHelper;
    }
    
    public static void recycle(){
        mActivity = null;
        mAppContext = null;
        dbHelper = null;
    }
    
    /**
     * 将数据写入数据库
     */
    public static void insertDataToDB(){
        AssessTable assessTable = new AssessTable();
        assessTable.tempInsert();
        CategoryTable categoryTable = new CategoryTable();
        categoryTable.tempInsert();
        RegularTable regularTable = new RegularTable();
        regularTable.tempInsert();
    }

    /*
     * dp与px转换
     */
    public static int dip2px(float dpValue) {
        return (int) (dpValue * dm.density + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / dm.density + 0.5f);
    }
    
    public static SharedPreferences getSharedPreferences() {
        return mAppContext != null ? mAppContext.getSharedPreferences("config", Context.MODE_PRIVATE) : null;
    }
    
    public static void saveDatabasePreferences() {
        SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            Editor editor = preferences.edit();
            editor.putBoolean(INSERT_BOOK_DATA, false);
            editor.commit();
        }
    }
    
    public static boolean isShouldInsertBookData(){
        SharedPreferences preferences = getSharedPreferences();
        return preferences.getBoolean(INSERT_BOOK_DATA, true);
    }
    
    public static void saveStartPreferences(){
        SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            Editor editor = preferences.edit();
            editor.putBoolean(SHOULD_SHOW_START_ACTIVITY, false);
            editor.commit();
        }
    }
    
    public static boolean isShouldStartActivity(){
        SharedPreferences preferences = getSharedPreferences();
        return preferences.getBoolean(SHOULD_SHOW_START_ACTIVITY, true);
    }
    
    /**
     * 返回数据库存储的单线程 
     * @return
     */
    public static ExecutorService getDatabaseExecutorService(){
        if(mDatabaseExecutorService == null)
            mDatabaseExecutorService = Executors.newSingleThreadScheduledExecutor();
        return mDatabaseExecutorService;
    }
    
    /**
     * XYMultipleSeriesRenderer 类型的对象，用于提供图表展现时的一些样式， 这里我们用 getBarDemoRenderer
     * 方法来得到它。 getBarDemoRenderer 方法构建了一个 XYMultipleSeriesRenderer 用来设置2个系列各自的颜色
     */
    public static XYMultipleSeriesRenderer getFractionBarRenderer(int colorCount) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
        int []colors = new int[colorCount];
        for(int i = 0; i < colorCount; i++){
            colors[i] = mColors[i];
        }
//        r.setColor(Color.BLUE);//设置柱子的颜色
        seriesRenderer.setColors(colors);
        renderer.addSeriesRenderer(seriesRenderer);
        setChartSettings(renderer);
        return renderer;
    }

    /**
     * setChartSettings 方法设置了下坐标轴样式。
     */
    public static void setChartSettings(XYMultipleSeriesRenderer renderer) {
        renderer.setPanEnabled(false, false);// 不允许左右拖动,但不允许上下拖动.
        renderer.setZoomEnabled(false);
        renderer.setXLabels(5);// X轴的近似坐标数
        // renderer.setYLabels(5);
//        renderer.setShowAxes(false);   // 是否显示轴线
        renderer.setAxesColor(Color.BLACK); //设置轴颜色
        renderer.setXLabelsColor(Color.BLACK); // 设置X轴标签的显示颜色
        renderer.setLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK); // 设置Y轴标签的显示颜色
        renderer.setXLabelsAlign(Align.CENTER); // 设置X轴在标签哪边对齐方式
        renderer.setYLabelsAlign(Align.RIGHT); // 设置Y轴在标签哪边的对齐方式
        renderer.setMargins(new int[] { 50, 35, 15, 10 }); // 设置边距 (top, left, bottom, right)
        renderer.setMarginsColor(Color.TRANSPARENT);//设置4边留白透明
        renderer.setBackgroundColor(Color.TRANSPARENT);//(Color.parseColor("#f3f3f3")); // 图表部分的背景颜色
        renderer.setApplyBackgroundColor(true);
        renderer.setChartTitleTextSize(20); // 图表标题字体大小：20
        renderer.setLabelsTextSize(15); // 轴标签字体大小：15
        renderer.setLegendTextSize(15); // 图例字体大小：15
        renderer.setAxisTitleTextSize(16); // 坐标轴标题字体大小：16
        renderer.setDisplayChartValues(true);
        renderer.setXLabels(0); // 设置X轴不显示数字（改用我们手动添加的文字标签）
        renderer.setBarSpacing(0.5f);// 柱子间宽度
        renderer.setBarWidth(20f);
        // 重写 BarChart的drawSeries方法,让每个柱子显示不同颜色啊
    }
}
