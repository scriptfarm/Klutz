package com.duckwarlocks.klutz.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;

import com.duckwarlocks.klutz.MainActivity;
import com.duckwarlocks.klutz.R;
import com.duckwarlocks.klutz.constants.CommonConstants;
import com.duckwarlocks.klutz.fragments.MainFragment;
import com.duckwarlocks.klutz.fragments.TripFragment;
import com.duckwarlocks.klutz.services.MainIntentService;

/**
 * Created by ngmat_000 on 8/3/2015.
 */
public class ResponseReceiver extends BroadcastReceiver {

    public static final String ACTION_RESP = "com.mamlambo.intent.action.MESSAGE_PROCESSED";
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        Boolean trip  = extras.getBoolean(MainIntentService.OUT_TRIP);
        if(!trip){
            TextView result = (TextView) (((MainActivity)context).findViewById(R.id.currentCoordinates));
            String lat = intent.getStringExtra(MainIntentService.OUT_LAT);
            String lon = intent.getStringExtra(MainIntentService.OUT_LON);
            MainFragment.mCityName = intent.getStringExtra(MainIntentService.OUT_CITY);
            result.setText(CommonConstants.LATITUDE_ABBREV + " : " + lat + " " + CommonConstants.LONGITUDE_ABBREV + " : " + lon);
            Button btn = (Button) ((MainActivity)context).findViewById(R.id.saveCoordinatesBtn);
            btn.setEnabled(true);
        }else{
            //Trip textView
            String lat = intent.getStringExtra(MainIntentService.OUT_LAT);
            String lon = intent.getStringExtra(MainIntentService.OUT_LON);
            MainFragment.mCityName = intent.getStringExtra(MainIntentService.OUT_CITY);
            TextView coordinatesTrip = (TextView) (((MainActivity)context).findViewById(R.id.currentCoordinatesTrip));
            coordinatesTrip.setText(CommonConstants.LATITUDE_ABBREV + " : " + lat + " " + CommonConstants.LONGITUDE_ABBREV + " : " + lon);

        }


    }

}
