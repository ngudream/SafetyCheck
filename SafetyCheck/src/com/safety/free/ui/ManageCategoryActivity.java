package com.safety.free.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.data.CategoryElement;
import com.safety.free.data.CategoryTable;
import com.safety.free.data.RegularTable;
import com.safety.free.utils.SCLog;
import com.safety.free.view.DragSortListView;

/**
 * 主界面
 */
public class ManageCategoryActivity extends Activity {

    private DragSortListView listview_category;
    private TextView tv_manage_category_title;
    private TextView tv_manage_category_no_data;
    private RelativeLayout layout_manage_category_back;
    private RelativeLayout layout_manage_category_clear;
    private RelativeLayout layout_manage_category_recovery;
    private RelativeLayout layout_delete_all_category_progress;
    private ImageView iv_manage_category_process_box;
    private LinearLayout layout_manage_category_bottom_process;
    
    private ListCategoryAdapter mListCategoryAdapter;

    private HashMap<Integer, Boolean> isSelected;
    /**
     * 保存删除的category，传回上一个activity
     */
    private ArrayList<String> mDeleteCategoriesId = new ArrayList<String>(); 
    /**
     * 保存添加的category，传回上一个activity
     */
    private ArrayList<String> mAddCategoriesId = new ArrayList<String>(); 
    
    /**
     * 第三级菜单标题
     */
    private LinkedList<CategoryTable> mCategoryList = new LinkedList<CategoryTable>();
    /**
     * 删除内容项的个数提示
     */
    private CategoryTable mPromptTable = new CategoryTable("Prompt", false);
    
//    private HashMap<Integer, ListCategoryAdapter.ViewHolder> mViewHolderMap = new HashMap<Integer, ManageCategoryActivity.ListCategoryAdapter.ViewHolder>();
    /**
     * 删除内容项的个数
     */
    private int mDeleteCategoryCount = 0;
    
    private int mCategoryPosition = -1;
    
    private int DELETE_REGULAR_REQUEST_CODE = 1;
    
    private boolean isDeleteAllCategory = false;
    
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
                    int promptPosition = mCategoryList.indexOf(mPromptTable);
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
                        return ((float) mListCategoryAdapter.getCount()) / 0.001f;
                    } else {
                        return 10.0f * w;
                    }
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.manage_category);
        
        Intent intent = getIntent();
        mCategoryPosition = intent.getIntExtra("position", -1);
        String assessId = intent.getStringExtra("assessId");
        String assessName = intent.getStringExtra("assessName");
        if(assessId != null && assessId.length() > 0){
            ArrayList<CategoryElement> elements = CategoryTable.getAllCategoryElements(assessId);
            boolean isAddDeletePrompt = true;
            ArrayList<CategoryTable> trueTables = new ArrayList<CategoryTable>();
            ArrayList<CategoryTable> falseTables = new ArrayList<CategoryTable>();
            for (int i = 0; i < elements.size(); i++) {
                CategoryElement element = elements.get(i);
                if(element.isShow()){
                    trueTables.add(new CategoryTable(element));
                }
                else{
                    if(isAddDeletePrompt){
                        isAddDeletePrompt = false;
                        falseTables.add(mPromptTable);
                    }
                    mDeleteCategoryCount++;
                    falseTables.add(new CategoryTable(element));
                }
            }
            for(int i = 0; i < trueTables.size(); i++){
                mCategoryList.add(trueTables.get(i));
            }
            for(int j = 0; j < falseTables.size(); j++){
                mCategoryList.add(falseTables.get(j));
            }
        }
        tv_manage_category_title = (TextView) findViewById(R.id.tv_manage_category_title);
        tv_manage_category_no_data = (TextView) findViewById(R.id.tv_manage_category_no_data);
        layout_manage_category_back = (RelativeLayout) findViewById(R.id.layout_manage_category_back);
        layout_manage_category_clear = (RelativeLayout) findViewById(R.id.layout_manage_category_clear);
        layout_manage_category_recovery = (RelativeLayout) findViewById(R.id.layout_manage_category_recovery);
        layout_delete_all_category_progress = (RelativeLayout) findViewById(R.id.layout_delete_all_category_progress);
        iv_manage_category_process_box = (ImageView) findViewById(R.id.iv_manage_category_process_box);
        layout_manage_category_bottom_process = (LinearLayout) findViewById(R.id.layout_manage_category_bottom_process);
        if(assessName != null)
            tv_manage_category_title.setText(assessName);
        listview_category = (DragSortListView) findViewById(R.id.listview_manage_category);
        listview_category.setDropListener(onDrop);
        listview_category.setRemoveListener(onRemove);
        listview_category.setDragScrollProfile(ssProfile);
        
        if(mCategoryList.size() <= 0){
            tv_manage_category_no_data.setVisibility(View.VISIBLE);
            listview_category.setVisibility(View.GONE);
        }
        
        mListCategoryAdapter = new ListCategoryAdapter(getApplicationContext(), R.layout.manage_category_item, mCategoryList);
        mListCategoryAdapter.init();
        listview_category.setAdapter(mListCategoryAdapter);
        
        setListeners();
    }
    

    /**
     * 设置view的监听器
     */
    private void setListeners() {
        listview_category.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        
        layout_manage_category_back.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                back();
            }
        });
        
        layout_manage_category_clear.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(isSelected.containsValue(false))//只有存在还没有删除的项才执行删除操作
                    new DeleteAllCategoryTask().execute();
            }
        });
        
        layout_manage_category_recovery.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(isSelected.containsValue(true))//只有存在已删除的项才执行恢复操作
                    new AddAllCategoryTask().execute();
            }
        });
        
        iv_manage_category_process_box.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(layout_manage_category_bottom_process.getVisibility() == View.VISIBLE){
                    closeBox();
                }
                else{
                    openBox();
                }
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        openBox();
    }
    
    private void openBox(){
        Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.safety_base_box_open);
        layout_manage_category_bottom_process.startAnimation(slideIn);
        iv_manage_category_process_box.setImageResource(R.drawable.manage_menu_box_open);
        layout_manage_category_bottom_process.setVisibility(View.VISIBLE);
        slideIn.reset();
    }
    
    private void closeBox(){
        Animation slideOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.safety_base_box_closed);
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                iv_manage_category_process_box.setImageResource(R.drawable.manage_menu_box_close);
            }
        }, 200);
        layout_manage_category_bottom_process.startAnimation(slideOut);
        layout_manage_category_bottom_process.setVisibility(View.GONE);
        slideOut.reset();
    }
    
    /**
     *删除全部数据，异步线程更新数据库
     */
    private class DeleteAllCategoryTask extends AsyncTask<Void, Void, Integer>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layout_manage_category_clear.setEnabled(false);
            layout_delete_all_category_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if(mCategoryList != null && mCategoryList.size() > 0){
                for(int i = 0; i < mCategoryList.size(); i++){
                    CategoryTable table = mCategoryList.get(i);
                    if(table.getElement().isShow()){
                        table.getElement().setShow(false);
                        table.save();
                    }
                    isSelected.put(i, true);
                }
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(mCategoryList != null){
                if(mCategoryList.indexOf(mPromptTable) != -1){
                    CategoryTable promptTable = mCategoryList.remove(mCategoryList.indexOf(mPromptTable));
                    mCategoryList.addFirst(promptTable);
                    mDeleteCategoryCount = mCategoryList.size() - 1;
                }
                else{ 
                    mDeleteCategoryCount = mCategoryList.size();
                    mCategoryList.addFirst(mPromptTable);
                }
                if(mDeleteCategoryCount < 0)
                    mDeleteCategoryCount = 0;
            }
            mListCategoryAdapter.notifyDataSetChanged();
            isDeleteAllCategory = true;
            layout_manage_category_clear.setEnabled(true);
            layout_delete_all_category_progress.setVisibility(View.GONE);
        }
    }
    
    /**
     *恢复全部数据，异步线程更新数据库
     */
    private class AddAllCategoryTask extends AsyncTask<Void, Void, Integer>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layout_manage_category_recovery.setEnabled(false);
            layout_delete_all_category_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if(mCategoryList != null && mCategoryList.size() > 0){
                if(mCategoryList.indexOf(mPromptTable) != -1){
                    mCategoryList.remove(mPromptTable);
                    isSelected.remove(0);
                }
                for(int i = 0; i < mCategoryList.size(); i++){
                    CategoryTable table = mCategoryList.get(i);
                    if(!table.getElement().isShow()){
                        table.getElement().setShow(true);
                        table.save();
                    }
                    isSelected.put(i, false);
                }
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mListCategoryAdapter.notifyDataSetChanged();
            isDeleteAllCategory = false;
            layout_manage_category_recovery.setEnabled(true);
            layout_delete_all_category_progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == DELETE_REGULAR_REQUEST_CODE){
                int position = data.getIntExtra("position", -1);
                boolean isDeleteAll = data.getBooleanExtra("deleteAll", false);
                if(position >= 0 && position < mCategoryList.size()){
                    if(isDeleteAll)
                        changeDataSet(position);
                }
            }
        }
    }
    
    /**
     * 删除评估细则后更新评估项的显示列表。如果某评估项细则全部删除，则此评估项也删除
     * @param position
     */
    private void changeDataSet(int position){
        mDeleteCategoryCount++;
        CategoryTable deleteCategory = mCategoryList.remove(position);
        mDeleteCategoriesId.add(deleteCategory.getElement().getId());
        mAddCategoriesId.remove(deleteCategory.getElement().getId());
        deleteCategory.getElement().setShow(false);
        deleteCategory.save();// 实时更新数据库
        if (mDeleteCategoryCount > 0) {// 删除个数大小0才提示
            if (mCategoryList.indexOf(mPromptTable) == -1) {// 如果listview中不存在则添加
                mCategoryList.add(mPromptTable);
            }
        }
        mCategoryList.add(deleteCategory);
        isSelected.put(mCategoryList.size() - 1, true);
        int size = mCategoryList.size();
        for (int i = position; i < size; i++) {
            if (!isSelected.get(i)) {
                continue;
            } else {// 添加选中时将isSelect中第 一个为ture的前一个置为true
                isSelected.put(i - 1, true);
                break;
            }
        }
        setMark();
        mListCategoryAdapter.notifyDataSetChanged();
    }
    
    private void setMark(){
        if(mDeleteCategoryCount >= mCategoryList.size()){
            isDeleteAllCategory = true;
//            layout_manage_category_clear.setEnabled(false);
        }
        else{
            isDeleteAllCategory = false;
//            layout_manage_category_clear.setEnabled(true);
        }
    }
    
    /**
     * add or remove后更新listview
     * @param position
     */
    private void updateListView(int position){
        if (!isSelected.get(position)) {
            mDeleteCategoryCount++;
            CategoryTable deleteCategory = mCategoryList.remove(position);
            mDeleteCategoriesId.add(deleteCategory.getElement().getId());
            mAddCategoriesId.remove(deleteCategory.getElement().getId());
            deleteCategory.getElement().setShow(false);
            deleteCategory.save();//实时更新数据库
            if (mDeleteCategoryCount > 0) {// 删除个数大小0才提示
                if (mCategoryList.indexOf(mPromptTable) == -1) {// 如果listview中不存在则添加
                    mCategoryList.add(mPromptTable);
                }
            }
            mCategoryList.add(deleteCategory);
            isSelected.put(mCategoryList.size() - 1, true);
            int size = mCategoryList.size();
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
            mDeleteCategoryCount--;
            SCLog.d("cugblog", "mDeleteCategoryCount=" + mDeleteCategoryCount);
            if (mDeleteCategoryCount <= 0 && mCategoryList.indexOf(mPromptTable) != -1) {
                isSelected.remove(mCategoryList.size() - 1);
                mCategoryList.remove(mPromptTable);
                isSelected.put(mCategoryList.size() - 1, false);
            } 
            else {
                LinkedList<CategoryTable> tempList = new LinkedList<CategoryTable>();
                for (int i = 0; i < mCategoryList.size(); i++) {// 重新排序listview的内容，这有待后续优化
                    if (!isSelected.get(i)) {//将没选中的项添加到新队列
                        tempList.add(mCategoryList.get(i));
                    }
                    else{
                        break;
                    }
                }
                CategoryTable addCategory = mCategoryList.remove(position);
                mAddCategoriesId.add(addCategory.getElement().getId());
                mDeleteCategoriesId.remove(addCategory.getElement().getId());
                addCategory.getElement().setShow(true);
                addCategory.save();
                tempList.add(addCategory);
                for (int i = 0; i < mCategoryList.size(); i++) {// 重新排序listview的内容，这有待后续优化
                    if (isSelected.get(i)) {//将没选中的项添加到新队列
                        tempList.add(mCategoryList.get(i));
                    }
                }
                mCategoryList = (LinkedList<CategoryTable>) tempList.clone();
                //将DELETE_PROMPT置为true
                isSelected.put(mCategoryList.indexOf(addCategory), false);// 修改选择的项为非选中
                SCLog.d("cugblog", "addCategory index=" + mCategoryList.indexOf(addCategory));
            }
        }
        setMark();
        mListCategoryAdapter.notifyDataSetChanged();
    }

    private class ListCategoryAdapter extends ArrayAdapter<CategoryTable> {

        public ListCategoryAdapter(Context context, int textViewResourceId, List<CategoryTable> objects) {
            super(context, textViewResourceId, objects);
        }

        // 初始化 设置所有checkbox都为未选择
        public void init() {
            isSelected = new HashMap<Integer, Boolean>();
            for (int i = 0; i < mCategoryList.size(); i++) {
                if(mCategoryList.get(i).getElement().isShow())
                    isSelected.put(i, false);
                else
                    isSelected.put(i, true);
            }
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder = convertView == null ? new ViewHolder() : (ViewHolder) convertView.getTag();
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_category_item, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_manage_category_icon);
                holder.titleView = (TextView) convertView.findViewById(R.id.tv_manage_category_title);
                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.layout_manage_category_content);
                holder.leftLayout = (RelativeLayout) convertView.findViewById(R.id.layout_manage_category_left);
                holder.middleLayout = (RelativeLayout) convertView.findViewById(R.id.layout_manage_category_middle);
                holder.layout_manage_category_right = (RelativeLayout) convertView.findViewById(R.id.layout_manage_category_right);
                holder.promptView = (TextView) convertView.findViewById(R.id.tv_manage_category_delete_num);
                convertView.setTag(holder);
            }
            int promptPosition = mCategoryList.indexOf(mPromptTable);
            if (promptPosition == position) {// 如果是提示项，则隐藏内容项
                holder.contentLayout.setVisibility(View.GONE);
                holder.promptView.setVisibility(View.VISIBLE);
                String prompt = getResources().getString(R.string.safety_manage_category_delete_num);
                prompt = prompt.replace("%1", String.valueOf(mDeleteCategoryCount));
                holder.promptView.setText(prompt);
            } else {
                holder.contentLayout.setVisibility(View.VISIBLE);
                holder.promptView.setVisibility(View.GONE);
                holder.titleView.setText(mCategoryList.get(position).getElement().getName());
                
                if (isSelected.get(position)) {// checkbox选中则将背景换成浅色背景
                    holder.layout_manage_category_right.setEnabled(false);
                    holder.imageView.setImageResource(R.drawable.safety_manage_item_deleted);
                    holder.leftLayout.setBackgroundResource(R.drawable.safety_category_delete_background_left_disabled);
                    holder.middleLayout.setBackgroundResource(R.drawable.safety_category_delete_background_middle_disabled);
                    holder.layout_manage_category_right.setBackgroundResource(R.drawable.safety_category_delete_background_right_disabled);
                } else {
                    holder.layout_manage_category_right.setOnClickListener(new View.OnClickListener() {//只有没有删除的才能进行regular的管理操作
                        
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ManageCategoryActivity.this, ManageRegularActivity.class);
                            CategoryElement element = mCategoryList.get(position).getElement();
                            intent.putExtra("categoryId", element.getId());
                            intent.putExtra("categoryName", element.getName());
                            intent.putExtra("position", position);
                            startActivityForResult(intent, DELETE_REGULAR_REQUEST_CODE);
//                            overridePendingTransition(R.anim.base_slide_bottom_in, R.anim.base_slide_bottom_out);//第一个为第二个activity时的动画，第二个为第一个activity退出时动画
                        }
                    });
                    holder.layout_manage_category_right.setEnabled(true);
                    holder.imageView.setImageResource(R.drawable.safety_manage_category_item_mark);
                    holder.leftLayout.setBackgroundResource(R.drawable.safety_category_delete_background_left);
                    holder.middleLayout.setBackgroundResource(R.drawable.safety_category_delete_background_middle);
                    holder.layout_manage_category_right.setBackgroundResource(R.drawable.safety_category_delete_background_right);
                }
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return mCategoryList.size();
        }

        @Override
        public CategoryTable getItem(int position) {
            return mCategoryList.get(position);
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
            return position != mCategoryList.indexOf(mPromptTable);
        }

        public class ViewHolder {
            ImageView imageView;
            TextView titleView;
            TextView promptView;
            LinearLayout contentLayout;
            RelativeLayout leftLayout;
            RelativeLayout middleLayout;
            RelativeLayout layout_manage_category_right;
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
        Bundle bundle = new Bundle();
        bundle.putBoolean("deleteAll", isDeleteAllCategory);
        bundle.putStringArrayList("deletes", mDeleteCategoriesId);
        bundle.putStringArrayList("adds", mAddCategoriesId);
        bundle.putInt("position", mCategoryPosition);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        this.finish();
    }
}
