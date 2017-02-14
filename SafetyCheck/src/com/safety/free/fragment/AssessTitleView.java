package com.safety.free.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.safety.free.R;
import com.safety.free.data.AssessElement;
import com.safety.free.data.AssessTable;
import com.safety.free.ui.ManageCategoryActivity;
import com.safety.free.view.DragSortController;
import com.safety.free.view.DragSortListView;

public class AssessTitleView extends RelativeLayout{

    private ListTitleAdapter mlistTitleAdapter;
    
    /**
     * 第三级菜单标题
     */
    private ArrayList<AssessElement> mAssessList;
    
    private int mCurrentListItemPosition = -1;
    
    private ContentFragment mContentFragment;
    
    private int DELETE_CATEGORY_REQUEST_CODE = 1;
    
    private DragSortListView listview_assess_title;
    private DragSortController mController;
    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = false;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                AssessElement item = (AssessElement) mlistTitleAdapter.getItem(from);
                mlistTitleAdapter.notifyDataSetChanged();
                mlistTitleAdapter.remove(item);
                mlistTitleAdapter.insert(item, to);
            }
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
//            mlistTitleAdapter.remove(mlistTitleAdapter.getItem(which));
        }
    };

    public AssessTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.assess_title_view, this);
        findViews();
        setListeners();
    }
    
    public void initData(ContentFragment fragment, final ArrayList<AssessElement> assessList){
        this.mContentFragment = fragment;
        this.mAssessList = assessList;
        mlistTitleAdapter = new ListTitleAdapter(getContext(), R.layout.safety_third_title_item, mAssessList);
        listview_assess_title.setAdapter(mlistTitleAdapter);
    }
    
    private void findViews() {
       listview_assess_title = (DragSortListView) findViewById(R.id.listview_assess_title);
       mController = new AssessController(listview_assess_title);
    }


    public DragSortController getController() {
        return mController;
    }

    public void setListeners() {
        listview_assess_title.setDropListener(onDrop);
        listview_assess_title.setRemoveListener(onRemove);
        listview_assess_title.setFloatViewManager(mController);
        listview_assess_title.setOnTouchListener(mController);
        listview_assess_title.setDragEnabled(dragEnabled);
        
        listview_assess_title.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != mCurrentListItemPosition){
                    mCurrentListItemPosition = position;
                    mContentFragment.setTabsTitle(position);
                }
            }
        });
    }
    
    public void setCurrentListItemPosition(int position){
        this.mCurrentListItemPosition = position;
    }
    
    public int getCurrentListItemPosition(){
        return mCurrentListItemPosition;
    }
    
    public AssessElement getCurrentAssessElement(){
        return mAssessList.get(mCurrentListItemPosition);
    }
    
    private class AssessController extends DragSortController {

        DragSortListView mDslv;

        public AssessController(DragSortListView dslv) {
            super(dslv);
            setDragHandleId(R.id.layout_assess_title);
            mDslv = dslv;
        }

        @Override
        public View onCreateFloatView(int position) {
            View v = mlistTitleAdapter.getView(position, null, mDslv);
            v.getBackground().setLevel(10000);
            return v;
        }

        @Override
        public void onDestroyFloatView(View floatView) {
            //do nothing; block super from crashing
        }

        @Override
        public int startDragPosition(MotionEvent ev) {
            int res = super.dragHandleHitPosition(ev);
            int width = mDslv.getWidth();

            if ((int) ev.getX() < width / 3) {
                return res;
            } else {
                return DragSortController.MISS;
            }
        }
    }
    
    private class ListTitleAdapter extends ArrayAdapter<AssessElement>{

        public ListTitleAdapter(Context context, int textViewResourceId, List<AssessElement> objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder = convertView == null ? new ViewHolder() : (ViewHolder) convertView.getTag();

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.safety_third_title_item, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_third_title_icon);
                holder.titleView = (TextView) convertView.findViewById(R.id.tv_third_title);
                holder.settingView = (TextView) convertView.findViewById(R.id.tv_third_title_setting);
                holder.layout_third_title_setting = (RelativeLayout) convertView.findViewById(R.id.layout_third_title_setting);
                convertView.setTag(holder);
            }
//            mViewHolderMap.put(position, holder);
            if(mAssessList.get(position).isEvaluation())
                holder.imageView.setImageResource(R.drawable.safety_third_title_item_selected);
            holder.titleView.setText(mAssessList.get(position).getName());
            holder.layout_third_title_setting.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.layout_third_title_setting.setEnabled(false);//防止重复点击
                    Intent intent = new Intent(mContentFragment.getActivity(), ManageCategoryActivity.class);
                    intent.putExtra("assessId", mAssessList.get(position).getId());
                    intent.putExtra("assessName", mAssessList.get(position).getName());
                    intent.putExtra("position", position);
                    mContentFragment.startActivityForResult(intent, DELETE_CATEGORY_REQUEST_CODE);
                    holder.layout_third_title_setting.setEnabled(true);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return mAssessList.size();
        }

        @Override
        public AssessElement getItem(int position) {
            return mAssessList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }
        
        private class ViewHolder {
            ImageView imageView;
            TextView titleView;
            TextView settingView;
            RelativeLayout layout_third_title_setting;
        }
    }
}
