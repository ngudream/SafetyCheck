/*
 * Copyright (C) 2012 北京平安世纪有限公司
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.safety.free.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.ui.ClearDataActivity;
import com.safety.free.ui.SearchDataActivity;
import com.safety.free.ui.SystemSettingActivity;
import com.safety.free.utils.CommonDialog;

public class RightMenuFragment extends Fragment {
    
    private GridView gv_sliding_menu_other_content ;  
    private List<GridInfo> mGridInfosList;  
    private GridAdapter mGridAdapter;
    private LinearLayout layout_sliding_menu_tv_setting_icon;
    private LinearLayout layout_sliding_menu_tv_search_icon;
    private LinearLayout layout_sliding_menu_tv_change_bg_icon;
    private LinearLayout layout_sliding_menu_tv_night_state_icon;
    private LinearLayout layout_sliding_menu_tv_exit_icon;
    private LinearLayout layout_sliding_menu_tv_clear_data_icon;
    private RelativeLayout layout_clear_data;
    
    private HashMap<Integer, GridAdapter.GridHolder> mGridHolderMap = new HashMap<Integer, RightMenuFragment.GridAdapter.GridHolder>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadGridViewData();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sliding_menu_main, null);
        gv_sliding_menu_other_content = (GridView) view.findViewById(R.id.gv_sliding_menu_other_content);
        mGridAdapter = new GridAdapter(getActivity());
        gv_sliding_menu_other_content.setAdapter(mGridAdapter);
        layout_sliding_menu_tv_setting_icon = (LinearLayout) view.findViewById(R.id.layout_sliding_menu_tv_setting_icon);
        layout_sliding_menu_tv_search_icon = (LinearLayout) view.findViewById(R.id.layout_sliding_menu_tv_search_icon);
        layout_sliding_menu_tv_change_bg_icon = (LinearLayout) view.findViewById(R.id.layout_sliding_menu_tv_change_bg_icon);
        layout_sliding_menu_tv_night_state_icon = (LinearLayout) view.findViewById(R.id.layout_sliding_menu_tv_night_state_icon);
        layout_sliding_menu_tv_exit_icon = (LinearLayout) view.findViewById(R.id.layout_sliding_menu_tv_exit_icon);
        layout_sliding_menu_tv_clear_data_icon = (LinearLayout) view.findViewById(R.id.layout_sliding_menu_tv_clear_data_icon);
        setListeners();
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    private void loadGridViewData(){
        mGridInfosList = new ArrayList<RightMenuFragment.GridInfo>();
        mGridInfosList.add(new GridInfo(getResources().getString(R.string.company_overview)));
        mGridInfosList.add(new GridInfo(getResources().getString(R.string.item_veto)));
        mGridInfosList.add(new GridInfo(getResources().getString(R.string.range_assessment)));
        mGridInfosList.add(new GridInfo(getResources().getString(R.string.check_assessment)));
        mGridInfosList.add(new GridInfo(getResources().getString(R.string.hazard_identification)));
        mGridInfosList.add(new GridInfo(getResources().getString(R.string.level_risk)));
        mGridInfosList.add(new GridInfo(getResources().getString(R.string.hidden_report)));
        mGridInfosList.add(new GridInfo(getResources().getString(R.string.use_instructions)));
    }
    
    private void setListeners(){
        layout_sliding_menu_tv_setting_icon.setOnClickListener(new View.OnClickListener() {//设置
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SystemSettingActivity.class);
                startActivity(intent);
            }
        });
        
        layout_sliding_menu_tv_search_icon.setOnClickListener(new View.OnClickListener() {//搜索
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchDataActivity.class);
                startActivity(intent);
            }
        });
        
        layout_sliding_menu_tv_change_bg_icon.setOnClickListener(new View.OnClickListener() {//换背景
            
            @Override
            public void onClick(View view) {
            }
        });
        
        layout_sliding_menu_tv_night_state_icon.setOnClickListener(new View.OnClickListener() {//日夜间状态
            
            @Override
            public void onClick(View view) {
            }
        });
        
        layout_sliding_menu_tv_exit_icon.setOnClickListener(new View.OnClickListener() {//退出程序
            
            @Override
            public void onClick(View view) {
                CommonDialog.confirm( getActivity(), R.string.confirm_exit_title, R.string.confirm_exit_text, R.string.cancel, R.string.ok, null, new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity() != null)
                            getActivity().finish();
                    }
                });
            }
        });
        
        layout_sliding_menu_tv_clear_data_icon.setOnClickListener(new View.OnClickListener() {//清空数据
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ClearDataActivity.class);
                startActivity(intent);
            }
        });
        
        gv_sliding_menu_other_content.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long id) {
                Object object = view.getTag();
                if(object != null && object instanceof GridAdapter.GridHolder){
                    GridAdapter.GridHolder holder = (GridAdapter.GridHolder) object;
                    if(holder.titleName != null){
                        holder.titleName.setTextColor(Color.WHITE);
                    }
                }
                if(mGridHolderMap != null && mGridHolderMap.size() > 0){
                    Iterator it = mGridHolderMap.entrySet().iterator();
                    while(it.hasNext()){
                        Entry entry = (Entry) it.next();
                        int key = (Integer) entry.getKey();
                        GridAdapter.GridHolder holder = (GridAdapter.GridHolder) entry.getValue();
                        if(key == index){
                            holder.titleName.setTextColor(Color.WHITE);
                        }
                        else{
                            holder.titleName.setTextColor(Color.parseColor("#003366"));
                        }
                    }
                }
            }
        });
        
    }
    
    private class GridInfo {  
        
        private String name;  
      
          
        public GridInfo(String name) {  
            this.name = name;  
        }  
      
        public String getName() {  
            return name;  
        }  
      
        public void setName(String name) {  
            this.name = name;  
        }  
          
    }  
    
    public class GridAdapter extends BaseAdapter {  
        
        private class GridHolder {  
//            ImageView appImage;  
            TextView titleName;  
        }  
      
        private Context context;  
        private LayoutInflater mInflater;  
      
        public GridAdapter(Context c) {  
            super();  
            this.context = c;  
            mInflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        }  
      
        public int getCount() {  
            return mGridInfosList.size();  
        }  
      
        @Override  
        public Object getItem(int index) {  
      
            return mGridInfosList.get(index);  
        }  
      
        @Override  
        public long getItemId(int index) {  
            return index;  
        }  
      
        @Override  
        public View getView(int index, View convertView, ViewGroup parent) {  
            GridHolder holder;  
            if (convertView == null) {     
                convertView = mInflater.inflate(R.layout.sliding_menu_gridview_item, null);     
                holder = new GridHolder();  
                holder.titleName = (TextView)convertView.findViewById(R.id.tv_gv_item_title);  
                convertView.setTag(holder);     
      
            }else{  
                 holder = (GridHolder) convertView.getTag();     
            }
            GridInfo info = mGridInfosList.get(index);  
            if (info != null) {     
                holder.titleName.setText(info.getName());  
            }
            mGridHolderMap.put(index, holder);
            return convertView;  
        }  
    }
}
