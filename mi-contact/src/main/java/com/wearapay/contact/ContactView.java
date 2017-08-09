package com.wearapay.contact;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.wearapay.mi_contact.R;

/**
 * Created by lyz on 2017/8/3.
 */
public class ContactView extends LinearLayout {

  public enum Status {
    First,
    Last,
    Normal
  }

  private LinearLayout llContact;
  private TextView tvContact;
  private ImageView ivDel;
  private EditText edInput;
  private IContact contact;
  private int maxHeight = 30;
  private int margin = 2;
  private Status currentStatus = Status.Normal;
  private OnContactClickListener onContactClickListener;

  public Status getCurrentStatus() {
    return currentStatus;
  }

  public EditText getEdInput() {
    return edInput;
  }

  public void setOnContactClickListener(OnContactClickListener onContactClickListener) {
    this.onContactClickListener = onContactClickListener;
  }

  public ContactView(Context context) {
    this(context, null);
  }

  public ContactView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ContactView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
    LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        UIUtil.dip2px(getContext(), maxHeight));
    margin = UIUtil.dip2px(getContext(), margin);
    layoutParams.bottomMargin = margin;
    layoutParams.topMargin = margin;
    layoutParams.rightMargin = margin;
    layoutParams.leftMargin = margin;
    setLayoutParams(layoutParams);
  }

  private void init(final Context context) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.mi_contact_view, this, true);
    llContact = (LinearLayout) findViewById(R.id.llContact);
    tvContact = (TextView) findViewById(R.id.tvContact);
    ivDel = (ImageView) findViewById(R.id.ivDel);
    edInput = (EditText) findViewById(R.id.edInput);
    edInput.clearFocus();
    ivDel.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        if (onContactClickListener != null) {
          onContactClickListener.delClick(ContactView.this, contact);
        }
      }
    });
    edInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
          edInput.clearFocus();
          if (onContactClickListener != null) {
            onContactClickListener.editTextClickNext();
          }
          return true;
        }
        return false;
      }
    });
    setOnFocusChangeListener(false);
    setStatus(currentStatus);
  }

  private void setOnFocusChangeListener(boolean isListener) {
    if (isListener) {
      edInput.setOnFocusChangeListener(new OnFocusChangeListener() {
        @Override public void onFocusChange(View v, boolean hasFocus) {
          if (hasFocus) {
            // 此处为得到焦点时的处理内容
          } else {
            // 此处为失去焦点时的处理内容
            if (onContactClickListener != null) {
              onContactClickListener.editTextChangeNoFocus(getEditTextString());
              edInput.setText("");
            }
          }
        }
      });
    } else {
      if (edInput.getOnFocusChangeListener() != null) {
        edInput.setOnFocusChangeListener(null);
      }
    }
  }

  public void setStatus(Status currentStatus) {
    this.currentStatus = currentStatus;
    switch (currentStatus) {
      case First:
        setTvContact(null);
        setContactTextVisibility(false);
        setEditTextVisibility(true);
        setOnFocusChangeListener(true);
        break;
      case Normal:
        setContactTextVisibility(true);
        setEditTextVisibility(false);
        setOnFocusChangeListener(false);
        break;
      case Last:
        setContactTextVisibility(true);
        setEditTextVisibility(true);
        setOnFocusChangeListener(true);
        break;
    }
  }

  public void setTvContact(IContact contact) {
    this.contact = contact;
    tvContact.setText(contact == null ? "" : contact.getDisplayName());
  }

  public void setEditTextVisibility(boolean isVisibility) {
    if (isVisibility) {
      edInput.setVisibility(VISIBLE);
    } else {
      edInput.setVisibility(GONE);
    }
  }

  public void setContactTextVisibility(boolean isVisibility) {
    if (isVisibility) {
      llContact.setVisibility(VISIBLE);
    } else {
      llContact.setVisibility(GONE);
    }
  }

  public String getEditTextString() {
    return edInput.getText().toString().trim();
  }

  public interface OnContactClickListener {
    void delClick(View view, IContact contact);

    void editTextChangeNoFocus(String contact);

    void editTextClickNext();
  }
}
