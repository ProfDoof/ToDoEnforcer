package com.devilsadvocate.to_doenforcer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class NewTaskActivity extends AppCompatActivity {

    public static final String TASK_NAME_REPLY = "com.devilsadvocate.to_doenforcer.TASK_NAME";
    public static final String TASK_DESCRIPTION_REPLY = "com.devilsadvocate.to_doenforcer.TASK_DESCRIPTION";
    public static final String TASK_REPEAT_SCHEDULE_REPLY = "com.devilsadvocate.to_doenforcer.TASK_REPEAT_SCHEDULE";

    private EditText mEditTaskNameView;
    private EditText mEditTaskDescView;
    private CheckBox mMonday;
    private CheckBox mTuesday;
    private CheckBox mWednesday;
    private CheckBox mThursday;
    private CheckBox mFriday;
    private CheckBox mSaturday;
    private CheckBox mSunday;
    private Spinner  mPrioritySpinner;
    private int      mPriority;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        mEditTaskNameView = findViewById(R.id.task_name_edit);
        mEditTaskDescView = findViewById(R.id.task_description_edit);
        mMonday = findViewById(R.id.checkbox_monday);
        mTuesday = findViewById(R.id.checkbox_tuesday);
        mWednesday = findViewById(R.id.checkbox_wednesday);
        mThursday = findViewById(R.id.checkbox_thursday);
        mFriday = findViewById(R.id.checkbox_friday);
        mSaturday = findViewById(R.id.checkbox_saturday);
        mSunday = findViewById(R.id.checkbox_sunday);
        mPrioritySpinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> staticAdaptor = ArrayAdapter.createFromResource(this, R.array.priority_values, R.layout.spinner_item);
        mPrioritySpinner.setAdapter(staticAdaptor);
        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                try
                {
                    mPriority = Integer.parseInt((String) parent.getItemAtPosition(position));
                }
                catch (NumberFormatException e)
                {
                    mPriority = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditTaskNameView.getText()))
                {
                    setResult(RESULT_CANCELED, replyIntent);
                }
                else
                {
                    String task_name = mEditTaskNameView.getText().toString();
                    String task_desc = mEditTaskDescView.getText().toString();
                    String repeatSchedule = generateRepeatScheduleString();

                    replyIntent.putExtra(TASK_NAME_REPLY, task_name);
                    replyIntent.putExtra(TASK_DESCRIPTION_REPLY, task_desc);
                    replyIntent.putExtra(TASK_REPEAT_SCHEDULE_REPLY, repeatSchedule);

                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }

    private String generateRepeatScheduleString()
    {
        String repeatSchedule = "";
        if (mMonday.isChecked())
        {
            repeatSchedule += "M";
        }
        if (mTuesday.isChecked())
        {
            repeatSchedule += "T";
        }
        if (mWednesday.isChecked())
        {
            repeatSchedule += "W";
        }
        if (mThursday.isChecked())
        {
            repeatSchedule += "R";
        }
        if (mFriday.isChecked())
        {
            repeatSchedule += "F";
        }
        if (mSaturday.isChecked())
        {
            repeatSchedule += "S";
        }
        if (mSunday.isChecked())
        {
            repeatSchedule += "U";
        }

        return repeatSchedule;
    }
}
