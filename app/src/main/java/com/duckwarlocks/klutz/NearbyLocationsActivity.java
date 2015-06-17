package com.duckwarlocks.klutz;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.duckwarlocks.klutz.Exceptions.StopProcessingException;
import com.duckwarlocks.klutz.constants.CommonConstants;
import com.duckwarlocks.klutz.utilities.GpsCoordinatesHelper;
import com.duckwarlocks.klutz.utilities.JSONHelper;
import com.duckwarlocks.klutz.utilities.JsonRequestTask;
import com.duckwarlocks.klutz.vo.GooglePlacesResponseVO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class NearbyLocationsActivity extends Activity implements OnItemClickListener {

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    GpsCoordinatesHelper mGpsHelper;
    private static final String API_KEY = "AIzaSyBBr9gsNuTtEUq1BsenaAuHp6Dw1LF0q34";
    LatLng currentLoc;
    private JSONArray mPredsJsonArray;
    private ArrayList<GooglePlacesResponseVO> mPlacesVOList;
    GoogleMap mMap;
    double mSrchLat;
    double mSrchLng;
    boolean centerLoc = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_locations);
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.nearbylist_piece));
        autoCompView.setOnItemClickListener(this);

        mGpsHelper = new GpsCoordinatesHelper(this);
        mGpsHelper.getmLocation();

        currentLoc = new LatLng(mGpsHelper.getmLatitude(),mGpsHelper.getmLongitude());
        if(mMap!=null){
            Marker mapMarker =
                    mMap.addMarker(new MarkerOptions().position(currentLoc).title("You Are Here!"));
            mapMarker.showInfoWindow();
//                    mMap.addMarker(new MarkerOptions().position(currentLoc).title(mGpsHelper.getmCityName()));
            //move camera to where you are
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,15));
            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }
    }

    private void updateDistanceBox(Location locationA, Location locationB){
        double distance = locationA.distanceTo(locationB);
        TextView distanceBox = (TextView)findViewById(R.id.distanceBox);

        distance = (distance / 1609.344);
        distanceBox.setText(Double.toString(distance) + " Miles");
    }

    /**
     * Plot your searched location on the map fragment.
     */
//    public void markSrchOnMap(View view){
    public void markSrchOnMap(View view){
        mGpsHelper.getmLocation();
        PolylineOptions line =
                new PolylineOptions().add(
                  new LatLng(mGpsHelper.getmLatitude(),mGpsHelper.getmLongitude()),
                        new LatLng(mSrchLat,mSrchLng)
                ).width(20).color(Color.CYAN);

        mMap.addPolyline(line);
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
//        String str = (String) adapterView.getItemAtPosition(position);
//        markSrchOnMap();
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

//        Geocoder gc = new Geocoder(getBaseContext());

        LatLng tempPosition = null;

        Marker tempMarker = null;
        try {
            if(mGpsHelper != null){
                AutoCompleteTextView srchBox = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
                String srchBoxTxt = srchBox.getText().toString().substring(0,srchBox.getText().toString().indexOf(","));

                List<Address> list = mGpsHelper.getSearchedLocation(getBaseContext(),str);

                if(list.size() > 0){
                    Address address = list.get(0);

                    mSrchLat = address.getLatitude();
                    mSrchLng = address.getLongitude();

                    tempPosition = new LatLng(address.getLatitude(), address.getLongitude());

                    centerLoc = true;
                    tempMarker = mMap.addMarker(new MarkerOptions().position(tempPosition).title(srchBoxTxt));
                    tempMarker.showInfoWindow();

                    Location locationStart = new Location("Starting Point");
                    mGpsHelper.getmLocation();
                    locationStart.setLatitude(mGpsHelper.getmLatitude());
                    locationStart.setLongitude(mGpsHelper.getmLongitude());
                    Location locationEnd = new Location("Ending Point");
                    locationEnd.setLatitude(mSrchLat);
                    locationEnd.setLongitude(mSrchLng);
                    updateDistanceBox(locationStart,locationEnd);
                }
                else{
                    Toast.makeText(this, "Calm!, I'm getting the information",Toast.LENGTH_SHORT).show();
                }


                //Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(mPosition.latitude, mPosition.longitude), new LatLng(mPositiontemp.latitude, mPositiontemp.longitude)).width(2)
                //    .color(Color.BLUE).geodesic(true));

                centerOnLocation(tempPosition, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        } catch(StopProcessingException sp){
            Log.e(NearbyLocationsActivity.class.getName(),sp.toString());
        }
    }

    public void centerOnLocation(LatLng position, GoogleMap.CancelableCallback callback) {
        if (centerLoc && mMap != null) {
            centerLoc = false;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12), 1500, callback);
        }
    }

    public  ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + CommonConstants.API_KEY);
            sb.append("&components=country:us");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            Log.e(NearbyLocationsActivity.class.getName(),"THE URL IS : " + sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(NearbyLocationsActivity.class.getName(), "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(NearbyLocationsActivity.class.getName(), "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONHelper jsonHelper = new JSONHelper(jsonResults.toString());
            jsonHelper.parseRawJSON();
            mPlacesVOList = jsonHelper.getLocationList();
            mPredsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(mPredsJsonArray.length());
            for (int i = 0; i < mPredsJsonArray.length(); i++) {
                System.out.println(mPredsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(mPredsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(NearbyLocationsActivity.class.getName(), "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return (String)resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}