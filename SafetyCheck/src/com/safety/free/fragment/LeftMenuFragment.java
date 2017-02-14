package com.safety.free.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.utils.SCLog;
import com.slidingmenu.lib.SlidingMenu;

public class LeftMenuFragment extends Fragment {

    private ExpandableListView ep_listview_left_menu;
    /**
     * 保存菜单项
     */
//    private ArrayList<MenuItem> mMenuItems = new ArrayList<LeftMenuFragment.MenuItem>();
    /**
     * 左侧菜单
     */
    private SlidingMenu mSlidingMenu;

    private OnMenuSelectedListener mCallback;
    
    private int mCurrentListItem = 0;
    
    private ExpandableAdapter mExpandableAdapter;
    
    private HashMap<Integer, ExpandableAdapter.GroupViewHolder>  mGroupViewHolderMap = new HashMap<Integer, LeftMenuFragment.ExpandableAdapter.GroupViewHolder>();
    
    private List<MenuItem> mGroupData;//定义组数据    
    private List<List<MenuItem>> mChildrenData;//定义组中的子数据  
    
    public void initData(SlidingMenu slidingMenu) {
        this.mSlidingMenu = slidingMenu;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadListData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.left_menu_list, null);
        ep_listview_left_menu = (ExpandableListView) view.findViewById(R.id.ep_listview_left_menu);
        ep_listview_left_menu.setGroupIndicator(null);//去除箭头
        mExpandableAdapter = new ExpandableAdapter();
        ep_listview_left_menu.setAdapter(mExpandableAdapter);
        setListeners();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnMenuSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    private void setListeners(){
        ep_listview_left_menu.setOnChildClickListener(new OnChildClickListener() {//子菜单
            
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                SCLog.d("cugblog", "setOnChildClickListener");
                return true;
            }
        });
        
        ep_listview_left_menu.setOnGroupExpandListener(new OnGroupExpandListener() {//打开
            
            @Override
            public void onGroupExpand(int groupPosition) {
                SCLog.d("cugblog", "onGroupExpand");
                ExpandableAdapter.GroupViewHolder holder = mGroupViewHolderMap.get(groupPosition);
                if (holder.imageGroup != null && holder.imageGroup.getVisibility() == View.VISIBLE) {
                    holder.imageGroup.setImageResource(R.drawable.triangle_unfold);
                }
            }
        });
        
        ep_listview_left_menu.setOnGroupCollapseListener(new OnGroupCollapseListener() {//合上
            
            @Override
            public void onGroupCollapse(int groupPosition) {
                SCLog.d("cugblog", "onGroupCollapse");
                ExpandableAdapter.GroupViewHolder holder = mGroupViewHolderMap.get(groupPosition);
                if(holder.imageGroup != null){
                    holder.imageGroup.setImageResource(R.drawable.triangle_contract);
                }
            }
        });
        
        ep_listview_left_menu.setOnGroupClickListener(new OnGroupClickListener() {
            
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                SCLog.d("cugblog", "onGroupClick");
                if(groupPosition != mCurrentListItem){
                    mCurrentListItem = groupPosition;
                    if(mChildrenData.get(groupPosition).size() <= 0){
                        showCenterContent(groupPosition);
                    }
                }
                return false;
            }
        });
        
    }
    
    /**
     * 选择中某个项后在中间显示其内容
     */
    private void showCenterContent(int position){
      if(mSlidingMenu != null){
          if (mCallback != null) {
              mCallback.onMenuSelected(position);
          }
          mSlidingMenu.toggle();
      }
    }

    public interface OnMenuSelectedListener {
        public void onMenuSelected(int position);
    }
    
    /**
     * 菜单内容的内部类
     */
    private class MenuItem {
        private String id;
        /**
         * 菜单的名字
         */
        private String name;
        /**
         * 菜单的图片
         */
        public int iconRes;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public MenuItem(String name) {
            this.setName(name);
        }

        public void setMenuIcon(int res) {
            this.iconRes = res;
        }

        public int getMenuIcon() {
            return iconRes;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    
    private void LoadListData() {    
        
        MenuItem sysItem = new MenuItem("消防安全系统检查评估管理");
        sysItem.setMenuIcon(R.drawable.icon);
        sysItem.setId("00");

        // 菜单项
        MenuItem houseItem = new MenuItem("家庭住宅防火检查");
        houseItem.setId("01");
        houseItem.setMenuIcon(R.drawable.icon_list2);
        
        MenuItem houseItem1 = new MenuItem("火灾风险分析与安全评估");
        houseItem1.setId("02");
        houseItem1.setMenuIcon(R.drawable.icon_list2);
        
        MenuItem houseItem2 = new MenuItem("火灾危险源辨识与控制");
        houseItem2.setId("03");
        houseItem2.setMenuIcon(R.drawable.icon_list2);
        
        mGroupData = new ArrayList<MenuItem>();    
        mGroupData.add(sysItem);    
        mGroupData.add(houseItem);    
        mGroupData.add(houseItem1);    
        mGroupData.add(houseItem2);    
    
        mChildrenData = new ArrayList<List<MenuItem>>(); 
        
        List<MenuItem> child1 = new ArrayList<MenuItem>();
        List<MenuItem> child2 = new ArrayList<MenuItem>();
        mChildrenData.add(child1);
        mChildrenData.add(child2);
        
        List<MenuItem> child3 = new ArrayList<MenuItem>();
        MenuItem house1 = new MenuItem("火灾风险");
        house1.setId("020");
        MenuItem house2 = new MenuItem("火灾风险分析方法");
        houseItem.setId("021");
        child3.add(house1);    
        child3.add(house2);  
        mChildrenData.add(child3);   
        
        List<MenuItem> child4 = new ArrayList<MenuItem>();    
        MenuItem house3 = new MenuItem("引火源的辨识与控制");
        houseItem.setId("030");
        MenuItem house4 = new MenuItem("火灾危险物的辨识与控制");
        houseItem.setId("031");
        child4.add(house3);
        child4.add(house4);
        mChildrenData.add(child4);    
    }    
    
    private class ExpandableAdapter extends BaseExpandableListAdapter{

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mChildrenData.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder = convertView == null ? new ChildViewHolder() : (ChildViewHolder) convertView.getTag();

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_menu_second_title, null);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_left_menu_second_title);
                convertView.setTag(holder);
            }
            holder.textView.setText(mChildrenData.get(groupPosition).get(childPosition).getName());
//            convertView.setBackgroundResource(R.drawable.biz_navigation_tab_bg_pressed);
            return convertView;    
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mChildrenData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroupData.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return mGroupData.size();  
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = convertView == null ? new GroupViewHolder() : (GroupViewHolder) convertView.getTag();

            if (convertView == null) {
                if (groupPosition == 0) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.system_preference, null);
                    holder.imageIcon = (ImageView) convertView.findViewById(R.id.iv_system_menu_icon);
                    holder.textView = (TextView) convertView.findViewById(R.id.tv_system_menu_title);
                    convertView.setTag(holder);
                } else {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_menu_list_item, null);
                    holder.imageIcon = (ImageView) convertView.findViewById(R.id.menu_icon);
                    holder.imageGroup = (ImageView) convertView.findViewById(R.id.iv_left_menu_group);
                    holder.textView = (TextView) convertView.findViewById(R.id.menu_title);
                    convertView.setTag(holder);
                }
            }
            MenuItem menuItem = mGroupData.get(groupPosition);
            holder.imageIcon.setImageResource(menuItem.getMenuIcon());
            holder.textView.setText(menuItem.getName());
            if(holder.imageGroup != null){
                if(mChildrenData.get(groupPosition).size() <= 0)
                    holder.imageGroup.setVisibility(View.GONE);
                else 
                    holder.imageGroup.setVisibility(View.VISIBLE);
            }
            mGroupViewHolderMap.put(groupPosition, holder);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
        
        private TextView createView(String content) {    
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(      
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);      
            TextView childView = new TextView(getActivity());      
            childView.setLayoutParams(layoutParams);      
            childView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);      
            childView.setPadding(80, 0, 0, 0);      
            childView.setText(content);    
            return childView;    
        }  
        
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }
        
        private class GroupViewHolder {
            ImageView imageIcon;
            ImageView imageGroup;
            TextView textView;
        }
        
        private class ChildViewHolder{
            TextView textView;
        }
    }
}
