package com.appfree.newedong.fragment.supportView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.appfree.newedong.R;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.LoadingCustom;
import com.appfree.newedong.common.MyLibs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CskhSupportFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.tv_info_support)
    TextView tvInfoSupport;
    @BindView(R.id.btn_hotline)
    Button btnHotLine;
    @BindView(R.id.btn_fb)
    Button btnFb;
    @BindView(R.id.btn_zalo)
    Button btnZalo;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static final String HELP_SUPPORT = "support_customer";
    public static final String HELP_SUPPORT_NUMBER = "support_customer_number";
    public static final String HELP_SUPPORT_ZALO = "support_customer_zalo";
    public static final String HELP_SUPPORT_FB = "support_customer_fb";
    private LoadingCustom loadingCustom;
    private String linkFb;
    private String linkZalo;
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private String telephone;
    private String infoSupport;
    private int REQUEST_CALL = 1020;


    public CskhSupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_c_s_k_h, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);
        initView();
    }

    private void initView() {

        loadingCustom = new LoadingCustom(getActivity());
        btnFb.setOnClickListener(this::onClick);
        btnHotLine.setOnClickListener(this::onClick);
        btnZalo.setOnClickListener(this::onClick);

        clipboardManager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);

        loadingCustom.showDialog();
        getRemoteConfig();

    }

    private void getRemoteConfig() {
        // get config instance
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // enable dev mode
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        // fetch config with callback
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        loadingCustom.hideDialog();
                        if (task.isSuccessful() && task.getResult() != null) {
                            boolean updated = task.getResult();
                            Log.d(Common.TAG_eDong, "Config params updated: " + updated);
                        } else {
                            Log.d(Common.TAG_eDong, "Fetch failed");
                        }
                        // get config value
                        telephone = mFirebaseRemoteConfig.getString(HELP_SUPPORT_NUMBER);
                        infoSupport = mFirebaseRemoteConfig.getString(HELP_SUPPORT);
                        linkFb = mFirebaseRemoteConfig.getString(HELP_SUPPORT_FB);
                        linkZalo = mFirebaseRemoteConfig.getString(HELP_SUPPORT_ZALO);
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        loadingCustom.hideDialog();
        tvInfoSupport.setText(infoSupport);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hotline:
                if (telephone == null || telephone.isEmpty()) {
                    showDialogAlert(getResources().getString(R.string.support_additional_later));
                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_DIAL); //use ACTION_CALL class
                callIntent.setData(Uri.parse("tel:" + telephone));
                getActivity().startActivity(callIntent);
                break;

            case R.id.btn_zalo:
                if (linkZalo == null || linkZalo.isEmpty()) {
                    showDialogAlert(getResources().getString(R.string.support_additional_later));
                    return;
                }

                copyToClipboard(getContext(), linkZalo);
                Toast.makeText(getContext(), R.string.zalo_copied, Toast.LENGTH_SHORT).show();

                break;

            case R.id.btn_fb:
                if (linkFb == null || linkFb.isEmpty()) {
                    showDialogAlert(getResources().getString(R.string.support_additional_later));
                    return;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkFb));
                startActivity(browserIntent);
                break;

        }
    }

    private void showDialogAlert(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle(R.string.alert_dialog);
        builder1.setMessage(message);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert11.getButton(BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color_4));
            }
        });
        alert11.show();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private boolean copyToClipboard(Context context, String text) {
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText(
                                context.getResources().getString(
                                        R.string.message), text);
                clipboard.setPrimaryClip(clip);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
