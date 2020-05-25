package com.appfree.newedong;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appfree.newedong.activity.DetailActivity;
import com.appfree.newedong.activity.StateActivity;
import com.appfree.newedong.activity.SupportActivity;
import com.appfree.newedong.activity.VerificationActivity;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.ConvertMd5;
import com.appfree.newedong.common.CustomInfo;
import com.appfree.newedong.common.LoadingCustom;
import com.appfree.newedong.common.MyLibs;
import com.appfree.newedong.common.SharePref;
import com.appfree.newedong.common.ShowCustomDialog;
import com.appfree.newedong.model.DayModel;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.hanks.htextview.base.HTextView;
import com.tsy.plutusnative.callback.PlutusResultCallback;
import com.tsy.plutusnative.callback.UserInfoCallback;
import com.tsy.plutusnative.model.Result;
import com.tsy.plutusnative.model.UserInfoResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private static final String SERVER_DAY_SHOW = "server_day_show";
    ImageView img;
    RelativeLayout layout;
    FloatingActionButton floatingActionButton;
    FloatingActionButton mFabMain, mFabCustomer, mFabInfor, mFabRepay;
    Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView tv_support_customer, tv_information, tv_repay, tv_day_fk;
    private HTextView hTextView;
    int delay = 5000;
    Handler handler;
    private ArrayList<String> arrListSuccess = new ArrayList<>();
    private ArrayList<String> listFistNumber = new ArrayList<>();
    private ArrayList<String> listRandom = new ArrayList<>();
    int position = 0;
    int minNumber = 1000;
    int maxNumber = 9999;
    Boolean isOpen = false;
    private NumberPicker moneyPicker;
    private NumberPicker dayPicker;
    MaterialButton btnVerify;
    MaterialButton btnLoan;

    private View viewCover;

    List<String> listValue;
    String[] item;
    private String currentBalance;
    private int currentBalanceInt;
    private ArrayList<DayModel> arrayList;
    private Double maxFee;
    private Double minFee;
    private String mDayBorrow;
    private int mInterest;
    private int mManagementFee;
    private Double mResult;
    private int mActualReceived;
    private int mStatusCode;
    private String mMaxmoney;
    private int maxMoneyInt;
    private int minMoneyInt;
    private String mExpiredState;
    private String userId;
    private String userMd5;
    private UserInfoResult infoResult;
    private static final int STEP = 500000;
    public int MIN_BALANCE = 1500000;
    private LoadingCustom loadingCustom;
    private CardView cardView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShowCustomDialog customDialog;
    private boolean isIdCardVerified = false;
    private boolean isBankVerified = false;
    private boolean isReferencesVerified = false;

    // location
    private static final int REQUEST_ERROR = 101;
    private static final int PERMISSION_CODE = 1002;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String mLat;
    private String mLng;
    private boolean isDoubleBackPressToExit = false;
    private View view;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private boolean isShowDay = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();


    }

    private void initView() {

        userId = SharePref.read(SharePref.USER_ID_eDong, "");
        userMd5 = ConvertMd5.getMD5(userId, Common.MD5_TYPE_USER_ID);
        Log.d(Common.TAG_eDong, "userId = " + userId);
        Log.d(Common.TAG_eDong, "userMd5 = " + userMd5);


        view = findViewById(R.id.main_home_layout);
        hTextView = findViewById(R.id.tv_show_success);
        img = findViewById(R.id.img);
        layout = findViewById(R.id.layout);

        loadingCustom = new LoadingCustom(this);
        customDialog = new ShowCustomDialog(this);

        mFabMain = findViewById(R.id.fab);
        mFabCustomer = findViewById(R.id.fab_customer);
        mFabInfor = findViewById(R.id.fab_info);
        mFabRepay = findViewById(R.id.fab_repay);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_anticlock);

        tv_support_customer = (TextView) findViewById(R.id.tv_support_customer);
        tv_information = (TextView) findViewById(R.id.tv_information);
        tv_repay = findViewById(R.id.tv_info_repay);
        tv_day_fk = findViewById(R.id.tv_day_fk);

        moneyPicker = findViewById(R.id.number_picker);
        dayPicker = findViewById(R.id.number_picker_day);
        btnVerify = findViewById(R.id.btn_verify);
        btnLoan = findViewById(R.id.btn_accepet_loan);
        viewCover = findViewById(R.id.view_cover);
        cardView = findViewById(R.id.cardview);
        swipeRefreshLayout = findViewById(R.id.simpleSwipeRefreshLayout);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        swipeRefreshLayout.setOnRefreshListener(this);
        mFabMain.setOnClickListener(this::onClick);
        mFabCustomer.setOnClickListener(this::onClick);
        mFabInfor.setOnClickListener(this::onClick);
        mFabRepay.setOnClickListener(this::onClick);
        btnVerify.setOnClickListener(this::onClick);
        btnLoan.setOnClickListener(this::onClick);

        SharePref.init(getApplicationContext());

        SharePref.write(SharePref.DONE_STEP_1_eDong, "");
        SharePref.write(SharePref.DONE_STEP_2_eDong, "");
        SharePref.write(SharePref.DONE_STEP_3_eDong, "");

        loadingCustom.showDialog();
        getConfig();
        addPhoneToListNumber();
        getRandomInListNumber();
        addValueToList();
        callApiGetInfo();
        getHashKey();

    }

    private void getHashKey() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.d(Common.TAG_eDong,"hash key = " +something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    private void getConfig() {
        // get config instance
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // enable dev mode
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful() && task.getResult()!= null) {
                            boolean updated = task.getResult();
                            Log.d(Common.TAG_eDong, "Config params updated: " + updated);
                        } else {
                            Log.d(Common.TAG_eDong, "Fetch failed");
                        }
                        // get config value
                        isShowDay = firebaseRemoteConfig.getBoolean(SERVER_DAY_SHOW);
                        if (!isShowDay){
                            tv_day_fk.setVisibility(View.GONE);
                            dayPicker.setVisibility(View.VISIBLE);
                        } else {
                            tv_day_fk.setVisibility(View.VISIBLE);
                            dayPicker.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void callApiGetInfo() {

        MyLibs.getInstance().getPlutusSDK().getUserInfo(userId, new UserInfoCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(boolean b, @Nullable UserInfoResult userInfoResult) {

                if (userInfoResult == null) {
                    Log.d(Common.TAG_eDong, "userInfoResult null ");
                    return;
                }

                infoResult = userInfoResult;

                // global variable
                CustomInfo.getInstance().userInfoResult = userInfoResult;


//                 testdemo
//                Map<String, List<Double>> MapTestValue = new LinkedHashMap<>();
//                MapTestValue.put("7", Arrays.asList(7.01, 7.30));
//                MapTestValue.put("10", Arrays.asList(10.01, 10.30));
//                MapTestValue.put("20", Arrays.asList(20.01, 22.30));
//                MapTestValue.put("30", Arrays.asList(30.01, 30.30));
//
//                List<DayModel> listTest = MapTestValue.entrySet()
//                        .stream()
//                        .map(e -> new DayModel(e.getKey(), e.getValue()))
//                        .collect(Collectors.toList());

                List<DayModel> list = new ArrayList<>();
                //Load your values here
                Set<String> keys = userInfoResult.filterAvailableValues().keySet();
                String[] keysArray = keys.toArray(new String[keys.size()]);
                for (int i = 0; i < keysArray.length && i < 10; i++) {
                    DayModel dayModel = new DayModel(keysArray[i], userInfoResult.filterAvailableValues().get(keysArray[i]));
                    list.add(dayModel);
                }


                arrayList = new ArrayList<>(list);

                Log.d(Common.TAG_eDong, "getBankVerified  = " + userInfoResult.getBankVerified());
                Log.d(Common.TAG_eDong, "getIdentityVerified  = " + userInfoResult.getIdentityVerified());
                Log.d(Common.TAG_eDong, "getReferencesVerified  = " + userInfoResult.getReferencesVerified());
                // check step 1 is done?
                isBankVerified = userInfoResult.getBankVerified();
                if (isBankVerified) {
                    SharePref.write(SharePref.DONE_STEP_1_eDong, Common.DONE_eDong);
                }

                // check step 2 is done ?
                isIdCardVerified = userInfoResult.getIdentityVerified();
                if (isIdCardVerified) {
                    SharePref.write(SharePref.DONE_STEP_2_eDong, Common.DONE_eDong);
                }

                // check step 3 is done ?
                isReferencesVerified = userInfoResult.getReferencesVerified();
                if (isReferencesVerified) { // vl :))
                    SharePref.write(SharePref.DONE_STEP_3_eDong, Common.DONE_eDong);
                }

                loadingCustom.hideDialog();
                updateUI();


                Log.d(Common.TAG_eDong, "getRequestedLoan = " + userInfoResult.getRequestedLoan());
                Log.d(Common.TAG_eDong, "getRequestedLoanPeriod = " + userInfoResult.getRequestedLoanPeriod());
                Log.d(Common.TAG_eDong, "getActualReceivedLoan = " + userInfoResult.getActualReceivedLoan());
                Log.d(Common.TAG_eDong, "getExpiredDayCount = " + userInfoResult.getExpiredDayCount());
                Log.d(Common.TAG_eDong, "getExpiredTime = " + userInfoResult.getExpiredTime());
                Log.d(Common.TAG_eDong, "getExpiredFee = " + userInfoResult.getExpiredFee());


                Log.d(Common.TAG_eDong, "status Code = " + userInfoResult.getStatusIndex());
                switch (userInfoResult.getStatusIndex()) {
                    case AVAILABLE_TO_REQUEST:
                        //2
                        Log.d(Common.TAG_eDong, "AVAILABLE_TO_REQUEST");
                        mStatusCode = 2;
                        break;

                    case AVAILABLE_TO_UPLOAD_VIDEO:
                        //3
                        Log.d(Common.TAG_eDong, "AVAILABLE_TO_UPLOAD_VIDEO");


                        String userName = userInfoResult.getUserBankCard().getAccountName();
                        Log.d(Common.TAG_eDong, "name user = " + userName);
                        SharePref.write(SharePref.NAME_USER_eDong, userName);
                        mStatusCode = 3;
                        Intent intent = new Intent(HomeActivity.this, StateActivity.class);
                        intent.putExtra("status_code", mStatusCode);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;

                    case DEEPLY_PHONE_VERIFICATION_REQUIRED:

                        break;

                    case WAITING_FOR_RELEASE:
                        // 6
                        Log.d(Common.TAG_eDong, "WAITING_FOR_RELEASE");
                        mStatusCode = 6;
                        Intent intent2 = new Intent(HomeActivity.this, StateActivity.class);
                        intent2.putExtra("status_code", mStatusCode);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent2);
                        break;

                    case LOAN_SUCCEED:
                        // 4
                        Log.d(Common.TAG_eDong, "LOAN_SUCCEED");
                        mStatusCode = 4;
                        Intent intent3 = new Intent(HomeActivity.this, StateActivity.class);
                        intent3.putExtra("status_code", mStatusCode);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent3);
                        break;

                    case UPLOADED_REPAY_BILL:
                        //5
                        Log.d(Common.TAG_eDong, "UPLOADED_REPAY_BILL");
                        mStatusCode = Common.UPLOADED_REPAY_BILL;
                        Intent intent4 = new Intent(HomeActivity.this, StateActivity.class);
                        intent4.putExtra("status_code", mStatusCode);
                        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent4);
                        break;

                    case PENDING_WORKING:
                        // 7
                        Log.d(Common.TAG_eDong, "PENDING_WORKING");
                        mStatusCode = 7;
                        Log.d(Common.TAG_eDong, "getRequestedLoan = " + userInfoResult.getRequestedLoan());
                        Log.d(Common.TAG_eDong, "getRequestedLoanPeriod = " + userInfoResult.getRequestedLoanPeriod());

                        String valueLoan = infoResult.getRequestedLoan();
                        String valueLoanInt = valueLoan.replace(",", "");
                        int loanValueInt = Integer.parseInt(valueLoanInt);
                        String dayBorrow = infoResult.getRequestedLoanPeriod();
                        String[] days = dayBorrow.split(" ");
                        String dayLoan = days[0];
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (dayLoan.equals(arrayList.get(i).getDay())) {
                                maxFee = Collections.max(arrayList.get(i).getListFee());
                                minFee = Collections.min(arrayList.get(i).getListFee());

                                mResult = maxFee - minFee;
                                mInterest = (int) (loanValueInt * minFee);
                                mManagementFee = (int) (mResult * loanValueInt);
                                Log.d(Common.TAG_eDong, "================== Loan requested ===============");
                                Log.d(Common.TAG_eDong, "mManagementFee = " + mManagementFee);
                                Log.d(Common.TAG_eDong, "mResult = " + mResult);
                                Log.d(Common.TAG_eDong, "loanValueInt = " + loanValueInt);
                                Log.d(Common.TAG_eDong, "dayLoan = " + dayLoan);
                                Log.d(Common.TAG_eDong, "maxFeeEd = " + maxFee);
                                Log.d(Common.TAG_eDong, "minFeeEd = " + minFee);
                                Log.d(Common.TAG_eDong, "mInterest = " + mInterest);
                                Log.d(Common.TAG_eDong, "mResult = " + mResult);
                                Log.d(Common.TAG_eDong, "mManagementFee = " + mManagementFee);
                                Log.d(Common.TAG_eDong, "=================================================");
                                mActualReceived = loanValueInt - (mInterest + mManagementFee);
                            }
                        }

                        saveValueLoan(loanValueInt, mInterest, mManagementFee, mActualReceived, dayLoan);
                        openDetailActivity();

                        break;

                    case UNKNOWN:
                        showDialogEdong(getResources().getString(R.string.unknow_error));
                        break;
                }
            }
        });

    }


    private void updateUI() {
        try {
            Log.d(Common.TAG_eDong, "updateUI");


            //get max value loan
            String maxLoan = infoResult.getCreditLimit();
            Log.d(Common.TAG_eDong, "maxLoan = " + maxLoan);
            maxLoan = maxLoan.replace(",", "");
            maxMoneyInt = Integer.parseInt(maxLoan);

            // get min value loan
            String minLoan = infoResult.getMinCreditLimit();
            Log.d(Common.TAG_eDong, "minLoan = " + minLoan);
            minLoan = minLoan.replace(",", "");
            minMoneyInt = Integer.parseInt(minLoan);


            //balance spinner
            listValue = new ArrayList<>();
            for (int i = minMoneyInt; i <= maxMoneyInt; i += STEP) {
                listValue.add(formatVn(i + ""));
                Log.d(Common.TAG_eDong, "i = " + i);
            }
            item = listValue.toArray(new String[listValue.size()]);
            currentBalance = item[0];
            currentBalanceInt = Integer.parseInt(currentBalance.replace(",", ""));
            moneyPicker.setMinValue(0);
            moneyPicker.setMaxValue((item.length - 1));
            moneyPicker.setWrapSelectorWheel(false);
            moneyPicker.setDisplayedValues(item);
            moneyPicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return item[value];
                }
            });
            moneyPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    Log.d(Common.TAG_eDong, "currentBalance = " + item[newVal]);
                    currentBalance = item[newVal];
                    currentBalanceInt = Integer.parseInt(currentBalance.replace(",", ""));
                }
            });


            // day spinner
            ArrayList<String> listShowDay = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                Log.d(Common.TAG_eDong, "" + arrayList.get(i).getDay());
                listShowDay.add(arrayList.get(i).getDay());
            }

            String[] itemDay = listShowDay.toArray(new String[listShowDay.size()]);

            dayPicker.setMinValue(0);
            dayPicker.setMaxValue(itemDay.length - 1);
            dayPicker.setWrapSelectorWheel(false);
            dayPicker.setDisplayedValues(itemDay);


            mDayBorrow = itemDay[0];
            maxFee = Collections.max(arrayList.get(0).getListFee());
            minFee = Collections.min(arrayList.get(0).getListFee());
            Log.d(Common.TAG_eDong, "maxFeePicker = " + maxFee);
            Log.d(Common.TAG_eDong, "minFeePicker = " + minFee);
            dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    Log.d(Common.TAG_eDong, "newVal = " + newVal);
                    Log.d(Common.TAG_eDong, "maxFeePicker = " + Collections.max(arrayList.get(newVal).getListFee()));
                    Log.d(Common.TAG_eDong, "minFeePicker = " + Collections.min(arrayList.get(newVal).getListFee()));
                    mDayBorrow = itemDay[newVal] + "";
                    maxFee = Collections.max(arrayList.get(newVal).getListFee());
                    minFee = Collections.min(arrayList.get(newVal).getListFee());
                }
            });

            mResult = maxFee - minFee;
            mInterest = (int) (currentBalanceInt * minFee);
            mManagementFee = (int) (mResult * currentBalanceInt);
            mActualReceived = currentBalanceInt - (mInterest + mManagementFee);


            checkShowTutorial();
            if (isBankVerified && isIdCardVerified && isReferencesVerified) {
                btnVerify.setVisibility(View.GONE);
                btnLoan.setVisibility(View.VISIBLE);
            } else {
                btnVerify.setVisibility(View.VISIBLE);
                btnLoan.setVisibility(View.GONE);
            }

            String uploadVideoValue = SharePref.read(SharePref.VERIFY_VIDEO_eDong, "");
            Log.d(Common.TAG_eDong, "uploadVideoValue = " + uploadVideoValue);

            if (uploadVideoValue != null && !uploadVideoValue.isEmpty()) {
                showDialogReject(getResources().getString(R.string.reject_video));
                SharePref.write(SharePref.VERIFY_VIDEO_eDong, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showDialogReject(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
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


    private void checkShowTutorial() {
        String showTutorial = SharePref.read(SharePref.SHOW_TUTORIAL_eDong, "");
        if (showTutorial == null || showTutorial.isEmpty()) {
            showHowToUse();
        }
    }

    public String formatVn(String price) {
        NumberFormat format = new DecimalFormat("#,###");
        price = format.format(Double.parseDouble(price));
        price = price.replace(".", ",");
        return price;
    }


    private void getRandomInListNumber() {
        Random random = new Random();
        int size = listFistNumber.size();

        for (int i = 0; i <= 10; i++) {
            int randomIndex = random.nextInt(size);
            listRandom.add(listFistNumber.get(randomIndex));
        }
    }

    private void addPhoneToListNumber() {
        // mobi
        listFistNumber.add("089");
        listFistNumber.add("090");
        listFistNumber.add("070");
        listFistNumber.add("093");
        listFistNumber.add("079");
        listFistNumber.add("077");
        listFistNumber.add("076");
        listFistNumber.add("078");

        // vina
        listFistNumber.add("088");
        listFistNumber.add("091");
        listFistNumber.add("094");
        listFistNumber.add("083");
        listFistNumber.add("084");
        listFistNumber.add("085");
        listFistNumber.add("081");
        listFistNumber.add("089");
        listFistNumber.add("082");

        // viettel
        listFistNumber.add("086");
        listFistNumber.add("096");
        listFistNumber.add("097");
        listFistNumber.add("098");
        listFistNumber.add("032");
        listFistNumber.add("033");
        listFistNumber.add("035");
        listFistNumber.add("036");
        listFistNumber.add("037");
        listFistNumber.add("038");
        listFistNumber.add("039");

    }

    private void addValueToList() {

        for (int i = 0; i < listRandom.size(); i++) {
            arrListSuccess.add(getResources().getString(R.string.sdt_congratulation) + " " +
                    listRandom.get(i) +
                    getResources().getString(R.string.sdt_xxxxx) +
                    getRandomNumber() + " " +
                    getResources().getString(R.string.sdt_register_success));
        }
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this::run, delay);
                if (position >= arrListSuccess.size())
                    position = 0;
                hTextView.animateText(arrListSuccess.get(position));
                position++;

            }
        }, delay);

        hTextView.animateText(arrListSuccess.get(position));
    }

    private int getRandomNumber() {
        Random random = new Random();
        int valueRandom = random.nextInt(maxNumber - minNumber + 1) + minNumber;

        return valueRandom;
    }


    private void showHowToUse() {

        TapTargetView.showFor(this,
                TapTarget.forView(moneyPicker,
                        getResources().getString(R.string.tutorial_new_user),
                        getResources().getString(R.string.tutorial_step_1))
                        .titleTextColor(R.color.color_4)
                        .descriptionTextColor(R.color.color_splash)
                        .cancelable(false)
                        .transparentTarget(true)
                        .targetRadius(50)
                , new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        view.dismiss(true);
                        showStep2();
                    }
                });
    }

    private void showStep2() {
        TapTargetView.showFor(this,
                TapTarget.forView(dayPicker,
                        getResources().getString(R.string.tutorial_new_user),
                        getResources().getString(R.string.tutorial_step_2))
                        .titleTextColor(R.color.color_4)
                        .descriptionTextColor(R.color.color_splash)
                        .cancelable(false)
                        .transparentTarget(true)
                        .targetRadius(50)
                , new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        view.dismiss(true);
                        showStep3();
                    }
                });
    }

    private void showStep3() {
        TapTargetView.showFor(this,
                TapTarget.forView(mFabMain,
                        getResources().getString(R.string.tutorial_new_user),
                        getResources().getString(R.string.tutorial_step_3))
                        .titleTextColor(R.color.color_4)
                        .descriptionTextColor(R.color.color_splash)
                        .cancelable(false)
                        .transparentTarget(true)
                        .targetRadius(50)
                , new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        view.dismiss(true);
                        showStep4();
                    }
                });


    }

    private void showStep4() {
        TapTargetView.showFor(this,
                TapTarget.forView(cardView,
                        getResources().getString(R.string.tutorial_new_user),
                        getResources().getString(R.string.tutorial_step_4))
                        .titleTextColor(R.color.color_4)
                        .descriptionTextColor(R.color.color_splash)
                        .cancelable(false)
                        .transparentTarget(true)
                        .targetRadius(50)
                , new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        view.dismiss(true);
                    }
                });

        SharePref.write(SharePref.SHOW_TUTORIAL_eDong, "SHOW_TUTORIAL");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:

                if (isOpen) {
                    fabCollapse();
                } else {
                    fabExpand();
                }
                break;

            case R.id.fab_customer:

                if (isOpen) {
                    fabCollapse();
                }
                Intent intent = new Intent(this, SupportActivity.class);
                intent.putExtra("code_support", Common.SUPPORT_CUSTOMER);
                startActivity(intent);

                break;

            case R.id.fab_info:

                if (isOpen) {
                    fabCollapse();
                }
                Intent intent2 = new Intent(this, SupportActivity.class);
                intent2.putExtra("code_support", Common.SUPPORT_INFORMATION);
                startActivity(intent2);

                break;

            case R.id.fab_repay:
                if (isOpen) {
                    fabCollapse();
                }
                Intent intent3 = new Intent(this, SupportActivity.class);
                intent3.putExtra("code_support", Common.SUPPORT_REPAY);
                startActivity(intent3);

                break;

            case R.id.btn_verify:
                showDialogRequestLocation(getResources().getString(R.string.request_location));
                break;

            case R.id.btn_accepet_loan:
                showDetailtLoan();
                break;

        }
    }

    private void showDetailtLoan() {
        mResult = maxFee - minFee;
        mInterest = (int) (currentBalanceInt * minFee);
        mManagementFee = (int) (mResult * currentBalanceInt);
        mActualReceived = currentBalanceInt - (mInterest + mManagementFee);

        Log.d(Common.TAG_eDong, "================ Loan Detail =================");
        Log.d(Common.TAG_eDong, "mDayBorrow = " + mDayBorrow);
        Log.d(Common.TAG_eDong, "mResult = " + mResult);
        Log.d(Common.TAG_eDong, "mInterest = " + mInterest);
        Log.d(Common.TAG_eDong, "mManagementFee = " + mManagementFee);
        Log.d(Common.TAG_eDong, "mActualReceived = " + mActualReceived);
        Log.d(Common.TAG_eDong, "currentBalanceInt = " + currentBalanceInt);
        Log.d(Common.TAG_eDong, "maxFee = " + maxFee);
        Log.d(Common.TAG_eDong, "minFee = " + minFee);
        Log.d(Common.TAG_eDong, "=================================================");
        saveValueLoan(currentBalanceInt, mInterest, mManagementFee, mActualReceived, mDayBorrow);

        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        intent.putExtra(Common.KEY_MONEY_LOAN_eDong, currentBalanceInt + "");
        intent.putExtra(Common.KEY_INTEREST_eDong, mInterest + "");
        intent.putExtra(Common.KEY_FEE_eDong, mManagementFee + "");
        intent.putExtra(Common.KEY_ACTUAL_RECEIVED_eDong, mActualReceived + "");
        intent.putExtra(Common.KEY_TIME_LOAN_eDong, mDayBorrow);
        intent.putExtra("status_code", mStatusCode);

        startActivity(intent);
    }

    private void openDetailActivity() {
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        intent.putExtra("status_code", mStatusCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void saveValueLoan(int money, int interest, int fee, int actualMoney, String day) {
        SharePref.writeInt(SharePref.LOAN_MONEY_eDong, money);
        SharePref.writeInt(SharePref.LOAN_INTEREST_eDong, interest);
        SharePref.writeInt(SharePref.LOAN_FEE_eDong, fee);
        SharePref.writeInt(SharePref.LOAN_ACTURAL_RECEIVED_eDong, actualMoney);
        SharePref.write(SharePref.LOAN_DAY_eDong, day);
    }

    private void fabExpand() {
        tv_support_customer.setVisibility(View.VISIBLE);
        tv_information.setVisibility(View.VISIBLE);
        tv_repay.setVisibility(View.VISIBLE);
        mFabInfor.startAnimation(fab_open);
        mFabCustomer.startAnimation(fab_open);
        mFabRepay.startAnimation(fab_open);
        mFabMain.startAnimation(fab_clock);
        mFabInfor.setClickable(true);
        mFabCustomer.setClickable(true);
        mFabRepay.setClickable(true);
        isOpen = true;
        viewCover.setVisibility(View.VISIBLE);
    }

    private void fabCollapse() {
        tv_support_customer.setVisibility(View.INVISIBLE);
        tv_information.setVisibility(View.INVISIBLE);
        tv_repay.setVisibility(View.INVISIBLE);
        mFabInfor.startAnimation(fab_close);
        mFabCustomer.startAnimation(fab_close);
        mFabRepay.startAnimation(fab_close);
        mFabMain.startAnimation(fab_anticlock);
        mFabInfor.setClickable(false);
        mFabCustomer.setClickable(false);
        mFabRepay.setClickable(false);
        isOpen = false;
        viewCover.setVisibility(View.GONE);
    }

    private void showDialogEdong(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
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
                alert11.getButton(BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color_1));
            }
        });
        alert11.show();
    }

    private void showDialogRequestLocation(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(
                R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isPlayServicesAvailable()) {
                            getLocation();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.device_not_support), Toast.LENGTH_SHORT).show();
                        }
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


    private void fabVisible() {
        mFabMain.show();
    }

    private void fabGone() {
        mFabMain.hide();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void openVerificationActivity() {
        Intent intent = new Intent(HomeActivity.this, VerificationActivity.class);
        startActivity(intent);
    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        Log.d(Common.TAG_eDong, "getLocation");
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    Log.d(Common.TAG_eDong, "location = " + location);
                                    requestNewLocationData();
                                } else {
                                    String lat = location.getLatitude() + "";
                                    String lng = location.getLongitude() + "";
                                    loadingCustom.showDialog();

                                    callRequestLocation(lat, lng);
                                    Log.d(Common.TAG_eDong, "lat getLastLocation = " + location.getLatitude());
                                    Log.d(Common.TAG_eDong, "lon getLastLocation = " + location.getLongitude());
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, R.string.request_location, Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);

                GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getBaseContext())
                        .addApi(LocationServices.API).build();
                googleApiClient.connect();

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(5000);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);

                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                Log.d(Common.TAG_eDong, "Success");
                                break;

                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.d(Common.TAG_eDong, "RESOLUTION_REQUIRED");
                                try {
                                    status.startResolutionForResult(HomeActivity.this, REQUEST_ERROR);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case LocationSettingsStatusCodes.CANCELED:
                                Log.d(Common.TAG_eDong, "CANCELED");
                                break;
                        }
                    }
                });

            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    private void requestPermissions() {
        Log.d(Common.TAG_eDong, "request Permission");
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_CODE);
    }

    private boolean isPlayServicesAvailable() {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
                == ConnectionResult.SUCCESS;
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        Log.d(Common.TAG_eDong, "requestNewLocationData");

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            mLat = mLastLocation.getLatitude() + "";
            mLng = mLastLocation.getLongitude() + "";

            callRequestLocation(mLat, mLng);
            Log.d(Common.TAG_eDong, "mLat mLocationCallback = " + mLastLocation.getLatitude());
            Log.d(Common.TAG_eDong, "mLng mLocationCallback = " + mLastLocation.getLongitude());

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        Log.d(Common.TAG_eDong, "PRIORITY_HIGH_ACCURACY: GPS Enabled by user");
                        break;
                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d(Common.TAG_eDong, "PRIORITY_HIGH_ACCURACY: User rejected GPS request");
                        Toast.makeText(this, R.string.request_location, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;

            case REQUEST_ERROR:
                switch (resultCode) {
                    case RESULT_OK:
                        // get location method
                        Log.d(Common.TAG_eDong, "REQUEST_RESOLVE_ERROR: GPS Enabled by user");
                        loadingCustom.showDialog();
                        requestNewLocationData();
                        break;
                    case RESULT_CANCELED:
                        Log.d(Common.TAG_eDong, "REQUEST_RESOLVE_ERROR: User rejected GPS request");
                        super.onBackPressed();
                        ;
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(Common.TAG_eDong, "onRequestPermissionsResult");
                getLocation();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    finish();
                    Toast.makeText(this, R.string.request_location, Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                    Toast.makeText(this, R.string.request_location, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void callRequestLocation(String mLat, String mLng) {
        Log.d(Common.TAG_eDong, "callRequestLocation");

        MyLibs.getInstance().getPlutusSDK().uploadLocation(userId, mLat, mLng, new PlutusResultCallback() {
            @Override
            public void onResult(String s, Result result) {
                loadingCustom.hideDialog();
                Log.d(Common.TAG_eDong, "status = " + result.getIsSuccess());
                Log.d(Common.TAG_eDong, "message = " + result.getMessage());

                if (!result.getIsSuccess() && result.getMessage() != null) {
                    showDialogEdong(result.getMessage());
                } else {
                    openVerificationActivity();
                }

            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Lỗi kết nối: " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onBackPressed() {
        if (isDoubleBackPressToExit) {
            super.onBackPressed();
            return;
        }

        isDoubleBackPressToExit = true;
        Snackbar.make(view,
                getResources().getString(R.string.back_press_again),
                Snackbar.LENGTH_SHORT).show();
//        Toast.makeText(this, R.string.back_press_again, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDoubleBackPressToExit = false;
            }
        }, 2000);

    }
}
