package com.safety.free.ui;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.safety.free.R;
import com.safety.free.data.AssessTable;
import com.safety.free.data.CategoryTable;
import com.safety.free.data.RegularTable;
import com.safety.free.model.Global;
import com.safety.free.view.Pager;
import com.safety.free.view.PagerFactory;

public class StartActivity extends Activity {
    /** Called when the activity is first created. */

    private LinearLayout layout_book_pager;
    private Pager mPager;
    private Bitmap mFirstPageBitmap, mSecondPageBitmap;
    private Canvas mCurPageCanvas, mNextPageCanvas;
    private PagerFactory pagerFactory;
    private RelativeLayout layout_start_progress;
    private TextView tv_start;
    private ProgressBar progress_start;
    
    private boolean isInitBookDataFinished = false;
    
    private int mCurrentPageIndex = 0;

    private boolean isStartedMainActivity = false;
    
    private boolean isHaveTurnPage = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        Global.initApplication(this);
        
        layout_start_progress = (RelativeLayout) findViewById(R.id.layout_start_progress);
        tv_start = (TextView) findViewById(R.id.tv_start);
        progress_start = (ProgressBar) findViewById(R.id.progress_start);
        
        tv_start.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });

        if (Global.isShouldStartActivity()) {
            new StartTask().execute();
            showStartActivity();
        } else {
            finishActivity();
        }
    }

    private void showStartActivity() {
        layout_book_pager = (LinearLayout) findViewById(R.id.layout_book_pager);
        DisplayMetrics dm = new DisplayMetrics();
        dm = this.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels / 2;
        int screenHeight = dm.heightPixels;
        layout_book_pager.getLayoutParams().width = screenWidth;
        Log.i("cugblog", "screenWidth = " + screenWidth + "; screenHeight = " + screenHeight);

        mPager = new Pager(this, screenWidth, screenHeight);
        mFirstPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        mSecondPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        mCurPageCanvas = new Canvas(mFirstPageBitmap);

        Paint paint = new Paint();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.safety_start_book_first_page);
        Bitmap newmap = bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, true);
        mCurPageCanvas.drawBitmap(newmap, 0, 0, paint);
        bitmap.recycle();
        bitmap = null;
        newmap.recycle();
        newmap = null;
        Bitmap book = BitmapFactory.decodeResource(getResources(), R.drawable.safety_start_book);
        mCurPageCanvas.drawBitmap(book, 80, 30, paint);
        book.recycle();
        book = null;
        Bitmap people = BitmapFactory.decodeResource(getResources(), R.drawable.people);
        Bitmap newPeople = Bitmap.createScaledBitmap(people, 120, 120, true);
        mCurPageCanvas.drawBitmap(newPeople, 320, 540, paint);
        if (people != newPeople) {
            people.recycle();
            people = null;
        }
        newPeople.recycle();
        newPeople = null;

        mNextPageCanvas = new Canvas(mSecondPageBitmap);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.safety_start_book_second_page);
        Bitmap newmap2 = bitmap.createScaledBitmap(bitmap2, screenWidth, screenHeight, true);
        mNextPageCanvas.drawBitmap(newmap2, 0, 0, paint);
        if (bitmap2 != newmap2) {
            bitmap2.recycle();
            bitmap2 = null;
        }
        newmap2.recycle();
        newmap2 = null;
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.public_people);
        mNextPageCanvas.drawBitmap(bitmap3, 16, 0, paint);
        bitmap3.recycle();
        bitmap3 = null;

        pagerFactory = new PagerFactory(screenWidth, screenHeight);

        layout_book_pager.addView(mPager, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // pagerFactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(),
        // R.drawable.book_with_birds));

        try {
            pagerFactory.openbook(null);// "/data/data/com.homer.pager/files/test.txt"
            pagerFactory.onDraw(mCurPageCanvas, getApplicationContext(), 0);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        mPager.setBitmaps(mFirstPageBitmap, mFirstPageBitmap);
        mPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                boolean ret = false;
                if (v == mPager && !isHaveTurnPage) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        mPager.abortAnimation();
                        mPager.calcCornerXY(e.getX(), e.getY());

                        // pagerFactory.onDraw(mCurPageCanvas);
                        if (mPager.DragToRight()) { // 向右滑动，显示前一页
                            try {
                                pagerFactory.prePage();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
//                            if (pagerFactory.isfirstPage())
//                                return false;
                            pagerFactory.onDraw(mNextPageCanvas, getApplicationContext(), 1);
                        } else { // 向左滑动，显示后一页
                            try {
                                // pagerFactory.nextPage();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            if (pagerFactory.islastPage())
                                return false;
                            pagerFactory.onDraw(mNextPageCanvas, getApplicationContext(), 1);
                        }
                        mPager.setBitmaps(mFirstPageBitmap, mSecondPageBitmap);
                        Log.i("cugblog", "pager down");
                    }
                    ret = mPager.doTouchEvent(e, callback);
                    return ret;
                }
                Log.i("cugblog", "pager up");
                return false;
            }

        });
    }

    /**
     * 初始化数据库的异步任务
     */
    private class StartTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // 初始化测试数据
            if (Global.isShouldInsertBookData()) {
                Global.saveDatabasePreferences();
                Global.insertDataToDB();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            isInitBookDataFinished = true;
            progress_start.setVisibility(View.GONE);
        }
    }

    private FinishActivityCallback callback = new FinishActivityCallback() {

        @Override
        public void finish() {
            layout_start_progress.setVisibility(View.VISIBLE);
            isHaveTurnPage = true;
            if(isInitBookDataFinished){
                progress_start.setVisibility(View.GONE);
                tv_start.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * 结束当前activity的回调接口，当画完book的第二页后回调，finish activity
     */
    private void finishActivity() {
        if (!isStartedMainActivity) {
            isStartedMainActivity = true;
            Global.saveStartPreferences();
            Log.i("cugblog", "finish activity");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    public interface FinishActivityCallback {
        public void finish();
    }
}
