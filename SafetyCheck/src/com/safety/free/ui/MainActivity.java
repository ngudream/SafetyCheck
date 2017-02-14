package com.safety.free.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.safety.free.R;
import com.safety.free.fragment.ContentFragment;
import com.safety.free.fragment.LeftMenuFragment.OnMenuSelectedListener;
import com.safety.free.fragment.OutlineFragment;
import com.safety.free.model.Global;
import com.safety.free.utils.CommonDialog;
import com.safety.free.utils.SCLog;

/**
 * 主界面。记住在androidmanifest.xml里设置相应的activity硬件加速，不然会有卡顿
 */
public class MainActivity extends BaseActivity implements OnMenuSelectedListener {

    /**
     * 前一个fragment的id
     */
    private int mPreviousFragmentId = 0;
    /**
     * 当前fragment的id
     */
    private int mCurrentFragmentId = 0;

    private ArrayList<Fragment> mTotalFragmentsArray = new ArrayList<Fragment>();

    public MainActivity() {
        super(R.string.fire_safety_check_system);
    }

    public MainActivity(int titleRes) {
        super(R.string.fire_safety_check_system);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content, new OutlineFragment(getString(R.string.outline)), "outline");
        fragmentTransaction.commit();

        mPreviousFragmentId = R.id.main_outline_fragment;
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        SCLog.d("cugblog", "main activity resume");
    }

    @Override
    public void onMenuSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (position == 0) {//书名栏
            ActionBar actionBar = this.getActionBar();
            actionBar.setIcon(R.drawable.icon);
            actionBar.setTitle(R.string.fire_safety_check_system);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            OutlineFragment outlineFragment = (OutlineFragment) fragmentManager.findFragmentByTag("outline");
            fragmentTransaction.replace(R.id.main_content, outlineFragment == null ? new OutlineFragment(getString(R.string.outline)) : outlineFragment, "outline");
        } else if (position == 1) {
            final ActionBar actionBar = this.getActionBar();
            actionBar.setIcon(R.drawable.ic_launcher);
            actionBar.setTitle(R.string.house_safety_check_title);
            ContentFragment contentFragment = (ContentFragment) fragmentManager.findFragmentByTag("content");
            if (contentFragment == null) {
                SCLog.d("cugblog", "create new contentfragment");
                contentFragment = new ContentFragment();
                contentFragment.initData(getSlidingMenu());
            }
            fragmentTransaction.replace(R.id.main_content, contentFragment, "content");
        }
        // fragmentTransaction.addToBackStack(null);//按返回键可以回到上一个fragment
        fragmentTransaction.commit();
//        toggle();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Global.recycle();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(!getSlidingMenu().isMenuShowing()){
                CommonDialog.confirm(MainActivity.this, R.string.confirm_exit_title, R.string.confirm_exit_text, R.string.cancel, R.string.ok, null, new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.finish();
                    }
                });
            }
        }
        return true;
    }
}
