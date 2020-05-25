package com.appfree.newedong.fragment.supportView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appfree.newedong.R;
import com.appfree.newedong.adapter.RepayAdapter;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.CustomInfo;
import com.appfree.newedong.common.MyLibs;
import com.appfree.newedong.common.SharePref;
import com.google.gson.JsonArray;
import com.tsy.plutusnative.callback.PlutusCallback;
import com.tsy.plutusnative.callback.UserInfoCallback;
import com.tsy.plutusnative.model.Bank;
import com.tsy.plutusnative.model.UserInfoResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RepaySupportFragment extends Fragment {

    List<Bank> banks;
    @BindView(R.id.rcview)
    RecyclerView recyclerView;
    RepayAdapter adapter;
    private String userId;

    public RepaySupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repay_info2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {
        userId = SharePref.read(SharePref.USER_ID_eDong, "");
        banks = new ArrayList<>();


        MyLibs.getInstance().getPlutusSDK().getRepayBankList(new PlutusCallback() {
            @Override
            public void success(String s, @Nullable Object o) {
                Log.d(Common.TAG_eDong, "list = " + o.toString());
                if (o != null) {
                    banks = Bank.convertToList(o);
                    Log.d(Common.TAG_eDong, "bank size = " + banks.size());
                    adapter = new RepayAdapter(banks);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        });


    }
}
