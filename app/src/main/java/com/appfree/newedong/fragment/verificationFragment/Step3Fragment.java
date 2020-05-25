package com.appfree.newedong.fragment.verificationFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appfree.newedong.HomeActivity;
import com.appfree.newedong.R;
import com.appfree.newedong.activity.VerificationActivity;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.LoadingCustom;
import com.appfree.newedong.common.MyLibs;
import com.appfree.newedong.common.SharePref;
import com.appfree.newedong.model.ContactManager;
import com.appfree.newedong.model.ContactModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tsy.plutusnative.callback.PlutusResultCallback;
import com.tsy.plutusnative.model.Contact;
import com.tsy.plutusnative.model.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class Step3Fragment extends Fragment implements View.OnClickListener, Animation.AnimationListener {

    private static final int REQUEST_GET_CONTACT_1 = 1102;
    private static final int REQUEST_GET_CONTACT_2 = 1103;
    @BindView(R.id.tv_name_person_1)
    TextView tvPerson1;
    @BindView(R.id.layout_step_1)
    RelativeLayout layoutPerson1;
    @BindView(R.id.tv_number__1)
    TextInputEditText tvPhoneNumber_1;
    @BindView(R.id.tv_name_contact_1)
    TextInputEditText tvName_1;
    @BindView(R.id.tv_relationship_contact_1)
    TextInputEditText tvRela_1;

    @BindView(R.id.tv_name_person_2)
    TextView tvPerson2;
    @BindView(R.id.layout_step_2)
    RelativeLayout layoutPerson2;
    @BindView(R.id.tv_number__2)
    TextInputEditText tvPhoneNumber_2;
    @BindView(R.id.tv_name_contact_2)
    TextInputEditText tvName_2;
    @BindView(R.id.tv_relationship_contact_2)
    TextInputEditText tvRela_2;

    @BindView(R.id.button_done)
    MaterialButton btnAccept;

    Animation animationBlink;
    private boolean isFirstContactSelected = false;
    private boolean isSecondContactSelected = false;
    private String userId;
    private ContactManager contactManager;
    private List<ContactModel> contactList;
    private LoadingCustom loadingCustom;
    private boolean isUploadAllContactSuccess = false;


    public Step3Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {

        if (getActivity() == null) {
            return;
        }
        animationBlink = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.blink);
        animationBlink.setAnimationListener(this);

        loadingCustom = new LoadingCustom(getActivity());
        layoutPerson1.setOnClickListener(this::onClick);
        layoutPerson2.setOnClickListener(this::onClick);
        btnAccept.setOnClickListener(this::onClick);

        tvPerson1.startAnimation(animationBlink);
        tvPerson2.startAnimation(animationBlink);

        getContact();


    }

    private void getContact() {
//        loadingCustom.showDialog();
        contactManager = new ContactManager(getContext());
        contactList = contactManager.getListContact();
        ArrayList<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {
            contacts.add(new Contact(contactList.get(i).getmName(), contactList.get(i).getmNumber()));
        }

        Log.d(Common.TAG_eDong, "list contact = " + contacts.size());


        userId = SharePref.read(SharePref.USER_ID_eDong, "");
        Log.d(Common.TAG_eDong, "Begin request");
        MyLibs.getInstance().getPlutusSDK().uploadContacts(userId,
                contacts,
                new PlutusResultCallback() {
                    @Override
                    public void onResult(String s, Result result) {
                        Log.d(Common.TAG_eDong, "s = " + s);
                        Log.d(Common.TAG_eDong, "result = " + result.getMessage());
                        loadingCustom.hideDialog();
                        if (result.getIsSuccess()) {
                            isUploadAllContactSuccess = true;
                        } else {
                            showDialog(result.getMessage(), Common.FAIL);
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_step_1:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_GET_CONTACT_1);
                break;

            case R.id.layout_step_2:
                Intent intent2 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent2, REQUEST_GET_CONTACT_2);
                break;

            case R.id.button_done:

                if (!checkEmpty(tvRela_1.getText().toString(), tvRela_1)) {
                    return;
                }

                if (!checkEmpty(tvRela_1.getText().toString(), tvRela_2)) {
                    return;
                }

                if (!isFirstContactSelected) {
                    showDialog(getResources().getString(R.string.step4_need_select_relatives), Common.FAIL);
                    return;

                } else if (!isSecondContactSelected) {
                    showDialog(getResources().getString(R.string.step4_need_select_relatives), Common.FAIL);
                    return;
                } else {

                    String nameUser1 = tvName_1.getText().toString();
                    String nameUser2 = tvName_2.getText().toString();
                    String number1 = tvPhoneNumber_1.getText().toString();
                    String number2 = tvPhoneNumber_2.getText().toString();
                    String relation1 = tvRela_1.getText().toString();
                    String relation2 = tvRela_2.getText().toString();
                    callApiVerifyReference(userId, nameUser1, number1, relation1,
                            nameUser2, number2, relation2);
                }


                break;

        }
    }

    private void callApiVerifyReference(String userId, String user1, String number1,
                                        String relation1, String user2,
                                        String number2, String relation2) {

        loadingCustom.showDialog();

        MyLibs.getInstance().getPlutusSDK().submitReferenceVerification(userId,
                number1,
                relation1,
                user1,
                number2,
                relation2,
                user2,
                new PlutusResultCallback() {
                    @Override
                    public void onResult(String s, Result result) {
                        loadingCustom.hideDialog();
                        if (result.getIsSuccess()){
                            showDialog(result.getMessage(), Common.SUCCESS);
                        } else {
                            showDialog(result.getMessage(), Common.FAIL);
                        }
                    }
                });


    }


    private boolean checkEmpty(String value, TextInputEditText textInputEditText) {
        if (value == null || value.isEmpty()) {
            textInputEditText.setError(getResources().getString(R.string.step4_need_relation));
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_GET_CONTACT_1) {
                contactPicked(data, REQUEST_GET_CONTACT_1);
            }
            if (requestCode == REQUEST_GET_CONTACT_2) {
                contactPicked(data, REQUEST_GET_CONTACT_2);
            }
        }
    }


    private void contactPicked(Intent data, int requestCode) {
        Uri uri = data.getData();
        Cursor cursor;
        String afterChosenContactName = "";
        String afterChosenContactPhone = "";
        ContentResolver cr = getActivity().getContentResolver();

        cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            afterChosenContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            if (hasPhone.equalsIgnoreCase("1")) {
                hasPhone = "true";
            } else {
                hasPhone = "false";
            }
            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + id, null, null);
                if (phones == null) return;
                while (phones.moveToNext()) {
                    afterChosenContactPhone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }
        }
        cursor.close();
        switch (requestCode) {
            case REQUEST_GET_CONTACT_1:
                tvPhoneNumber_1.setText(afterChosenContactPhone);
                tvName_1.setText(afterChosenContactName);
                isFirstContactSelected = true;
                break;
            case REQUEST_GET_CONTACT_2:
                tvPhoneNumber_2.setText(afterChosenContactPhone);
                tvName_2.setText(afterChosenContactName);
                isSecondContactSelected = true;
                break;
        }

    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void showDialog(String message, int type) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setTitle(R.string.alert_dialog);
        builder1.setMessage(message);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (type == Common.SUCCESS) {
                            dialog.cancel();
                            SharePref.write(SharePref.FIRST_FINISH_STEP_3, Common.DONE_eDong);
                            SharePref.write(SharePref.DONE_STEP_3_eDong, Common.DONE_eDong);
                            openMainVerifyFragment();
                        } else {
                            dialog.cancel();
                        }

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert11.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.color_4));
            }
        });
        alert11.show();
    }


    private void openMainVerifyFragment() {
        if (getFragmentManager() == null) {
            return;
        }
        int count = getFragmentManager().getBackStackEntryCount();
        if (count != 0)
            getFragmentManager().popBackStack();

        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
