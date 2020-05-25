package com.appfree.newedong.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.appfree.newedong.common.Common;

import java.util.ArrayList;
import java.util.List;

public class ContactManager {
    private Context mContext;
    private List<ContactModel> mListContact;
    private int nameIndex;
    private int numberIndex;

    public ContactManager(Context context){
        mContext = context;
        getContactData();
    }

    private void getContactData() {
        mListContact = new ArrayList<>();
        String[] projections = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.
                Phone.CONTENT_URI,projections, null,null,null);

        if (phones != null){
            nameIndex = phones.getColumnIndex(projections[0]);
            numberIndex = phones.getColumnIndex(projections[1]);
//            phones.moveToFirst();

            while (phones.moveToNext()){
                String name = phones.getString(nameIndex);
                String number = phones.getString(numberIndex);
                Log.d(Common.TAG_eDong, " name phone = " + name);
                Log.d(Common.TAG_eDong, " number phone = " + number);
                mListContact.add(new ContactModel(name,number));

            }
        }


    }

    public List<ContactModel> getListContact(){
        return mListContact;
    }
}
