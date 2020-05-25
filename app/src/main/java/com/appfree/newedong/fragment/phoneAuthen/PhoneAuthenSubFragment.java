package com.appfree.newedong.fragment.phoneAuthen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appfree.newedong.HomeActivity;
import com.appfree.newedong.R;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.ConvertMd5;
import com.appfree.newedong.common.LoadingCustom;
import com.appfree.newedong.common.MyLibs;
import com.appfree.newedong.common.SharePref;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tsy.plutusnative.PlutusSDK;
import com.tsy.plutusnative.callback.LoginCallback;
import com.tsy.plutusnative.callback.PlutusResultCallback;
import com.tsy.plutusnative.model.LoginResult;
import com.tsy.plutusnative.model.Result;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneAuthenSubFragment extends Fragment {


    @BindView(R.id.pinView)
    PinView pinView;

    private int otpCount;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerifyId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mPhoneNumber;
    private String mPhoneMD5;
    private String mPhoneMD5RequestCode;
    private String mOtpType;
    private LoadingCustom loadingCustom;
    private PlutusSDK plutusSDK;
    private String mPhoneModel;

    public PhoneAuthenSubFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPhoneNumber = getArguments().getString("phone");
        mPhoneMD5 = getArguments().getString("phone_md5");
        mPhoneMD5RequestCode = getArguments().getString("request_code_phone_md5");
        mOtpType = getArguments().getString("otp_type");
        Log.d(Common.TAG_eDong, "OTP = " + mOtpType);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_authen_sub, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        initView(view);
    }


    private void initView(View view) {

        loadingCustom = new LoadingCustom(getActivity());

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(Common.TAG_eDong, "onVerificationCompleted:" + phoneAuthCredential);
                String code = phoneAuthCredential.getSmsCode();
                verifyPhoneNumberWithCode(code);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(Common.TAG_eDong, "FirebaseException:" + e);
            }

            @Override
            public void onCodeSent(@NonNull String s,
                                   @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerifyId = s;
                mResendToken = forceResendingToken;

            }

        };



        switch (mOtpType) {
            case Common.FIRE_BASE_OTP:
                Log.d(Common.TAG_eDong, "FIRE_BASE_OTP");
                startPhoneNumberVerification(mPhoneNumber);
                break;

            case Common.BACK_END_OTP:
                Log.d(Common.TAG_eDong, "BACK_END_OTP");
                loadingCustom.showDialog();
                callApiRequestOTP();
                break;

            case Common.UNKNOW_OTP:
                Log.d(Common.TAG_eDong, "UNKNOW");
                loadingCustom.showDialog();
                callApiLogin(mPhoneNumber);
                break;
        }

        initPinView(mOtpType);

    }

    private void initPinView(String otpType){
        switch (otpType){
            case Common.FIRE_BASE_OTP:
                otpCount = 6;
                break;

            case Common.BACK_END_OTP:
                otpCount = 4;
                break;

        }

        pinView.setItemCount(otpCount);
        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == otpCount) {
                    Log.d(Common.TAG_eDong, "s = " + s);
                    switch (otpType){
                        case Common.FIRE_BASE_OTP:
                            loadingCustom.showDialog();
                            verifyPhoneNumberWithCode(s.toString());
                            break;

                        case Common.BACK_END_OTP:
                            loadingCustom.showDialog();
                            MyLibs.getInstance().getPlutusSDK().verifyBackendOtp(
                                    mPhoneNumber, s.toString(),
                                    mPhoneModel, new PlutusResultCallback() {
                                        @Override
                                        public void onResult(String s, Result result) {
                                            if (result.getCode() != Common.SUCCESS){
                                                loadingCustom.hideDialog();
                                                showDialog(result.getMessage());
                                            } else {
                                                callApiLogin(mPhoneNumber);
                                            }
                                        }
                                    });
                            break;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void verifyPhoneNumberWithCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerifyId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(Common.TAG_eDong, "signInWithCredential:success");
                            String mThreeNumber = mPhoneNumber.substring(0, 3);
                            callApiLogin(mPhoneNumber);

                        } else {
                            // Sign in failed, display a message and update the UI
                            loadingCustom.hideDialog();
                            Log.d(Common.TAG_eDong, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        Log.d(Common.TAG_eDong, "send code to phone");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    private void OpenHomeActivity() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setTitle(R.string.alert_dialog);
        builder1.setMessage(message);
        builder1.setCancelable(false);

//        builder1.setPositiveButton(
//                "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void callApiLogin(String phone) {
        mPhoneModel = getDeviceName();
        Log.d(Common.TAG_eDong, "phone model = " + mPhoneModel);
        MyLibs.getInstance().getPlutusSDK().login(phone, mPhoneModel, new LoginCallback() {
            @Override
            public void onResponse(LoginResult loginResult) {
                if (getActivity() == null){
                    Toast.makeText(getContext(), "getActivity = null", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(Common.TAG_eDong, "loginResult = " + loginResult);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingCustom.hideDialog();
                        if (loginResult.getCode() == Common.SUCCESS){
                            String md5UserId = ConvertMd5.getMD5(loginResult.getUserId(),Common.MD5_TYPE_USER_ID);
                            mPhoneMD5 = ConvertMd5.getMD5(mPhoneNumber,Common.MD5_TYPE_PHONE);

                            SharePref.write(SharePref.NUMBER_eDong,mPhoneNumber);
                            SharePref.write(SharePref.USER_ID_eDong, loginResult.getUserId());
                            SharePref.write(SharePref.MD5_USER_ID_eDong,md5UserId);
                            SharePref.write(SharePref.MD5_PHONE_eDong, mPhoneMD5);
                            OpenHomeActivity();
                        } else {
                            showDialog(loginResult.getMessage());
                        }
                    }
                });



            }
        });
    }

    private void callApiRequestOTP(){
        MyLibs.getInstance().getPlutusSDK().requestBackendOtp(mPhoneNumber, new PlutusResultCallback() {
            @Override
            public void onResult(String s, Result result) {
                loadingCustom.hideDialog();
                Log.d(Common.TAG_eDong, "s = " + s);
                Log.d(Common.TAG_eDong, "result = " + result);
                if (result.getCode() != Common.SUCCESS){
                    showDialog(result.getMessage());
                }
            }
        });
    }

    /** Returns device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }


}
