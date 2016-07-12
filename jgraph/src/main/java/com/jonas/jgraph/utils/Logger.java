package com.jonas.jgraph.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
/**
 * 打印或显示提示信息的工具类
 * 0全部显示  8全部隐藏
 * @author My
 *
 */
public class Logger {
	/**
	 * 0全部显示  8全部隐藏  默认0全部可显示
	 * 2 吐司没有
	 * 8 system.out没有
	 */
	public static int LOWEST_LOG_LEVEL = 0;// 0全部显示  8全部隐藏
	private static int TOAST = 1;
	private static int VERBOS = 2;
	private static int DEBUG = 3;
	private static int INFO = 4;
	private static int WARN = 5;
	private static int ERROR = 6;
	private static int SYSTEM = 7;

	/**
	 * info 信息
	 * 
	 * @param tag
	 * @param message
	 */
	public static void i(String tag, String message) {
		if (message == null) {
			return;
		}
		if (LOWEST_LOG_LEVEL <= INFO) {
			Log.i(tag, message);
		}
	}

	/**
	 * error 错误
	 * 
	 * @param tag
	 * @param message
	 */
	public static void e(String tag, String message) {
		if (message == null) {
			return;
		}
		if (LOWEST_LOG_LEVEL <= ERROR) {
			Log.e(tag, message);
		}
	}

	/**
	 * debug 调试
	 * 
	 * @param tag
	 * @param message
	 */
	public static void d(String tag, String message) {
		if (message == null) {
			return;
		}
		if (LOWEST_LOG_LEVEL <= DEBUG) {
			Log.d(tag, message);
		}
	}

	/**
	 * warn 警告
	 * 
	 * @param tag
	 * @param message
	 */
	public static void w(String tag, String message) {
		if (message == null) {
			return;
		}
		if (LOWEST_LOG_LEVEL <= WARN) {
			Log.w(tag, message);
		}
	}

	/**
	 * VERBOS
	 * 
	 * @param tag
	 * @param message
	 */
	public static void v(String tag, String message) {
		if (message == null) {
			return;
		}
		if (LOWEST_LOG_LEVEL <= VERBOS) {
			Log.v(tag, message);
		}
	}

	/**
	 * System.out.println(message);输出
	 * 
	 * @param message
	 */
	public static void s(String message) {
		if (message == null) {
			return;
		}
		if (LOWEST_LOG_LEVEL <= SYSTEM) {
			System.out.println(message);
		}
	}

	/**
	 * 吐司
	 * 
	 * @param context
	 * @param message
	 */
	public static void toast(Context context, String message) {
		if (LOWEST_LOG_LEVEL <= TOAST) {
			Toast.makeText(context.getApplicationContext(), message, 0).show();
		}
	}

}
