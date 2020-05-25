package com.appfree.newedong.fragment.supportView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appfree.newedong.R;
import com.appfree.newedong.common.CustomInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformationSupportFragment extends Fragment {

    @BindView(R.id.tv_cmnd)
    TextView tvCmnd;
    @BindView(R.id.tv_number_card)
    TextView tvNumberCard;
    @BindView(R.id.tv_bank_card)
    TextView tvBankCard;
    @BindView(R.id.tv_name_card)
    TextView tvNameCard;


    public InformationSupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);
        initView();
    }

    private void initView() {
        String cmnd = CustomInfo.getInstance().userInfoResult.getUserIdentity().getIdCard();
        if (checkNull(cmnd)){
            tvCmnd.setText(cmnd);
        }

        String number = CustomInfo.getInstance().userInfoResult.getUserBankCard().getAccountNumber();
        if (checkNull(number)){
            tvNumberCard.setText(number);
        }

        String name = CustomInfo.getInstance().userInfoResult.getUserBankCard().getAccountName();
        if (checkNull(name)){
            tvNameCard.setText(name);
        }

        String card = CustomInfo.getInstance().userInfoResult.getUserBankCard().getBankName();
        if (checkNull(card)){
            tvBankCard.setText(card);
        }


    }

    private boolean checkNull(String text){
        if (text != null && !text.isEmpty()) {
            return true;
        }
        return false;
    }
}
