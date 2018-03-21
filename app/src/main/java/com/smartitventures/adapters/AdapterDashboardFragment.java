package com.smartitventures.adapters;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kwiqdelivery.R;
import com.smartitventures.AppConstants;
import com.smartitventures.Response.AssignedOrderResponse.AssignedOrderPayload;
import com.smartitventures.applicationclass.AppController;
import com.smartitventures.di.modules.SharedPrefsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by dharamveer on 16/1/18.
 */

public class AdapterDashboardFragment extends RecyclerView.Adapter<AdapterDashboardFragment.MyHolder> {

    Context context;
    View view;
    ArrayList<AssignedOrderPayload> assignedOrderPayloadArrayList = new ArrayList<>();
    public RowClickListener rowClickListener;
    int row_index = -1;

    @Inject
    public SharedPrefsHelper sharedPrefsHelper;

    public AdapterDashboardFragment(Context context, ArrayList<AssignedOrderPayload> assignedOrderPayloadArrayList) {


        this.context = context;
        this.assignedOrderPayloadArrayList = assignedOrderPayloadArrayList;

        ((AppController) context.getApplicationContext()).getComponent().inject(AdapterDashboardFragment.this);

    }



    public interface RowClickListener{

        void onClick(View view, int position,String lat,String lng,String name,double distance);

    }


    public void setOnItemClickListener(RowClickListener rowClickListener) {
        this.rowClickListener = rowClickListener;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dashboard_frag, parent, false);

        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {


        AssignedOrderPayload assignedOrderPayload = assignedOrderPayloadArrayList.get(position);



        if(position==0){

            holder.viewFirst.setVisibility(View.INVISIBLE);
        }

        if(position==assignedOrderPayloadArrayList.size()-1){

            holder.viewLast.setVisibility(View.INVISIBLE);

        }


        double lat = Double.parseDouble(assignedOrderPayload.getUserLat());
        double lng = Double.parseDouble(assignedOrderPayload.getUserLng());


        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();


            holder.txtDiskm.setText(String.valueOf(assignedOrderPayload.getDistance()));
            holder.txtDestTitle.setText(assignedOrderPayload.getName());
            holder.txtDesDes.setText(address);


        } catch (IOException e) {
            e.printStackTrace();
        }








        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                row_index=position;
                notifyDataSetChanged();



            }
        });


        if(row_index==position){


            holder.txtDiskm.setBackgroundResource(R.drawable.circleredtxtview);
            holder.txtDiskm.setTextColor(Color.WHITE);
            holder.card_view.setBackgroundResource(R.drawable.cardredborder);


            sharedPrefsHelper.put(AppConstants.BUSINESS_ID,assignedOrderPayload.getBussinessId());
            sharedPrefsHelper.put(AppConstants.ORDER_NO,assignedOrderPayload.getOrderNo());

            rowClickListener.onClick(holder.row,position,assignedOrderPayload.getUserLat(),
                    assignedOrderPayload.getUserLng(),assignedOrderPayload.getName(),assignedOrderPayload.getDistance());



        }
        else {


            holder.txtDiskm.setBackgroundResource(R.drawable.circletextview);//light grey
            holder.txtDiskm.setTextColor(Color.BLACK);
            holder.card_view.setBackgroundColor(Color.parseColor("#F2F2F2")); //light grey
            holder.card_view.setRadius(5f);

        }






    }

    @Override
    public int getItemCount() {
        return assignedOrderPayloadArrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {


        private TextView txtDiskm,txtDestTitle,txtDesDes;

        private View viewFirst,viewLast;

        private RelativeLayout row;
        private CardView card_view;

        public MyHolder(View itemView) {

            super(itemView);

            row = itemView.findViewById(R.id.row);
            card_view = itemView.findViewById(R.id.card_view);

            txtDiskm = itemView.findViewById(R.id.txtDiskm);
            txtDestTitle = itemView.findViewById(R.id.txtDestTitle);
            txtDesDes = itemView.findViewById(R.id.txtDesDes);

            viewFirst = itemView.findViewById(R.id.viewFirst);
            viewLast = itemView.findViewById(R.id.viewLast);



        }
    }
}
