package com.appfree.newedong.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.appfree.newedong.HomeActivity;
import com.appfree.newedong.R;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.fragment.stateFragment.RepayInfoFragment;
import com.appfree.newedong.fragment.stateFragment.UpVideoFragment;
import com.appfree.newedong.fragment.stateFragment.WaitLoanFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class StateActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    int statusCode;
    FloatingActionButton mFabMain, mFabCustomer, mFabInfor, mFabRepay;
    Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView tv_support_customer, tv_information, tv_repay;
    private SwipeRefreshLayout swipeRefreshLayout;
    Boolean isOpen = false;
    private View viewCover;
    private View view;
    private boolean isDoubleBackPressToExit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        initView();
        updateUI();
    }

    private void updateUI() {
        mFabMain = findViewById(R.id.fab);
        mFabCustomer = findViewById(R.id.fab_customer);
        mFabInfor = findViewById(R.id.fab_info);
        mFabRepay = findViewById(R.id.fab_repay);
        view = findViewById(R.id.main_state_layout);

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
    }

    private void initView() {
        statusCode = getIntent().getIntExtra("status_code", 0);
        switch (statusCode){
            case Common.AVAILABLE_TO_UPLOAD_VIDEO:
                showUploadVideo();
                break;

            case Common.WAITING_FOR_RELEASE:
                showWaittingRelease();
                break;

            case Common.NOT_UPLOAD_REPAY_BILL:
            case Common.UPLOADED_REPAY_BILL:
                showRepayBillInfo();
                break;
        }
    }


    private void showUploadVideo() {
        Fragment fragment = new UpVideoFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frameLayoutState,fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    private void showWaittingRelease() {
        Fragment fragment = new WaitLoanFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frameLayoutState,fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    private void showRepayBillInfo() {
        Bundle bundle = new Bundle();
        bundle.putInt("status", statusCode);
        Fragment fragment = new RepayInfoFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frameLayoutState,fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                Intent intent = new Intent(StateActivity.this, SupportActivity.class);
                intent.putExtra("code_support", Common.SUPPORT_CUSTOMER);
                startActivity(intent);

                break;

            case R.id.fab_info:
                if (isOpen){
                    fabCollapse();
                }
                Intent intent2 = new Intent(StateActivity.this, SupportActivity.class);
                intent2.putExtra("code_support", Common.SUPPORT_INFORMATION);
                startActivity(intent2);
                break;

            case R.id.fab_repay:
                if (isOpen){
                    fabCollapse();
                }
                Intent intent3 = new Intent(StateActivity.this, SupportActivity.class);
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDoubleBackPressToExit = false;
            }
        }, 2000);
    }
}
