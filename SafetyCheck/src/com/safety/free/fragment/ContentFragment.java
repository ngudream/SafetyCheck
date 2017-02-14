package com.safety.free.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.data.AssessElement;
import com.safety.free.data.AssessTable;
import com.safety.free.data.CategoryElement;
import com.safety.free.data.CategoryTable;
import com.safety.free.model.Global;
import com.safety.free.ui.ManageCategoryActivity;
import com.safety.free.utils.SCLog;
import com.slidingmenu.lib.SlidingMenu;

/**
 * viewpager的内容页
 * @param <ViewHolder>
 * @param <ViewHolder>
 */
@SuppressLint("ValidFragment")
public class ContentFragment extends Fragment{

    private ViewPager mViewPager;
    /**
     * viewpager的适配器
     */
    private ViewPagerAdapter mViewPagerAdapter;
    private TabHost mTabHost;

    private AssessTitleView mAssessTitleView;

    private ProgressBar content_load_progressbar;
    
    private HorizontalScrollView content_tabs_hori_scroll_view;

    private SlidingMenu mSlidingMenu;

    private ArrayList<CategoryElement> mCategoryList = new ArrayList<CategoryElement>();

    /**
     * viewpager中的内容页
     */
    private HashMap<Integer, Object> mContentViewItemMap = new HashMap<Integer, Object>();
    /**
     * 存放tab标签
     */
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    
    /**
     * 标记viewpager是否在滑动
     */
    private boolean mIsScrolling = false;
    /**
     * 标记上一个tab的位置，以便计算是否需要滑动
     */
    private int mOldTabPosition = 0;
    /**
     * viewpager的当前页的index
     */
    private int mCurrentViewPagerPosition = 0;
    /**
     * 第三级菜单标题
     */
    private ArrayList<AssessElement> mAssessList = new ArrayList<AssessElement>();
    
    private int DELETE_CATEGORY_REQUEST_CODE = 1;
    /**
     * 标记viewpager是否初始化完成
     */
    private boolean isViewPagerInitFinished = false;
    
    private GestureDetector mGestureDetector;

    public ContentFragment() {
    }

    public ContentFragment(String text) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mViewPagerAdapter = new ViewPagerAdapter(getActivity());
        initListViewData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflater the layout
        SCLog.d("cugblog", "onCreateView accured");
        View view = inflater.inflate(R.layout.content_fragment, null);
        content_load_progressbar = (ProgressBar) view.findViewById(R.id.content_load_progressbar);
        content_tabs_hori_scroll_view = (HorizontalScrollView) view.findViewById(R.id.content_tabs_hori_scroll_view);
        
        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_check_content);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        
        mAssessTitleView = (AssessTitleView) view.findViewById(R.id.layout_assess_title_view);
        mAssessTitleView.initData(this, mAssessList);
        setListeners();
        
        return view;
    }
    
    /**
     * 设置view的监听器
     */
    private void setListeners() {
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {//viewpager监听器
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 1) {
                    mIsScrolling = true;
                } else {
                    mIsScrolling = false;
                }
                SCLog.d("cugblog", "mIsScrolling1=" + mIsScrolling);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                SCLog.d("cugblog", "mIsScrolling2=" + mIsScrolling);
                if (mIsScrolling) {
                    mIsScrolling = false;
                    mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                }
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentViewPagerPosition = position;
                if(mContentViewItemMap.get(position - 1) instanceof RegularContentViewItem){
                    RegularContentViewItem viewItem = (RegularContentViewItem) mContentViewItemMap.get(position - 1);
                    viewItem.saveRegularToDatabase();//保存前一页的数据到数据库
                }
                processTabHost(position);
                if(position == mCategoryList.size()){//最后一页
                    AssessElement assessElement = mAssessTitleView.getCurrentAssessElement();
                    if(mContentViewItemMap.get(position) instanceof RegularContentViewLast){
                        RegularContentViewLast viewLast = (RegularContentViewLast) mContentViewItemMap.get(position);
                        viewLast.showViews(assessElement);
                    }
                }
                if (position == 0 || position == mCategoryList.size()) {
                    mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                } else {
                    mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                }
            }
        });
        
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String s) {
                if(isViewPagerInitFinished){
                    SCLog.d("cugblog", "setOnTabChangedListener");
                    int position = mTabHost.getCurrentTab();
                    mViewPager.setCurrentItem(position); 
                }
            }
        });
    }

    /**
     * 处理viewpager顶部的tabhost
     * 
     * @param position
     */
    private void processTabHost(int position) {
        int totalWidth = Global.getDisplayMetrics().widthPixels - mAssessTitleView.getRight();
        TabWidget widget = mTabHost.getTabWidget();
        int count = widget.getChildCount();
        int width = widget.getChildTabViewAt(position).getWidth();
        int childLeft = widget.getChildTabViewAt(position).getLeft();
        int childRight = widget.getChildTabViewAt(position).getRight();
        
        SCLog.d("cugblog", "left=" + childLeft + "  " +  totalWidth);
        if(position > mOldTabPosition){//向右滑
            if(childLeft > totalWidth || childRight > totalWidth){
                int scroll = childRight - totalWidth;
                content_tabs_hori_scroll_view.smoothScrollTo(scroll, 0);
            }
        }
        else if(position < mOldTabPosition){//向左滑
            if((count - position) * width > totalWidth){
                int scroll = position * width;
                if(scroll < 0)
                    scroll = 0;
                content_tabs_hori_scroll_view.smoothScrollTo(scroll, 0);
            }
        }
        mOldTabPosition = position;
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mTabHost.setCurrentTab(position);
        widget.setDescendantFocusability(oldFocusability);
        
        for(int i = 0;i < count; i++) 
        { 
//            ImageView iv=(ImageView)tabWidget.getChildAt(i).findViewById(android.R.id.icon);
            TextView tv = (TextView) widget.getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.BLACK);
        } 
        TextView tv = (TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.RED);
    }

    private void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(getActivity()));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);
        mTabs.add(info);
        mTabHost.addTab(tabSpec);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
    
    /**
     * 从数据库中查找出评估表
     */
    private void initListViewData(){
        ArrayList<AssessElement> elements = AssessTable.getAllAssessElements("0000");
        if(elements != null && elements.size() > 0){
            for(AssessElement element : elements){
                mAssessList.add(element);
            }
//            for(int i = 0; i < 10; i++){
//                mAssessList.add(elements.get(0));
//            }
        }
    }

    /**
     * 以后根据数据动态读
     */
    public void setTabsTitle(int position) {
        SCLog.d("cugblog", "setTabsTitle begin");
        isViewPagerInitFinished = false;
        mCategoryList.clear();
//        mTabHost.setCurrentTab(0);
        mTabHost.clearAllTabs();
        mContentViewItemMap.clear();
        ArrayList<CategoryElement> elements = CategoryTable.getVisiableCategoryElements(mAssessList.get(position).getId());
        if(elements != null && elements.size() > 0){
            for(CategoryElement element : elements){
                //只显示用户没有删除的评估内容
                String title = element.getName();
                mCategoryList.add(element);
                addTab(mTabHost.newTabSpec(title).setIndicator(title), null, null);
            }
//            for(CategoryElement element : elements){//用一个循环的话会造成没加载完数据就notify，这得找原因。不应该用循环两次
//                String title = element.getName();
//                addTab(mTabHost.newTabSpec(title).setIndicator(title), null, null);
//            }
            addTab(mTabHost.newTabSpec("统计").setIndicator("统计"), null, null);//添加统计栏
            if(mTabHost.getChildCount() > 0){
                TextView tv = (TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
                tv.setTextColor(Color.RED);
            }
        }
//        TabWidget tabWidget = mTabHost.getTabWidget();
//        int count = tabWidget.getChildCount();
//        for(int i = 0; i < count; i++){//设置每个tab的最小宽度
//            tabWidget.getChildAt(i).setBackgroundResource(
//                    R.drawable.wl_taskview_task_pressed_background_middle);//设置背景
//        }

        mViewPagerAdapter.notifyDataSetChanged();
        isViewPagerInitFinished = true;
        SCLog.d("cugblog", "setTabsTitle end");
    }
    
    private View createIndicatorView() {  
        View tabIndicator = (RelativeLayout)LayoutInflater.from(getActivity()).inflate(  
                R.layout.tab_custom_indicator, mTabHost.getTabWidget(), false);  
        return tabIndicator;  
    }  

    @Override
    public void onStart() {
        super.onStart();
        SCLog.d("cugblog", "on start");
    }

    @Override
    public void onResume() {
        super.onResume();
        content_load_progressbar.setVisibility(View.GONE);
        SCLog.d("cugblog", "on resume");
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == DELETE_CATEGORY_REQUEST_CODE){
                Bundle bundle = data.getExtras();
                if(bundle != null){
                    boolean isDeleteAll = bundle.getBoolean("deleteAll");
                    if(isDeleteAll){
                        if(mCategoryList != null)
                            mCategoryList.clear();
                    }
                    else{
                        int position = bundle.getInt("position", -1);
                        if(position >= 0 && position < mAssessList.size()){
                            ArrayList<String> deletes = bundle.getStringArrayList("deletes");
                            ArrayList<String> adds = bundle.getStringArrayList("adds");
                            if((deletes != null && deletes.size() > 0) || (adds != null && adds.size() > 0)){
                                mAssessTitleView.setCurrentListItemPosition(position);
                                setTabsTitle(position);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        SCLog.d("cugblog", "on detach");
    }

    @Override
    public void onPause() {
        super.onPause();
        SCLog.d("cugblog", "on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        SCLog.d("cugblog", "on stop");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        SCLog.d("cugblog", "on destroy");
    }

    private class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(30);
            v.setMinimumHeight(30);
            return v;
        }
    }

    private final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {
        /*
         * 必须重载 instantiateItem(ViewGroup, int) destroyItem(ViewGroup, int,
         * Object) getCount() isViewFromObject(View, Object)
         */

        private Context mContext;

        public ViewPagerAdapter(Context context) {
            this.mContext = context;
        }
        
        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return POSITION_NONE;
        }
        
        // 这里进行回收，当我们左右滑动的时候，会把早期的图片回收掉.
        // 将距离该页2个步幅以上的那个View销毁，以此保证PagerAdapter最多只管辖3个View，且当前View是3个中的中间一个
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            SCLog.i("cugblog", "recycle accured " + position);
            if(object instanceof RegularContentViewItem){
                RegularContentViewItem viewItem = (RegularContentViewItem) object;
                viewItem.onDestroy();
            }
            ((ViewPager) container).removeView((View)object);
            mContentViewItemMap.remove(position);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
        }

        // 这里返回相册有多少条,和BaseAdapter一样.
        @Override
        public int getCount() {
            if (mCategoryList != null && mCategoryList.size() > 0)
                return mCategoryList.size() + 1;//+1是为了显示统计页
            return 0;
        }

        /**
         * Called to inform the adapter of which item is currently considered to
         * be the "primary", that is the one show to the user as the current
         * page.
         */
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            // HSLog.i("ihsshow", "primary item " + position);
        }

        // 重新reload，不存在则new一个并且填充数据.
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SCLog.i("cugblog", "instantiateItem");
            if(position < mCategoryList.size()){
                RegularContentViewItem showViewItem = null;
                if (mContentViewItemMap.containsKey(position)) {
                    showViewItem = (RegularContentViewItem) mContentViewItemMap.get(position);
                    showViewItem.loadContent(mAssessTitleView.getCurrentAssessElement(), mCategoryList.get(position));
                } else {
                    showViewItem = new RegularContentViewItem(mContext);
                    showViewItem.loadContent(mAssessTitleView.getCurrentAssessElement(), mCategoryList.get(position));
                    mContentViewItemMap.put(position, showViewItem);
                    ((ViewPager) container).addView(showViewItem);
                }
                return showViewItem;
            }
            else{
                SCLog.i("cugblog", "instantiate item " + position + " size=" + mCategoryList.size());
                RegularContentViewLast viewLast = new RegularContentViewLast(mContext);
                viewLast.loadContent(mCategoryList.get(position - 1).getAssess());
                mContentViewItemMap.put(position, viewLast);
                ((ViewPager) container).addView(viewLast);
                return viewLast;
            }
        }

        /**
         * Determines whether a page View is associated with a specific key
         * object as returned by instantiateItem(ViewGroup, int).
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    public void initData(SlidingMenu slidingMenu) {
        this.mSlidingMenu = slidingMenu;
    }
    
    private class ContentGestureListener implements OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            SCLog.d("cugblog", "onfling");
            if(x>0){
                //right
                SCLog.d("cugblog", "onfling right");
            }else if(x<=0){
                //left
                SCLog.d("cugblog", "onfling left " + mCurrentViewPagerPosition);
                if(mCurrentViewPagerPosition == mCategoryList.size() - 1){
                    mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
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
}
