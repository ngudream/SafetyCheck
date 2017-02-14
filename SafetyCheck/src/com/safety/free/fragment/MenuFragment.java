package com.safety.free.fragment;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import com.safety.free.R;
import com.safety.free.ui.MainActivity;
import com.slidingmenu.lib.SlidingMenu;

/**
 * menu fragment ，主要是用于显示menu菜单
 */
public class MenuFragment extends PreferenceFragment implements OnPreferenceClickListener {
    private int index = -1;
    private ViewPager mViewPager = null;
    private FrameLayout mFrameLayout = null;
    private MainActivity mActivity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mActivity = (MainActivity) getActivity();
        // mViewPager = (ViewPager)mActivity.findViewById(R.id.viewpager);
        // mFrameLayout = (FrameLayout)mActivity.findViewById(R.id.content);
        // set the preference xml to the content view
        addPreferencesFromResource(R.xml.menu);

        // add listener
        findPreference("system_pref").setOnPreferenceClickListener(this);
        findPreference("house_safety_pref").setOnPreferenceClickListener(this);
        // findPreference("n").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        // if("system_pref".equals(key)) {
        // //if the content view is that we need to show . show directly
        // if(index == 0) {
        // ((MainActivity)getActivity()).getSlidingMenu().toggle();
        // return true;
        // }
        // //otherwise , replace the content view via a new Content fragment
        // index = 0;
        // mFrameLayout.setVisibility(View.VISIBLE);
        // mViewPager.setVisibility(View.GONE);
        // getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        // FragmentManager fragmentManager = getFragmentManager();
        // VPContentFragment contentFragment =
        // (VPContentFragment)fragmentManager.findFragmentByTag("system_pref");
        // fragmentManager.beginTransaction()
        // .replace(R.id.content, contentFragment == null ? new
        // VPContentFragment("Menu:Fragment#First"):contentFragment
        // ,"system_pref")
        // .commit();
        // }else if("house_safety_pref".equals(key)) {
        // mActivity.setTitle(getResources().getString(R.string.house_safety_check_title));
        // if(index == 1) {
        // ((MainActivity)getActivity()).getSlidingMenu().toggle();
        // return true;
        // }
        // index = 1;
        // mFrameLayout.setVisibility(View.GONE);
        // mViewPager.setVisibility(View.VISIBLE);
        //
        // VPFragmentAdapter demoFragmentAdapter = new
        // VPFragmentAdapter(mActivity.getFragmentManager());
        // mViewPager.setOffscreenPageLimit(5);
        // mViewPager.setAdapter(demoFragmentAdapter);
        // mViewPager.setOnPageChangeListener(onPageChangeListener);
        //
        // ActionBar actionBar = mActivity.getActionBar();
        // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // actionBar.removeAllTabs();
        // actionBar.addTab(actionBar.newTab()
        // .setText("First Tab")
        // .setTabListener(tabListener));
        //
        // actionBar.addTab(actionBar.newTab()
        // .setText("Second Tab")
        // .setTabListener(tabListener));
        // actionBar.addTab(actionBar.newTab()
        // .setText("Third Tab")
        // .setTabListener(tabListener));
        // }
        // else if("n".equals(key)) {
        //
        // if(index == 2) {
        // ((MainActivity)getActivity()).getSlidingMenu().toggle();
        // return true;
        // }
        // index = 2;
        // mFrameLayout.setVisibility(View.VISIBLE);
        // mViewPager.setVisibility(View.GONE);
        // getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        // FragmentManager fragmentManager =
        // ((MainActivity)getActivity()).getFragmentManager();
        // ContentFragment contentFragment =
        // (ContentFragment)fragmentManager.findFragmentByTag("N");
        // fragmentManager.beginTransaction()
        // .replace(R.id.content, contentFragment == null ? new
        // ContentFragment("This is N Menu"):contentFragment,"C")
        // .commit();
        // }
        // anyway , show the sliding menu
        ((MainActivity) getActivity()).getSlidingMenu().toggle();
        return false;
    }

    private SlidingMenu getSlidingMenu() {
        return mActivity.getSlidingMenu();
    }

    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            getActivity().getActionBar().setSelectedNavigationItem(position);
            switch (position) {
                case 0:
                    getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    break;
                default:
                    getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    break;
            }
        }

    };

    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mViewPager.getCurrentItem() != tab.getPosition())
                mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    };
}
