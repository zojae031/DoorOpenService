
package com.example.user.dooropenservice.app.Activity;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dooropenservice.R;
import com.example.user.dooropenservice.app.Model.CompanyVO;
import com.example.user.dooropenservice.app.Server.ServerConnection.ServerRegistNewLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private Button searchBtn, scopeBtn, finishBtn;
    private EditText search, edit_scope;
    private TextView showAll;
    private double lat = 37.5434205;
    private double lon = 127.0506808;
    private MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getContents();
        initializeMarker();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                markerOptions.title(search.getText().toString() + " 좌표");
                lat = latLng.latitude;
                lon = latLng.longitude;

                markerOptions.snippet(lat + "/" + lon);

                markerOptions.position(new LatLng(lat, lon));
                mMap.clear();
                mMap.addMarker(markerOptions);

                setTextView("");
            }
        });

        LatLng basicPosition = new LatLng(lat, lon);
        mMap.addMarker(markerOptions.position(basicPosition).title("Basic"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(basicPosition));
    }

    private void getContents() {
        searchBtn = (Button) findViewById(R.id.searchBtn);
        scopeBtn = (Button) findViewById(R.id.setScopeBtn);
        finishBtn = (Button) findViewById(R.id.finishBtn);

        search = (EditText) findViewById(R.id.editText_search);
        edit_scope = (EditText) findViewById(R.id.editText_scope);

        showAll = (TextView) findViewById(R.id.textView_info);
    }

    private void initializeMarker() {
        markerOptions = new MarkerOptions();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchBtn:
                setLocationOnMap();
                break;
            case R.id.setScopeBtn:
                setScopeOnMap();
                break;
            case R.id.finishBtn:
                ServerRegistNewLocation serverRegistNewLocation = new ServerRegistNewLocation(new CompanyVO(search.getText().toString(), lat, lon, Double.parseDouble(edit_scope.getText().toString())));
                serverRegistNewLocation.start();
                finish();
                break;
        }
    }

    private void setLocationOnMap() {
        String str = search.getText().toString();

        if (!str.equals("")) {
            List<Address> addressList = null;

            try {
                addressList = geocoder.getFromLocationName(str, 10);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addressList != null){
                if(addressList.size() == 0){
                    Toast.makeText(getApplicationContext(),"검색결과가 없습니다.",Toast.LENGTH_SHORT).show();
                }
                else{

                    String[] splitStr = addressList.get(0).toString().split(",");
                    String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); //주소

                    String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); //위도
                    String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); //경도


                    lat = Double.parseDouble(latitude);
                    lon = Double.parseDouble(longitude);

                    LatLng point = new LatLng(lat, lon);

                    markerOptions.title(search.getText().toString() + " 좌표");
                    markerOptions.snippet(address);
                    markerOptions.position(point);

                    mMap.clear();
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));

                    setTextView("");


                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "장소를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    private void setScopeOnMap() {
        if (!edit_scope.getText().toString().equals("")) {
            double scope = Double.parseDouble(edit_scope.getText().toString());

            LatLng position = new LatLng(lat, lon);

            markerOptions.position(position);

            mMap.clear();

            CircleOptions circleOptions = new CircleOptions().center(position).radius(scope).strokeWidth(1f).fillColor(Color.parseColor("#8800BFFF"));

            mMap.addMarker(markerOptions);
            mMap.addCircle(circleOptions);

            setTextView(String.valueOf(scope));
        } else {
            Toast.makeText(getApplicationContext(), "범위를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTextView(String scope) {
        String showInfo = "";

        if (scope.equals("")) {
            showInfo = "위도 : " + String.valueOf(lat) + "\n경도 : " + String.valueOf(lon) + "\n";
        } else {
            showInfo = "위도 : " + String.valueOf(lat) + "\n경도 : " + String.valueOf(lon) + "\n" + "범위 : " + String.valueOf(scope);
        }
        showAll.setText(showInfo);
    }
}

