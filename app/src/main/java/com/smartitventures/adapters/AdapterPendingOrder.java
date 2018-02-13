package com.smartitventures.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kwiqdelivery.R;
import com.smartitventures.Models.PendingOrderModel;
import com.smartitventures.Response.CompletedOrPendingOrder.CompletedPendingPayload;

import java.util.ArrayList;

/**
 * Created by dharamveer on 16/1/18.
 */

public class AdapterPendingOrder extends RecyclerView.Adapter<AdapterPendingOrder.MyHolder> {


    Context context;
    View view;
    ArrayList<CompletedPendingPayload> completedPendingPayloads = new ArrayList<>();


    public AdapterPendingOrder(Context context, ArrayList<CompletedPendingPayload> completedPendingPayloads) {
        this.context = context;
        this.completedPendingPayloads = completedPendingPayloads;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pending_orders, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {


        CompletedPendingPayload completedPendingPayload = completedPendingPayloads.get(position);


        holder.txtTitle.setText(completedPendingPayload.getName());
        holder.txtDescription.setText(completedPendingPayload.getOrderNo());
        holder.txtPrice.setText(String.valueOf(completedPendingPayload.getTotal()));
        holder.txtDistance.setText(String.valueOf(completedPendingPayload.getDistance()));

    }

    @Override
    public int getItemCount() {
        return completedPendingPayloads.size();
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

