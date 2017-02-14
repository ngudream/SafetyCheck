package com.safety.free.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.model.Global;

public class ClearDataActivity extends Activity {
    
    private TextView tv_clear_data_prompt;
    private Button btn_clear_data_confirm;
    private Button btn_clear_data_cancel;
    private RelativeLayout layout_clear_data_progress;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.right_menu_clear_data);
        
        findViews();
        setListeners();
    }

    private void findViews() {
        tv_clear_data_prompt = (TextView) findViewById(R.id.tv_clear_data_prompt);
        btn_clear_data_confirm = (Button) findViewById(R.id.btn_clear_data_confirm);
        btn_clear_data_cancel = (Button) findViewById(R.id.btn_clear_data_cancel);
        layout_clear_data_progress = (RelativeLayout) findViewById(R.id.layout_clear_data_progress);
    }
    
    private void setListeners() {
        btn_clear_data_confirm.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                layout_clear_data_progress.setVisibility(View.VISIBLE);
                ClearDataTask dataTask = new ClearDataTask();
                dataTask.execute();
            }
        });
        
        btn_clear_data_cancel.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });
    }
    
    /**
     *异步线程更新数据库
     */
    private class ClearDataTask extends AsyncTask<Void, Void, Void>{
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Global.insertDataToDB();
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            layout_clear_data_progress.setVisibility(View.GONE);
            String prompt = getResources().getString(R.string.clear_data_success);
            tv_clear_data_prompt.setText(prompt);
        }
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
