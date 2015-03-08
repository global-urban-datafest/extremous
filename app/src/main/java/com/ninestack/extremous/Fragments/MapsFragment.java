package com.ninestack.extremous.Fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ninestack.extremous.MeetupActivity;
import com.ninestack.extremous.R;
import com.ninestack.extremous.Utils.WebClient;

public class MapsFragment extends android.support.v4.app.Fragment {
    private View view;
    private WebView webview;
    private GoogleMap mMap;

    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    public MapsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
        //setUpMapIfNeeded();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_web, container, false);
        getActivity().getActionBar().setTitle("Meetups");

        webview = (WebView) view.findViewById(R.id.web_client);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebClient());

        webview.loadUrl(getString(R.string.MOBILE));
        return view;
    }

    private void setUpMap() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new MyLocationListener());
    }

    public class MyLocationListener implements LocationListener {
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
            Toast.makeText(getActivity().getApplicationContext(), "GPS is disabled", Toast.LENGTH_SHORT).show();
        }
    }

}
