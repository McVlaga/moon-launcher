package io.github.mcvlaga.moonlauncher.home.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.tasks.utils.TasksUtils;
import io.github.mcvlaga.moonlauncher.utils.HomeDateUtils;

public class AddEditTaskDialogFragment extends DialogFragment {

    public static final String ID_KEY = "id";
    public static final String DIALOG_TITLE_KEY = "dialogTitle";
    public static final String TITLE_KEY = "title";
    public static final String TIME_KEY = "time";
    public static final String DATE_KEY = "date";
    public static final String COLOR_KEY = "color";

    private boolean isAdding;

    private String id;
    private String title;
    private String dialogTitle;
    private long dateInMillis = 0;
    private long timeInMillis = 0;
    private int color;

    private static final int DEFAULT_CIRCLE_INDEX = 0;
    private int mCurrentCircleIndex = DEFAULT_CIRCLE_INDEX;

    private static final long DAY_MINUS_ONE_MINUTE =
            TimeUnit.DAYS.toMillis(1) - TimeUnit.MINUTES.toMillis(1);

    private ImageButton[] mCircleButtons;
    private Button dateButton;
    private Button timeButton;
    private ImageButton removeDateButton;
    private ImageButton removeTimeButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        color = getResources().getColor(R.color.colorTasksOne);
        Bundle bundle = getArguments();
        isAdding = bundle.getString(ID_KEY) == null;
        if (!isAdding) {
            id = bundle.getString(ID_KEY);
            title = bundle.getString(TITLE_KEY);
            dateInMillis = bundle.getLong(DATE_KEY);
            timeInMillis = bundle.getLong(TIME_KEY);
            color = bundle.getInt(COLOR_KEY);
        }
        dialogTitle = bundle.getString(DIALOG_TITLE_KEY);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.tasks_add_edit_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText titleEditText = dialogView.findViewById(R.id.title_edit_text);
        dateButton = dialogView.findViewById(R.id.date_button);
        timeButton = dialogView.findViewById(R.id.time_button);
        removeDateButton = dialogView.findViewById(R.id.date_remove_button);
        removeTimeButton = dialogView.findViewById(R.id.time_remove_button);

        if (!isAdding) {
            titleEditText.setText(title);
            titleEditText.setSelection(title.length());
            if (dateInMillis != 0) {
                dateButton.setText(TasksUtils.getDateStringForDialog(dateInMillis));
            }
            if (timeInMillis != 0) {
                timeButton.setText(TasksUtils.getTimeStringForDialog(getActivity(), timeInMillis));
            }
        }

        setUpCircleButtons(dialogView);

        showDatePickerDialog(dateButton);
        showTimePickerDialog(timeButton);

        onRemoveDateButtonClicked();
        onRemoveTimeButtonClicked();

        dialogBuilder.setTitle(dialogTitle);
        dialogBuilder.setPositiveButton(
                getString(R.string.add_edit_task_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        title = titleEditText.getText().toString();
                        if (title.length() != 0) {
                            Intent intent = new Intent();
                            if (!isAdding) {
                                intent.putExtra(ID_KEY, id);
                            }
                            intent.putExtra(TITLE_KEY, title);
                            intent.putExtra(DATE_KEY, dateInMillis);
                            intent.putExtra(TIME_KEY, timeInMillis);
                            intent.putExtra(COLOR_KEY, color);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        } else {
                            Toast.makeText(getActivity(), "You should probably add a title", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dialogBuilder.setNegativeButton(getString(R.string.add_edit_task_cancel), null);
        return dialogBuilder.create();
    }


    private void showDatePickerDialog(final Button dateButton) {
        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                if (dateInMillis != 0) {
                    c.setTimeInMillis(dateInMillis);
                }

                final DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                c.set(year, monthOfYear, dayOfMonth);
                                c.set(Calendar.HOUR_OF_DAY, 0);
                                c.set(Calendar.MINUTE, 0);
                                if (timeInMillis == 0) {
                                    dateInMillis = c.getTimeInMillis() + DAY_MINUS_ONE_MINUTE;
                                } else {
                                    dateInMillis = c.getTimeInMillis();
                                }
                                dateButton.setText(TasksUtils.getDateStringForDialog(dateInMillis));
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            }
        });
    }

    private void showTimePickerDialog(final Button timeButton) {
        timeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                if (timeInMillis != 0) {
                    int offset = TimeZone.getDefault().getOffset(timeInMillis);
                    c.setTimeInMillis(timeInMillis - offset);
                }

                TimePickerDialog timePicker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (dateInMillis != 0 && timeInMillis == 0) {
                                    dateInMillis -= DAY_MINUS_ONE_MINUTE;
                                }
                                timeInMillis = TimeUnit.HOURS.toMillis(hourOfDay) +
                                        TimeUnit.MINUTES.toMillis(minute);
                                timeButton.setText(
                                        TasksUtils.getTimeStringForDialog(getActivity(), timeInMillis));
                            }
                        },
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        HomeDateUtils.is24HourFormat(getActivity()));
                timePicker.show();
            }
        });
    }

    public void onRemoveDateButtonClicked() {
        removeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateButton.setText(getString(R.string.add_edit_date_button));
                dateInMillis = 0;
            }
        });
    }

    public void onRemoveTimeButtonClicked() {
        removeTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateInMillis != 0 && timeInMillis != 0) {
                    dateInMillis += DAY_MINUS_ONE_MINUTE;
                }
                timeButton.setText(getString(R.string.add_edit_time_button));
                timeInMillis = 0;
            }
        });
    }

    private void setUpCircleButtons(View dialogView) {
        ImageButton circleOne = dialogView.findViewById(R.id.tasks_circle_color_1);
        ImageButton circleTwo = dialogView.findViewById(R.id.tasks_circle_color_2);
        ImageButton circleThree = dialogView.findViewById(R.id.tasks_circle_color_3);
        ImageButton circleFour = dialogView.findViewById(R.id.tasks_circle_color_4);
        ImageButton circleFive = dialogView.findViewById(R.id.tasks_circle_color_5);

        mCircleButtons = new ImageButton[]{circleOne, circleTwo, circleThree, circleFour, circleFive};

        GradientDrawable drawable1 = (GradientDrawable) mCircleButtons[0].getBackground();
        drawable1.setColor(getResources().getColor(R.color.colorTasksOne));
        GradientDrawable drawable2 = (GradientDrawable) mCircleButtons[1].getBackground();
        drawable2.setColor(getResources().getColor(R.color.colorTasksTwo));
        GradientDrawable drawable3 = (GradientDrawable) mCircleButtons[2].getBackground();
        drawable3.setColor(getResources().getColor(R.color.colorTasksThree));
        GradientDrawable drawable4 = (GradientDrawable) mCircleButtons[3].getBackground();
        drawable4.setColor(getResources().getColor(R.color.colorTasksFour));
        GradientDrawable drawable5 = (GradientDrawable) mCircleButtons[4].getBackground();
        drawable5.setColor(getResources().getColor(R.color.colorTasksFive));

        for (int i = 0; i < mCircleButtons.length; i++) {
            final int j = i;

            int currentColor = Color.parseColor(mCircleButtons[i].getTag().toString());
            if (currentColor == color) {
                mCurrentCircleIndex = i;
            }

            mCircleButtons[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int oldColor = Color.parseColor(mCircleButtons[mCurrentCircleIndex].getTag().toString());
                    Drawable oldCircleDrawable = getResources().getDrawable(R.drawable.tasks_hollow_circle);
                    oldCircleDrawable.setColorFilter(new
                            PorterDuffColorFilter(oldColor, PorterDuff.Mode.SRC_IN));
                    mCircleButtons[mCurrentCircleIndex].setBackground(oldCircleDrawable);

                    mCurrentCircleIndex = j;

                    color = Color.parseColor(mCircleButtons[mCurrentCircleIndex].getTag().toString());
                    Drawable circleDrawable = getResources().getDrawable(R.drawable.tasks_circle);
                    circleDrawable.setColorFilter(new
                            PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                    mCircleButtons[j].setBackground(circleDrawable);
                }
            });
        }
        color = Color.parseColor(mCircleButtons[mCurrentCircleIndex].getTag().toString());
        Drawable circleDrawable = getResources().getDrawable(R.drawable.tasks_circle);
        circleDrawable.setColorFilter(new
                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        mCircleButtons[mCurrentCircleIndex].setBackground(circleDrawable);
    }
}
