package com.devilsadvocate.to_doenforcer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;

    private TaskViewModel mTaskViewModel;

    //strings
    String package_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register Broadcast Receiver so app starts up on user unlock
        final String USER_PRESENT = "android.intent.action.USER_PRESENT";
        IntentFilter intentFilter = new IntentFilter(USER_PRESENT);
        UserUnlockPhone userUnlockPhone = new UserUnlockPhone();

        registerReceiver(userUnlockPhone, intentFilter);

        package_name = getPackageName();

        mTaskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final TaskListAdaptor adapter = new TaskListAdaptor(this, new TaskListAdaptor.TaskAdaptorListener() {
            @Override
            public void completeTaskOnClick(Task task) {
                completeTask(task);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_settings, null));
        recyclerView.addItemDecoration(divider);
        mTaskViewModel.getAllUncompletedTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setTasks(words);

                int startTime = 0;
                int endTime = 24;
                if (betweenTwoTimes(startTime, endTime) && adapter.getItemCount() != 0)
                {
                    Intent intent_service = new Intent(getApplicationContext(), TimerService.class);
                    intent_service.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startService(intent_service);
                }
                else
                {
                    Intent intent_service = new Intent(getApplicationContext(), TimerService.class);
                    intent_service.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    stopService(intent_service);
                }

            }
        });

        permission_check();
    }

    private boolean betweenTwoTimes(int startTime, int endTime)
    {
        int hours = (endTime - startTime); // here hours will be 14

        Calendar cal = Calendar.getInstance();
        // set calendar to TODAY 21:00:00.000
        cal.set(Calendar.HOUR_OF_DAY, startTime);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long startHourMilli = cal.getTimeInMillis();

        // add 14 hours = TOMORROW 07:00:00.000
        cal.add(Calendar.HOUR_OF_DAY, hours);
        long endHourMilli = cal.getTimeInMillis();

        return (System.currentTimeMillis() > startHourMilli && System.currentTimeMillis() < endHourMilli);
    }

    //Check if app usage access is granted
    public boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode;
            assert appOpsManager != null;
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //Show dialog if usage access permission not given
    public void permission_check() {
        //Usage Permission
        if (!isAccessGranted()) {
            new LovelyStandardDialog(MainActivity.this)
                    .setTopColorRes(R.color.colorPrimaryDark)
                    .setIcon(R.drawable.ic_perm_device_information_white_48dp)
                    .setTitle(getString(R.string.permission_check_title))
                    .setMessage(getString(R.string.permission_check_message))
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }

    public void isIgnoringBattery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(package_name)) return;
            new LovelyStandardDialog(MainActivity.this)
                    .setTopColorRes(R.color.colorPrimaryDark)
                    .setIcon(R.drawable.ic_perm_device_information_white_48dp)
                    .setTitle(getString(R.string.battery_dialog_title))
                    .setMessage(getString(R.string.battery_dialog_message))
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//                                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            //intent.setData(Uri.parse("package:" + package_name));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
                startActivityForResult(intent, NEW_TASK_ACTIVITY_REQUEST_CODE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Task task = new Task();
            task.taskName = data.getStringExtra(NewTaskActivity.TASK_NAME_REPLY);
            task.taskDescription = data.getStringExtra(NewTaskActivity.TASK_DESCRIPTION_REPLY);
            task.repeatSchedule = data.getStringExtra(NewTaskActivity.TASK_REPEAT_SCHEDULE_REPLY);
            task.created_date = new Date();
            task.completed = false;
            mTaskViewModel.insert(task);
        }
        else if (requestCode == NEW_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED)
        {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.cancelled_task_creation,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void completeTask(Task task)
    {
        mTaskViewModel.completeTask(task);
    }
}
