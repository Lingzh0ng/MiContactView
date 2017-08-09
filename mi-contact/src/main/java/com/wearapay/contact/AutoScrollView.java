package com.wearapay.contact;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;
import com.wearapay.mi_contact.R;

/**
 * Created by lyz on 2017/8/3.
 */
public class AutoScrollView extends ScrollView {

  private int maxHeight;

  public AutoScrollView(Context context) {
    this(context, null);
  }

  public AutoScrollView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AutoScrollView);
    maxHeight =
        (int) ta.getDimension(R.styleable.AutoScrollView_max_height, UIUtil.dip2px(context, 200));
    ta.recycle();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    try {
      heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
    } catch (Exception e) {
      e.printStackTrace();
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }
}
