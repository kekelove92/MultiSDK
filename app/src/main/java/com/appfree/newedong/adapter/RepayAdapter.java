package com.appfree.newedong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appfree.newedong.R;
import com.tsy.plutusnative.model.Bank;

import java.util.List;

public class RepayAdapter extends RecyclerView.Adapter<RepayAdapter.ViewHolder> {

    List<Bank> list;

    public RepayAdapter(List<Bank> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_list_repay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvAccountNumber.setText(list.get(position).getAccountNumber());
        holder.tvBankName.setText(list.get(position).getBankName());
        holder.tvAccountName.setText(list.get(position).getAccountName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccountName;
        TextView tvAccountNumber;
        TextView tvBankName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBankName = itemView.findViewById(R.id.tv_bank_card_sp);
            tvAccountNumber = itemView.findViewById(R.id.tv_number_card_sp);
            tvAccountName = itemView.findViewById(R.id.tv_name_card_sp);
        }
    }
}
