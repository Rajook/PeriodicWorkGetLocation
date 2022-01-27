package com.krunal.locationexample.Service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.krunal.locationexample.Utility.ClsGlobal;
import com.krunal.locationexample.Utility.Constants;

import java.util.List;

public class LocationWorker extends Worker {

    private Context context;
    private boolean CheckNewWorker = false;

    public LocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        Log.e("LocationWorker", "LocationWorker call");
    }


    @NonNull
    @Override
    public Result doWork() {
        Log.e("LocationWorker", "doWork call");

        Intent intent = new Intent(context, LocationBroadcastReceiver.class);
        context.sendBroadcast(intent);

        if (!isServiceRunning()){
            MyForegroundService.Companion.startService(context);
        }

//        Intent myServiceIntent = new Intent(context, MyForegroundService.class);
//        myServiceIntent.putExtra(Constants.inputExtra, "");
//        ContextCompat.startForegroundService(context, myServiceIntent);

        return Result.success();
    }

    private boolean isServiceRunning() {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(MyForegroundService.class.getName())){
                return true;
            }
        }
        return false;
    }
}
