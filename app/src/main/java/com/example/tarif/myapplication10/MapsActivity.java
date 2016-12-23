package com.example.tarif.myapplication10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import cz.msebera.android.httpclient.Header;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    private GoogleMap mMap;
    String[] mPlaceType=null;
    String[] mPlaceTypeName=null;
    EditText location_tf;
    double mLatitude=0;
    double mLongitude=0;

    double lng1=0;
    double lat1=0;
    Button b;
    String location1 ="";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MY_SHARED="mySharedPreferences";
    public static final String NAME="locationName";
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaTutorial";
    Marker o;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        b=(Button)findViewById(R.id.btnGet);
        b.setOnClickListener(this);
         location_tf = (EditText)findViewById(R.id.TFadress);
        sharedPreferences=getSharedPreferences(MY_SHARED, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        if(sharedPreferences.contains(NAME)){

            String n=sharedPreferences.getString(NAME,"");
            location_tf.setText(n);

        }
        File dir = new File(path);
        dir.mkdirs();



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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                String reference1 = arg0.getSnippet();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reference1));
                intent.putExtra("reference", reference1);
                startActivity(intent);
            }
        });

        // Add a marker in luxembourg and move the camera
        LatLng luxembourg = new LatLng(49.6265362, 6.15804613);
        mMap.addMarker(new MarkerOptions().position(luxembourg).title(" luxembourg university").snippet("http://www.uni.lu"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(luxembourg));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(luxembourg, 10));


        // Enabling MyLocation in Google Map
        mMap.setMyLocationEnabled(true);


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String provider = locationManager.getBestProvider(criteria, true);

        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                showCurrentLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        // Getting initial Location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        // Show the initial location
        if(location != null)
        {
            showCurrentLocation(location);
        }

    }
    private void showCurrentLocation(Location location){


        mLatitude=location.getLatitude();
        mLongitude=location.getLongitude();

        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());

        mMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude())


                .title("user location"));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));
    }
    public void onclick1(View view)
    {
        EditText location_tf = (EditText)findViewById(R.id.TFadress);
        String location1 = location_tf.getText().toString();
        editor.putString(NAME,location1);
        editor.commit();
        Toast.makeText(getApplicationContext(),"saved",Toast.LENGTH_LONG).show();

        List<Address> addressList = null;
        if(location1 != null || !location1.equals(""))
        {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location1 , 1);


            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude() , address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        }
    }

    @Override
    public void onClick(final View v) {

        v.setEnabled(false);
       // String url = "http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&stationProxy=yes&look_maxdist=300&look_x="+mLatitude+"&look_y="+mLongitude;
        String url = "http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&stationProxy=yes &look_maxdist=300&look_x=6112550&look_y=49610700 ";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) {

                    File file = new File (path + "/savedFile.txt");
                    String [] saveText = String.valueOf(new String(responseBody)).split(";");
                    Scanner scan=null;
                    Save(file, saveText);
                 //   File file1= new File (path + "/savedFile1.txt");
                    String [] loadText = Load(file);
               // Save(file1,String.valueOf(loadText[14]).split(System.getProperty("line.separator")));

                    for (int i = 0; i < loadText.length; i++)
                    {
                        if (i%5 == 0) {

                            Double [] d=null;

                            d[i]=Double.parseDouble(String.valueOf(String.valueOf(loadText[i]).split(System.getProperty("line.separator"))));
                            d[i+1]=Double.parseDouble(String.valueOf(String.valueOf(loadText[i+1]).split(System.getProperty("line.separator"))));

                            LatLng LatLng1 = new LatLng(d[i+1],d[i]);
                   //for the closest bus stop it is the first one and we put amarker with color AZURE
                       if(i==0){
                            MarkerOptions markerOptions=new MarkerOptions();
                           markerOptions.position(LatLng1);
                            markerOptions.title(" closest bus stop").visible(true);
                           markerOptions.icon(BitmapDescriptorFactory.defaultMarker
                                   (BitmapDescriptorFactory.HUE_AZURE));
                            String reference=null;
                            // Linking Marker id and refrence to second  activity for details
                              reference= String.valueOf(String.valueOf(loadText[i+4]).valueOf(loadText[i+1]).split(System.getProperty("line.separator")));
                            Marker m=mMap.addMarker(markerOptions);
                               m.setSnippet(reference);

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng1));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng1, 10));

                    }
                      // for other marker we left it with with default color
                      else {
                           MarkerOptions markerOptions=new MarkerOptions();
                           markerOptions.position(LatLng1);
                           markerOptions.title(" bus stop");
                           String reference=null;
                           // Linking Marker id and refrence to second  activity for details
                           reference= String.valueOf(String.valueOf(loadText[i+4]).valueOf(loadText[i+1]).split(System.getProperty("line.separator")));
                           Marker m=mMap.addMarker(markerOptions);
                           m.setSnippet(reference);

                           mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng1));
                           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng1, 10));
                       }
                    }


                    v.setEnabled(true);
                }

            }}
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                v.setEnabled(true);
            }


            public  void Save(File file, String[] data)
            {
                FileOutputStream fos = null;
                try
                {
                    fos = new FileOutputStream(file);
                }
                catch (FileNotFoundException e) {e.printStackTrace();}
                try
                {
                    try
                    {
                        for (int i = 0; i<data.length; i++)
                        {
                            fos.write(data[i].getBytes());
                            if (i < data.length-1)
                            {
                                fos.write("\n".getBytes());
                            }
                        }
                    }
                    catch (IOException e) {e.printStackTrace();}
                }
                finally
                {
                    try
                    {
                        fos.close();
                    }
                    catch (IOException e) {e.printStackTrace();}
                }
            }





            public  String[] Load(File file)
            {
                FileInputStream fis = null;
                try
                {
                    fis = new FileInputStream(file);
                }
                catch (FileNotFoundException e) {e.printStackTrace();}
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);

                String test;
                int anzahl=0;
                try
                {
                    while ((test=br.readLine()) != null)
                    {
                        anzahl++;
                    }
                }
                catch (IOException e) {e.printStackTrace();}

                try
                {
                    fis.getChannel().position(0);
                }
                catch (IOException e) {e.printStackTrace();}

                String[] array = new String[anzahl];

                String line;
                int i = 0;
                try
                {
                    while((line=br.readLine())!=null)
                    {
                        array[i] = line;
                        i++;
                    }
                }
                catch (IOException e) {e.printStackTrace();}
                return array;
            }
        });
    }
    }

