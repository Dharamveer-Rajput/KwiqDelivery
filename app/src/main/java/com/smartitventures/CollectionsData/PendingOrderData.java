package com.smartitventures.CollectionsData;

import com.smartitventures.Models.AcceptedOrderModel;
import com.smartitventures.Models.PendingOrderModel;

import java.util.ArrayList;

/**
 * Created by dharamveer on 16/1/18.
 */

public class PendingOrderData  {

    public static ArrayList<PendingOrderModel> getPendingOrder() {

        ArrayList<PendingOrderModel> pendingOrderModelArrayList = new ArrayList<>();

        PendingOrderModel p = new PendingOrderModel();


        p.setTitle("Smart It Ventures");
        p.setTitleDescription("SCF 110-111 Giani Zail Singh Colony, Ropar, Punjab.");
        p.setPrice("200");
        p.setTime("12:00");
        p.setDistance("2");

        pendingOrderModelArrayList.add(p);


        p.setTitle("Smart It Ventures");
        p.setTitleDescription("SCF 110-111 Giani Zail Singh Colony, Ropar, Punjab.");
        p.setPrice("200");
        p.setTime("12:00");
        p.setDistance("2");

        pendingOrderModelArrayList.add(p);


        p.setTitle("Smart It Ventures");
        p.setTitleDescription("SCF 110-111 Giani Zail Singh Colony, Ropar, Punjab.");
        p.setPrice("200");
        p.setTime("12:00");
        p.setDistance("2");

        pendingOrderModelArrayList.add(p);




        p.setTitle("Smart It Ventures");
        p.setTitleDescription("SCF 110-111 Giani Zail Singh Colony, Ropar, Punjab.");
        p.setPrice("200");
        p.setTime("12:00");
        p.setDistance("2");

        pendingOrderModelArrayList.add(p);






        return pendingOrderModelArrayList;
    }



}
