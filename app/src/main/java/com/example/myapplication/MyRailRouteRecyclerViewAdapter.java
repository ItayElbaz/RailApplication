package com.example.myapplication;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RailRoute}.
 */
public class MyRailRouteRecyclerViewAdapter extends RecyclerView.Adapter<MyRailRouteRecyclerViewAdapter.ViewHolder> {
    private final List<RailRoute> mValues;
    private RailUtils utils;
    private RailRouteFragment railRouteFragment;

    public MyRailRouteRecyclerViewAdapter(List<RailRoute> items, RailUtils utils, RailRouteFragment railRouteFragment) {
        mValues = items;
        this.utils = utils;
        this.railRouteFragment = railRouteFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTrainNumberView.setText("Train #" + mValues.get(position).trainNum);
        TrainStation fromStation = utils.getStationDataById(mValues.get(position).orignStation);
        holder.mFromView.setText("From: " + fromStation.EN);
        TrainStation toStation = utils.getStationDataById(mValues.get(position).destStation);
        holder.mToView.setText("To: " + toStation.EN);
        holder.mContentView.setText("Estimated time: " + mValues.get(position).estimatedTime);
        holder.mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.addScheduleRoute(mValues.get(position));
                railRouteFragment.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTrainNumberView;
        public final TextView mFromView;
        public final TextView mToView;
        public final TextView mContentView;
        public final Button mSelectBtn;
        public RailRoute mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTrainNumberView = view.findViewById(R.id.train_number);
            mFromView = view.findViewById(R.id.from_station);
            mToView = view.findViewById(R.id.to_station);
            mContentView = view.findViewById(R.id.supporting_text);
            mSelectBtn = view.findViewById(R.id.select_route_btn);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    private void onRouteSelect() {

    }
}