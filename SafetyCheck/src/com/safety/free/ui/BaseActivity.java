package com.safety.free.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.safety.free.R;
import com.safety.free.data.AssessTable;
import com.safety.free.data.CategoryTable;
import com.safety.free.data.RegularTable;
import com.safety.free.fragment.LeftMenuFragment;
import com.safety.free.fragment.RightMenuFragment;
import com.safety.free.model.Global;
import com.safety.free.utils.SCLog;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

    public static Activity instance;
    private int mTitleRes;
    protected LeftMenuFragment mFragmentLeft;
    protected Fragment mFragmentRight;

    public BaseActivity() {

    }

    public BaseActivity(int titleRes) {
        mTitleRes = titleRes;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        Global.initApplication(this);
        setTitle(mTitleRes);
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        // set the Behind View
        setBehindContentView(R.layout.left_menu_frame);
        slidingMenu.setSecondaryMenu(R.layout.right_menu_frame);// 设置右侧菜单
        SCLog.d("cugbtest", "on create");
        if (savedInstanceState == null) {
            FragmentTransaction left = this.getSupportFragmentManager().beginTransaction();// 设置左侧菜单
            mFragmentLeft = new LeftMenuFragment();
            mFragmentLeft.initData(getSlidingMenu());
            left.replace(R.id.menu_left, mFragmentLeft);
            left.commit();

            FragmentTransaction right = this.getSupportFragmentManager().beginTransaction();
            mFragmentRight = new RightMenuFragment();
            right.replace(R.id.menu_right, mFragmentRight);
            right.commit();
        } else {
            mFragmentLeft = (LeftMenuFragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_left);
            mFragmentRight = (Fragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_right);
        }
        initSlidingMenu();
    }
    
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        SCLog.d("cugbtest", "on start");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    /**
     * 设置slidingmenu样式
     */
    private void initSlidingMenu() {
        SCLog.d("cugblog", "base activity resume");
        final SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidth(12);
        sm.setShadowDrawable(R.drawable.biz_main_slide_left_shadow);
        sm.setSecondaryShadowDrawable(R.drawable.biz_main_slide_right_shadow);
        int screenWidth = Global.getDisplayMetrics().widthPixels;
        sm.setBehindWidth(screenWidth / 4);
//        sm.setBackgroundResource(R.drawable.bg_action_bar_flight_top);//设置左侧菜单的背景图

        // sm.setBehindOffset(screenWidth * 3 /
        // 4);//离右边的距离，实际上是设置slidingmenu的相对大小
        // sm.setFadeDegree(0.35f);
        // 设置slding menu的几种手势模式
        // TOUCHMODE_FULLSCREEN 全屏模式，在content页面中，滑动，可以打开sliding menu
        // TOUCHMODE_MARGIN 边缘模式，在content页面中，如果想打开slding ,你需要在屏幕边缘滑动才可以打开slding
        // menu
        // TOUCHMODE_NONE 自然是不能通过手势打开啦
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        
        sm.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            
            @Override
            public void onOpened() {
                sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            }
        });

        // 使用左上方icon可点，这样在onOptionsItemSelected里面才可以监听到R.id.home
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_action_bar_flight_top));//actionbar只能设置一次，不然背景会变白
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        SCLog.d("cugbtest", "on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        SCLog.d("cugbtest", "on stop");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        SCLog.d("cugbtest", "on destroy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {// 左上角
            toggle();
            return true;
        } else if (itemId == R.id.menu_personal) {// 右上角
            getSlidingMenu().showSecondaryMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.settings, menu);// 加载右上角的菜单menu
        toggle();
        return true;
    }
}
