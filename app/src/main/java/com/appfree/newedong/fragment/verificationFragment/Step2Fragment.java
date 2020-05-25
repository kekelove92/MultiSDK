package com.appfree.newedong.fragment.verificationFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.appfree.newedong.R;
import com.appfree.newedong.activity.VerificationActivity;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.LoadingCustom;
import com.appfree.newedong.common.MyLibs;
import com.appfree.newedong.common.OssInfo;
import com.appfree.newedong.common.SharePref;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;
import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog;
import com.tsy.plutusnative.callback.PlutusCallback;
import com.tsy.plutusnative.callback.PlutusResultCallback;
import com.tsy.plutusnative.model.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class Step2Fragment extends Fragment implements View.OnClickListener, SupportedDatePickerDialog.OnDateSetListener {


    private TextView mTvBday;
    private TextView mTvNext;
    private ImageView mImgBack;
    private String bday;
    private Calendar calendar;
    private int mYear;
    private int mMonth;
    private int mDay;


    @BindView(R.id.button_done)
    Button btnDone;
    @BindView(R.id.layout_front)
    RelativeLayout layoutFrontCMND;
    @BindView(R.id.layout_back)
    RelativeLayout layoutBackCMND;
    @BindView(R.id.layout_hand)
    RelativeLayout layoutHandCMND;
    @BindView(R.id.layout_bday)
    RelativeLayout layoutMainBDay;

    @BindView(R.id.img_front_camera)
    ImageView imgFront;
    @BindView(R.id.img_back_camera)
    ImageView imgBack;
    @BindView(R.id.img_hand_camera)
    ImageView imgHand;

    @BindView(R.id.edt_id_number)
    TextInputEditText mIdNumber;
    @BindView(R.id.edt_id_andress)
    TextInputEditText mIdAndress;
    @BindView(R.id.layout_click)
    RelativeLayout layoutBday;
    @BindView(R.id.edt_id_date)
    TextInputEditText edtBDay;

    private static final int PERMISSION_CODE = 2000;
    private static final int FRONT_CODE = 110;
    private static final int BACK_CODE = 111;
    private static final int HAND_CODE = 112;
    private String picFrontPath;
    private String picBackPath;
    private String picHandPath;
    File photoFile = null;
    private Context context;
    private String objKey;
    private String keyFront;
    private String keyBack;
    private String keyHand;
    private boolean isUpImgFrontSuccess;
    private boolean isUpImgBackSuccess;
    private boolean isUpImgHandSuccess;
    Uri uriFront;
    Uri uriBack;
    Uri uriHand;

    String userID;
    OssInfo ossInfo;
    private long timeStamp;
    Bitmap bitmap_front;
    Bitmap bitmap_back;
    Bitmap bitmap_hand;

    private LoadingCustom loadingCustom;


    public Step2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {

        userID = SharePref.read(SharePref.USER_ID_eDong, "");
        loadingCustom = new LoadingCustom(getActivity());
        btnDone.setOnClickListener(this::onClick);
        layoutBday.setOnClickListener(this::onClick);
        layoutMainBDay.setOnClickListener(this::onClick);
        layoutFrontCMND.setOnClickListener(this::onClick);
        layoutBackCMND.setOnClickListener(this::onClick);
        layoutHandCMND.setOnClickListener(this::onClick);

        layoutBday.bringToFront();

        getInfoOSS();

    }

    private void getInfoOSS() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_done:

                String number = mIdNumber.getText().toString();
                boolean isNumberOK = checkEmpty(number, mIdNumber);
                String address = mIdAndress.getText().toString();
                boolean isAndressOK = checkEmpty(address, mIdAndress);
                bday = edtBDay.getText().toString();

                if (edtBDay == null || edtBDay.getText().toString().equals("")) {
                    Toast.makeText(getContext(), getResources().getText(R.string.step2_bday_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isNumberLengthOK;
                if (number.length() >= 9) {
                    isNumberLengthOK = true;
                } else {
                    mIdNumber.setError(getResources().getString(R.string.verify_number_id));
                    isNumberLengthOK = false;
                }

                if (!isUpImgFrontSuccess) {
                    Toast.makeText(getContext(), R.string.step3_upload_image_front_fail, Toast.LENGTH_SHORT).show();
                } else if (!isUpImgBackSuccess) {
                    Toast.makeText(getContext(), R.string.step3_upload_image_back_fail, Toast.LENGTH_SHORT).show();
                } else if (!isUpImgHandSuccess) {
                    Toast.makeText(getContext(), R.string.step3_upload_image_hand_fail, Toast.LENGTH_SHORT).show();
                } else if (isAndressOK && isNumberOK && isNumberLengthOK) {
                    loadingCustom.showDialog();
                    callApiVerifyIdInformation(userID, keyFront, keyBack, keyHand, number, address, bday);

                }

                break;

            case R.id.layout_click:
            case R.id.edt_id_date:
            case R.id.layout_bday:

                calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);

                // new style
                SupportedDatePickerDialog supportedDatePickerDialog = new SupportedDatePickerDialog(
                        getContext(),
                        R.style.SpinnerDatePickerDialogTheme,
                        this::onDateSet,
                        mYear,
                        mMonth,
                        mDay
                );

                supportedDatePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        supportedDatePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                                .setTextColor(getResources().getColor(R.color.color_4));


                        supportedDatePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                                .setTextColor(getResources().getColor(R.color.color_1));

                    }
                });

                supportedDatePickerDialog.show();

                // old style
//                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
//                        AlertDialog.THEME_HOLO_LIGHT, // spinner date picker dialog
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//
//                                Calendar calendar = Calendar.getInstance();
//                                calendar.set(year,monthOfYear,dayOfMonth);
//                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//                                String strDate = format.format(calendar.getTime());
//                                edtBDay.setText(strDate);
//
//                            }
//                        }, mYear, mMonth, mDay);
//                datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                    @Override
//                    public void onShow(DialogInterface dialog) {
//                        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
//                                .setTextColor(getResources().getColor(R.color.color_4));
//
//                        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
//                                .setTextColor(getResources().getColor(R.color.color_1));
//                    }
//                });
//
//                datePickerDialog.show();
                break;

            case R.id.layout_front:
                callOpenCamera(FRONT_CODE);
                break;

            case R.id.layout_back:
                callOpenCamera(BACK_CODE);
                break;

            case R.id.layout_hand:
                callOpenCamera(HAND_CODE);
                break;
        }
    }

    private void callApiVerifyIdInformation(String userID, String keyFront, String keyBack, String keyHand, String number, String address, String bday) {
        MyLibs.getInstance().getPlutusSDK().submitIdentityVerification(userID,
                number,
                bday,
                address,
                keyFront,
                keyBack,
                keyHand,
                new PlutusResultCallback() {
                    @Override
                    public void onResult(String s, Result result) {
                        loadingCustom.hideDialog();
                        if (result.getIsSuccess()) {
                            showDialog(result.getMessage(), Common.SUCCESS);
                        } else {
                            showDialog(result.getMessage(), Common.FAIL);
                        }
                    }
                });
    }

    private boolean checkEmpty(String value, TextInputEditText inputEditText) {

        if (value == null || value.isEmpty()) {
            inputEditText.setError(getResources().getString(R.string.error_empty_value));
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(Common.TAG_eDong, "requestCode = " + requestCode);
        if (requestCode == FRONT_CODE) {
            requestOSS(picFrontPath, FRONT_CODE);
        }
        if (requestCode == BACK_CODE) {
            requestOSS(picBackPath, BACK_CODE);
        }
        if (requestCode == HAND_CODE) {
            requestOSS(picHandPath, HAND_CODE);
        }
    }

    private void requestOSS(String imagePath, final int captureCode) {
        timeStamp = System.currentTimeMillis();
        loadingCustom.showDialog();

//        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";

        //if null , default will be init
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
        conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
        conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
        conf.setMaxErrorRetry(2); // retry，default 2
        OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv

        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                ossInfo.getOS_IMAGE_KEY_ID(), ossInfo.getOS_IMAGE_KEY_SECRET());

        if (getContext() != null) {
            context = getContext();
        }
        objKey = SharePref.read(SharePref.USER_ID_eDong, "")
                + "=" + timeStamp + ".jpg";

        switch (captureCode) {
            case FRONT_CODE:
                keyFront = objKey;
                break;

            case BACK_CODE:
                keyBack = objKey;
                break;

            case HAND_CODE:
                keyHand = objKey;
                break;

        }

        Log.d(Common.TAG_eDong, " object key = " + objKey);

        // Construct an upload request
        PutObjectRequest put = new PutObjectRequest(ossInfo.getOS_BUCKET_NAME(),
                objKey,
                imagePath);

        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
            }
        });


        OSSClient ossClient = new OSSClient(context, ossInfo.getOS_IMAGE_ENDPOINT(),
                credentialProvider,
                conf);

        Log.d(Common.TAG_eDong, "image Path = " + imagePath);
        ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d(Common.TAG_eDong, "result = " + result.toString());
                Log.d(Common.TAG_eDong, "captureCode = " + captureCode);
                loadingCustom.hideDialog();
                switch (captureCode) {
                    case FRONT_CODE:
                        isUpImgFrontSuccess = true;
                        updateImage(FRONT_CODE, uriFront);
                        Log.d(Common.TAG_eDong, " FRONT_CODE UploadSuccess");
                        Log.d(Common.TAG_eDong, " uriFront " + uriFront);
                        break;
                    case BACK_CODE:
                        isUpImgBackSuccess = true;
                        updateImage(BACK_CODE, uriBack);
                        Log.d(Common.TAG_eDong, " BACK_CODE UploadSuccess");
                        break;
                    case HAND_CODE:
                        isUpImgHandSuccess = true;
                        updateImage(HAND_CODE, uriHand);
                        Log.d(Common.TAG_eDong, " HAND_CODE UploadSuccess");
                        break;
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                loadingCustom.hideDialog();
                showDialog(clientException.toString(), Common.FAIL);
                Log.d(Common.TAG_eDong, "Upload onFailure = " + clientException.toString());
                Log.d(Common.TAG_eDong, "Upload onFailure = " + serviceException.toString());
            }
        });

    }

    private void updateImage(int pos, Uri uri) {
        if (getActivity() == null){
            return;
        }
        getActivity().runOnUiThread(() -> {
            switch (pos) {
                case FRONT_CODE:
                    try {
                        bitmap_front = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        bitmap_front = Bitmap.createScaledBitmap(bitmap_front, 160, 160, true);
                        imgFront.setImageBitmap(bitmap_front);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                        imgFront.setImageURI(uri);
                    break;

                case BACK_CODE:
                    try {
                        bitmap_back = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        bitmap_back = Bitmap.createScaledBitmap(bitmap_back, 160, 160, true);
                        imgBack.setImageBitmap(bitmap_back);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    imgBack.setImageURI(uri);
                    break;

                case HAND_CODE:
                    try {
                        bitmap_hand = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        bitmap_hand = Bitmap.createScaledBitmap(bitmap_hand, 160, 160, true);
                        imgHand.setImageBitmap(bitmap_hand);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    imgHand.setImageURI(uri);
                    break;
            }

        });
    }

    private void callOpenCamera(int imageCapture) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {

            photoFile = createImageFile(imageCapture);
            Log.d(Common.TAG_eDong, " photo file =  " + photoFile.getAbsolutePath());
            Log.d(Common.TAG_eDong, photoFile.getAbsolutePath());

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFile);
                switch (imageCapture) {
                    case FRONT_CODE:
                        uriFront = photoURI;
                        break;

                    case BACK_CODE:
                        uriBack = photoURI;
                        break;

                    case HAND_CODE:
                        uriHand = photoURI;
                        break;
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, imageCapture);
            }
        } catch (Exception ex) {
            // Error occurred while creating the File
            Log.d(Common.TAG_eDong, " photo ex =  " + ex.toString());
        }
    }

    private File createImageFile(int imageCapture) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        switch (imageCapture) {
            case FRONT_CODE:
                picFrontPath = image.getAbsolutePath();
                break;

            case BACK_CODE:
                picBackPath = image.getAbsolutePath();
                break;

            case HAND_CODE:
                picHandPath = image.getAbsolutePath();
                break;
        }

        return image;
    }

    public static Bitmap scaleDown(File file, float maxImageSize,
                                   boolean filter) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);
        float ratio = Math.min(
                (float) maxImageSize / bitmap.getWidth(),
                (float) maxImageSize / bitmap.getHeight());
        int width = Math.round((float) ratio * bitmap.getWidth());
        int height = Math.round((float) ratio * bitmap.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width,
                height, filter);
        return newBitmap;
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
                            SharePref.write(SharePref.FIRST_FINISH_STEP_2, Common.DONE_eDong);
                            SharePref.write(SharePref.DONE_STEP_2_eDong, Common.DONE_eDong);
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = format.format(calendar.getTime());
        edtBDay.setText(strDate);
    }
}
