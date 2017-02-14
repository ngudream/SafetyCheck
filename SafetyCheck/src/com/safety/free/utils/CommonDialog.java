package com.safety.free.utils;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

import com.safety.free.R;

public class CommonDialog {

	/*
	 * 显示信息框
	 */
	public static void alert(Context context, int titleResId, int textResId, final Runnable callback) {
        alert(context, titleResId == 0 ? null : context.getString(titleResId), context.getString(textResId), callback);
	}

	public static void alert(Context context, String title, String text, final Runnable callback) {

		if (context == null)
			return;
		try {
			AlertDialog.Builder builder = new Builder(context);
			builder.setMessage(text);

			builder.setPositiveButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (callback != null)
						callback.run();
				}
			});

			AlertDialog alert = builder.create();
			if (title != null)
				alert.setTitle(title);
			// alert.setIcon(android.R.drawable.ic_dialog_info);
			alert.setCancelable(false);
			alert.setCanceledOnTouchOutside(false);
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * 显示确认对话框 titleResId --- 标题内容 textResId ---正文内容 callback --- 用户确认后的回调函数
	 */
    public static void confirm(Context context, final int titleResId, String text, int btnTxtResId1, int btnTxtResId2, final Runnable btnCallback1, final Runnable btnCallback2) {

		if (context == null)
			return;

        try {

		AlertDialog.Builder builder = new Builder(context);
		if (titleResId != 0)
			builder.setTitle(titleResId);
		builder.setMessage(text);

		builder.setPositiveButton(btnTxtResId1, new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (btnCallback1 != null)
					btnCallback1.run();
			}
		});

		builder.setNegativeButton(btnTxtResId2, new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (btnCallback2 != null)
					btnCallback2.run();
			}
		});
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(false);
		alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public static void confirm(Context context, int titleResId, int textResId, int btnTxtResId1, int btnTxtResId2, final Runnable btnCallback1, final Runnable btnCallback2) {
		confirm(context, titleResId, context.getString(textResId), btnTxtResId1, btnTxtResId2, btnCallback1, btnCallback2);
	}

}
