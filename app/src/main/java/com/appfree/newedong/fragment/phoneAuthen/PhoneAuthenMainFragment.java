package com.appfree.newedong.fragment.phoneAuthen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appfree.newedong.R;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.ConvertMd5;
import com.appfree.newedong.common.LoadingCustom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneAuthenMainFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.edt_number)
    TextInputEditText mEdtPhoneNumber;
    @BindView(R.id.btn_next)
    MaterialButton mBtnNext;

    public static final String OTP_BACK_END = "1";
    public static final String OTP_FIRE_BASE = "2";
    private String mOtpType = "2";
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static final String OTP_TYPE_KEY = "otp_type";
    private String mPhoneNumber;
    private FirebaseAuth mAuth;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mPhoneMD5RequestCode;
    private String mPhoneMD5;
    private LoadingCustom loadingCustom;

    public PhoneAuthenMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_authen_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initView();
    }


    private void initView() {

        loadingCustom = new LoadingCustom(getActivity());
        mBtnNext.setOnClickListener(this);

        mEdtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhoneNumber = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        loadingCustom.showDialog();
        getRemoteConfig();

    }

    private void getRemoteConfig() {
        if (getActivity() == null){
            Toast.makeText(getContext(), "Activity Null", Toast.LENGTH_SHORT).show();
            return;
        }

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // enable dev mode
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful() && task.getResult()!= null) {
                            boolean updated = task.getResult();
                            Log.d(Common.TAG_eDong, "Config params updated: " + updated);
                        } else {
                            Log.d(Common.TAG_eDong, "Fetch failed");
                        }
                        // get config value
                        mOtpType = mFirebaseRemoteConfig.getString(OTP_TYPE_KEY);
                        Log.d(Common.TAG_eDong, "OTP type = " + mOtpType);
                    }
                });

        loadingCustom.hideDialog();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:

                if (mPhoneNumber == null ||
                        mPhoneNumber.length() != 10 ||
                        !String.valueOf(mPhoneNumber.charAt(0)).equals("0")) {
                    mEdtPhoneNumber.setError(getResources().getString(R.string.number_incorrect));
                } else {
                    mPhoneMD5 = ConvertMd5.getMD5(mPhoneNumber,Common.MD5_TYPE_PHONE);
                    mPhoneMD5RequestCode = ConvertMd5.getMD5(mPhoneNumber,Common.MD5_TYPE_USER_ID);
                    openOTPVerifyFragment();
                }


                break;

        }
    }

    private void openOTPVerifyFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("phone",mPhoneNumber);
        bundle.putString("phone_md5",mPhoneMD5);
        bundle.putString("request_code_phone_md5",mPhoneMD5RequestCode);
        bundle.putString("otp_type", mOtpType);
        Fragment fragment = new PhoneAuthenSubFragment();
        fragment.setArguments(bundle);
        if (getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, fragment);
            transaction.addToBackStack(null);
//            transaction.disallowAddToBackStack();
            transaction.commit();
        }
    }
}
