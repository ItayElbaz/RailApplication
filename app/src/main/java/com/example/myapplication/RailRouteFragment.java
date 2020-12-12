package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A fragment representing a list of Items.
 */
public class RailRouteFragment extends Fragment {

    private static final String ARG_ROUTES = "ROUTES";
    private static final String ARG_DAYS = "DAYS";
    ArrayList<RailRoute> routes = new ArrayList<>();
    ArrayList<Integer> days;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RailRouteFragment() {
    }

    @SuppressWarnings("unused")
    public static RailRouteFragment newInstance(RailRoute[] routes, ArrayList<Integer> days) {
        RailRouteFragment fragment = new RailRouteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ROUTES, routes);
        args.putIntegerArrayList(ARG_DAYS, days);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            RailRoute[] routesArray = (RailRoute[]) getArguments().getSerializable(ARG_ROUTES);
            days = getArguments().getIntegerArrayList(ARG_DAYS);
            Collections.addAll(routes, routesArray);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyRailRouteRecyclerViewAdapter(routes, ((RailVoucherActivity)getActivity()).utils, this));
        }

        return view;
    }

    public void dismiss() {
        getActivity().getSupportFragmentManager().popBackStack(); // For current fragment
        getActivity().getSupportFragmentManager().popBackStack(); // For timepicker fragment
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity().getApplicationContext(), "Scheduled route added successfully", Toast.LENGTH_SHORT).show();
    }
}