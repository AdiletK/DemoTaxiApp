package com.webrand.taxi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.button.MaterialButton;
import com.webrand.taxi.R;
import com.webrand.taxi.models.CompaniesModel;
import com.webrand.taxi.utils.Utils;


import java.util.List;

public class CompaniesAdapter extends RecyclerView.Adapter<CompaniesAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<CompaniesModel> companiesModels;
    private Utils utils;


    public CompaniesAdapter(Context ctx, List<CompaniesModel> companiesModels){

        inflater = LayoutInflater.from(ctx);
        this.companiesModels = companiesModels;
        utils = new Utils(ctx);
    }

    @NonNull
    @Override
    public CompaniesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.order_taxi_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompaniesAdapter.MyViewHolder holder, int position) {
        holder.taxi_name.setText(companiesModels.get(position).name);
        holder.sms.setText(String.valueOf(companiesModels.get(position).contacts.get(0).contact));
        holder.nomer.setText(String.valueOf(companiesModels.get(position).contacts.get(1).contact));

    }

    @Override
    public int getItemCount() {
        return companiesModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView sms;
        TextView nomer;
        TextView taxi_name;
        MaterialButton btn_call,btn_send;

        MyViewHolder(View itemView) {
            super(itemView);
            sms = itemView.findViewById(R.id.type);
            nomer = itemView.findViewById(R.id.contact);
            taxi_name = itemView.findViewById(R.id.taxi_name);
            btn_call = itemView.findViewById(R.id.btn_call);
            btn_send = itemView.findViewById(R.id.btn_sms);

            btn_send.setOnClickListener(this);
            btn_call.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_call:
                    utils.call(nomer.getText().toString());
                    break;
                case R.id.btn_sms:
                    utils.sendSMS(sms.getText().toString());
                    break;
            }
        }
    }
}