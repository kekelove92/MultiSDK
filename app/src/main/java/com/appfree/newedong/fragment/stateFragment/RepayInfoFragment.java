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
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.tsy.plutusnative.utils.PlutusContrants.getOssInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class RepayInfoFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.tv_repay_date)
    TextView tvDate;
    @BindView(R.id.tv_repay_date_delay)
    TextView tvDateDelay;
    @BindView(R.id.tv_repay_fee)
    TextView tvFee;
    @BindView(R.id.tv_repay_total_money)
    TextView tvTotal;
    @BindView(R.id.tv_tutorial_loan)
    TextView tvLoanTutorial;
    @BindView(R.id.tv_delete_loan)
    TextView tvDeleteLoan;
    @BindView(R.id.button_upload_repay)
    Button btnUpload;
    @BindView(R.id.lottie_repay)
    LottieAnimationView lottieRepay;


    private LoadingCustom loadingCustom;
    private String objectKey;
    private File photoFile;
    private String picturePath;
    public static final int CAPTURE_REQUEST = 1120;
    private Context context;
    private boolean isUploadSuccess = false;
    private int codeStatus;
    String userID;
    private OssInfo ossInfo;

    public RepayInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        codeStatus = getArguments().getInt("status");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repay_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {
        loadingCustom = new LoadingCustom(getActivity());
        lottieRepay.playAnimation();
        lottieRepay.loop(true);

        userID = SharePref.read(SharePref.USER_ID_eDong, "");
        btnUpload.setOnClickListener(this);
        getRepayInfo();
        getInfoOss();

    }

    private void getInfoOss() {
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

                }
            }

        });

    }

    private void getRepayInfo() {
        String tempTime = CustomInfo.getInstance().userInfoResult.getExpiredTime();
        String[] abc = tempTime.split(" ");
        String expriedTime = abc[0];

        String dayCount = CustomInfo.getInstance().userInfoResult.getExpiredDayCount() + " ngày";

        String expriedFee = CustomInfo.getInstance().userInfoResult.getExpiredFee();
        int expriedFeeInt = Integer.parseInt(removeSpecialCharacter(expriedFee));

        String loanValue = CustomInfo.getInstance().userInfoResult.getRequestedLoan();
        int loanValueInt = Integer.parseInt(removeSpecialCharacter(loanValue));

        String totalValue = formatVnCurrence(String.valueOf(expriedFeeInt + loanValueInt));

        tvDate.setText(expriedTime);
        tvDateDelay.setText(dayCount);
        tvFee.setText(expriedFee);
        tvTotal.setText(totalValue);

        if (codeStatus == Common.UPLOADED_REPAY_BILL) {
            tvDeleteLoan.setVisibility(View.VISIBLE);
            tvLoanTutorial.setVisibility(View.GONE);
            btnUpload.setVisibility(View.GONE);
        }

        String uploadRepay = SharePref.read(SharePref.VERIFY_REPAY_BILL_eDong, "");
        Log.d(Common.TAG_eDong, "uploadRepay = " + uploadRepay);
        Log.d(Common.TAG_eDong, "codeStatus = " + codeStatus);
        if (codeStatus == Common.NOT_UPLOAD_REPAY_BILL
                && uploadRepay != null
                && !uploadRepay.isEmpty()){
            showDialogReject(getResources().getString(R.string.reject_repay));
            SharePref.write(SharePref.VERIFY_REPAY_BILL_eDong, "");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_upload_repay:
                checkPermission();
                break;
        }
    }

    private void checkPermission() {
        // request permission Camera and write external
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                            getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                String[] permission = {Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, CAPTURE_REQUEST);
            } else {
                // permission granted
                callOpenCamera();
            }
        } else {
            // OS device < M
            callOpenCamera();
        }
    }

    private void callOpenCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {

            photoFile = createImageFile();
            Log.d(Common.TAG_eDong, " photo file =  " + photoFile.getAbsolutePath());

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_REQUEST);
            }
        } catch (Exception ex) {
            // Error occurred while creating the File
            Log.d(Common.TAG_eDong, " photo ex =  " + ex.toString());
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        picturePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAPTURE_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callOpenCamera();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_REQUEST) {
            objectKey = SharePref.read(SharePref.USER_ID_eDong, "")
                    + "=" + System.currentTimeMillis() + ".jpg";
            long length = photoFile.length();
            if (length > 0){
                loadingCustom.showDialog();
                callApiUploadRepayBill(userID, objectKey);
            }
//            requestOSS(picturePath);
        }
    }

    private void requestOSS(String picturePath) {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
        conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
        conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
        conf.setMaxErrorRetry(2); // retry，default 2
        OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv


        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                ossInfo.OS_IMAGE_KEY_ID, ossInfo.OS_IMAGE_KEY_SECRET);

        if (getContext() != null) {
            context = getContext();
        }

        Log.d(Common.TAG_eDong, "obj Key = " + objectKey);

        // Construct an upload request
        PutObjectRequest put = new PutObjectRequest( ossInfo.OS_BUCKET_NAME,
                objectKey,
                picturePath);

        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }
        });

        OSSClient ossClient = new OSSClient(context,  ossInfo.OS_IMAGE_ENDPOINT,
                credentialProvider,
                conf);

        ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d(Common.TAG_eDong, "result = " + result.toString());
                Log.d(Common.TAG_eDong, " UploadSuccess");
                isUploadSuccess = true;
                loadingCustom.hideDialog();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvDeleteLoan.setVisibility(View.VISIBLE);
                        tvLoanTutorial.setVisibility(View.GONE);
                        btnUpload.setVisibility(View.GONE);
                    }
                });

                SharePref.write(SharePref.VERIFY_REPAY_BILL_eDong, "success");
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                loadingCustom.hideDialog();
                Toast.makeText(getContext(), "" + clientException.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "" + serviceException.toString(), Toast.LENGTH_SHORT).show();
                Log.d(Common.TAG_eDong, "Upload onFailure = " + clientException.toString());
                Log.d(Common.TAG_eDong, "Upload onFailure = " + serviceException.toString());
            }
        });

    }

    private void callApiUploadRepayBill(String userId, String objectKey) {
        MyLibs.getInstance().getPlutusSDK().uploadRepayBill(userId,
                objectKey,
                new PlutusResultCallback() {
                    @Override
                    public void onResult(String s, Result result) {
                        if (result.getIsSuccess()){
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
                            dialog.dismiss();
                            requestOSS(picturePath);
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

    private void showDialogReject(String message) {
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

    private void goHomeActivity() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private String removeSpecialCharacter(String s) {
        s = s.replace(",", "");
        return s;
    }

    public String formatVnCurrence(String price) {
        NumberFormat format =
                new DecimalFormat("#,###");// #,##0.00 ¤ (¤:// Currency symbol)
        price = format.format(Double.parseDouble(price));
        price = price.replace(".", ",");
        return price;
    }
}
