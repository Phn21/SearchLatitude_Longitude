package com.example.weatherappl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button addressButton;
    TextView textViewAddress;
    TextView textViewLatLong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewAddress = findViewById(R.id.textViewAddress);
        textViewLatLong = findViewById(R.id.latLongTV);
        addressButton = findViewById(R.id.addressButton);
        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditText editText = findViewById(R.id.editTextAddress);
                String address = editText.getText().toString();
                GeoCodeLocation locationAddress = new GeoCodeLocation();
                locationAddress.getAddressFromLocation(address, getApplicationContext(), new
                        GeoCoderHandler());
            }
        });

    }

    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            textViewLatLong.setText(locationAddress);
        }
    }
}

class GeoCodeLocation {
    private static final String TAG = "GeoCodeLocation";
    public static void getAddressFromLocation(final String
                                                      locationAddress,
                                              final Context
                                                      context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context,
                        Locale.getDefault());
                String result = null;
                String latitude = null;
                String longitude = null;
                try {
                    List addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = (Address)
                                addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        StringBuilder lt = new StringBuilder();
                        StringBuilder lo = new StringBuilder();
                        sb.append(address.getLatitude()).append("");
                        sb.append(address.getLongitude()).append("");
                        longitude = lo.append(address.getLongitude()).append("").toString();
                        result = sb.toString();
                        
                        
                        latitude = lt.append(address.getLatitude()).append("").toString();

                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +"Latitude and Longitude:" + latitude + "\n" + longitude;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +"Unable to get Latitude and Longitude for this address location.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}