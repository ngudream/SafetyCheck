package com.safety.free.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.safety.free.R;

/**
 * viewpager页显示评估内容
 *
 */
public class RightMenuClearView extends RelativeLayout {
    
    private Button btn_clear_data_confirm;
    private Button btn_clear_data_cancel;
    private RightMenuFragment mRightMenuFragment;

    public RightMenuClearView(Context context, AttributeSet attrs) {
        super(context, attrs);
        findViews();
        setListeners();
    }

    private void findViews() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.right_menu_clear_data, null);
        btn_clear_data_confirm = (Button) view.findViewById(R.id.btn_clear_data_confirm);
        btn_clear_data_cancel = (Button) view.findViewById(R.id.btn_clear_data_cancel);
        addView(view);
    }
    
    private void setListeners() {
        btn_clear_data_confirm.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                
            }
        });
        
        btn_clear_data_cancel.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                if(mRightMenuFragment != null){
                }
            }
        });
    }

    public void initData(RightMenuFragment rightMenuFragment) {
        this.mRightMenuFragment = rightMenuFragment;
    }
    
}
