package com.safety.free.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.model.Global;

public class SearchDataActivity extends Activity {
    
    private Button btn_search_data_finished;
    private Button btn_search_data_unfinished;
    private Button btn_search_data_result;
    private RelativeLayout layout_search_data_process;
    private int mXTopPix = 0;
    private int mYTopPix = 0;
    private int mProcessWidth = 0;
    private int mProcessHeight = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.right_menu_search_data);
        
        findViews();
        setListeners();
    }

    private void findViews() {
        btn_search_data_finished = (Button) findViewById(R.id.btn_search_data_finished);
        btn_search_data_unfinished = (Button) findViewById(R.id.btn_search_data_unfinished);
        btn_search_data_result = (Button) findViewById(R.id.btn_search_data_result);
        layout_search_data_process = (RelativeLayout) findViewById(R.id.layout_search_data_process);
        
    }
    
    private void setListeners() {
        btn_search_data_finished.setOnClickListener(new View.OnClickListener() {//已评估项
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchDataActivity.this, SearchFinishedActivity.class);
                startActivity(intent);
            }
        });
        
        btn_search_data_unfinished.setOnClickListener(new View.OnClickListener() {//未评估项
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchDataActivity.this, SearchUnfinishedActivity.class);
                startActivity(intent);
            }
        });
        
        btn_search_data_result.setOnClickListener(new View.OnClickListener() {//查询结果
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchDataActivity.this, SearchResultActivity.class);
                startActivity(intent);
            }
        });
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {//处理点击弹出框以外的地方的事件，隐藏当前弹出框
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                computeLocation();
                float y = event.getY();
                float x = event.getX();
                if(x < mXTopPix || y < mYTopPix || (x > (mXTopPix + mProcessWidth) || (y > mYTopPix + mProcessHeight))){
                    this.finish();
                }
                break;
        }
        return false;
    }
    
    private void computeLocation(){
        int[] location = new int[2];
        mProcessHeight = layout_search_data_process.getHeight();
        mProcessWidth = layout_search_data_process.getWidth();
        layout_search_data_process.getLocationInWindow(location);
        mXTopPix = location[0];
        mYTopPix = location[1];
    }
    
    private void finishActivity(){
        this.finish();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
}
