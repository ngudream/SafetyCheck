package com.safety.free.fragment;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.safety.free.R;
import com.safety.free.data.AssessElement;
import com.safety.free.data.AssessTable;
import com.safety.free.data.CategoryElement;
import com.safety.free.data.CategoryTable;
import com.safety.free.model.Global;
import com.safety.free.utils.SCLog;
import com.slidingmenu.lib.SlidingMenu;

public class RegularContentViewLast extends RelativeLayout {
    
    private Button btn_statistic_quarter;//按季度统计
    private Button btn_statistic_fraction;//按分数统计
    private LinearLayout layout_last_statistic_process;
    private LinearLayout layout_statistic_bar;
    private TextView tv_finally_statistic_prompt;
    private RelativeLayout layout_finally_statistic_prompt;
    private RelativeLayout layout_regular_content_last_progress;
    private ImageView iv_show_last_statistic_process;
    private RelativeLayout layout_show_last_statistic_process;
    private GraphicalView mFractionBarView;
    private Context mContext;
    private String mAssessId;
    private AssessElement mCurrentAssessElement;
    private GestureDetector mGestureDetector;
    private ProcessGestureListener mGestureListener;

    public RegularContentViewLast(Context context) {
        super(context);
        this.mContext = context;
        findViews();
        setListeners();
        mGestureListener = new ProcessGestureListener();
        mGestureDetector = new GestureDetector(context, mGestureListener);
    }

    private void findViews() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.regular_content_last, null);
        btn_statistic_quarter = (Button) view.findViewById(R.id.btn_statistic_quarter);
        btn_statistic_fraction = (Button) view.findViewById(R.id.btn_statistic_fraction);
        layout_last_statistic_process = (LinearLayout) view.findViewById(R.id.layout_last_statistic_process);
        layout_statistic_bar = (LinearLayout) view.findViewById(R.id.layout_statistic_bar);
        tv_finally_statistic_prompt = (TextView) view.findViewById(R.id.tv_finally_statistic_prompt);
        layout_finally_statistic_prompt = (RelativeLayout) view.findViewById(R.id.layout_finally_statistic_prompt);
        layout_regular_content_last_progress = (RelativeLayout) view.findViewById(R.id.layout_regular_content_last_progress);
        iv_show_last_statistic_process = (ImageView) view.findViewById(R.id.iv_show_last_statistic_process);
        layout_show_last_statistic_process = (RelativeLayout) view.findViewById(R.id.layout_show_last_statistic_process);
        
        addView(view);
    }
    
    /**
     * 设置view的监听器
     */
    private void setListeners() {
        btn_statistic_quarter.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new RegularFinallyStatisticQuarterTask().execute();
                
            }
        });
        
        btn_statistic_fraction.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
            }
        });
        
        layout_show_last_statistic_process.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }
    
    /**
     * 从数据库中读取并显示数据
     */
    public void loadContent(String assessId) {
        this.mAssessId = assessId;
    }
    
    public void showViews(AssessElement assessElement){
        this.mCurrentAssessElement = assessElement;
        layout_regular_content_last_progress.setVisibility(View.VISIBLE);
        new LastContentLoadTask().execute();
    }
    
    /**
     *初始化时计算是否应该显示统计按钮
     */
    private class LastContentLoadTask extends AsyncTask<Void, Void, Boolean>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return CategoryTable.isAllVisibalCategoryFinished(mCurrentAssessElement.getId());
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                layout_finally_statistic_prompt.setVisibility(View.GONE);
                layout_show_last_statistic_process.setVisibility(View.VISIBLE);
                showProcessButton();
            }
            else{
                layout_finally_statistic_prompt.setVisibility(View.VISIBLE);
                layout_last_statistic_process.setVisibility(View.GONE);
                layout_show_last_statistic_process.setVisibility(View.GONE);
            }
            layout_regular_content_last_progress.setVisibility(View.INVISIBLE);
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
            ArrayList<CategoryElement> categoryElements = CategoryTable.getVisiableCategoryElements(mAssessId);
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
            mFractionBarView = ChartFactory.getBarChartView(mContext, dataSet, renderer, Type.DEFAULT);
            LayoutParams mChartParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layout_statistic_bar.addView(mFractionBarView, mChartParams);
            Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.biz_news_column_edit_fade_in);
            layout_statistic_bar.startAnimation(fadeIn);
            layout_statistic_bar.setVisibility(View.VISIBLE);
            fadeIn.reset();
            hideProcessButton();
        }
    }
    
    /**
     * 显示统计按钮
     */
    private void showProcessButton(){
        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.base_slide_up_in);
        layout_last_statistic_process.startAnimation(slideIn);
        layout_last_statistic_process.setVisibility(View.VISIBLE);
        slideIn.reset();
    }
    
    /**
     * 隐藏统计按钮
     */
    private void hideProcessButton(){
        Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.base_slide_up_out);
        layout_last_statistic_process.startAnimation(slideOut);
        layout_last_statistic_process.setVisibility(View.GONE);
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
        renderer.setGridColor(Color.LTGRAY); // 设置网格线的颜色
        renderer.setShowGrid(true);// 是否显示网格  
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
                    if(layout_last_statistic_process.getVisibility() != View.VISIBLE)
                        showProcessButton();
                }else if(y<=0){
                    //up
                    SCLog.d("cugblog", "onfling up " + y);
                    if(layout_last_statistic_process.getVisibility() == View.VISIBLE)
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
}
