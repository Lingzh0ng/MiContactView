package com.wearapay.micontactview;

import android.text.TextUtils;
import com.wearapay.contact.IContact;

/**
 * Created by lyz on 2017/8/9.
 */
public class Contact implements IContact {

  private String name;
  private String phoneNumber;

  @Override public String toString() {
    return "Contact{" +
        "name='" + name + '\'' +
        ", phoneNumber='" + phoneNumber + '\'' +
        '}';
  }

  @Override public String getDisplayName() {
    if (TextUtils.isEmpty(name)) {
      if (TextUtils.isEmpty(phoneNumber)) {
        return "0000";
      } else {
        return phoneNumber;
      }
    } else {
      return name;
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
