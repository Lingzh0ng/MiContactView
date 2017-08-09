package com.wearapay.contact;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by lyz on 2017/8/9.
 */
public class UIUtil {
  public static int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * 隐藏键盘
   */
  public static boolean hideInputMethod(final Context destContext, final View view) {
    InputMethodManager imm =
        (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
      return true;
    }
    return false;
  }

  /**
   * 显示键盘
   */
  public static boolean showInputMethod(final Context destContext, final View view) {
    view.requestFocus();
    InputMethodManager imm =
        (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      //imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
      imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
      return true;
    }
    return false;
  }
}
