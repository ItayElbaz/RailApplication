package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindRouteFragment extends Fragment {
    private TrainStation[] items;
    private int year;
    private int month;
    private int day;
    private Date date;
    private TextView dateView;

    public FindRouteFragment() {
        // Required empty public constructor
    }

    public static FindRouteFragment newInstance() {
        return new FindRouteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = new Date();
        items = ((RailVoucherActivity)getActivity()).utils.getStationsList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newroute, container, false);

        Spinner dropdownFrom = (Spinner) view.findViewById(R.id.spinner_from);
        Spinner dropdownTo = (Spinner) view.findViewById(R.id.spinner_to);

        ArrayAdapter<TrainStation> adapter = new ArrayAdapter<TrainStation>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdownFrom.setAdapter(adapter);
        dropdownTo.setAdapter(adapter);

        Button submitBtn = view.findViewById(R.id.train_addRoute);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRouteResults();
            }
        });

        year = date.getYear() + 1900;
        month = date.getMonth();
        day = date.getDate();
        dateView = view.findViewById(R.id.train_date);
        dateView.setText(String.format("%02d/%02d/%d", day, month + 1,  year));
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        CheckBox repeat = view.findViewById(R.id.train_repeat);
        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                showDaysCheckboxes(isChecked);
            }
        });

        return view;
    }

    public void showDaysCheckboxes(boolean toShow) {
        View view = getView();
        LinearLayout daysLayout = view.findViewById(R.id.train_recurring_options);
        TextView dateText = view.findViewById(R.id.train_date);
        if (toShow){
            daysLayout.setVisibility(View.VISIBLE);
            dateText.setEnabled(false);
        } else {
            daysLayout.setVisibility(View.GONE);
            dateText.setEnabled(true);
        }
    }

    public void showTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        setNewDate(year, monthOfYear, dayOfMonth);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public void showRouteResults() {
        View view = getView();
        Spinner dropdownFrom = (Spinner) view.findViewById(R.id.spinner_from);
        Spinner dropdownTo = (Spinner) view.findViewById(R.id.spinner_to);
        TrainStation t_from = (TrainStation) dropdownFrom.getSelectedItem();
        TrainStation t_to = (TrainStation) dropdownTo.getSelectedItem();

        TimePicker timepicker = (TimePicker) view.findViewById(R.id.train_timePicker);
        Date date;

        CheckBox repeat = view.findViewById(R.id.train_repeat);
        if (repeat.isChecked()) {
            ArrayList<Integer> selectedDays = getSelectedDays(view);
            if (selectedDays.size() == 0) {
                Toast.makeText(getActivity().getApplicationContext(), "Please select days to repeat", Toast.LENGTH_SHORT).show();
                return;
            } else {
                date = getNextXday(selectedDays.get(0));
            }
        } else {
            date = new Date();
            date.setYear(year - 1900);
            date.setMonth(month);
            date.setDate(day);
        }

        date.setHours(timepicker.getHour());
        date.setMinutes(timepicker.getMinute());

        RailRoute[] routes = ((RailVoucherActivity)getActivity()).utils.getTrainRoutes(t_from.code, t_to.code, date);

        if (routes != null && routes.length > 0) {
            Fragment fr = new RailRouteFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("ROUTES", routes);
            if (repeat.isChecked()) {
                bundle.putIntegerArrayList("DAYS", getSelectedDays(view));
            }
            fr.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, fr).addToBackStack(null).commit();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Couldn't find results for selected route", Toast.LENGTH_SHORT).show();
        }
    }

    public Date getNextXday(int day) {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        while (date.getDay() != day) {
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }

        return cal.getTime();
    }

    private void setNewDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;

        dateView.setText(String.format("%02d/%02d/%d", day, month + 1, year));
    }

    private ArrayList<Integer> getSelectedDays(View v) {
        ArrayList<Integer> days = new ArrayList<>();
        if (((CheckBox)v.findViewById(R.id.train_recurring_CheckSun)).isChecked()) {
            days.add(0);
        }
        if (((CheckBox)v.findViewById(R.id.train_recurring_checkMon)).isChecked()) {
            days.add(1);
        }
        if (((CheckBox)v.findViewById(R.id.train_recurring_checkTue)).isChecked()) {
            days.add(2);
        }
        if (((CheckBox)v.findViewById(R.id.train_recurring_checkWed)).isChecked()) {
            days.add(3);
        }
        if (((CheckBox)v.findViewById(R.id.train_recurring_checkThu)).isChecked()) {
            days.add(4);
        }

        return days;
    }
}
