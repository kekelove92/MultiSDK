package com.appfree.newedong.fragment.verificationFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appfree.newedong.HomeActivity;
import com.appfree.newedong.R;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.LoadingCustom;
import com.appfree.newedong.common.SharePref;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepMainFragment extends Fragment implements View.OnClickListener {
    private LoadingCustom loadingCustom;
    private String userId;
    private boolean isBankVerified = false;
    private boolean isIdVerified = false;
    private boolean isReferenceVerified = false;
    private String mDoneStep1;
    private String mDoneStep2;
    private String mDoneStep3;
    public static final int PERMISSION_CAMERA_CODE = 1221;
    public static final int PERMISSION_CONTACT_CODE = 1331;

    @BindView(R.id.layout_bank)
    RelativeLayout layoutBank;
    @BindView(R.id.layout_id_card)
    RelativeLayout layoutId;
    @BindView(R.id.layout_reference)
    RelativeLayout layoutReference;
    @BindView(R.id.img_bank)
    ImageView imgBank;
    @BindView(R.id.img_information)
    ImageView imgInfo;
    @BindView(R.id.img_reference)
    ImageView imgReference;

    @BindView(R.id.tv_bank_verify)
    TextView tvBank;
    @BindView(R.id.tv_id_verify)
    TextView tvIdVerify;
    @BindView(R.id.tv_person)
    TextView tvReference;


    public StepMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        initView(view);
    }

    private void initView(View view) {

        layoutId.setOnClickListener(this);
        layoutBank.setOnClickListener(this);
        layoutReference.setOnClickListener(this);

        loadingCustom = new LoadingCustom(getActivity());
        userId = SharePref.read(SharePref.USER_ID_eDong, "");

       updateView();

    }

    private void updateView(){
        mDoneStep1 = SharePref.read(SharePref.DONE_STEP_1_eDong, "");
        if (mDoneStep1 != null && !mDoneStep1.isEmpty()) {
            tvBank.setTextColor(getResources().getColor(R.color.gray));
            imgBank.setImageDrawable(getResources().getDrawable(R.drawable.icon_done));
            layoutBank.setEnabled(false);
            isBankVerified = true;
        }

        mDoneStep2 = SharePref.read(SharePref.DONE_STEP_2_eDong, "");
        if (mDoneStep2 != null && !mDoneStep2.isEmpty()) {
            tvIdVerify.setTextColor(getResources().getColor(R.color.gray));
            imgInfo.setImageDrawable(getResources().getDrawable(R.drawable.icon_done));
            layoutId.setEnabled(false);
            isIdVerified = true;
        }

        mDoneStep3 = SharePref.read(SharePref.DONE_STEP_3_eDong, "");
        Log.d(Common.TAG_eDong, "mDone3 = " + mDoneStep3);
        if (mDoneStep3 != null && !mDoneStep3.isEmpty()) {
            tvReference.setTextColor(getResources().getColor(R.color.gray));
            imgReference.setImageDrawable(getResources().getDrawable(R.drawable.icon_done));
            layoutReference.setEnabled(false);
            isReferenceVerified = true;
        }

        if (isBankVerified && isIdVerified && isReferenceVerified){
            Intent intent = new Intent(getContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(Common.TAG_eDong, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(Common.TAG_eDong, "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
        Log.d(Common.TAG_eDong, "onResume");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_bank:
                openStep1Fragment();
                break;

            case R.id.layout_id_card:
//                openStep2Fragment();
                requestPermission();
                break;

            case R.id.layout_reference:
                if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        getContext(),Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    openStep3Fragment();
                } else {
                    showDialogRequestContact(getResources().getString(R.string.step4_message_request_contact));
                }
                break;
        }
    }

    private void openStep1Fragment() {
        if (getActivity() == null) {
            return;
        }

        Fragment fragment = new Step1Fragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openStep2Fragment() {
        if (getActivity() == null) {
            return;
        }

        Fragment fragment = new Step2Fragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openStep3Fragment() {
        if (getActivity() == null) {
            return;
        }

        Fragment fragment = new Step3Fragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void requestPermission() {
        // request permission Camera and write external
        if (getContext() == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                            getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                String[] permission = {Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CAMERA_CODE);
            } else {
                // permission granted
                openStep2Fragment();
            }
        } else {
            // OS device < M
            openStep2Fragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openStep2Fragment();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            getActivity(),
                            Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(
                            getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

//                        requestPermission();
                        getActivity().finish();
                        Toast.makeText(getContext(), R.string.need_permission_capture, Toast.LENGTH_SHORT).show();
                    } else {
                        getActivity().finish();
                        Toast.makeText(getContext(), R.string.need_permission_capture, Toast.LENGTH_SHORT).show();
                    }

                }
                break;

            case PERMISSION_CONTACT_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openStep3Fragment();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            getActivity(),
                            Manifest.permission.READ_CONTACTS) && ActivityCompat.shouldShowRequestPermissionRationale(
                            getActivity(), Manifest.permission.WRITE_CONTACTS)) {
                        getActivity().finish();
                        Toast.makeText(getContext(), R.string.step4_need_permission_contact, Toast.LENGTH_SHORT).show();
                    } else {
                        getActivity().finish();
                        Toast.makeText(getContext(), R.string.step4_need_permission_contact, Toast.LENGTH_SHORT).show();
                    }

                }
                break;

        }
    }

    private void requestPermissionContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                            getContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                String[] permission = {Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS};
                requestPermissions(permission, PERMISSION_CONTACT_CODE);
            } else {
                // permission granted
                openStep3Fragment();
            }
        } else {
            // OS device < M
            openStep3Fragment();
        }
    }

    private void showDialogRequestContact(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.alert_dialog);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(
                R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionContact();
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(
                R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color_1));
                alertDialog.getButton(BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_4));
            }
        });
        alertDialog.show();
    }


}
