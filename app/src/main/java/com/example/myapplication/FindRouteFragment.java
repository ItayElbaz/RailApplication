package com.example.myapplication;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindRouteFragment extends Fragment {
    private TrainStation[] items;

    public FindRouteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment frag_newroute.
     */
    public static FindRouteFragment newInstance() {
        return new FindRouteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items = ((RailActivity)getActivity()).utils.getStationsList();
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

        return view;
    }

    public void showRouteResults() {
        View view = getView();
        Spinner dropdownFrom = (Spinner) view.findViewById(R.id.spinner_from);
        Spinner dropdownTo = (Spinner) view.findViewById(R.id.spinner_to);
        TrainStation t_from = (TrainStation) dropdownFrom.getSelectedItem();
        TrainStation t_to = (TrainStation) dropdownTo.getSelectedItem();

        TimePicker timepicker = (TimePicker) view.findViewById(R.id.train_timePicker);
        Date date = getNextXday(2);
        date.setHours(timepicker.getHour());
        date.setMinutes(timepicker.getMinute());

        RailRoute[] routes = ((RailActivity)getActivity()).utils.getTrainRoutes(t_from.code, t_to.code, date);

        if (routes != null) {
            Fragment fr = new RailRouteFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("ROUTES", routes);
            fr.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_place, fr).addToBackStack(null).commit();
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
}
