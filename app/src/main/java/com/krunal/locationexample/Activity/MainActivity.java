package com.krunal.locationexample.Activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.krunal.locationexample.Service.MyForegroundService;
import com.krunal.locationexample.Utility.ClsCheckLocation;
import com.krunal.locationexample.R;
import com.krunal.locationexample.Utility.ClsGlobal;
import com.krunal.locationexample.Utility.Constants;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private TextView mTvMac;
    private Button check, Logs;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    private boolean isDownloading = false;

    String url = "";
    String name = "";
    String id = "";


    public static final String  BROADCAST_DOWNLOAD_EVENT = "BROADCAST_DOWNLOAD_EVENT";
    public static final String  BROADCAST_DOWNLOAD_EVENT_PROGRESS = "download-progress";
    public static final String  BROADCAST_DOWNLOAD_EVENT_NAME = "BROADCAST_DOWNLOAD_EVENT_NAME";
    public static final String  BROADCAST_DOWNLOAD_PRODUCT_NAME = "product-name";
    public static final String  BROADCAST_DOWNLOAD_PRODUCT_ID = "product-id";
    public static final String  BROADCAST_DOWNLOAD_PRODUCT_URL = "product-url";
    public static final String  BROADCAST_DOWNLOAD_EVENT_START = "download-start";
    public static final String  BROADCAST_DOWNLOAD_EVENT_SUCCESS = "download-success";
    public static final String BROADCAST_DOWNLOAD_EVENT_FAILED = "download-failed";

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(BROADCAST_DOWNLOAD_EVENT_PROGRESS,0);
            String event = intent.getStringExtra(BROADCAST_DOWNLOAD_EVENT_NAME);
            name = intent.getStringExtra(BROADCAST_DOWNLOAD_PRODUCT_NAME);
            id = intent.getStringExtra(BROADCAST_DOWNLOAD_PRODUCT_ID);
            url = intent.getStringExtra(BROADCAST_DOWNLOAD_PRODUCT_URL);

            switch (event){
                case BROADCAST_DOWNLOAD_EVENT_START:{
                    isDownloading = true;
                }break;
                case BROADCAST_DOWNLOAD_EVENT_SUCCESS:{
                    Toast.makeText(getApplicationContext(), "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    isDownloading = false;
                }break;
                case BROADCAST_DOWNLOAD_EVENT_FAILED:{
                    isDownloading = false;
                }break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        check = findViewById(R.id.check);
        Logs = findViewById(R.id.Logs);
        mTvMac = findViewById(R.id.tvMac);

        mTvMac.setText(getMacAddress());

        requestLocationPermission();

        Log.e("Hour", String.valueOf(ClsGlobal.getCurrentHour()));

        int getCurrentHour = ClsGlobal.getCurrentHour();


        Log.e("isWorkScheduled",
                String.valueOf(ClsGlobal.isWorkScheduled(ClsGlobal.packageName.concat(".Location"))));


        check.setOnClickListener(v -> {

            // To Check if GPS is on or not.
            if (CheckGpsStatus()) {
                ClsCheckLocation.checkLocationServiceNew(MainActivity.this);
            }

            ClsGlobal.ScheduleWorker(ClsGlobal.packageName.concat(".Location.15Minutes"),15);

        });

        Logs.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LogsActivity.class);
            startActivity(intent);
        });


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(BROADCAST_DOWNLOAD_EVENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(BROADCAST_DOWNLOAD_EVENT_SUCCESS));
    }


    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }


    public boolean CheckGpsStatus() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void startService(View view){
//        Intent myServiceIntent = new Intent(this, MyForegroundService.class);
//        myServiceIntent.putExtra(Constants.inputExtra, "");
//        ContextCompat.startForegroundService(this, myServiceIntent);
        if (!isServiceRunning()){
            MyForegroundService.Companion.startService(this);
        }else {
            Toast.makeText(this, "Service already in process!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isServiceRunning() {
        final ActivityManager activityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(MyForegroundService.class.getName())){
                return true;
            }
        }
        return false;
    }

    public void stopService(View view){
        Intent myServiceIntent = new Intent(this, MyForegroundService.class);
        stopService(myServiceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif: all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "Mac Address Not Found";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b: macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }

}
