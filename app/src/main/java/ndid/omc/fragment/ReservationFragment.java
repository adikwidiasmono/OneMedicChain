package ndid.omc.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.flexbox.FlexboxLayout;
import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnSelectClickListener;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import ndid.omc.main.R;
import ndid.omc.utils.MedUtils;

/**
 * Created by adikwidiasmono on 15/11/17.
 */

public class ReservationFragment extends Fragment implements VerticalStepperForm {

    private static final String EXTRA_TEXT = "text";

    // Information about the steps/fields of the form
    private static final int SELECT_HOSPITAL = 0;
    private static final int SELECT_DOCTOR = 1;
    private static final int DAYS_STEP_NUM = 2;
    private static final int TIME_STEP_NUM = 3;

    // Select hospital
    private Spinner spHospital;
    public static final String[] LIST_OF_HOSPITAL
            = {"Hospital de Barcelona (8.8/10)", "Provincial Hospital of Barcelona (8.7/10)",
            "CIMA (8.5/10)", "Hospital FREMAP Barcelona (8.4/10)", "Hospital Sant Joan de Déu Barcelona (8.3/10)"};

    // Select doctor
    private Spinner spDoctor;
    public static final String[] LIST_OF_DOCTOR
            = {"Dr. Marc (9/10)", "Dr. Alex (8.8/10)", "Dr. Eric (8.7/10)", "Dr. Julia (8.5/10)", "Dr. Martina (8.2/10)"};

    // Week days step
    private boolean[] weekDays;
    private LinearLayout daysStepContent;
    private LinearLayout[] dayLayouts = new LinearLayout[7];

    // Time step
    // Using time picker
    private TextView timeTextView;
    private TimePickerDialog timePicker;
    // Using selection like day selection
    private LinearLayout timesStepContent;
    private Pair<Integer, Integer> time;

    private ProgressDialog progressDialog;
    private VerticalStepperFormLayout verticalStepperForm;

    private TextView tvCancelReservation;
    private View layoutQR;

    public static ReservationFragment createFor(String text) {
        ReservationFragment fragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reservation, container, false);

        verticalStepperForm = v.findViewById(R.id.vertical_stepper_form);
        tvCancelReservation = v.findViewById(R.id.tv_cancel_registration);
        layoutQR = v.findViewById(R.id.rl_qr_code);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initializeActivity(view);

        boolean isRegistered = MedUtils.getPreferences(getActivity()).getBoolean(MedUtils.PREF_REGISTERED_STATUS, false);
        if (isRegistered) {
            verticalStepperForm.setVisibility(View.GONE);
            layoutQR.setVisibility(View.VISIBLE);
        } else {
            verticalStepperForm.setVisibility(View.VISIBLE);
            layoutQR.setVisibility(View.GONE);
        }

        tvCancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MedUtils.setPreferences(getActivity())
                        .putBoolean(MedUtils.PREF_REGISTERED_STATUS, false).commit();

                verticalStepperForm.setVisibility(View.VISIBLE);
                layoutQR.setVisibility(View.GONE);
            }
        });
    }

    private void initializeActivity(View view) {
        // Time step vars
        time = new Pair<>(8, 30);
        setTimePicker(8, 30);

        // Week days step vars
        weekDays = new boolean[7];

        // Vertical Stepper form vars
        int colorPrimary = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark);
        String[] stepsTitles = {"Hospital", "Doctor", "Day", "Time"};
        String[] stepsSubtitles = {"Which hospital you want to visit?",
                "Who is the doctor you want?",
                "What day you want to visit",
                "What time you want to visit"};

        // Here we find and initialize the form
        VerticalStepperFormLayout
                .Builder
                .newInstance(verticalStepperForm, stepsTitles, this, getActivity())
                .stepsSubtitles(stepsSubtitles)
                .materialDesignInDisabledSteps(true) // false by default
//                .showVerticalLineWhenStepsAreCollapsed(true)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(false)
                .init();

    }

    @Override
    public View createStepContentView(int stepNumber) {
        // Here we generate the content view of the correspondent step and we return it so it gets
        // automatically added to the step layout (AKA stepContent)
        View view = null;
        switch (stepNumber) {
            case SELECT_HOSPITAL:
                view = selectHospitalStep();
                break;
            case SELECT_DOCTOR:
                view = selectDoctorStep();
                break;
            case DAYS_STEP_NUM:
                view = selectDayStep();
                break;
            case TIME_STEP_NUM:
                view = selectTimeStep();
                break;
        }
        return view;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case SELECT_HOSPITAL:

            case SELECT_DOCTOR:

            case DAYS_STEP_NUM:

            case TIME_STEP_NUM:

        }
    }

    @Override
    public void sendData() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.vertical_form_stepper_form_sending_data_message));
        executeDataSending();
    }

    // OTHER METHODS USED TO MAKE THIS EXAMPLE WORK

    private void executeDataSending() {
        // Fake data sending effect
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    MedUtils.setPreferences(getActivity())
                            .putBoolean(MedUtils.PREF_REGISTERED_STATUS, true).commit();

                    // TODO : Show QR Code Layout
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            verticalStepperForm.setVisibility(View.GONE);
                            layoutQR.setVisibility(View.VISIBLE);
                        }
                    });

                    progressDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // You should delete this code and add yours
    }

    private View selectHospitalStep() {
        // This step view is generated programmatically
        spHospital = new Spinner(getActivity());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, LIST_OF_HOSPITAL);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spHospital.setAdapter(spinnerArrayAdapter);
        spHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                verticalStepperForm.setStepAsCompleted(SELECT_HOSPITAL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return spHospital;
    }

    private View selectDoctorStep() {
        // This step view is generated programmatically
        spDoctor = new Spinner(getActivity());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, LIST_OF_DOCTOR);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spDoctor.setAdapter(spinnerArrayAdapter);
        spDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                verticalStepperForm.setStepAsCompleted(SELECT_DOCTOR);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return spDoctor;
    }

    private View selectDayStep() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        daysStepContent = (LinearLayout) inflater.inflate(
                R.layout.step_days_of_week_layout, null, false);

        String[] weekDays = {"We", "Th", "Fr", "St", "Su", "Mo", "Tu"};
        for (int i = 0; i < weekDays.length; i++) {
            final int index = i;
            dayLayouts[i] = getDayLayout(index);
//            if(index == 0) {
//                activateDay(index, dayLayouts[i], false);
//            } else {
            deactivateDay(index, dayLayouts[i], false);
//            }

            dayLayouts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((boolean) v.getTag()) {
//                        deactivateDay(index, dayLayout, true);
                    } else {
                        activateDay(index, dayLayouts[index], true);
                    }
                }
            });

            final TextView dayText = (TextView) dayLayouts[i].findViewById(R.id.day);
            dayText.setText(weekDays[index]);
        }
        return daysStepContent;
    }

    private View selectTimeStep() {
        // USING TIME PICKER
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        FlexboxLayout timesStepContent = (FlexboxLayout) inflater.inflate(R.layout.step_time_layout, null, false);
        timeTextView = timesStepContent.findViewById(R.id.time);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show();
            }
        });

        // Add chip view
        String[] doctorOpenTime = {"09.00 AM", "09.30 AM", "10.00 AM", "12.00 PM", "12.30 PM"};
        for (String dctTime : doctorOpenTime) {
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16, 16, 16, 16);

            final Chip ch = new Chip(getActivity());
            ch.setLayoutParams(params);
            ch.setChipText(dctTime);
            ch.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.c_white));
            ch.setSelectedCloseColor(ContextCompat.getColor(getActivity(), R.color.c_blue));
            ch.setSelectable(true);
            ch.setSelectedTextColor(ContextCompat.getColor(getActivity(), R.color.c_white));
            ch.setTextColor(ContextCompat.getColor(getActivity(), R.color.c_blue));
            ch.setOnSelectClickListener(new OnSelectClickListener() {
                @Override
                public void onSelectClick(View v, boolean selected) {
                    verticalStepperForm.setStepAsCompleted(TIME_STEP_NUM);
                }
            });

            timesStepContent.addView(ch);
        }

        return timesStepContent;
    }

    private void setTimePicker(int hour, int minutes) {
        timePicker = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setTime(hourOfDay, minute);
                    }
                }, hour, minutes, true);
    }

    private void setTime(int hour, int minutes) {
        time = new Pair<>(hour, minutes);
        String hourString = ((time.first > 9) ?
                String.valueOf(time.first) : ("0" + time.first));
        String minutesString = ((time.second > 9) ?
                String.valueOf(time.second) : ("0" + time.second));
        String time = hourString + ":" + minutesString;
        timeTextView.setText(time);
    }

    private void activateDay(int index, LinearLayout dayLayout, boolean check) {
        weekDays[index] = true;

        dayLayout.setTag(true);

        Drawable bg = ContextCompat.getDrawable(getActivity(),
                ernestoyaquello.com.verticalstepperform.R.drawable.circle_step_done);
        int colorPrimary = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        bg.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
        dayLayout.setBackground(bg);

        TextView dayText = dayLayout.findViewById(R.id.day);
        dayText.setTextColor(Color.rgb(255, 255, 255));

        if (check) {
            checkDays(index);
        }
    }

    private void deactivateDay(int index, LinearLayout dayLayout, boolean check) {
        weekDays[index] = false;

        dayLayout.setTag(false);

        dayLayout.setBackgroundResource(0);

        TextView dayText = dayLayout.findViewById(R.id.day);
        int colour = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        dayText.setTextColor(colour);

        if (check) {
            checkDays(index);
        }
    }

    private boolean checkDays(int processedIndex) {
        boolean thereIsAtLeastOneDaySelected = false;
        for (int i = 0; i < weekDays.length; i++) {
            if (weekDays[i] && (i == processedIndex)) {
                verticalStepperForm.setStepAsCompleted(DAYS_STEP_NUM);
                thereIsAtLeastOneDaySelected = true;
            } else if (weekDays[i]) {
                deactivateDay(i, dayLayouts[i], false);
            }
        }

        if (!thereIsAtLeastOneDaySelected) {
            verticalStepperForm.setStepAsUncompleted(DAYS_STEP_NUM, null);
        }

        return thereIsAtLeastOneDaySelected;
    }

    private LinearLayout getDayLayout(int i) {
        int id = daysStepContent.getResources().getIdentifier(
                "day_" + i, "id", getActivity().getPackageName());
        return (LinearLayout) daysStepContent.findViewById(id);
    }

}