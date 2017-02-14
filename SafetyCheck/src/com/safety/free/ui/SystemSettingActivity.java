package com.safety.free.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.safety.free.R;
import com.safety.free.utils.SCLog;

public class SystemSettingActivity extends Activity {
    /** Called when the activity is first created. */
    
    private GestureDetector mGestureDetector;
    private SettingGestureListener mGestureListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.right_menu_setting);
        
        mGestureListener = new SettingGestureListener();
        mGestureDetector = new GestureDetector(getApplicationContext(), mGestureListener);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return mGestureDetector.onTouchEvent(event);
    }
    
    private class SettingGestureListener implements OnGestureListener{

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
                    SystemSettingActivity.this.finish();
                }else if(x<=0){
                  //left
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
}
