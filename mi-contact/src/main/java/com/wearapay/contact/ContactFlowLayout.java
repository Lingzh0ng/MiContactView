package com.wearapay.contact;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ScrollView;
import com.wearapay.mi_contact.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyz on 2017/8/3.
 */
public class ContactFlowLayout extends ViewGroup {
  private static final int LEFT = -1;
  private static final int CENTER = 0;
  private static final int RIGHT = 1;

  protected List<List<View>> mAllViews = new ArrayList<>();
  protected List<Integer> mLineHeight = new ArrayList<>();
  protected List<Integer> mLineWidth = new ArrayList<>();
  private int mGravity;
  private List<View> lineViews = new ArrayList<>();

  private List<IContact> contacts = new ArrayList<>();
  private List<ContactView> contactsViews = new ArrayList<>();

  private OnContactsChangeListener onContactsChangeListener;
  private ScrollView autoScrollView;
  private boolean notAutoScrollView = true;

  public ContactFlowLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ContactFlowLayout);
    mGravity = ta.getInt(R.styleable.ContactFlowLayout_gravity, LEFT);
    ta.recycle();
    initContactView();
  }

  private void initParent() {
    ViewParent parent = getParent();
    if (parent != null) {
      if (parent instanceof ScrollView) {
        autoScrollView = (ScrollView) parent;
      } else {
        notAutoScrollView = false;
      }
    }
  }

  public ContactFlowLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ContactFlowLayout(Context context) {
    this(context, null);
  }

  public interface OnContactsChangeListener {
    void addContact(IContact contact);

    void removeContact(IContact contact);

    void editTextClickNext();

    /**
     * 自定义查询规则
     */
    IContact queryRule(String contact);
  }

  /**
   * 设置联系人改变监听
   */
  public void setOnContactsChangeListener(OnContactsChangeListener onContactsChangeListener) {
    this.onContactsChangeListener = onContactsChangeListener;
  }

  /**
   * 重新设置联系人
   */
  public void setContacts(List<IContact> contacts) {
    this.contacts.clear();
    this.contactsViews.clear();
    this.contacts.addAll(contacts);
    removeAllViews();
    initContactView();
  }

  /**
   * 添加一个联系人
   */
  public void addContact(IContact contact) {
    addContactView(contact);
  }

  /**
   * 添加一个联系人集合
   */
  public void addContacts(List<IContact> contact) {
    this.contacts.addAll(contacts);
    for (int i = 0; i < contact.size(); i++) {
      addContact(contact.get(i));
    }
  }

  /**
   * 得到联系人集合
   *
   * @return 联系人集合
   */
  public List<IContact> getContacts() {
    return contacts;
  }

  public EditText getLastEditText() {
    if (contactsViews != null && contactsViews.size() > 0) {
      ContactView contactView = contactsViews.get(contactsViews.size() - 1);
      return contactView.getEdInput();
    }
    return null;
  }

  private void addContactView(IContact contact) {
    if (contacts.size() >= 1) {
      contactsViews.get(contacts.size() - 1).setStatus(ContactView.Status.Normal);
      ContactView contactView = createContact(ContactView.Status.Last, contact);
      contactsViews.add(contactView);
      addView(contactView);
    } else {
      contactsViews.get(0).setStatus(ContactView.Status.Last);
      contactsViews.get(0).setTvContact(contact);
    }
    contacts.add(contact);
    delaySlide();
  }

  private void initContactView() {
    ContactView contactView;
    if (contacts == null || contacts.size() == 0) {
      contactView = createContact(ContactView.Status.First, null);
      contactsViews.add(contactView);
      addView(contactView);
    } else {
      for (int i = 0; i < contacts.size(); i++) {
        if (i == contacts.size() - 1) {
          contactView = createContact(ContactView.Status.Last, contacts.get(i));
        } else {
          contactView = createContact(ContactView.Status.Normal, contacts.get(i));
        }
        contactsViews.add(contactView);
        addView(contactView);
      }
    }

    delaySlide();

    setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (contactsViews != null && contactsViews.size() > 0) {
          ContactView contactView1 = contactsViews.get(contactsViews.size() - 1);
          if (contactView1.getCurrentStatus() == ContactView.Status.Last
              || contactView1.getCurrentStatus() == ContactView.Status.First) {
            contactView1.getEdInput().requestFocus();
            UIUtil.showInputMethod(getContext(), contactView1.getEdInput());
          }
        }
      }
    });
  }

  private ContactView createContact(ContactView.Status status, IContact contact) {
    ContactView contactView = new ContactView(getContext());
    contactView.setStatus(status);
    contactView.setTvContact(contact);
    contactView.setOnContactClickListener(new ContactView.OnContactClickListener() {
      @Override public void delClick(View view, IContact contact) {
        //Toast.makeText(getContext(), contact, Toast.LENGTH_SHORT).show();
        removeContactView(view, contact);
        if (onContactsChangeListener != null) {
          onContactsChangeListener.removeContact(contact);
        }
      }

      @Override public void editTextChangeNoFocus(final String contact) {
        if (!TextUtils.isEmpty(contact)) {
          IContact contactByPhoneNumber = null;
          if (onContactsChangeListener != null) {
            contactByPhoneNumber = onContactsChangeListener.queryRule(contact);
          }
          if (contactByPhoneNumber == null) {
            contactByPhoneNumber = new IContact() {
              @Override public String getDisplayName() {
                return contact;
              }
            };
          }
          addContactView(contactByPhoneNumber);
          if (onContactsChangeListener != null) {
            onContactsChangeListener.addContact(contactByPhoneNumber);
          }
          delaySlide();
        }
      }

      @Override public void editTextClickNext() {
        if (onContactsChangeListener != null) {
          onContactsChangeListener.editTextClickNext();
        }
      }
    });
    return contactView;
  }

  private void removeContactView(View view, IContact contact) {
    if (contacts.size() > 1) {
      if (contacts.get(contacts.size() - 1).equals(contact)) {
        contactsViews.get(contacts.size() - 2).setStatus(ContactView.Status.Last);
      }
      contacts.remove(contact);
      removeView(view);
      contactsViews.remove(view);
    } else {
      contacts.remove(contact);
      contactsViews.get(0).setStatus(ContactView.Status.First);
      contactsViews.get(0).setTvContact(null);
    }
  }

  private void delaySlide() {
    if (autoScrollView != null) {
      autoScrollView.postDelayed(new Runnable() {
        @Override public void run() {
          autoScrollView.fullScroll(View.FOCUS_DOWN);
        }
      }, 100);
    } else {
      if (notAutoScrollView) {
        initParent();
      }
    }
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
    int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
    int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
    int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

    // wrap_content
    int width = 0;
    int height = 0;

    int lineWidth = 0;
    int lineHeight = 0;

    int cCount = getChildCount();

    for (int i = 0; i < cCount; i++) {
      View child = getChildAt(i);
      if (child.getVisibility() == View.GONE) {
        if (i == cCount - 1) {
          width = Math.max(lineWidth, width);
          height += lineHeight;
        }
        continue;
      }
      measureChild(child, widthMeasureSpec, heightMeasureSpec);
      MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

      int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
      int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

      if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
        width = Math.max(width, lineWidth);
        lineWidth = childWidth;
        height += lineHeight;
        lineHeight = childHeight;
      } else {
        lineWidth += childWidth;
        lineHeight = Math.max(lineHeight, childHeight);
      }
      if (i == cCount - 1) {
        width = Math.max(lineWidth, width);
        height += lineHeight;
      }
    }
    setMeasuredDimension(
        //
        modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
        modeHeight == MeasureSpec.EXACTLY ? sizeHeight
            : height + getPaddingTop() + getPaddingBottom()//
    );
  }

  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    mAllViews.clear();
    mLineHeight.clear();
    mLineWidth.clear();
    lineViews.clear();

    int width = getWidth();

    int lineWidth = 0;
    int lineHeight = 0;

    int cCount = getChildCount();

    for (int i = 0; i < cCount; i++) {
      View child = getChildAt(i);
      if (child.getVisibility() == View.GONE) continue;
      MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

      int childWidth = child.getMeasuredWidth();
      int childHeight = child.getMeasuredHeight();

      if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin
          > width - getPaddingLeft() - getPaddingRight()) {
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);
        mLineWidth.add(lineWidth);

        lineWidth = 0;
        lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
        lineViews = new ArrayList<>();
      }
      lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
      lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
      lineViews.add(child);
    }
    mLineHeight.add(lineHeight);
    mLineWidth.add(lineWidth);
    mAllViews.add(lineViews);

    int left = getPaddingLeft();
    int top = getPaddingTop();

    int lineNum = mAllViews.size();

    for (int i = 0; i < lineNum; i++) {
      lineViews = mAllViews.get(i);
      lineHeight = mLineHeight.get(i);

      // set gravity
      int currentLineWidth = this.mLineWidth.get(i);
      switch (this.mGravity) {
        case LEFT:
          left = getPaddingLeft();
          break;
        case CENTER:
          left = (width - currentLineWidth) / 2 + getPaddingLeft();
          break;
        case RIGHT:
          left = width - currentLineWidth + getPaddingLeft();
          break;
      }

      for (int j = 0; j < lineViews.size(); j++) {
        View child = lineViews.get(j);
        if (child.getVisibility() == View.GONE) {
          continue;
        }

        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        int lc = left + lp.leftMargin;
        int tc = top + lp.topMargin;
        int rc = lc + child.getMeasuredWidth();
        int bc = tc + child.getMeasuredHeight();

        child.layout(lc, tc, rc, bc);

        left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
      }
      top += lineHeight;
    }
  }

  @Override public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new MarginLayoutParams(getContext(), attrs);
  }

  @Override protected LayoutParams generateDefaultLayoutParams() {
    return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  @Override protected LayoutParams generateLayoutParams(LayoutParams p) {
    return new MarginLayoutParams(p);
  }
}
