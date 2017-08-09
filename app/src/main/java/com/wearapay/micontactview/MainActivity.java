package com.wearapay.micontactview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import com.wearapay.contact.ContactFlowLayout;
import com.wearapay.contact.IContact;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

  private ContactFlowLayout contactFlowLayout;

  private List<IContact> contactList = new ArrayList<>();
  private ImageView ibAddReceiver;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    contactFlowLayout = (ContactFlowLayout) findViewById(R.id.flowLayout);
    ibAddReceiver = (ImageView) findViewById(R.id.ibAddReceiver);
    ibAddReceiver.setOnClickListener(this);
    addDate();
    contactFlowLayout.setOnContactsChangeListener(new ContactFlowLayout.OnContactsChangeListener() {
      @Override public void addContact(IContact contact) {
        //添加时调用
        contactList.add(contact);
        System.out.println("add" + contact);
      }

      @Override public void removeContact(IContact contact) {
        //删除时调用
        contactList.remove(contact);
        System.out.println("remove" + contact);
      }

      @Override public void editTextClickNext() {
        //按下软键盘回车时调用 可以用于焦点切换
      }

      @Override public IContact queryRule(String contact) {
        //自定义联系人规则
        Contact con = new Contact();
        con.setPhoneNumber(contact);
        if (contact.contains("155")) {
          con.setName("小马");
        }
        return con;
      }
    });
  }

  private void addDate() {
    Contact contact;
    for (int i = 0; i < 5; i++) {
      contact = new Contact();
      contact.setName("小李" + i);
      contactList.add(contact);
    }
    contactFlowLayout.setContacts(contactList);
  }

  @Override public void onClick(View view) {
    addDate();
  }
}
