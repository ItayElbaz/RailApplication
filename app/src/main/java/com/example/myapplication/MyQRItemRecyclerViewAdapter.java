package com.example.myapplication;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyQRItemRecyclerViewAdapter extends RecyclerView.Adapter<MyQRItemRecyclerViewAdapter.ViewHolder> {
    private List<QRRouteItem> mValues;
    private RailVoucherActivity myActivity;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final String QR_URL = "https://www.rail.co.il/taarif/pages/qrcode.aspx?GEneratedref=%s";

    public MyQRItemRecyclerViewAdapter(RailVoucherActivity myActivity) {
        mValues = myActivity.QRItems;
        this.myActivity = myActivity;
        myActivity.QRListner.observe(myActivity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_qr_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        TrainStation fromStation = myActivity.utils.getStationDataById(mValues.get(position).route.orignStation);
        holder.mFromView.setText("From: " + fromStation.EN);
        TrainStation toStation = myActivity.utils.getStationDataById(mValues.get(position).route.destStation);
        holder.mToView.setText("To: " + toStation.EN);
        holder.mDepartureView.setText("Departure: " + dateFormat.format(mValues.get(position).route.departureTime));
        holder.mArrivalView.setText("Arrival: " + dateFormat.format(mValues.get(position).route.arrivalTime));

        Bitmap qr = getQRImage(holder.mItem.barcode);
        if (qr == null) {
            holder.mQRView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WebViewFragment fr = new WebViewFragment();
                    Bundle args = new Bundle();
                    String url = String.format(QR_URL, holder.mItem.QRref);
                    args.putString("URL", url);
                    fr.setArguments(args);
                    myActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, fr).addToBackStack(null).commit();
                }
            });
        } else {
            holder.mQRView.setImageBitmap(qr);
            holder.mQRView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowQrFragment fr = new ShowQrFragment();
                    Bundle args = new Bundle();
                    args.putString("QR", holder.mItem.barcode);
                    fr.setArguments(args);
                    myActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, fr).addToBackStack(null).commit();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mQRView;
        public final TextView mFromView;
        public final TextView mToView;
        public final TextView mDepartureView;
        public final TextView mArrivalView;
        public QRRouteItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mQRView = view.findViewById(R.id.qr_image);
            mFromView = view.findViewById(R.id.qr_from_station);
            mToView = view.findViewById(R.id.qr_to_station);
            mDepartureView = view.findViewById(R.id.qr_departure_time);
            mArrivalView = view.findViewById(R.id.qr_arrival_time);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFromView.getText() + "'";
        }
    }

    public Bitmap getQRImage(String barcode) {
        if (barcode == "null") {
            return null;
        }
        InputStream stream = new ByteArrayInputStream(Base64.decode(barcode.getBytes(), Base64.DEFAULT));

        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        return bitmap;
    }
}