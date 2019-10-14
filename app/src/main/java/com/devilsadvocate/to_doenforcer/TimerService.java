package com.devilsadvocate.to_doenforcer;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class TimerService extends Service {

    public static final long NOTIFY_INTERVAL = 1000;
    private Timer mTimer = null;
    private Handler mHandler = new Handler();

    private Intent lockIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        lockIntent = new Intent(this, MainActivity.class);
        lockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 2, NOTIFY_INTERVAL);
    }

    @Override
    public void onDestroy()
    {
        mTimer.cancel();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        assert alarmService != null;
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
        Log.e("Service_Auto_Restart", "ON");
    }

    class TimeDisplayTimerTask extends TimerTask
    {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    LockApps();
                }

            });
        }

    }

    private void LockApps()
    {
        if (!printForegroundTask().equalsIgnoreCase("com.devilsadvocate.to_doenforcer"))
        {
            startActivity(lockIntent);
        }
    }

    private String printForegroundTask()
    {
        String currentApp = "";
        @SuppressLint("WrongConstant") UsageStatsManager usm = (UsageStatsManager) this.getSystemService("usagestats");
        long time = System.currentTimeMillis();
        assert usm != null;
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            assert am != null;
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        Log.e("Foreground App", "Current App in foreground is: " + currentApp);
        return currentApp;
    }
}
