package com.appfree.newedong.fragment.stateFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.appfree.newedong.BuildConfig;
import com.appfree.newedong.HomeActivity;
import com.appfree.newedong.R;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.CustomInfo;
import com.appfree.newedong.common.LoadingCustom;
import com.appfree.newedong.common.MyLibs;
import com.appfree.newedong.common.OssInfo;
import com.appfree.newedong.common.SharePref;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.tsy.plutusnative.callback.PlutusCallback;
import com.tsy.plutusnative.callback.PlutusResultCallback;
import com.tsy.plutusnative.model.Result;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpVideoFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.lottie_upload_video)
    LottieAnimationView lottiUpload;
    @BindView(R.id.button_upload)
    MaterialButton btnUpLoad;
    private String mVideoPath;
    private File recordFile = null;
    private static final int VIDEO_CAPTURED = 1001;
    private LoadingCustom loadingCustom;
    private Context context;
    private String objectKey;
    private String userID;
    private String userName;
    private String loanValue;
    private boolean isUploadOssSuccess = false;
    private OssInfo ossInfo;
    private long timeStamp;


    public UpVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_up_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {

        userID = SharePref.read(SharePref.USER_ID_eDong, "");
        userName = SharePref.read(SharePref.NAME_USER_eDong, "");
        loanValue = CustomInfo.userInfoResult.getRequestedLoan();

//        loanValue = String.valueOf(SharePref.readInt(SharePref.LOAN_MONEY_eDong, -1));
//        loanValue = formatVnCurrence(loanValue);

        btnUpLoad.setOnClickListener(this);

        lottiUpload.playAnimation();
        lottiUpload.loop(true);



        loadingCustom = new LoadingCustom(getActivity());
        getOssInfo();

    }

    private void getOssInfo() {
        loadingCustom.showDialog();
        MyLibs.getInstance().getPlutusSDK().getOssInformation(new PlutusCallback() {
            @Override
            public void success(String s, @Nullable Object o) {
                loadingCustom.hideDialog();
                if (o != null) {
                    Log.d(Common.TAG_eDong, " o = " + o.toString());
                    ossInfo = new Gson().fromJson(o.toString(), OssInfo.class);
                    Log.d(Common.TAG_eDong, " json = " + ossInfo.toString());
                    Log.d(Common.TAG_eDong, "info oss = " + ossInfo.OS_BUCKET_NAME);
                    Log.d(Common.TAG_eDong, "info oss = " + ossInfo.OS_IMAGE_KEY_ID);
                    Log.d(Common.TAG_eDong, "info oss = " + ossInfo.OS_IMAGE_ENDPOINT);
                    Log.d(Common.TAG_eDong, "info oss = " + ossInfo.OS_IMAGE_KEY_SECRET);

                } else {
                    showDialog(s,Common.FAIL);
                }
            }

        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_upload:
//                checkPermissionVideo();
                String message = "Vui lòng khi quay video nói như sau: \n" + "Tôi tên là " + userName
                        + "\n" + "Tôi xác nhận đã vay thành công số tiền " + loanValue
                        + " từ ứng dụng " + getResources().getString(R.string.app_name);
                showDialogTutorialSpeak(message);
                break;
        }
    }

    public String formatVnCurrence(String price) {
        NumberFormat format =
                new DecimalFormat("#,###");// #,##0.00 ¤ (¤:// Currency symbol)
        price = format.format(Double.parseDouble(price));
        price = price.replace(".", ",");
        return price;
    }

    private void checkPermissionVideo() {
        if (getContext() == null) {
            return;
        }
        // request permission Camera and write external
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                            getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                String[] permission = {Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, VIDEO_CAPTURED);
            } else {
                // permission granted
                callRecordCamera();
            }
        } else {
            // OS device < M
            callRecordCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case VIDEO_CAPTURED: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callRecordCamera();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            getActivity(),
                            Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(
                            getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

//                        requestPermission();
//                        getActivity().finish();
                        Toast.makeText(getContext(), R.string.need_permission_capture, Toast.LENGTH_SHORT).show();
                    } else {
//                        getActivity().finish();
                        Toast.makeText(getContext(), R.string.need_permission_capture, Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
        }
    }

    private void callRecordCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        try {

            recordFile = createImageFileHand();
            Log.d(Common.TAG_eDong, " recordFile  =  " + recordFile.getAbsolutePath());

            // Continue only if the File was successfully created
            if (recordFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        recordFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                startActivityForResult(intent, VIDEO_CAPTURED);
            }
        } catch (Exception ex) {
            // Error occurred while creating the File
            Log.d(Common.TAG_eDong, " photo ex =  " + ex.toString());
        }
    }

    private File createImageFileHand() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MP4_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mVideoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURED) {

            long length = recordFile.length();
            timeStamp = System.currentTimeMillis();

            objectKey = SharePref.read(SharePref.USER_ID_eDong, "")
                    + "=" + timeStamp + ".mp4";

            if (length > 0) {
                loadingCustom.showDialog();
                length = length / 1024;
                Log.d(Common.TAG_eDong, " size = " + length + " KB");
                callUploadVideo(objectKey);
            }


//            requestOss(mVideoPath);
        }
    }

    private void requestOss(String videoPath) {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
        conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
        conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
        conf.setMaxErrorRetry(2); // retry，default 2
        OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv

        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                ossInfo.OS_IMAGE_KEY_ID,  ossInfo.OS_IMAGE_KEY_SECRET);

        if (getContext() != null) {
            context = getContext();
        }
        objectKey = SharePref.read(SharePref.USER_ID_eDong, "")
                + "=" + timeStamp + ".mp4";

        Log.d(Common.TAG_eDong, " object key = " + objectKey);

        // Construct an upload request
        PutObjectRequest put = new PutObjectRequest( ossInfo.OS_BUCKET_NAME,
                objectKey,
                videoPath);

        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d(CommonData.TAG, "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });


        OSSClient ossClient = new OSSClient(context,  ossInfo.OS_IMAGE_ENDPOINT,
                credentialProvider,
                conf);

        Log.d(Common.TAG_eDong, "image Path = " + videoPath);
        ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d(Common.TAG_eDong, "result = " + result.toString());
                loadingCustom.hideDialog();
                SharePref.write(SharePref.VERIFY_VIDEO_eDong, Common.DONE_eDong);
                goHomeActivity();

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                loadingCustom.hideDialog();
                Toast.makeText(context, "" + clientException.toString(), Toast.LENGTH_SHORT).show();
                Log.d(Common.TAG_eDong, "Upload onFailure = " + clientException.toString());
                Log.d(Common.TAG_eDong, "Upload onFailure = " + serviceException.toString());
            }
        });

    }

    private void callUploadVideo(String objectKey) {
        Log.d(Common.TAG_eDong, "callUploadVideo");
        Log.d(Common.TAG_eDong, "userid = " + userID);
        Log.d(Common.TAG_eDong, "objectKey = " + objectKey);

        MyLibs.getInstance().getPlutusSDK().uploadVideo(userID,
                objectKey,
                new PlutusResultCallback() {
                    @Override
                    public void onResult(String s, Result result) {
                        Log.d(Common.TAG_eDong, "result = " + result.getIsSuccess());
                        if (result.getIsSuccess()) {
                            showDialog(result.getMessage(), Common.SUCCESS);
                        } else {
                            showDialog(result.getMessage(), Common.FAIL);
                        }
                    }
                });
    }

    private void showDialog(String message, final int code) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle(R.string.alert_dialog);
        builder1.setMessage(message);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (code == Common.SUCCESS) {
                            requestOss(mVideoPath);
                        } else {
                            dialog.dismiss();
                        }
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

    private void showDialogTutorialSpeak(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle(R.string.alert_dialog);
        builder1.setMessage(message);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        checkPermissionVideo();
                    }
                });
        builder1.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert11.getButton(BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color_4));
                alert11.getButton(BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_1));
            }
        });
        alert11.show();
    }


    private void goHomeActivity() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
