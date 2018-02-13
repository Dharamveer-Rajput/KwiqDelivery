package com.smartitventures.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kwiqdelivery.R;
import com.smartitventures.Models.AcceptedOrderModel;
import com.smartitventures.Response.CompletedOrPendingOrder.CompletedOrPendingOrderSuccess;
import com.smartitventures.Response.CompletedOrPendingOrder.CompletedPendingPayload;

import java.util.ArrayList;

/**
 * Created by dharamveer on 16/1/18.
 */

public class AdapeterAcceptedOrder extends RecyclerView.Adapter<AdapeterAcceptedOrder.MyHolder> {


    Context context;
    View view;
    ArrayList<CompletedPendingPayload> completedOrPendingOrderSuccesses = new ArrayList<>();

    public AdapeterAcceptedOrder(Context context, ArrayList<CompletedPendingPayload> completedOrPendingOrderSuccesses) {
        this.context = context;
        this.completedOrPendingOrderSuccesses = completedOrPendingOrderSuccesses;
    }

    @Override
    public AdapeterAcceptedOrder.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_accepted_orders, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapeterAcceptedOrder.MyHolder holder, int position) {


        CompletedPendingPayload completedPendingPayload = completedOrPendingOrderSuccesses.get(position);


        holder.txtTitle.setText(completedPendingPayload.getName());
        holder.txtDescription.setText(completedPendingPayload.getOrderNo());
        holder.txtPrice.setText(String.valueOf(completedPendingPayload.getTotal()));
        holder.txtDistance.setText(String.valueOf(completedPendingPayload.getDistance()));
    }

    @Override
    public int getItemCount() {
        return completedOrPendingOrderSuccesses.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {


        private TextView txtTitle,txtDescription,txtPrice,txtTime,txtDistance;

        public MyHolder(View itemView) {
            super(itemView);


            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDistance = itemView.findViewById(R.id.txtDistance);

        }
    }
}
