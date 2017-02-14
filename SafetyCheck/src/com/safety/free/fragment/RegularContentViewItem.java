package com.safety.free.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.data.AssessElement;
import com.safety.free.data.CategoryElement;
import com.safety.free.data.CategoryTable;
import com.safety.free.data.RegularElement;
import com.safety.free.data.RegularTable;
import com.safety.free.model.Global;
import com.safety.free.utils.SCLog;

/**
 * viewpager页显示评估内容
 *
 */
public class RegularContentViewItem extends RelativeLayout {

    private ListView listview_vp_content;

    private ListContentAdapter mlistContentAdapter;

    private ArrayList<RegularTable> mContentsList = new ArrayList<RegularTable>();;

    private Button btn_vp_content_statistics;
    private Button btn_vp_content_commit;

    private GraphicalView mFractionBarView;
    
    private TextView tv_remain_regular;
    private TextView tv_category_fraction;
    private LinearLayout layout_vp_content_statistics;//显示结果
    private RelativeLayout layout_regular_content_load_progress;
    private RelativeLayout layout_statistic_regular_load_progress;
    private LinearLayout layout_regular_content_process;
    private ImageView iv_regular_content_process_box;

    /**
     * 显示柱状图
     */
    private LinearLayout layout_vp_content_statistics_bar;
    
    private Context mContext;
    /**
     * 标记是否显示结果
     */
    private boolean isStatisticShow = true;
    
    private CategoryElement mCategoryElement = null;
    private AssessElement mAssessElement;
    
    private int mToBeAssessedCount = 0;
    /**
     * 保存评估分数
     */
//    private HashMap<String, Double> mFractionMap = null;
    /**
     * 保存未评估项评估后的分数
     */
//    private HashMap<String, Double> mRemainMap = null;
    /**
     * 标记是否需要执行数据库操作，保存regular
     */
    private boolean isNeedSaveToDatabase = false;
    /**
     * 标记box是否是第一次打开
     */
    private boolean isBoxFirstTimeOpen = true;
    
    /**
     * 每个category下的regular总数
     */
//    private int mTotalRegularCount = 0;
    
    private Handler mHandler = new Handler();

    public RegularContentViewItem(Context context) {
        super(context);
        this.mContext = context;
        findViews();
        setListeners();
    }

    private void findViews() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.regular_content, null);
        listview_vp_content = (ListView) view.findViewById(R.id.listview_vp_content);

        tv_remain_regular = (TextView) view.findViewById(R.id.tv_remain_regular);
        tv_category_fraction = (TextView) view.findViewById(R.id.tv_category_fraction);
        layout_vp_content_statistics = (LinearLayout) view.findViewById(R.id.layout_vp_content_statistics);
        btn_vp_content_statistics = (Button) view.findViewById(R.id.btn_vp_content_statistics);
        btn_vp_content_commit = (Button) view.findViewById(R.id.btn_vp_content_commit);
        layout_vp_content_statistics_bar = (LinearLayout) view.findViewById(R.id.layout_vp_content_statistics_bar);
        layout_regular_content_load_progress = (RelativeLayout) view.findViewById(R.id.layout_regular_content_load_progress);
        layout_statistic_regular_load_progress = (RelativeLayout) view.findViewById(R.id.layout_statistic_regular_load_progress);
        layout_regular_content_process = (LinearLayout) view.findViewById(R.id.layout_regular_content_process);
        iv_regular_content_process_box = (ImageView) view.findViewById(R.id.iv_regular_content_process_box);
        
        addView(view);
    }
    
    /**
     * 设置view的监听器
     */
    private void setListeners() {
        listview_vp_content.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        
        btn_vp_content_commit.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                saveRegularToDatabase();
            }
        });

        btn_vp_content_statistics.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isStatisticShow){//还没有统计，开异步任务去统计
                    isStatisticShow = false;
                    layout_statistic_regular_load_progress.setVisibility(View.VISIBLE);
                    RegularStatisticLoadTask statisticLoadTask = new RegularStatisticLoadTask();
                    statisticLoadTask.execute();
                }
                else{
                    isStatisticShow = true;
                    btn_vp_content_statistics.setText(getResources().getString(R.string.regular_statistics));
                    layout_vp_content_statistics_bar.setVisibility(View.GONE);
                    layout_vp_content_statistics.setVisibility(View.GONE);
                }
            }
        });
        
        iv_regular_content_process_box.setOnClickListener(new View.OnClickListener() {//箱子
            
            @Override
            public void onClick(View v) {
                if(!isBoxFirstTimeOpen){
                    if(layout_regular_content_process.getVisibility() == View.VISIBLE)
                        closeBox();
                    else
                        openBox();
                }
            }
        });
    }
    
    private ArrayList<Object> getStatisticResult(){
        ArrayList<Object> arrayList = new ArrayList<Object>();
        XYMultipleSeriesRenderer renderer = Global.getFractionBarRenderer(4);
        setChartSettings(renderer);
        arrayList.add(renderer);
        XYMultipleSeriesDataset dataSet = getFractionBarDataset();
        arrayList.add(dataSet);
        return arrayList;
    }
    
    /**
     * 从数据库中读取并显示数据
     */
    public void loadContent(AssessElement assessElement, CategoryElement categoryElement) {
        SCLog.d("cugblog", "loadContent");
        mAssessElement = assessElement;
        mCategoryElement = categoryElement;
        mContentsList = new ArrayList<RegularTable>();
        layout_regular_content_load_progress.setVisibility(View.VISIBLE);
        RegularContentLoadTask loadTask = new RegularContentLoadTask();//异步加载
        loadTask.execute();
    }
    
    /**
     * 处理是显示提示还是显示统计按钮
     */
    private void processPrompt(int remainCount){
        if(remainCount > 0){//如果还有没评估的，则显示提示
            tv_remain_regular.setVisibility(View.VISIBLE);
            layout_regular_content_process.setVisibility(View.GONE);
            btn_vp_content_commit.setVisibility(View.GONE);
            btn_vp_content_statistics.setVisibility(View.GONE);
            iv_regular_content_process_box.setVisibility(View.GONE);
            String prompt = getResources().getString(R.string.remain_regular);
            prompt = prompt.replace("%1", String.valueOf(remainCount));
            tv_remain_regular.setText(prompt);
        }
        else{//全部评估过了，则显示统计按钮
            SCLog.d("cugblog", "processPrompt");
            if(isBoxFirstTimeOpen){
                isBoxFirstTimeOpen = false;
//                mAssessElement.setEvaluation(true);
                mCategoryElement.setEvaluation(true);
                iv_regular_content_process_box.setVisibility(View.VISIBLE);
                openBox();
            }
        }
    }
    
    private void openBox(){
        tv_remain_regular.setVisibility(View.GONE);
        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.safety_base_box_open);
        layout_regular_content_process.startAnimation(slideIn);
        iv_regular_content_process_box.setImageResource(R.drawable.box_open);
        layout_regular_content_process.setVisibility(View.VISIBLE);
        btn_vp_content_commit.setVisibility(View.VISIBLE);
        btn_vp_content_statistics.setVisibility(View.VISIBLE);
        slideIn.reset();
    }
    
    private void closeBox(){
        Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.safety_base_box_closed);
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                iv_regular_content_process_box.setImageResource(R.drawable.box_closed);
            }
        }, 200);
        layout_regular_content_process.startAnimation(slideOut);
        layout_regular_content_process.setVisibility(View.GONE);
        btn_vp_content_commit.setVisibility(View.GONE);
        btn_vp_content_statistics.setVisibility(View.GONE);
        slideOut.reset();
    }
    
    public void onDestroy() {
        saveRegularToDatabase();
        mAssessElement = null;
        mContentsList = null;
//        mFractionMap = null;
//        mRemainMap = null;
        SCLog.d("cugblog", "onDestroy");
    }
    
    /**
     * 从数据库取数据
     */
    private void prepareData(){
        ArrayList<RegularElement> elements = RegularTable.getAllRegularElements(mCategoryElement.getId());
        try {
            int remainCount = 0;
            if(elements != null){
                for(int i = 0; i < elements.size(); i++){
                    RegularElement element = elements.get(i);
                    SCLog.d("cugblog", "load data=" + element.getName() + "  " + element.getScore());
                    if(!element.isEvaluation()){
                        remainCount++;
                    }
                    mContentsList.add(new RegularTable(element));
                }
            }
            mToBeAssessedCount = remainCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 从数据库取完数据后更新UI
     */
    private void getDataFinished(){
        processPrompt(mToBeAssessedCount);
        layout_regular_content_load_progress.setVisibility(View.INVISIBLE);
        mlistContentAdapter = new ListContentAdapter();
        listview_vp_content.setAdapter(mlistContentAdapter);
    }
    
    /**
     *初始化时加载内容异步线程 
     */
    private class RegularContentLoadTask extends AsyncTask<Void, Void, Void>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SCLog.d("cugblog", "doInBackground");
            prepareData();
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            getDataFinished();
//            mlistContentAdapter.notifyDataSetChanged();
        }
    }
    
    /**
     * 统计分数的异步任务
     */
    private class RegularStatisticLoadTask extends AsyncTask<Void, Void, ArrayList<Object>>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Object> doInBackground(Void... params) {
            return getStatisticResult();
        }
        
        @Override
        protected void onPostExecute(ArrayList<Object> result) {
            super.onPostExecute(result);
            XYMultipleSeriesRenderer renderer = (XYMultipleSeriesRenderer) result.get(0);
            XYMultipleSeriesDataset dataSet = (XYMultipleSeriesDataset) result.get(1);
            mFractionBarView = ChartFactory.getBarChartView(mContext, dataSet, renderer, Type.DEFAULT);
            LayoutParams mChartParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layout_vp_content_statistics_bar.addView(mFractionBarView, mChartParams);
            layout_vp_content_statistics_bar.setVisibility(View.VISIBLE);
            // Intent intent = ChartFactory.getBarChartIntent(getActivity(),
            // //跳转到别一个activity显示
            // getFractionBarDataset(), renderer, Type.DEFAULT);
            // startActivity(intent);
            layout_vp_content_statistics.setVisibility(View.VISIBLE);
            btn_vp_content_statistics.setText(getResources().getString(R.string.hide_statistics));
            layout_statistic_regular_load_progress.setVisibility(View.INVISIBLE);
        }
    }
    
    /**
     * 将分数保存到数据库
     */
    public void  saveRegularToDatabase(){
        if(isNeedSaveToDatabase){
            SCLog.d("cugblog", "save regular to database");
            isNeedSaveToDatabase = false;
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    computeCategoryFraction();
                    for(RegularTable table : mContentsList){
                        table.save();
                    }
                    //category分数
                    new CategoryTable(mCategoryElement).save();
                }
            }).start();
        }
    }
    
    /**
     * 计算分数
     * @return
     */
    private ArrayList<Object> computeCategoryFraction(){
        ArrayList<Object> fractions = new ArrayList<Object>();
        int zero = 0, one = 0, five = 0, seven = 0;
        for(RegularTable table : mContentsList){
            double score = table.getElement().getScore();
            if (score <= 0)
                zero++;
            else if (score == 1)
                one++;
            else if (score == 5)
                five++;
            else if (score == 7)
                seven++;
        }
        
        double score = ((double)(one * 1 + five * 5 + seven * 7) / (7 * mContentsList.size())) * 100;
        fractions.add(zero);
        fractions.add(one);
        fractions.add(five);
        fractions.add(seven);
        fractions.add(score);
        
        mCategoryElement.setScore(score);
        mCategoryElement.setScoreZero(zero);
        mCategoryElement.setScoreOne(one);
        mCategoryElement.setScoreFive(five);
        mCategoryElement.setScoreSeven(seven);
        return fractions;
    }
    
    /**
     * XYMultipleSeriesDataset 类型的对象，用于提供图表需要表示的数据集， 这里我们用 getBarDemoDataset
     * 来得到它。
     */
    private XYMultipleSeriesDataset getFractionBarDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        CategorySeries series = new CategorySeries(getResources().getString(R.string.fraction_distribute));
        
        final ArrayList<Object> fractions = computeCategoryFraction();
        for(int i = 0; i < fractions.size() - 1; i++){
            series.add((Integer)fractions.get(i));//添加y轴数据
        }
        
        dataset.addSeries(series.toXYSeries());
        
        mHandler.post(new Runnable() {//回主线程
            
            @Override
            public void run() {
                java.text.DecimalFormat df=new java.text.DecimalFormat("#.##");
                String average = getResources().getString(R.string.category_fraction_text);
                average = average.replace("%1", String.valueOf(df.format((Double)fractions.get(4))));
                tv_category_fraction.setText(average);
            }
        });
        
        //将结果保存到数据库
//        saveRegularToDatabase();
        
        return dataset;
    }

    /**
     * setChartSettings 方法设置了下坐标轴样式。
     */
    private void setChartSettings(XYMultipleSeriesRenderer renderer) {
        renderer.addXTextLabel(0, "");
        renderer.addXTextLabel(1, "0");
        renderer.addXTextLabel(2, "1");
        renderer.addXTextLabel(3, "5");
        renderer.addXTextLabel(4, "7");

        renderer.setChartTitle(getResources().getString(R.string.fraction_distribute_statistics));
//        renderer.setXTitle("横坐标");
//        renderer.setYTitle("纵坐标");
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(mContentsList.size() + 5);
    }

    private class ListContentAdapter extends BaseAdapter {

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder = convertView == null ? new ViewHolder() : (ViewHolder) convertView.getTag();

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.regular_content_item, null);
                holder.indexView = (TextView) convertView.findViewById(R.id.tv_viewpager_content_index);
                holder.contentView = (TextView) convertView.findViewById(R.id.tv_viewpager_content);
                holder.radioGroup = (RadioGroup) convertView.findViewById(R.id.rg_viewpager_content_score);
                holder.radioButtonZero = (RadioButton) convertView.findViewById(R.id.rb_viewpager_content_score_zero);
                holder.radioButtonOne = (RadioButton) convertView.findViewById(R.id.rb_viewpager_content_score_one);
                holder.radioButtonFive = (RadioButton) convertView.findViewById(R.id.rb_viewpager_content_score_five);
                holder.radioButtonSeven = (RadioButton) convertView.findViewById(R.id.rb_viewpager_content_score_seven);
                holder.radioGroup.clearCheck();
                convertView.setTag(holder);
            }
            final RegularElement element = mContentsList.get(position).getElement();
            holder.indexView.setText(String.valueOf(position));
            if(element.isEvaluation()){
                holder.indexView.setBackgroundResource(R.drawable.btn_radio_off_focused_holo_dark);
            }
            else{
                holder.indexView.setBackgroundResource(R.drawable.circle_out);
            }
            holder.contentView.setText(element.getName());
            SCLog.d("cugblog", "load data2=" + element.getName() + "  " + element.getScore());
            int score = (int)element.getScore();
            SCLog.d("cugblog", "position=" + position + " score=" + score + " real=" + element.getScore() + " name="+ element.getName());
            if(score == 0){
                holder.radioButtonZero.setChecked(true);
            }
            else if(score == 1){
                holder.radioButtonOne.setChecked(true);
            }
            else if(score == 5){
                holder.radioButtonFive.setChecked(true);
            }
            else if(score == 7){
                holder.radioButtonSeven.setChecked(true);
            }
            holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {//单选按钮监听器
                
                @Override
                public void onCheckedChanged(RadioGroup radiogroup, int i) {
                    holder.indexView.setBackgroundResource(R.drawable.btn_radio_off_focused_holo_dark);
                    isNeedSaveToDatabase = true;
                    double fraction = 0;
                    switch (i) {
                        case R.id.rb_viewpager_content_score_zero:
                            fraction = 0;
                            break;
                            
                        case R.id.rb_viewpager_content_score_one:
                            fraction = 1;
                            break;

                        case R.id.rb_viewpager_content_score_five:
                            fraction = 5;
                            break;
                            
                        case R.id.rb_viewpager_content_score_seven:
                            fraction = 7;
                            break;
                    }
                    RegularElement element = mContentsList.get(position).getElement();
                    if(!element.isEvaluation())
                        mToBeAssessedCount--;
                    element.setEvaluation(true);
                    element.setScore(fraction);
//                    mRemainMap.put(element.getName(), fraction);
//                    mFractionMap.put(element.getName(), fraction);
                    processPrompt(mToBeAssessedCount);
//                    SCLog.d("cugblog", "mRemainMap size=" + mRemainMap.size());
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return mContentsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mContentsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView indexView;// 索引
            TextView contentView;// 标题
            RadioGroup radioGroup;
            RadioButton radioButtonZero;
            RadioButton radioButtonOne;
            RadioButton radioButtonFive;
            RadioButton radioButtonSeven;
        }
    }
}
