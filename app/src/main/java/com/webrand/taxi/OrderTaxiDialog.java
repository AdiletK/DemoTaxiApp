package com.webrand.taxi;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.webrand.taxi.adapters.CompaniesAdapter;
import com.webrand.taxi.models.CompaniesModel;

import java.util.List;
import java.util.Objects;

public class OrderTaxiDialog extends DialogFragment {

    private List<CompaniesModel>companiesModelList;

    OrderTaxiDialog(List<CompaniesModel> companiesModelList) {
        this.companiesModelList = companiesModelList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.dialog_order_taxi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                dismiss();
            }
        });
        toolbar.setTitle("Order Taxi");

        RecyclerView recyclerView = view.findViewById(R.id.order_taxi_recycler_view);

        setRecyclerView(recyclerView,companiesModelList);
    }

    private void setRecyclerView(RecyclerView recyclerView, List<CompaniesModel> contactsModels) {
        CompaniesAdapter adapter = new CompaniesAdapter(getActivity(), contactsModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }
}