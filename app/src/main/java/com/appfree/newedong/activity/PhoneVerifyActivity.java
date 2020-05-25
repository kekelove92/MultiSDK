package com.appfree.newedong.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.appfree.newedong.R;
import com.appfree.newedong.fragment.phoneAuthen.PhoneAuthenMainFragment;
import com.appfree.newedong.fragment.phoneAuthen.PhoneAuthenSubFragment;

public class PhoneVerifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        show();
    }

    private void show() {
        Fragment fragment = new PhoneAuthenMainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.framelayout, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }
}
