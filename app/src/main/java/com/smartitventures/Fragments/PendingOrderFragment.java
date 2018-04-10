package com.smartitventures.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kwiqdelivery.R;
import com.smartitventures.AppConstants;
import com.smartitventures.BaseFragment;
import com.smartitventures.Response.CompletedOrPendingOrder.CompletedOrPendingOrderSuccess;
import com.smartitventures.Response.CompletedOrPendingOrder.CompletedPendingPayload;
import com.smartitventures.Utils.NetUtils;
import com.smartitventures.adapters.AdapeterAcceptedOrder;
import com.smartitventures.adapters.AdapterPendingOrder;
import com.smartitventures.CollectionsData.PendingOrderData;
import com.smartitventures.Models.AcceptedOrderModel;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction LocationUpdateEvent.
 * Use the {@link PendingOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingOrderFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recyclerViewPendingOrder)
    RecyclerView recyclerViewPendingOrder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private Unbinder unbinder;

    private AdapterPendingOrder adapterPendingOrder;
    ArrayList<CompletedPendingPayload> completedPendingPayloads;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public PendingOrderFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PendingOrderFragment newInstance(String param1, String param2) {
        PendingOrderFragment fragment = new PendingOrderFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_pending_order, container, false);
        unbinder = ButterKnife.bind(this, view);
        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getActivity()).build();


        recyclerViewPendingOrder.setHasFixedSize(true);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        recyclerViewPendingOrder.setLayoutManager(mLayoutManager);
        recyclerViewPendingOrder.setItemAnimator(new DefaultItemAnimator());



        String driverID1 = String.valueOf(sharedPrefsHelper.get(AppConstants.DRIVER_ID,0));
        String bussinessId1 = String.valueOf(sharedPrefsHelper.get(AppConstants.BUSINESS_ID,0));
        String deliveryStatus = "0";


        if(NetUtils.hasConnectivity(getActivity())){
            compositeDisposable.add(apiService.completedOrPendingOrder(driverID1,deliveryStatus,bussinessId1)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<CompletedOrPendingOrderSuccess>() {
                        @Override
                        public void accept(CompletedOrPendingOrderSuccess completedOrPendingOrderSuccess) throws Exception {


                            if(completedOrPendingOrderSuccess.getIsSuccess()){


                                Log.d("completedPendOrder True",completedOrPendingOrderSuccess.getMessage());

                                completedPendingPayloads = new ArrayList<>(completedOrPendingOrderSuccess.getPayload());


                                adapterPendingOrder = new AdapterPendingOrder(getActivity(), completedPendingPayloads);

                                recyclerViewPendingOrder.setAdapter(adapterPendingOrder);

                            }
                            else {

                                Log.d("completedPendOrd False",completedOrPendingOrderSuccess.getMessage());


                                //showAlertDialog("Retry",completedOrPendingOrderSuccess.getMessage());
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("completedendOrd Failure",throwable.getMessage());


                        }
                    }));



        }
        else {
            internetDialog(getActivity());
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
