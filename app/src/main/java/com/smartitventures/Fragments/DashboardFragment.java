package com.smartitventures.Fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kwiqdelivery.R;
import com.smartitventures.AppConstants;
import com.smartitventures.BaseFragment;
import com.smartitventures.Dialog.SignatureDialogActivity;
import com.smartitventures.GoogleMapsDirections.DirectionsJSONParser;
import com.smartitventures.Response.AssignedOrderResponse.AssignedOrderPayload;
import com.smartitventures.Response.AssignedOrderResponse.AssignedOrderSuccess;
import com.smartitventures.Response.UpdateLatLng.UpdateLatLong;
import com.smartitventures.adapters.AdapterDashboardFragment;
import com.smartitventures.quickdelivery.DashboardActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DashboardFragment extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    @BindView(R.id.recyclerViewbottomsheet)
    RecyclerView recyclerViewbottomsheet;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    BottomSheetBehavior sheetBehavior;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tvTotalDistance)
    TextView tvTotalDistance;
    @BindView(R.id.tvDuration)
    TextView tvDuration;

    private double user_latitude, user_longitude;

    private static final int REQUEST_LOCATION = 0;

    private ArrayList<LatLng> mMarkerPoints;

    private AdapterDashboardFragment adapterDashboardFragment;
    private ArrayList<AssignedOrderPayload> assignedOrderPayloadArrayList = new ArrayList<>();

    private Unbinder unbinder;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private NoInternetDialog noInternetDialog;

    private static String TAG = "Dashboard Fragment ---------------------------------- ------>";

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Context context;
    private String driverId;
    private Handler h;
    private double curLat, curLong;
    private int markerCount;
    private Marker currentLocationMarker;

    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String ACTION_STOP = "STOP_ACTION";
    BroadcastReceiver mMessageReceiver;
    protected Location mCurrentLocation;
    SignatureDialogActivity signatureDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        markerCount = 0;

        context = getActivity();


        noInternetDialog = new NoInternetDialog.Builder(getActivity()).build();

        //Check if Google Play Services Available or not
        if (!getServicesAvailable()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            getActivity().finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapLayoutAddress);
        mapFragment.getMapAsync(this);

        driverId = String.valueOf(sharedPrefsHelper.get(AppConstants.DRIVER_ID, 0));



        // Initializing
        mMarkerPoints = new ArrayList<LatLng>();

        sheetBehavior = BottomSheetBehavior.from(bottomSheet);

        recyclerViewbottomsheet.setHasFixedSize(true);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewbottomsheet.setLayoutManager(mLayoutManager);
        recyclerViewbottomsheet.setItemAnimator(new DefaultItemAnimator());


        GetAssignedOrderApi();


        return view;

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
        compositeDisposable.dispose();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle b = intent.getBundleExtra("Location");
                Location lastKnownLoc =  b.getParcelable("Location");

                if (lastKnownLoc != null) {

                  /*  Toast.makeText(getContext(), "Latitude: =" + lastKnownLoc.getLatitude() + " Longitude:=" + lastKnownLoc.getLongitude(),
                            Toast.LENGTH_SHORT).show();*/


                    curLat  = lastKnownLoc.getLatitude();
                    curLong = lastKnownLoc.getLongitude();


                    if (currentLocationMarker != null) {
                        currentLocationMarker.remove();
                    }



                    compositeDisposable.add(apiService.updateLatLng(driverId,String.valueOf(curLat),String.valueOf(curLong))
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<UpdateLatLong>() {
                                @Override
                                public void accept(UpdateLatLong updateLatLong) throws Exception {


                                    if(updateLatLong.getIsSuccess()){


                                    }
                                    else {

                                        showAlertDialog("Retry", updateLatLong.getMessage());

                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    showAlertDialog("Retry", throwable.getMessage());

                                }
                            }));





                    if (mMarkerPoints.size() < 2) {


                        LatLng point = new LatLng(curLat, curLong);

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLat, curLong), 13));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(curLat,curLong)  )    // Sets the center of the map to location user
                                .zoom(17)                   // Sets the zoom
                                .bearing(10)                // Sets the orientation of the camera to east
                                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        //Add pointer to the map at location
                        addMarker(point, mMap, curLat, curLong);

                    }

                }

            }

        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("GPSLocationUpdates"));

    }

    private void GetAssignedOrderApi() {


        compositeDisposable.add(apiService.getAssignedOrder(driverId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AssignedOrderSuccess>() {
                    @Override
                    public void accept(AssignedOrderSuccess assignedOrderSuccess) throws Exception {


                        if (assignedOrderSuccess.getIsSuccess()) {

                            assignedOrderPayloadArrayList = new ArrayList<>(assignedOrderSuccess.getPayload());

                            int noOfOrder = assignedOrderPayloadArrayList.size();

                            ((DashboardActivity) getActivity()).getOrdersText().setText(String.valueOf(noOfOrder));

                            sharedPrefsHelper.put(AppConstants.NO_OF_ORDER, noOfOrder);


                            adapterDashboardFragment = new AdapterDashboardFragment(getActivity(), assignedOrderPayloadArrayList);

                            recyclerViewbottomsheet.setAdapter(adapterDashboardFragment);

                            adapterDashboardFragment.notifyDataSetChanged();

                            adapterDashboardFragment.setOnItemClickListener(new AdapterDashboardFragment.RowClickListener() {
                                @Override
                                public void onClick(View view, int position, String lat, String lng, String customername,double distance) {

                                    sharedPrefsHelper.put(AppConstants.USER_LAT, String.valueOf(user_latitude));
                                    sharedPrefsHelper.put(AppConstants.USER_LONG, String.valueOf(user_longitude));


                                    mMap.clear();

                                    //checkContinuousDistance(Double.parseDouble(lat), Double.parseDouble(lng));


                                    // float results[] = new float[10];

                                    // Location.distanceBetween(curLat, curLong, Double.parseDouble(lat), Double.parseDouble(lng), results);
                                    //Toast.makeText(getActivity(), "Distance Updated" + results[0], Toast.LENGTH_SHORT).show();


                                    mMessageReceiver = new BroadcastReceiver() {
                                        @Override
                                        public void onReceive(Context context, Intent intent) {


                                            Bundle b = intent.getBundleExtra("Location");
                                            Location lastKnownLoc = b.getParcelable("Location");

                                            if (lastKnownLoc != null) {


                                                curLat = lastKnownLoc.getLatitude();
                                                curLong = lastKnownLoc.getLongitude();


                                                double userLat = Double.parseDouble(sharedPrefsHelper.get(AppConstants.USER_LAT, ""));
                                                double userLong = Double.parseDouble(sharedPrefsHelper.get(AppConstants.USER_LONG, ""));


                                                float results[] = new float[10];

                                                Location.distanceBetween(curLat, curLong, Double.valueOf(lat), Double.valueOf(lng), results);


                                                final float radius = 100;


                                               // Toast.makeText(getActivity(), "Running Distance : " + results[0] ,Toast.LENGTH_SHORT).show();


                                                if(results[0]<=radius){

                                                    startActivity(new Intent(getActivity(),SignatureDialogActivity.class));

                                                }






                                            }
                                        }
                                    };

                                    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("GPSLocationUpdates"));


                                    tvTotalDistance.setText("Total Distance " + distance + " apart.");

                                    LatLng point = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                                    mMarkerPoints.add(point);

                                    // Checks, whether start and end locations are captured
                                    if (mMarkerPoints.size() >= 2) {


                                        LatLng origin = new LatLng(curLat,curLong);
                                        LatLng dest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                                        // Getting URL to the Google Directions API
                                        String url = getDirectionsUrl(origin, dest);

                                        DownloadTask downloadTask = new DownloadTask();

                                        // Start downloading json data from Google Directions API
                                        downloadTask.execute(url);
                                    }


                                }
                            });

                        } else {
                            showAlertDialog("Retry", "You Have No Orders");

                        }
                    }






                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));


    }

    Marker mk = null;

    // Add A Map Pointer To The MAp
    public void addMarker(LatLng point, GoogleMap googleMap, double lat, double lon) {


        mMarkerPoints.add(point);

        mMap.addMarker(new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromResource(com.ahmadrosid.lib.drawroutemap.R.drawable.delivery)));

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (mMarkerPoints.size() == 1) {
            animateMarker(mLastLocation, mk);
        } else if (mMarkerPoints.size() == 2) {
            animateMarker(mLastLocation, mk);
        } else if (markerCount == 0) {
            //Set Custom BitMap for Pointer
            int height = 80;
            int width = 45;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.delivery);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            mMap = googleMap;

            LatLng latlong = new LatLng(lat, lon);
            mk = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3))
                    .icon(BitmapDescriptorFactory.fromBitmap((smallMarker))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));

            //Set Marker Count to 1 after first marker is created
            markerCount = 1;

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                startLocationUpdates();

            }
        }


    }


    private void createLocationRequest() {
        //Creating location request object
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(AppConstants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(AppConstants.FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    public boolean getServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(getActivity());
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {

            Dialog dialog = api.getErrorDialog(getActivity(), isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "Cannot Connect To Play Services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onLocationChanged(Location location) {


        Log.d("onLocationChanged", "entered");

      /*  mLastLocation = location;

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        curLat = location.getLatitude();
        curLong = location.getLongitude();


        double userLat = Double.parseDouble(sharedPrefsHelper.get(AppConstants.USER_LAT, ""));
        double userLong = Double.parseDouble(sharedPrefsHelper.get(AppConstants.USER_LONG, ""));


        float results[] = new float[10];

        Location.distanceBetween(curLat, curLong, userLat, userLong, results);
        Toast.makeText(getActivity(), "Distance Updated" + results[0], Toast.LENGTH_SHORT).show();


        final float radius = 50;
        if(results[0]<=radius){

            SignatureDialogActivity signatureDialog = new SignatureDialogActivity(getActivity());
            signatureDialog.show();


        }

        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        String loc = "" + latitude + " ," + longitude + " ";

        Toast.makeText(getActivity(), loc, Toast.LENGTH_SHORT).show();

        driverId = String.valueOf(sharedPrefsHelper.get(AppConstants.DRIVER_ID, 0));

        compositeDisposable.add(apiService.getAssignedOrder(driverId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AssignedOrderSuccess>() {
                    @Override
                    public void accept(AssignedOrderSuccess assignedOrderSuccess) throws Exception {


                        if (assignedOrderSuccess.getIsSuccess()) {

                            assignedOrderPayloadArrayList = new ArrayList<>(assignedOrderSuccess.getPayload());

                            int noOfOrder = assignedOrderPayloadArrayList.size();

                            ((DashboardActivity) getActivity()).getOrdersText().setText(String.valueOf(noOfOrder));

                            sharedPrefsHelper.put(AppConstants.NO_OF_ORDER, noOfOrder);


                            adapterDashboardFragment = new AdapterDashboardFragment(getActivity(), assignedOrderPayloadArrayList);

                            recyclerViewbottomsheet.setAdapter(adapterDashboardFragment);

                            adapterDashboardFragment.notifyDataSetChanged();

                            adapterDashboardFragment.setOnItemClickListener(new AdapterDashboardFragment.RowClickListener() {
                                @Override
                                public void onClick(View view, int position, String lat, String lng, String customername) {


                                    user_latitude = Double.parseDouble(lat);
                                    user_longitude = Double.parseDouble(lng);

                                    sharedPrefsHelper.put(AppConstants.USER_LAT, String.valueOf(user_latitude));
                                    sharedPrefsHelper.put(AppConstants.USER_LONG, String.valueOf(user_longitude));


                                    float results[] = new float[10];

                                    Location.distanceBetween(curLat, curLong, user_latitude, user_longitude, results);
                                    Toast.makeText(getActivity(), "Distance Updated" + results[0], Toast.LENGTH_SHORT).show();


                                    tvTotalDistance.setText("Total Distance " + formatNumber(results[0]) + " apart.");

                                    LatLng point = new LatLng(user_latitude, user_longitude);

                                    mMarkerPoints.add(point);

                                    // Checks, whether start and end locations are captured
                                    if (mMarkerPoints.size() >= 2) {


                                        LatLng origin = mMarkerPoints.get(0);
                                        LatLng dest = mMarkerPoints.get(1);

                                        // Getting URL to the Google Directions API
                                        String url = getDirectionsUrl(origin, dest);

                                        DownloadTask downloadTask = new DownloadTask();

                                        // Start downloading json data from Google Directions API
                                        downloadTask.execute(url);
                                    }


                                }
                            });

                        } else {
                            showAlertDialog("Retry", "You Have No Orders");

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));


        if (mMarkerPoints.size() < 2) {


            LatLng point = new LatLng(latitude, longitude);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(10)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            //Add pointer to the map at location
            addMarker(point, mMap, latitude, longitude);

        }


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
*/


    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            //PolylineOptions lineOptions = null;
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(6);
            lineOptions.color(Color.RED);

            String distance = "";
            String duration = "";

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }


                lineOptions.addAll(points);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(path.get(path.size() - 1).get("lat")), Double.parseDouble(path.get(path.size() - 1).get("lng"))))
                        .icon(BitmapDescriptorFactory.fromResource(com.ahmadrosid.lib.drawroutemap.R.drawable.homemarker)));

            }


            // Drawing polyline in the Google Map for the i-th route
            if (points.size() != 0) mMap.addPolyline(lineOptions);//to avoid crash

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);


            tvDuration.setVisibility(View.VISIBLE);
            tvDuration.setText("Time to deliver: " + duration);
        }

    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception dowoading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }



    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
    }



    //Starting the location updates
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Check Permissions Now
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }


    public static void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_LONG).show();

    }


    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    private synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }


    @OnClick(R.id.fab)
    public void onViewClicked() {

        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

//        startLocationUpdates();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.e("onConnected-->", bundle + "");

        createLocationRequest();

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, connectionResult.getErrorMessage());
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private boolean checkLocationPermission() {


        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }


    }


}
