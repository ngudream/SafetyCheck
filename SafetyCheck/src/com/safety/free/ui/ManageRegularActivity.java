package com.safety.free.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.data.CategoryTable;
import com.safety.free.data.RegularElement;
import com.safety.free.data.RegularTable;
import com.safety.free.utils.SCLog;
import com.safety.free.view.DragSortListView;

public class ManageRegularActivity extends Activity {
    
    private DragSortListView listview_regular;
    private TextView tv_manage_regular_title;
    private TextView tv_manage_regular_no_data;
    private RelativeLayout layout_manage_regular_back;
    private RelativeLayout layout_manage_regular_recovery;
    private RelativeLayout layout_manage_regular_clear;
    private RelativeLayout layout_delete_all_regular_progress;
    private ImageView iv_manage_regular_process_box;
    private LinearLayout layout_manage_regular_bottom_process;
    
    private ListRegularAdapter mlistRegularAdapter;

    public HashMap<Integer, Boolean> isSelected;
    
    private String mCategoryID;

    /**
     *细则数
     */
    private LinkedList<RegularTable> mRegularList = new LinkedList<RegularTable>();
    /**
     * 删除细则的个数提示
     */
    private RegularTable mPromptTable = new RegularTable("Prompt", false);
    
    /**
     * 删除细则的个数
     */
    private int mDeleteRegularCount = 0;
    
    private int mRegularPosition = -1;
    
    private boolean isDeleteAllRegular = false;
    
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
//                    adapter.remove(adapter.getItem(which));
                    SCLog.d("cugblog", "remove listener " + which);
                    int promptPosition = mRegularList.indexOf(mPromptTable);
                    if(which != promptPosition)
                        updateListView(which);
                }
            };

        private DragSortListView.DragScrollProfile ssProfile =
            new DragSortListView.DragScrollProfile() {
                @Override
                public float getSpeed(float w, long t) {
                    if (w > 0.8f) {
                        // Traverse all views in a millisecond
                        return ((float) mlistRegularAdapter.getCount()) / 0.001f;
                    } else {
                        return 10.0f * w;
                    }
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.manage_regular);
        
        Intent intent = getIntent();
        mRegularPosition = intent.getIntExtra("position", -1);
        mCategoryID = intent.getStringExtra("categoryId");
        String categoryName = intent.getStringExtra("categoryName");
        if(mCategoryID != null && mCategoryID.length() > 0){
            ArrayList<RegularElement> elements = RegularTable.getAllRegularElements(mCategoryID);
            boolean isAddDeletePrompt = true;
            ArrayList<RegularTable> trueTables = new ArrayList<RegularTable>();
            ArrayList<RegularTable> falseTables = new ArrayList<RegularTable>();
            for (int i = 0; i < elements.size(); i++) {
                RegularElement element = elements.get(i);
                if(element.isShow()){
                    trueTables.add(new RegularTable(element));
                }
                else{
                    if(isAddDeletePrompt){
                        isAddDeletePrompt = false;
                        falseTables.add(mPromptTable);
                    }
                    mDeleteRegularCount++;
                    falseTables.add(new RegularTable(element));
                }
            }
            for(int i = 0; i < trueTables.size(); i++){
                mRegularList.add(trueTables.get(i));
            }
            for(int j = 0; j < falseTables.size(); j++){
                mRegularList.add(falseTables.get(j));
            }
        }
        tv_manage_regular_title = (TextView) findViewById(R.id.tv_manage_regular_title);
        tv_manage_regular_no_data = (TextView) findViewById(R.id.tv_manage_regular_no_data);
        layout_manage_regular_back = (RelativeLayout) findViewById(R.id.layout_manage_regular_back);
        layout_manage_regular_clear = (RelativeLayout) findViewById(R.id.layout_manage_regular_clear);
        layout_manage_regular_recovery = (RelativeLayout) findViewById(R.id.layout_manage_regular_recovery);
        layout_delete_all_regular_progress = (RelativeLayout) findViewById(R.id.layout_delete_all_regular_progress);
        iv_manage_regular_process_box = (ImageView) findViewById(R.id.iv_manage_regular_process_box);
        layout_manage_regular_bottom_process = (LinearLayout) findViewById(R.id.layout_manage_regular_bottom_process);
        
        if(mDeleteRegularCount >= mRegularList.size())
            layout_manage_regular_clear.setEnabled(false);
        if(categoryName != null)
            tv_manage_regular_title.setText(categoryName);
        listview_regular = (DragSortListView) findViewById(R.id.listview_manage_regular);
        listview_regular.setDropListener(onDrop);
        listview_regular.setRemoveListener(onRemove);
        listview_regular.setDragScrollProfile(ssProfile);
        
        mlistRegularAdapter = new ListRegularAdapter(getApplicationContext(), R.layout.manage_regular_item, mRegularList);
        mlistRegularAdapter.init();
        listview_regular.setAdapter(mlistRegularAdapter);
        
        if(mRegularList.size() <= 0){
            tv_manage_regular_no_data.setVisibility(View.VISIBLE);
            listview_regular.setVisibility(View.GONE);
        }
        
        setListeners();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        openBox();
    }

    /**
     * 设置view的监听器
     */
    private void setListeners() {
        listview_regular.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        
        layout_manage_regular_back.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                back();
            }
        });
        
        layout_manage_regular_clear.setOnClickListener(new OnClickListener() {//删除全部
            
            @Override
            public void onClick(View v) {
                if(isSelected.containsValue(false))
                    new DeleteAllRegularDBTask().execute();
            }
        });
        
        layout_manage_regular_recovery.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(isSelected.containsValue(true))
                    new AddAllRegularTask().execute();
            }
        });
        
        iv_manage_regular_process_box.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(layout_manage_regular_bottom_process.getVisibility() == View.VISIBLE)
                    closeBox();
                else 
                    openBox();
            }
        });
    }
    
    private void openBox(){
        Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.safety_base_box_open);
        layout_manage_regular_bottom_process.startAnimation(slideIn);
        iv_manage_regular_process_box.setImageResource(R.drawable.box_open);
        layout_manage_regular_bottom_process.setVisibility(View.VISIBLE);
        slideIn.reset();
    }
    
    private void closeBox(){
        Animation slideOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.safety_base_box_closed);
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                iv_manage_regular_process_box.setImageResource(R.drawable.box_closed);
            }
        }, 200);
        layout_manage_regular_bottom_process.startAnimation(slideOut);
        layout_manage_regular_bottom_process.setVisibility(View.GONE);
        slideOut.reset();
    }
    
    /**
     *删除数据，异步线程更新数据库
     */
    private class DeleteAllRegularDBTask extends AsyncTask<Void, Void, Integer>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layout_manage_regular_clear.setEnabled(false);
            layout_delete_all_regular_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if(mRegularList != null && mRegularList.size() > 0){
                for(int i = 0; i < mRegularList.size(); i++){
                    RegularTable table = mRegularList.get(i);
                    if(table.getElement().isShow()){
                        table.getElement().setShow(false);
                        table.save();
                    }
                    isSelected.put(i, true);
                }
                if(mCategoryID != null && mCategoryID.length() > 0)
                    CategoryTable.hideCategory(mCategoryID);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(mRegularList != null){
                if(mRegularList.indexOf(mPromptTable) != -1){
                    RegularTable promptTable = mRegularList.remove(mRegularList.indexOf(mPromptTable));
                    mRegularList.addFirst(promptTable);
                    mDeleteRegularCount = mRegularList.size() - 1;
                }
                else{
                    mDeleteRegularCount = mRegularList.size();
                    mRegularList.addFirst(mPromptTable);
                }
                if(mDeleteRegularCount < 0)
                    mDeleteRegularCount = 0;
            }
            mlistRegularAdapter.notifyDataSetChanged();
            isDeleteAllRegular = true;
            layout_delete_all_regular_progress.setVisibility(View.GONE);
        }
    }
    
    /**
     *恢复全部数据，异步线程更新数据库
     */
    private class AddAllRegularTask extends AsyncTask<Void, Void, Integer>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layout_manage_regular_recovery.setEnabled(false);
            layout_delete_all_regular_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if(mRegularList != null && mRegularList.size() > 0){
                if(mRegularList.indexOf(mPromptTable) != -1){
                    mRegularList.remove(mPromptTable);
                    isSelected.remove(0);
                }
                for(int i = 0; i < mRegularList.size(); i++){
                    RegularTable table = mRegularList.get(i);
                    if(!table.getElement().isShow()){
                        table.getElement().setShow(true);
                        table.save();
                    }
                    isSelected.put(i, false);
                }
                if(mCategoryID != null && mCategoryID.length() > 0)
                    CategoryTable.showCategory(mCategoryID);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mlistRegularAdapter.notifyDataSetChanged();
            isDeleteAllRegular = false;
            layout_manage_regular_recovery.setEnabled(true);
            layout_delete_all_regular_progress.setVisibility(View.GONE);
        }
    }

    private void updateListView(int position){
        if (!isSelected.get(position)) {
            mDeleteRegularCount++;
            RegularTable deleteregular = mRegularList.remove(position);
            deleteregular.getElement().setShow(false);
            deleteregular.save();//实时更新数据库
            if (mDeleteRegularCount > 0) {// 删除个数大小0才提示
                if (mRegularList.indexOf(mPromptTable) == -1) {// 如果listview中不存在则添加
                    mRegularList.add(mPromptTable);
                }
            }
            mRegularList.add(deleteregular);
            isSelected.put(mRegularList.size() - 1, true);
            int size = mRegularList.size();
            for(int i = position; i < size; i++){
                if(!isSelected.get(i)){
                    continue;
                }
                else{// 添加选中时将isSelect中第 一个为ture的前一个置为true
                    isSelected.put(i - 1, true);
                    break;
                }
            }
            SCLog.d("cugblog", "is select size=" + isSelected.size());
        } else {// 去除选中
            mDeleteRegularCount--;
            SCLog.d("cugblog", "mDeleteregularCount=" + mDeleteRegularCount);
            if (mDeleteRegularCount <= 0 && mRegularList.indexOf(mPromptTable) != -1) {
                isSelected.remove(mRegularList.size() - 1);
                mRegularList.remove(mPromptTable);
                isSelected.put(mRegularList.size() - 1, false);
            } 
            else {
                LinkedList<RegularTable> tempList = new LinkedList<RegularTable>();
                for (int i = 0; i < mRegularList.size(); i++) {// 重新排序listview的内容，这有待后续优化
                    if (!isSelected.get(i)) {//将没选中的项添加到新队列
                        tempList.add(mRegularList.get(i));
                    }
                    else{
                        break;
                    }
                }
                RegularTable addregular = mRegularList.remove(position);
                addregular.getElement().setShow(true);
                addregular.save();
                tempList.add(addregular);
                for (int i = 0; i < mRegularList.size(); i++) {// 重新排序listview的内容，这有待后续优化
                    if (isSelected.get(i)) {//将没选中的项添加到新队列
                        tempList.add(mRegularList.get(i));
                    }
                }
                mRegularList = (LinkedList<RegularTable>) tempList.clone();
                //将DELETE_PROMPT置为true
                isSelected.put(mRegularList.indexOf(addregular), false);// 修改选择的项为非选中
                SCLog.d("cugblog", "addregular index=" + mRegularList.indexOf(addregular));
            }
        }
        if(mDeleteRegularCount == mRegularList.size()){
            layout_manage_regular_clear.setEnabled(false);
            isDeleteAllRegular = true;
        }
        else{
            layout_manage_regular_clear.setEnabled(true);
            isDeleteAllRegular = false;
        }
        mlistRegularAdapter.notifyDataSetChanged();
    }

    private class ListRegularAdapter extends ArrayAdapter<RegularTable> {

        public ListRegularAdapter(Context context, int textViewResourceId, List<RegularTable> objects) {
            super(context, textViewResourceId, objects);
        }
        // 初始化 设置所有checkbox都为未选择
        public void init() {
            isSelected = new HashMap<Integer, Boolean>();
            for (int i = 0; i < mRegularList.size(); i++) {
                if(mRegularList.get(i).getElement().isShow())
                    isSelected.put(i, false);
                else
                    isSelected.put(i, true);
            }
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder = convertView == null ? new ViewHolder() : (ViewHolder) convertView.getTag();

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_regular_item, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_manage_regular_icon);
                holder.titleView = (TextView) convertView.findViewById(R.id.tv_manage_regular_title);
                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.layout_manage_regular_content);
                holder.leftLayout = (RelativeLayout) convertView.findViewById(R.id.layout_manage_regular_left);
                holder.middleLayout = (RelativeLayout) convertView.findViewById(R.id.layout_manage_regular_middle);
                holder.layout_manage_regular_right = (RelativeLayout) convertView.findViewById(R.id.layout_manage_regular_right);
                holder.promptView = (TextView) convertView.findViewById(R.id.tv_manage_regular_delete_num);
                convertView.setTag(holder);
            }
            int promptPosition = mRegularList.indexOf(mPromptTable);
            if (promptPosition == position) {// 如果是提示项，则隐藏内容项
                holder.contentLayout.setVisibility(View.GONE);
                holder.promptView.setVisibility(View.VISIBLE);
                String prompt = getResources().getString(R.string.safety_manage_regular_delete_num);
                prompt = prompt.replace("%1", String.valueOf(mDeleteRegularCount));
                holder.promptView.setText(prompt);
            } else {
                holder.contentLayout.setVisibility(View.VISIBLE);
                holder.promptView.setVisibility(View.GONE);
                holder.titleView.setText(mRegularList.get(position).getElement().getName());
                
                if (isSelected.get(position)) {// checkbox选中则将背景换成浅色背景
                    holder.imageView.setImageResource(R.drawable.safety_manage_regular_item_mark_deleted);
                    holder.leftLayout.setBackgroundResource(R.drawable.safety_category_delete_background_left_disabled);
                    holder.middleLayout.setBackgroundResource(R.drawable.safety_category_delete_background_middle_disabled);
                    holder.layout_manage_regular_right.setBackgroundResource(R.drawable.safety_category_delete_background_right_disabled);
                } else {
                    holder.imageView.setImageResource(R.drawable.safety_manage_regular_item_mark);
                    holder.leftLayout.setBackgroundResource(R.drawable.safety_regular_delete_background_left);
                    holder.middleLayout.setBackgroundResource(R.drawable.safety_regular_delete_background_middle);
                    holder.layout_manage_regular_right.setBackgroundResource(R.drawable.safety_regular_delete_background_right);
                }
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return mRegularList.size();
        }

        @Override
        public RegularTable getItem(int position) {
            return mRegularList.get(position);
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
            int promptPosition = mRegularList.indexOf(mPromptTable);
            if(position == promptPosition)
                return false;
            return true;
        }

        public class ViewHolder {
            ImageView imageView;
            TextView titleView;
            TextView promptView;
            LinearLayout contentLayout;
            RelativeLayout leftLayout;
            RelativeLayout middleLayout;
            RelativeLayout layout_manage_regular_right;
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
        Intent intent = new Intent();
        intent.putExtra("position", mRegularPosition);
        intent.putExtra("deleteAll", isDeleteAllRegular);
        setResult(Activity.RESULT_OK, intent);
        this.finish();
    }
}
