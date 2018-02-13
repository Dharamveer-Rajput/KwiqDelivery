package com.smartitventures.CollectionsData;

import com.smartitventures.Models.AcceptedOrderModel;

import java.util.ArrayList;

/**
 * Created by dharamveer on 16/1/18.
 */

public class AccedptedOrderData {


    public static ArrayList<AcceptedOrderModel> getAccedptedOrder() {

        ArrayList<AcceptedOrderModel> acceptedOrderModelArrayList = new ArrayList<>();

        AcceptedOrderModel a = new AcceptedOrderModel();


        a.setTitle("Smart It Ventures");
        a.setTitleDescription("SCF 110-111 Giani Zail Singh Colony, Ropar, Punjab.");
        a.setPrice("200");
        a.setTime("12:00");
        a.setDistance("2");

        acceptedOrderModelArrayList.add(a);


        a.setTitle("Smart It Ventures");
        a.setTitleDescription("SCF 110-111 Giani Zail Singh Colony, Ropar, Punjab.");
        a.setPrice("200");
        a.setTime("12:00");
        a.setDistance("2");
        acceptedOrderModelArrayList.add(a);


        a.setTitle("Smart It Ventures");
        a.setTitleDescription("SCF 110-111 Giani Zail Singh Colony, Ropar, Punjab.");
        a.setPrice("200");
        a.setTime("12:00");
        a.setDistance("2");
        acceptedOrderModelArrayList.add(a);



        a.setTitle("Smart It Ventures");
        a.setTitleDescription("SCF 110-111 Giani Zail Singh Colony, Ropar, Punjab.");
        a.setPrice("200");
        a.setTime("12:00");
        a.setDistance("2");

        acceptedOrderModelArrayList.add(a);







        return acceptedOrderModelArrayList;
    }


}
