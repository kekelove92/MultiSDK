package com.appfree.newedong.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.appfree.newedong.HomeActivity;
import com.appfree.newedong.R;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.CustomInfo;
import com.appfree.newedong.common.LoadingCustom;
import com.appfree.newedong.common.MyLibs;
import com.appfree.newedong.common.SharePref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tsy.plutusnative.callback.PlutusResultCallback;
import com.tsy.plutusnative.model.Result;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.tv_money)
    TextView mTvMoneyLoan;
    @BindView(R.id.tv_money_interest)
    TextView mTvInterest;
    @BindView(R.id.tv_money_fee)
    TextView mTvFee;
    @BindView(R.id.tv_money_received)
    TextView mTvActualLoan;
    @BindView(R.id.tv_day_loan)
    TextView mTvDay;
    @BindView(R.id.lottie_loan)
    LottieAnimationView lottieLoan;
    @BindView(R.id.button_accept_loan)
    Button btnAcceptLoan;

    private int mCodeDate;
    private static final String Day30 = "30";
    private static final String Day15 = "15";
    private static final String Day7 = "7";
    private static final String Day14 = "14";
    private static final String Day21 = "21";
    private static final String Day28 = "28";
    String money;
    String interest;
    String fee;
    String actualReceived;
    String timeLoan;
    private int mCodeStatus;
    private String userId;
    FloatingActionButton mFabMain, mFabCustomer, mFabInfor, mFabRepay;
    Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView tv_support_customer, tv_information, tv_repay;
    private SwipeRefreshLayout swipeRefreshLayout;
    Boolean isOpen = false;
    private View viewCover;

    @BindView(R.id.main_detail_layout)
    View view;


    private LoadingCustom loadingDialog;
    private boolean isDoubleBackPressToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        loadingDialog = new LoadingCustom(this);
        userId = SharePref.read(SharePref.USER_ID_eDong, "");
        btnAcceptLoan.setOnClickListener(this);

        mFabMain = findViewById(R.id.fab);
        mFabCustomer = findViewById(R.id.fab_customer);
        mFabInfor = findViewById(R.id.fab_info);
        mFabRepay = findViewById(R.id.fab_repay);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_anticlock);

        swipeRefreshLayout = findViewById(R.id.simpleSwipeRefreshLayout);
        tv_support_customer = (TextView) findViewById(R.id.tv_support_customer);
        tv_information = (TextView) findViewById(R.id.tv_information);
        tv_repay = findViewById(R.id.tv_info_repay);
        viewCover = findViewById(R.id.view_cover);

        swipeRefreshLayout.setOnRefreshListener(this);
        mFabMain.setOnClickListener(this::onClick);
        mFabCustomer.setOnClickListener(this::onClick);
        mFabInfor.setOnClickListener(this::onClick);
        mFabRepay.setOnClickListener(this::onClick);

        mCodeStatus = getIntent().getIntExtra("status_code", 0);
        if (mCodeStatus == Common.PENDING_WORKING) {
            money = SharePref.readInt(SharePref.LOAN_MONEY_eDong, -1) + "";
            interest = SharePref.readInt(SharePref.LOAN_INTEREST_eDong, -1) + "";
            fee = SharePref.readInt(SharePref.LOAN_FEE_eDong, -1) + "";
            Log.d(Common.TAG_eDong, "actualReceived = " + CustomInfo.userInfoResult.getActualReceivedLoan());
            actualReceived = SharePref.readInt(SharePref.LOAN_ACTURAL_RECEIVED_eDong, -1) + "";
            timeLoan = SharePref.read(SharePref.LOAN_DAY_eDong, "");

            money = formatVnCurrence(money);
            interest = formatVnCurrence(interest);
            fee = formatVnCurrence(fee);
            actualReceived = formatVnCurrence(actualReceived);

            waittingLoanView();

        } else {
            money = getIntent().getStringExtra(Common.KEY_MONEY_LOAN_eDong);
            money = formatVnCurrence(money);
            interest = getIntent().getStringExtra(Common.KEY_INTEREST_eDong);
            interest = formatVnCurrence(interest);
            fee = getIntent().getStringExtra(Common.KEY_FEE_eDong);
            fee = formatVnCurrence(fee);
            actualReceived = getIntent().getStringExtra(Common.KEY_ACTUAL_RECEIVED_eDong);
            actualReceived = formatVnCurrence(actualReceived);
            timeLoan = getIntent().getStringExtra(Common.KEY_TIME_LOAN_eDong);

        }

        updateUI();
    }

    private void updateUI() {
        mTvMoneyLoan.setText(money);
        mTvInterest.setText(interest);
        mTvFee.setText(fee);
        mTvActualLoan.setText(actualReceived);
        mTvDay.setText(timeLoan + " " + getResources().getString(R.string.day));
//
//        switch (timeLoan) {
//
//            case Day30:
//                mCodeDate = 2;
//                break;
//            case Day15:
//                mCodeDate = 1;
//                break;
//            case Day7:
//                mCodeDate = 3;
//                break;
//            case Day14:
//                mCodeDate = 4;
//                break;
//            case Day21:
//                mCodeDate = 5;
//                break;
//            case Day28:
//                mCodeDate = 6;
//                break;
//        }


    }

    public String formatVnCurrence(String price) {
        NumberFormat format =
                new DecimalFormat("#,###");// #,##0.00 ¤ (¤:// Currency symbol)
        price = format.format(Double.parseDouble(price));
        price = price.replace(".", ",");
        return price;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_accept_loan:
                loadingDialog.showDialog();

                MyLibs.getInstance().getPlutusSDK().submitLoan(userId,
                        money,
                        timeLoan,
                        new PlutusResultCallback() {
                            @Override
                            public void onResult(String s, Result result) {
                                loadingDialog.hideDialog();
                                if (result.getIsSuccess()) {
                                    showDialog(result.getMessage());
                                    waittingLoanView();
                                } else {
                                    showDialog(result.getMessage());
                                }
                            }
                        });

                break;

            case R.id.fab:

                if (isOpen) {
                    fabCollapse();
                } else {
                    fabExpand();
                }
                break;

            case R.id.fab_customer:
                if (isOpen){
                    fabCollapse();
                }
                Intent intent = new Intent(this, SupportActivity.class);
                intent.putExtra("code_support", Common.SUPPORT_CUSTOMER);
                startActivity(intent);

                break;

            case R.id.fab_info:
                if (isOpen){
                    fabCollapse();
                }
                Intent intent2 = new Intent(this, SupportActivity.class);
                intent2.putExtra("code_support", Common.SUPPORT_INFORMATION);
                startActivity(intent2);
                break;

            case R.id.fab_repay:
                if (isOpen){
                    fabCollapse();
                }
                Intent intent3 = new Intent(this, SupportActivity.class);
                intent3.putExtra("code_support", Common.SUPPORT_REPAY);
                startActivity(intent3);
                break;
        }
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


    private void waittingLoanView() {
        btnAcceptLoan.setText(R.string.loan_waitting);
        btnAcceptLoan.setEnabled(false);
        lottieLoan.playAnimation();
        lottieLoan.loop(true);
    }

    private void showDialog(String message) {
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
                alert11.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.color_4));
            }
        });
        alert11.show();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
