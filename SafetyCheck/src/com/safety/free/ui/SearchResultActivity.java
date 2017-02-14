package com.safety.free.ui;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.data.AssessElement;
import com.safety.free.data.CategoryElement;
import com.safety.free.data.CategoryTable;
import com.safety.free.model.Global;
import com.safety.free.utils.SCLog;

public class SearchResultActivity extends Activity {
    
    private Button btn_search_result_statistic_quarter;//按季度统计
    private Button btn_search_result_statistic_fraction;//按分数统计
    private LinearLayout layout_search_result_statistic_process_btn;
    private LinearLayout layout_search_result_statistic_bar;
    private TextView tv_search_result_finally_statistic_prompt;
    private RelativeLayout layout_search_result_finally_statistic_prompt;
    private RelativeLayout layout_search_result_progress;
    private ImageView iv_search_result_statistic_process;
    private RelativeLayout layout_search_result_statistic_process_line;
    private GraphicalView mFractionBarView;
//    private Context mContext;
    private String mAssessId;
    private GestureDetector mGestureDetector;
    private ProcessGestureListener mGestureListener;
    
    private GestureDetector mResultGestureDetector;
    private SearchResultGestureListener mResultGestureListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_result);
        
        findViews();
        setListeners();
        mGestureListener = new ProcessGestureListener();
        mGestureDetector = new GestureDetector(getApplicationContext(), mGestureListener);
        
        mResultGestureListener = new SearchResultGestureListener();
        mResultGestureDetector = new GestureDetector(getApplicationContext(), mResultGestureListener);
        
        new StatisticContentLoadTask().execute();
    }
    
    private void findViews() {
        btn_search_result_statistic_quarter = (Button) findViewById(R.id.btn_search_result_statistic_quarter);
        btn_search_result_statistic_fraction = (Button) findViewById(R.id.btn_search_result_statistic_fraction);
        layout_search_result_statistic_process_btn = (LinearLayout) findViewById(R.id.layout_search_result_statistic_process_btn);
        layout_search_result_statistic_bar = (LinearLayout) findViewById(R.id.layout_search_result_statistic_bar);
        tv_search_result_finally_statistic_prompt = (TextView) findViewById(R.id.tv_search_result_finally_statistic_prompt);
        layout_search_result_finally_statistic_prompt = (RelativeLayout) findViewById(R.id.layout_search_result_finally_statistic_prompt);
        layout_search_result_progress = (RelativeLayout) findViewById(R.id.layout_search_result_progress);
        iv_search_result_statistic_process = (ImageView) findViewById(R.id.iv_search_result_statistic_process);
        layout_search_result_statistic_process_line = (RelativeLayout) findViewById(R.id.layout_search_result_statistic_process_line);
        
    }
    
    /**
     * 设置view的监听器
     */
    private void setListeners() {
        btn_search_result_statistic_quarter.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new RegularFinallyStatisticQuarterTask().execute();
            }
        });
        
        btn_search_result_statistic_fraction.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
            }
        });
        
        layout_search_result_statistic_process_line.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return mResultGestureDetector.onTouchEvent(event);
    }
    
    private class SearchResultGestureListener implements OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            SCLog.d("cugblog", "onfling");
            float x_limit = 80;
            if(x > x_limit || x < -x_limit){
                if(x>0){
                  //right
                }else if(x<=0){
                  //left
                    SearchResultActivity.this.finish();
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }
    }
    
    /**
     *初始化时计算是否应该显示统计按钮
     */
    private class StatisticContentLoadTask extends AsyncTask<Void, Void, Boolean>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return CategoryTable.isAllVisibalCategoryFinished("0000");
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                layout_search_result_finally_statistic_prompt.setVisibility(View.GONE);
                layout_search_result_statistic_process_line.setVisibility(View.VISIBLE);
                showProcessButton();
            }
            else{
                layout_search_result_finally_statistic_prompt.setVisibility(View.VISIBLE);
                layout_search_result_statistic_process_line.setVisibility(View.GONE);
                layout_search_result_statistic_process_btn.setVisibility(View.GONE);
            }
            layout_search_result_progress.setVisibility(View.INVISIBLE);
        }
    }
    
    /**
     * 统计分数的异步任务
     */
    private class RegularFinallyStatisticQuarterTask extends AsyncTask<Void, Void, ArrayList<Object>>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Object> doInBackground(Void... params) {
            ArrayList<CategoryElement> categoryElements = CategoryTable.getVisiableCategoryElements("0000");
            if(categoryElements == null)
                return null;
            String title = getResources().getString(R.string.quarter_distribute);
            ArrayList<Object> xLabels = new ArrayList<Object>();
            xLabels.add("第一季度");
            xLabels.add("第二季度");
            xLabels.add("第三季度");
            xLabels.add("第四季度");
            ArrayList<Object> yLabels = new ArrayList<Object>();
            yLabels.add(20);
            yLabels.add(40);
            yLabels.add(60);
            yLabels.add(80);
            yLabels.add(100);
            return getStatisticResult(categoryElements, title, xLabels, yLabels, 100);
        }
        
        @Override
        protected void onPostExecute(ArrayList<Object> result) {
            super.onPostExecute(result);
            XYMultipleSeriesRenderer renderer = (XYMultipleSeriesRenderer) result.get(0);
            XYMultipleSeriesDataset dataSet = (XYMultipleSeriesDataset) result.get(1);
            mFractionBarView = ChartFactory.getBarChartView(getApplicationContext(), dataSet, renderer, Type.DEFAULT);
            LayoutParams mChartParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layout_search_result_statistic_bar.addView(mFractionBarView, mChartParams);
            Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.biz_news_column_edit_fade_in);
            layout_search_result_statistic_bar.startAnimation(fadeIn);
            layout_search_result_statistic_bar.setVisibility(View.VISIBLE);
            fadeIn.reset();
            hideProcessButton();
        }
    }
    
    /**
     * 显示统计按钮
     */
    private void showProcessButton(){
        Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.base_slide_up_in);
        layout_search_result_statistic_process_btn.startAnimation(slideIn);
        layout_search_result_statistic_process_btn.setVisibility(View.VISIBLE);
        slideIn.reset();
    }
    
    /**
     * 隐藏统计按钮
     */
    private void hideProcessButton(){
        Animation slideOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.base_slide_up_out);
        layout_search_result_statistic_process_btn.startAnimation(slideOut);
        layout_search_result_statistic_process_btn.setVisibility(View.GONE);
        slideOut.reset();
    }
    
    private ArrayList<Object> getStatisticResult(ArrayList<CategoryElement> categoryElements, String title, ArrayList<Object> xLabels, ArrayList<Object> yLabels, double yAxisMax){
        ArrayList<Object> arrayList = new ArrayList<Object>();
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        Global.setChartSettings(renderer);
        setChartSettings(renderer, xLabels, yLabels, yAxisMax);
        int size = categoryElements.size();
        int []colors = new int[size];
        for(int i = 0; i < size; i++){
            colors[i] = Global.mColors[i];
        }
        for(int i = 0; i < categoryElements.size(); i++){
            XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
            xyRenderer.setColor(colors[i]);
//            xyRenderer.setDisplayChartValues(true);//轴上方是否显示结果
            renderer.addSeriesRenderer(xyRenderer);
        }
        XYMultipleSeriesDataset dataSet = getFractionBarDataset(title, categoryElements, xLabels.size());
        arrayList.add(renderer);
        arrayList.add(dataSet);
        return arrayList;
        // Intent intent = ChartFactory.getBarChartIntent(getActivity(),
        // //跳转到别一个activity显示
        // getFractionBarDataset(), renderer, Type.DEFAULT);
        // startActivity(intent);
    }
    
    /**
     * XYMultipleSeriesDataset 类型的对象，用于提供图表需要表示的数据集， 这里我们用 getBarDemoDataset
     * 来得到它。
     */
    private XYMultipleSeriesDataset getFractionBarDataset(String title, ArrayList<CategoryElement> categoryElements, int xCount) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//        CategorySeries series = new CategorySeries(title);
        int size = categoryElements.size();
        for(int i = 0; i < size; i++){
            CategoryElement element = categoryElements.get(i);
            CategorySeries series = new CategorySeries(element.getName());
            SCLog.d("cugblog", "element score=" + element.getScore());
            for(int j = 0; j < xCount; j++){
                series.add(element.getScore());//添加y轴数据
            }
            dataset.addSeries(series.toXYSeries());
        }
        
//        java.text.DecimalFormat df=new java.text.DecimalFormat("#.##");
//        String average = getResources().getString(R.string.category_fraction_text);
//        average = average.replace("%1", String.valueOf(df.format((Double)fractions.get(4))));
        
        return dataset;
    }

    /**
     * setChartSettings 方法设置了下坐标轴样式。
     */
    private void setChartSettings(XYMultipleSeriesRenderer renderer, ArrayList<Object> xLabels, ArrayList<Object> yLabels, double yAxisMax) {
        renderer.setPanEnabled(true, false);
        renderer.setZoomButtonsVisible(true);
        renderer.setGridColor(Color.BLACK); // 设置网格线的颜色
//        renderer.setShowGrid(true);// 是否显示网格  
        renderer.setShowGridX(true);
        renderer.setBarSpacing(0.1f);// 柱子间宽度
        renderer.setBarWidth(16f);
        renderer.setYLabels(0);//设置y轴不显示数据，自定义数据
//        renderer.setYLabels(5);
        renderer.addXTextLabel(0, "");
        for(int i = 0; i < xLabels.size(); i++){
            renderer.addXTextLabel(i + 1, String.valueOf(xLabels.get(i)));
        }
//        renderer.addXTextLabel(0, "");
//        renderer.addXTextLabel(1, "0");
//        renderer.addXTextLabel(2, "1");
//        renderer.addXTextLabel(3, "5");
//        renderer.addXTextLabel(4, "7");
        
        renderer.addYTextLabel(0, "");
        for(int i = 0; i < yLabels.size(); i++){
            int label = (Integer) yLabels.get(i);
            renderer.addYTextLabel(i + label, String.valueOf(label));
        }
        
        renderer.setChartTitle(getResources().getString(R.string.safety_check_assess_result));
//        renderer.setXTitle("横坐标");
        renderer.setYTitle("分数");
        
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(yAxisMax + 5);
        // 等价于:
//        double[] range = { 0, 10, 1, 200 };
//        renderer.setRange(range);
    }
    
    public void onDestroy() {
        super.onDestroy();
    }
    
    private class ProcessGestureListener implements OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            SCLog.d("cugblog", "onfling");
            float y_limit = 50;
            if(y > y_limit || y < -y_limit){
                if(y>0){
                    //down
                    SCLog.d("cugblog", "onfling down " + y);
                    if(layout_search_result_statistic_process_btn.getVisibility() != View.VISIBLE)
                        showProcessButton();
                }else if(y<=0){
                    //up
                    SCLog.d("cugblog", "onfling up " + y);
                    if(layout_search_result_statistic_process_btn.getVisibility() == View.VISIBLE)
                        hideProcessButton();
                }
            }
            
//            if(x>0){
//                //right
//            }else if(x<=0){
//                //left
//            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * finish当前activity
     */
    private void back(){
        finish();
    }
}
