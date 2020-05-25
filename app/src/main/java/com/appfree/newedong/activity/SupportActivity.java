package com.appfree.newedong.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.appfree.newedong.R;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.fragment.supportView.CskhSupportFragment;
import com.appfree.newedong.fragment.supportView.InformationSupportFragment;
import com.appfree.newedong.fragment.supportView.RepaySupportFragment;

public class SupportActivity extends AppCompatActivity {

    private int codeSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        codeSupport = getIntent().getIntExtra("code_support", 0);


//        Fragment fragment = new MainViewSupportFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.frameLayoutSupport,fragment);
//        transaction.disallowAddToBackStack();
//        transaction.commit();

        switch (codeSupport){
            case Common.SUPPORT_REPAY:
                openRepayInfoSupport();
                break;

            case Common.SUPPORT_INFORMATION:
                openInfoSupport();
                break;

            case Common.SUPPORT_CUSTOMER:
                openCustomerSupport();
                break;
        }
    }

    private void openCustomerSupport() {
        Fragment fragment = new CskhSupportFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frameLayoutSupport, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    private void openInfoSupport() {
        Fragment fragment = new InformationSupportFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frameLayoutSupport, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    private void openRepayInfoSupport() {
        Fragment fragment = new RepaySupportFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frameLayoutSupport, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }


}
