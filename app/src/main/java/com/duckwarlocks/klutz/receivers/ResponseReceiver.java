package com.duckwarlocks.klutz.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.duckwarlocks.klutz.MainActivity;
import com.duckwarlocks.klutz.R;
import com.duckwarlocks.klutz.constants.CommonConstants;
import com.duckwarlocks.klutz.fragments.MainFragment;
import com.duckwarlocks.klutz.services.MainIntentService;

/**
 * Created by ngmat_000 on 8/3/2015.
 */
public class ResponseReceiver extends BroadcastReceiver {
    public static final String ACTION_RESP =
            "com.mamlambo.intent.action.MESSAGE_PROCESSED";

    @Override
    public void onReceive(Context context, Intent intent) {
        TextView result = (TextView) (((MainActivity)context).findViewById(R.id.currentCoordinates));
        String lat = intent.getStringExtra(MainIntentService.OUT_LAT);
        String lon = intent.getStringExtra(MainIntentService.OUT_LON);
        MainFragment.mCityName = intent.getStringExtra(MainIntentService.OUT_CITY);
        result.setText(CommonConstants.LATITUDE_ABBREV + " : " + lat + " " + CommonConstants.LONGITUDE_ABBREV + " : " + lon);
    }

}
