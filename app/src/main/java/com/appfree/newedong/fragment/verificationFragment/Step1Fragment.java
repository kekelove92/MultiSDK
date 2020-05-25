package com.appfree.newedong.fragment.verificationFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.appfree.newedong.R;
import com.appfree.newedong.activity.VerificationActivity;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.ConvertMd5;
import com.appfree.newedong.common.LoadingCustom;
import com.appfree.newedong.common.MyLibs;
import com.appfree.newedong.common.SharePref;
import com.appfree.newedong.common.ShowCustomDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.annotations.JsonAdapter;
import com.tsy.plutusnative.callback.PlutusCallback;
import com.tsy.plutusnative.callback.PlutusResultCallback;
import com.tsy.plutusnative.model.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class Step1Fragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.img_bank_verify)
    ImageView imgBank;
    @BindView(R.id.layout_bank_name)
    TextInputLayout layoutNameBank;
    @BindView(R.id.edt_bank)
    AppCompatAutoCompleteTextView edtNameBank;
    @BindView(R.id.edt_number_account)
    TextInputEditText edtNumberBank;
    @BindView(R.id.edt_number_account_2)
    TextInputEditText edtReinputBank;
    @BindView(R.id.edt_name_account)
    TextInputEditText edtNameAccount;
    @BindView(R.id.edt_bank_account_department)
    TextInputEditText edtBankDepartment;
    @BindView(R.id.button_done)
    MaterialButton btnOk;

    private String[] banks;
    ArrayList<String> mylist;
    private LoadingCustom loadingCustom;
    private String userID;
    private ShowCustomDialog showCustomDialog;

    public Step1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        initView(view);
    }

    private void initView(View view) {

        loadingCustom = new LoadingCustom(getActivity());
        showCustomDialog = new ShowCustomDialog(getActivity());
        btnOk.setOnClickListener(this);
        userID = SharePref.read(SharePref.USER_ID_eDong, "");
        loadingCustom.showDialog();
        setValueForBank();

    }

    private void setValueForBank() {
        MyLibs.getInstance().getPlutusSDK().getBankList(new PlutusCallback() {
            @Override
            public void success(String s, @Nullable Object o) {
                loadingCustom.hideDialog();
                if (o == null) {
                    Toast.makeText(getContext(), getResources().getString(R.string.error_get_bank_list), Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(Common.TAG_eDong, "bank = " + o.toString());
                String obj = o.toString();

                try {
                    JSONArray bankArray = new JSONArray(obj);
                    mylist = new ArrayList<String>();
                    for (int i = 0; i < bankArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) bankArray.get(i);
                        String nameBank = jsonObject.getString("name");
                        mylist.add(nameBank);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                banks = new String[mylist.size()];
                banks = mylist.toArray(banks);
                if (getActivity() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.select_dialog_item,
                            banks);
                    edtNameBank.setThreshold(1);
                    edtNameBank.setAdapter(adapter);
                    Log.d(Common.TAG_eDong, "bank list = " + banks.length);
                }

            }
        });
    }


    private boolean checkEmpty(String value, TextInputEditText textInputEditText) {
        if (value == null || value.isEmpty()) {
            textInputEditText.setError(getResources().getString(R.string.error_empty_value));
            return false;
        }

        return true;
    }

    private boolean checkEmpty(String value, AutoCompleteTextView autoCompleteTextView) {
        if (value == null || value.isEmpty()) {
            autoCompleteTextView.setError(getResources().getString(R.string.error_empty_value));
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_done) {
            String numberAccount = edtNumberBank.getText().toString();
            String reNumberAccount = edtReinputBank.getText().toString();
            String nameAccount = edtNameAccount.getText().toString();
            String bankName = edtNameBank.getText().toString();
            String bankDepartment = edtBankDepartment.getText().toString();

            if (!reNumberAccount.equals(numberAccount)) {
                edtReinputBank.setError(getResources().getString(R.string.error_not_same));
                return;
            }
            if (checkEmpty(numberAccount, edtNumberBank) &&
                    checkEmpty(reNumberAccount, edtReinputBank) &&
                    checkEmpty(nameAccount, edtNameAccount) &&
                    checkEmpty(bankName, edtNameBank) &&
                    checkEmpty(bankDepartment, edtBankDepartment)) {


                loadingCustom.showDialog();
                Log.d(Common.TAG_eDong, numberAccount + "");
                Log.d(Common.TAG_eDong, reNumberAccount + "");
                Log.d(Common.TAG_eDong, nameAccount + "");
                Log.d(Common.TAG_eDong, bankName + "");
                Log.d(Common.TAG_eDong, bankDepartment + "");
                MyLibs.getInstance().getPlutusSDK().submitBankVerification(userID,
                        bankName,
                        bankDepartment,
                        nameAccount,
                        numberAccount,
                        new PlutusResultCallback() {
                            @Override
                            public void onResult(String s, Result result) {
                                loadingCustom.hideDialog();
                                if (result.getIsSuccess()) {
                                    String success = getString(R.string.verify_bank_success);
                                    SharePref.write(SharePref.NAME_USER_eDong, nameAccount);
                                    showDialog(success, Common.SUCCESS);
                                } else {
                                    String message = result.getMessage();
                                    showDialog(message, Common.FAIL);
                                }
                            }
                        });

            }

        }
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
                            SharePref.write(SharePref.FIRST_FINISH_STEP_1, Common.DONE_eDong);
                            SharePref.write(SharePref.DONE_STEP_1_eDong, Common.DONE_eDong);
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
