package com.safety.free.ui;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.data.CategoryElement;
import com.safety.free.data.CategoryTable;
import com.safety.free.utils.SCLog;
import com.safety.free.view.DragSortListView;

public class SearchUnfinishedActivity extends Activity {
    
    private DragSortListView listview_search_unfinished;
    private TextView tv_search_unfinished_data;
    private TextView tv_search_unfinished_prompt;
    private RelativeLayout layout_search_unfinished_progress;
    private ListRegularAdapter mCategoryAdapter;
    
    /**
     *细则数
     */
    private LinkedList<CategoryElement> mCategoryLinkedList = new LinkedList<CategoryElement>();
    
    private GestureDetector mGestureDetector;
    private SearchUnfinishedGestureListener mGestureListener;

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    SCLog.d("cugblog", "drag listener");
                }
            };

        private DragSortListView.RemoveListener onRemove = 
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                }
            };

        private DragSortListView.DragScrollProfile ssProfile =
            new DragSortListView.DragScrollProfile() {
                @Override
                public float getSpeed(float w, long t) {
                    if (w > 0.8f) {
                        // Traverse all views in a millisecond
                        return ((float) mCategoryAdapter.getCount()) / 0.001f;
                    } else {
                        return 10.0f * w;
                    }
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_unfinished);
        
        tv_search_unfinished_data = (TextView) findViewById(R.id.tv_search_unfinished_data);
        tv_search_unfinished_prompt = (TextView) findViewById(R.id.tv_search_unfinished_prompt);
        layout_search_unfinished_progress = (RelativeLayout) findViewById(R.id.layout_search_unfinished_progress);
        listview_search_unfinished = (DragSortListView) findViewById(R.id.listview_search_unfinished);
        listview_search_unfinished.setDropListener(onDrop);
        listview_search_unfinished.setRemoveListener(onRemove);
        listview_search_unfinished.setDragScrollProfile(ssProfile);
        
        mCategoryAdapter = new ListRegularAdapter(getApplicationContext(), R.layout.search_unfinished_item, mCategoryLinkedList);
        listview_search_unfinished.setAdapter(mCategoryAdapter);
        
        setListeners();
        
        new SearchUnfinishedTask().execute();
        mGestureListener = new SearchUnfinishedGestureListener();
        mGestureDetector = new GestureDetector(getApplicationContext(), mGestureListener);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return mGestureDetector.onTouchEvent(event);
    }
    
    /**
     * 设置view的监听器
     */
    private void setListeners() {
        listview_search_unfinished.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }
    
    private class SearchUnfinishedGestureListener implements OnGestureListener{

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
                    SearchUnfinishedActivity.this.finish();
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
     *异步线程更新数据库
     */
    private class SearchUnfinishedTask extends AsyncTask<Void, Void, Integer>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layout_search_unfinished_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            ArrayList<CategoryElement> elements = CategoryTable.getUnfinishedCategoryElements("0000");
            if(elements != null){
                for(CategoryElement element : elements){
                    mCategoryLinkedList.add(element);
                }
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(mCategoryLinkedList.size() > 0){
                tv_search_unfinished_data.setVisibility(View.VISIBLE);
                mCategoryAdapter.notifyDataSetChanged();
            }
            else{
                tv_search_unfinished_data.setVisibility(View.INVISIBLE);
                tv_search_unfinished_prompt.setVisibility(View.VISIBLE);
            }
            layout_search_unfinished_progress.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    private class ListRegularAdapter extends ArrayAdapter<CategoryElement> {

        public ListRegularAdapter(Context context, int textViewResourceId, LinkedList<CategoryElement> objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder = convertView == null ? new ViewHolder() : (ViewHolder) convertView.getTag();

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_unfinished_item, null);
                holder.titleView = (TextView) convertView.findViewById(R.id.tv_search_unfinished_title);
                convertView.setTag(holder);
            }
            holder.titleView.setText(mCategoryLinkedList.get(position).getName());
            return convertView;
        }

        @Override
        public int getCount() {
            return mCategoryLinkedList.size();
        }

        @Override
        public CategoryElement getItem(int position) {
            return mCategoryLinkedList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        public class ViewHolder {
            TextView titleView;
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
