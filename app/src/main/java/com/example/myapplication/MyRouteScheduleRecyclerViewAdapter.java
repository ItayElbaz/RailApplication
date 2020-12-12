package com.example.myapplication;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class MyRouteScheduleRecyclerViewAdapter extends RecyclerView.Adapter<MyRouteScheduleRecyclerViewAdapter.ViewHolder> {

    private List<ScheduledRoute> mValues;
    private RailVoucherActivity myActivity;
    private DateFormat hourFormat = new SimpleDateFormat("HH:mm");
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public MyRouteScheduleRecyclerViewAdapter(RailVoucherActivity myActivity) {
        mValues = myActivity.schedulesRoutes;
        this.myActivity = myActivity;
        myActivity.listner.observe(myActivity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_scheduled_route_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTrainNumberView.setText("Train #" + mValues.get(position).route.trainNum);
        TrainStation fromStation = myActivity.utils.getStationDataById(mValues.get(position).route.orignStation);
        holder.mFromView.setText("From: " + fromStation.EN);
        TrainStation toStation = myActivity.utils.getStationDataById(mValues.get(position).route.destStation);
        holder.mToView.setText("To: " + toStation.EN);
        holder.mDepartureView.setText("Departure: " + hourFormat.format(mValues.get(position).route.departureTime));
        holder.mArrivalView.setText("Arrival: " + hourFormat.format(mValues.get(position).route.arrivalTime));
        holder.mScheduleText.setText(getScheduleText(mValues.get(position)));
        holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: on edit click create fragment that allow to change repeat + delete option
            }
        });
    }

    private CharSequence getScheduleText(ScheduledRoute route) {
        if (route.repeated) {
            return "Every " + getDaysString(route.days);
        } else {
            return "No repeated. Schedule to " + dateFormat.format(route.route.departureTime) + ".";
        }
    }

    private String getDaysString(ArrayList<Integer> days) {
        StringBuilder builder = new StringBuilder();
        if (days.contains(0)) {
            builder.append("Sun,");
        }
        if (days.contains(1)) {
            builder.append("Mon,");
        }
        if (days.contains(2)) {
            builder.append("Tue,");
        }
        if (days.contains(3)) {
            builder.append("Wen,");
        }
        if (days.contains(4)) {
            builder.append("Thu,");
        }

        builder.setLength(builder.length() - 1);
        return builder.toString();
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
        public final TextView mDepartureView;
        public final TextView mArrivalView;
        public final TextView mScheduleText;
        public final Button mEditBtn;
        public ScheduledRoute mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTrainNumberView = view.findViewById(R.id.scheduled_train_number);
            mFromView = view.findViewById(R.id.scheduled_from_station);
            mToView = view.findViewById(R.id.scheduled_to_station);
            mDepartureView = view.findViewById(R.id.scheduled_depart_time);
            mArrivalView = view.findViewById(R.id.scheduled_arrival_time);
            mScheduleText = view.findViewById(R.id.scheduled_text);
            mEditBtn = view.findViewById(R.id.edit_route_btn);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTrainNumberView.getText() + "'";
        }
    }
}