package com.smartitventures.quickdelivery;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.droidbyme.dialoglib.AnimUtils;
import com.droidbyme.dialoglib.DroidDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.Dash;
import com.kwiqdelivery.R;
import com.rom4ek.arcnavigationview.ArcNavigationView;
import com.smartitventures.AppConstants;
import com.smartitventures.BaseActivity;
import com.smartitventures.Fragments.DashboardFragment;
import com.smartitventures.Fragments.OrderHistoryFragment;
import com.smartitventures.Fragments.ProfileFragment;
import com.smartitventures.SharedPreferences.UserDataUtility;
import com.smartitventures.notification.NotificationHandler;
import com.smartitventures.service.LocationService;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class DashboardActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int REQUEST_CHECK_SETTINGS = 1;
    private static final String TAG = "DashboardActivity";
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    TextView totalOrders;
    private CircleImageView profile_image;
    private TextView tvDriverName, tvDriverPhone;

    Context context;
    NoInternetDialog noInternetDialog;


    private boolean mIsServiceStarted = false;
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String ACTION_STOP = "STOP_ACTION";
    public static final String ACTION_FROM_NOTIFICATION = "isFromNotification";
    private String action;
    private int notifID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        displayLocationSettingsRequest(this);

        DashboardActivityPermissionsDispatcher.getLocationWithPermissionCheck(this);

        context  = this;
        noInternetDialog = new NoInternetDialog.Builder(context).build();

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationDrawerCode();

        if (savedInstanceState == null) {

            Fragment fragment = null;
            fragment = new DashboardFragment();

            if (fragment != null) {


                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_layout, fragment, "dashboard").commit();

                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                    fragmentManager.popBackStack();
                }

            }

        }

    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(DashboardActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getAction() != null) {
            action = getIntent().getAction();
            notifID = getIntent().getIntExtra(EXTRA_NOTIFICATION_ID, 0);
            if (action.equalsIgnoreCase(ACTION_FROM_NOTIFICATION)) {
                mIsServiceStarted = true;

            }
        }
    }


    /**
     * Method to generate OnGoingLocationNotification
     *
     * @param mcontext
     */
    public static void OnGoingLocationNotification(Context mcontext) {
        int mNotificationId;

        mNotificationId = (int) System.currentTimeMillis();

        //Broadcast receiver to handle the stop action
        Intent mstopReceive = new Intent(mcontext, NotificationHandler.class);
        mstopReceive.putExtra(EXTRA_NOTIFICATION_ID, mNotificationId);
        mstopReceive.setAction(ACTION_STOP);
        PendingIntent pendingIntentStopService = PendingIntent.getBroadcast(mcontext, (int) System.currentTimeMillis(), mstopReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mcontext)
                        .setSound(alarmSound)
                        .setSmallIcon(R.drawable.marker)
                        .setContentTitle("Location Service")
                        .addAction(R.drawable.ic_action_close, "Stop Service", pendingIntentStopService)
                        .setOngoing(true).setContentText("Running...");
        mBuilder.setAutoCancel(false);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mcontext, DashboardActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.setAction(ACTION_FROM_NOTIFICATION);
        resultIntent.putExtra(EXTRA_NOTIFICATION_ID, mNotificationId);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mcontext, (int) System.currentTimeMillis(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancel(mNotificationId);

        Notification mNotification = mBuilder.build();
        mNotification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify(mNotificationId, mNotification);

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }



    private void navigationDrawerCode() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ArcNavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        tvDriverName = hView.findViewById(R.id.tvDriverName);
        tvDriverPhone = hView.findViewById(R.id.tvDriverPhone);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        tvDriverName.setText(sharedPrefsHelper.get(AppConstants.USER_NAME,""));
        tvDriverPhone.setText(sharedPrefsHelper.get(AppConstants.PHONE_NUMBER,""));

        totalOrders = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.my_order));

        //This method will initialize the count value
        initializeCountDrawer();



    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void initializeCountDrawer() {

        //Gravity property aligns the text
        totalOrders.setGravity(Gravity.CENTER_VERTICAL);
        totalOrders.setTypeface(null, Typeface.BOLD);
        totalOrders.setTextColor(getResources().getColor(R.color.colorAccent));

        totalOrders.setText("");


    }
    @NonNull
    public TextView getOrdersText(){

        return totalOrders;
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment;


        switch (id){

            case R.id.home:

                fragment = new DashboardFragment();
                fragmentManager.beginTransaction().replace(R.id.main_layout, fragment, "dashboardFragment").addToBackStack(null).commit();

                toolbarTitle.setText("Dashboard");
                break;

            case R.id.my_profile:

                fragment = new ProfileFragment();
                fragmentManager.beginTransaction().replace(R.id.main_layout, fragment, "profileFragment").addToBackStack(null).commit();

                toolbarTitle.setText("Profile");

                break;


            case R.id.my_order:

                fragment = new OrderHistoryFragment();
                fragmentManager.beginTransaction().replace(R.id.main_layout, fragment, "orderHistoryFragment").addToBackStack(null).commit();

                toolbarTitle.setText("Order History");

                break;

            case R.id.log_out_frag:

                new DroidDialog.Builder(context)
                        .icon(R.drawable.ic_action_tick)
                        .title("Logout")
                        .content(getString(R.string.areyousuretologout))
                        .cancelable(true, true)
                        .positiveButton("YES", new DroidDialog.onPositiveListener() {
                            @Override
                            public void onPositive(Dialog droidDialog) {
                                droidDialog.dismiss();
                                // Toast.makeText(context, "YES", Toast.LENGTH_SHORT).show();

                                UserDataUtility.setLogin(false,DashboardActivity.this);
                                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();


                            }
                        })
                        .negativeButton("No", new DroidDialog.onNegativeListener() {
                            @Override
                            public void onNegative(Dialog droidDialog) {
                                droidDialog.dismiss();
                                //Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .neutralButton("SKIP", new DroidDialog.onNeutralListener() {
                            @Override
                            public void onNeutral(Dialog droidDialog) {
                                droidDialog.dismiss();
                                // Toast.makeText(context, "Skip", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .typeface("regular.ttf")
                        .animation(AnimUtils.AnimZoomInOut)
                        .color(ContextCompat.getColor(context, R.color.signinbtncolor), ContextCompat.getColor(context, R.color.white),
                                ContextCompat.getColor(context, R.color.dark_indigo))
                        .divider(true, ContextCompat.getColor(context, R.color.orange))
                        .show();




                break;




            case R.id.settings_frag:
                break;



        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getLocation() {


        if (!mIsServiceStarted)
        {
            mIsServiceStarted = true;
            // OnGoingLocationNotification(this);
            startService(new Intent(this, LocationService.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DashboardActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getLocationOnShowRationale(final PermissionRequest request) {
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getLocationOnPermissionDenied() {
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getLocationOnNeverAskAgain() {
    }
}
