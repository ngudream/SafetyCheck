package com.safety.free.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.safety.free.R;

/**
 * 简介页
 */
public class OutlineFragment extends Fragment {
    String text = null;

    public OutlineFragment() {
    }

    public OutlineFragment(String text) {
        this.text = text;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflater the layout
        View view = inflater.inflate(R.layout.outline_fragment, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_outline);
        if (!TextUtils.isEmpty(text)) {
            text = text.replace("%1", "      ");
            textView.setText(text);
        }
        return view;
    }

    public String getText() {
        return text;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
//        Log.d("cugblog", "onDestroy:" + text);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
//        Log.d("cugblog", "onPause:" + text);
        super.onPause();
    }

    @Override
    public void onStart() {
//        Log.d("cugblog", "onStart:" + text);
        super.onStart();
    }

    @Override
    public void onStop() {
//        Log.d("cugblog", "onStop:" + text);
        super.onStop();
    }

}
