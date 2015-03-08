package com.ninestack.extremous;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ninestack.extremous.Utils.JSONParser;
import com.ninestack.extremous.Utils.ServiceHandler;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    Intent i = new Intent(getApplicationContext(), MeetupActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    return true;
                }
            });

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new MyLocationListener());
    }

    private class GetMeetups extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected  Void doInBackground(Void... voids){
            ServiceHandler http = new ServiceHandler();
            JSONArray data = http.getJsonArray("http://extremo.us/api/meetups");

            try{
                for(int i=0; i < data.length(); i++){
                    JSONObject meetup   = data.getJSONObject(i);
                    JSONObject add      = meetup.getJSONObject("address");
                    JSONObject geo      = add.getJSONObject("geo");

                    double lat = Double.parseDouble(geo.getString("lat"));
                    double lng = Double.parseDouble(geo.getString("lng"));

                    mMap.addMarker(
                        new MarkerOptions().position(
                            new LatLng(lat, lng)
                        ).title(meetup.getString("username"))
                    );
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected  void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);

//            if(pDialog.isShowing()){
//                pDialog.dismiss();
//            }
        }
    }

    public class MyLocationListener implements LocationListener{
        public void onLocationChanged(Location location){
            mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("My Location")
                .snippet("We are showing you all nearby meetups and events for you to interacti with")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerft))
                .flat(true)
            );
            faker();
        }

        public void onStatusChanged(String provider, int status, Bundle extras){}

        private void faker(){
            double[] lat = {-66.1314, 24.6285,-24.2216};
            double[] lng = {33.9186, -81.9178, -86.7368};
            String[] meetup = {"White, Howe and Prosacco", "Schowalter, Wolff and Kuhlman", "Carter LLC"};

            for(int i=0; i<lat.length; i++){
                mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat[i], lng[i]))
                    .title(meetup[i])
                    .snippet("Just showing you some awesome meetups for you to join and have fun")
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                );
            }
        }

        public void onProviderEnabled(String provider){}

        public void onProviderDisabled(String provider){
            Toast.makeText(getApplicationContext(), "GPS is disabled", Toast.LENGTH_SHORT).show();
        }
    }
}
