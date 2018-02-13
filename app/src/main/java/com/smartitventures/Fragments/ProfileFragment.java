package com.smartitventures.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kwiqdelivery.R;
import com.smartitventures.AppConstants;
import com.smartitventures.BaseFragment;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.tvTotalOrders)
    TextView tvTotalOrders;
    @BindView(R.id.tvDriverName)
    TextView tvDriverName;
    @BindView(R.id.tvDriverPhone)
    TextView tvDriverPhone;
    @BindView(R.id.tvDriverAddress)
    TextView tvDriverAddress;

    private Unbinder unbinder;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);


        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getActivity()).build();


        tvDriverName.setText(sharedPrefsHelper.get(AppConstants.USER_NAME,""));
        tvDriverPhone.setText(sharedPrefsHelper.get(AppConstants.PHONE_NUMBER,""));
        tvDriverAddress.setText(sharedPrefsHelper.get(AppConstants.ADDRESS,""));


        tvTotalOrders.setText(String.valueOf(sharedPrefsHelper.get(AppConstants.NO_OF_ORDER, 1)));


        return view;

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
