package com.smartitventures.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.kwiqdelivery.R;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction LocationUpdateEvent.
 * Use the {@link OrderHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.txtAccepted)
    TextView txtAccepted;
    @BindView(R.id.txtPending)
    TextView txtPending;
    @BindView(R.id.frameLayoutOrder)
    FrameLayout frameLayoutOrder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Unbinder unbinder;



    private OnFragmentInteractionListener mListener;




    // TODO: Rename and change types and number of parameters
    public static OrderHistoryFragment newInstance(String param1, String param2) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        unbinder = ButterKnife.bind(this, view);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getActivity()).build();

        if (savedInstanceState == null) {

            Fragment fragment = null;
            fragment = new CompletedOrderFragment();

            if (fragment != null) {


                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameLayoutOrder, fragment, "acce").commit();



            }


        }



        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



    @OnClick({R.id.txtAccepted, R.id.txtPending})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txtAccepted:


                txtAccepted.setBackgroundResource(R.drawable.layout_bg_white);
                txtPending.setBackgroundResource(R.drawable.layout_bg_red);

                txtAccepted.setTextColor(Color.parseColor("#c3002d")); //red
                txtPending.setTextColor(Color.parseColor("#ffffff"));  //white



                CompletedOrderFragment accedptedOrderFragment = new CompletedOrderFragment();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.frameLayoutOrder,accedptedOrderFragment,"accedptedOrderFragment");
                transaction.commit();





                break;

            case R.id.txtPending:


                txtPending.setBackgroundResource(R.drawable.layout_bg_white);
                txtAccepted.setBackgroundResource(R.drawable.layout_bg_red);

                txtPending.setTextColor(Color.parseColor("#c3002d"));  //red
                txtAccepted.setTextColor(Color.parseColor("#ffffff")); //white


                PendingOrderFragment pendingOrderFragment = new PendingOrderFragment();
                FragmentManager manager1 = getFragmentManager();
                FragmentTransaction transaction1 = manager1.beginTransaction();
                transaction1.replace(R.id.frameLayoutOrder,pendingOrderFragment,"pendingOrderFragment");
                transaction1.commit();




                break;
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
