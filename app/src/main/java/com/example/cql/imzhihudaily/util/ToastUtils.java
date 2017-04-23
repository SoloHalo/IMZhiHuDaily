package com.example.cql.imzhihudaily.util;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class ToastUtils {
	
	private static WeakReference<Toast> mToast;
	
	/**
	 * 显示Toast
	 */
	public static void showToast(Context context, CharSequence text, int duration) {
		if(mToast == null||mToast.get() == null) {
			Toast toast = Toast.makeText(context, text, duration);
			mToast = new WeakReference<Toast>(toast);
		} else {
			mToast.get().setText(text);
			mToast.get().setDuration(duration);
		}
		mToast.get().show();
	}

	public static void showToast(Context context, CharSequence text) {
		showToast(context,text,Toast.LENGTH_SHORT);
	}

}
