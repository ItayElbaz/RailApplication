package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class ShowQrFragment extends Fragment {
    private String qr;

    public ShowQrFragment() {
        // Required empty public constructor
    }

    public static ShowQrFragment newInstance(String qr) {
        ShowQrFragment fragment = new ShowQrFragment();
        Bundle args = new Bundle();
        args.putString("QR", qr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            qr = getArguments().getString("QR");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_show_qr, container, false);
        ImageView img = view.findViewById(R.id.qr_frag_image);
        img.setImageBitmap(getQRImage(qr));
        Button closeBtn = view.findViewById(R.id.close_qr);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeQR();
            }
        });

        return view;
    }

    public Bitmap getQRImage(String barcode)  {
        InputStream stream = new ByteArrayInputStream(Base64.decode(barcode.getBytes(), Base64.DEFAULT));

        return BitmapFactory.decodeStream(stream);
    }

    public void closeQR() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}