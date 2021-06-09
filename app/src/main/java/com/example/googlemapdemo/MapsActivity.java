package com.example.googlemapdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 1;
    private static final int POLYGON_NUM = 4;
    List<Marker> markersList = new ArrayList<>();
    String[] nameOfMarkers = {"A", "B", "C", "D"};
    private int checkMarkerNamePosition = 0;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Marker curr_marker;
    private Marker dest_marker;
    private Marker centerMarker;
    private List<Polyline> polyline = new ArrayList<>();
    private ArrayList<LatLng> markerLatLng = new ArrayList<>();
    private Polygon polygon;
    private Location startPoint, endPoint, aMarker, bMarker, cMarker, dMarker;
    private DecimalFormat df;
    private String streetName, postalCode, city, country;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback mLocationCallback;
    private
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        df = new DecimalFormat("#.##");
        // init location manager
        //initLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                setDestinationLocation(latLng);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //int position = (int)(marker.getTag());
                if (marker.getSnippet().equals("center")) {
                    aMarker = new Location("locationA");
                    aMarker.setLatitude(markersList.get(0).getPosition().latitude);
                    aMarker.setLongitude(markersList.get(0).getPosition().longitude);
                    bMarker = new Location("locationA");
                    bMarker.setLatitude(markersList.get(1).getPosition().latitude);
                    bMarker.setLongitude(markersList.get(1).getPosition().longitude);
                    cMarker = new Location("locationA");
                    cMarker.setLatitude(markersList.get(2).getPosition().latitude);
                    cMarker.setLongitude(markersList.get(2).getPosition().longitude);
                    dMarker = new Location("locationA");
                    dMarker.setLatitude(markersList.get(3).getPosition().latitude);
                    dMarker.setLongitude(markersList.get(3).getPosition().longitude);
                    double distance = aMarker.distanceTo(bMarker) + bMarker.distanceTo(cMarker) + cMarker.distanceTo(dMarker);
                    //Toast.makeText(MapsActivity.this, "Total Distance " + df.format(distance / 1000) + " km", Toast.LENGTH_LONG).show();
                    showAlertDialog("Total Distance " + df.format(distance / 1000) + " km");
                } else {
                    endPoint = new Location("locationA");
                    endPoint.setLatitude(marker.getPosition().latitude);
                    endPoint.setLongitude(marker.getPosition().longitude);

                    if (startPoint != null) {
                        double distance = startPoint.distanceTo(endPoint);

                        marker.setSnippet(String.valueOf(df.format(distance / 1000)) + " km");

                        Toast.makeText(MapsActivity.this, getCompleteAddressString(marker.getPosition().latitude, marker.getPosition().longitude), Toast.LENGTH_LONG).show();
                        showAlertDialog("Street Name : " + streetName + "\n" + "Postal Code : " + postalCode + "\n" + "City : " + city + "\n" + "Country : " + country);
                    } else {
                        Toast.makeText(MapsActivity.this, "", Toast.LENGTH_LONG).show();
                    }
                }

                return false;
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                aMarker = new Location("locationA");
                aMarker.setLatitude(polyline.getPoints().get(0).latitude);
                aMarker.setLongitude(polyline.getPoints().get(0).longitude);
                bMarker = new Location("locationA");
                bMarker.setLatitude(polyline.getPoints().get(1).latitude);
                bMarker.setLongitude(polyline.getPoints().get(1).longitude);

                Toast.makeText(MapsActivity.this, String.valueOf(df.format((aMarker.distanceTo(bMarker)) / 1000) + " km"), Toast.LENGTH_LONG).show();
                showAlertDialog(String.valueOf(df.format((aMarker.distanceTo(bMarker)) / 1000) + " km"));
            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {

                // Toast.makeText(MapsActivity.this, String.valueOf(getPolygonCenterPoint(markerLatLng)), Toast.LENGTH_LONG).show();


            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);

                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                polygon.remove();
                for (Polyline line : polyline) {
                    line.remove();
                }
                polyline.clear();
                if (markersList.size() > 3) {
                    drawLine(markersList.get(0).getPosition(), markersList.get(1).getPosition());
                    drawLine(markersList.get(1).getPosition(), markersList.get(2).getPosition());
                    drawLine(markersList.get(2).getPosition(), markersList.get(3).getPosition());
                    drawLine(markersList.get(3).getPosition(), markersList.get(0).getPosition());
                }
                drawShape();
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
            }
        });

        if (!checkPermission())
            requestPermission();
        else {
            //initLocationCallback();
            getSplashLocation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (latLng != null)
                        setHomeLocation(latLng);
                    else {
                        latLng = new LatLng(51.213890, -102.462776);
                        setHomeLocation(latLng);
                    }
                }
            }, 2000);
        }
    }

    // Get Complete Address
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    streetName = returnedAddress.getFeatureName();
                    postalCode = returnedAddress.getPostalCode();
                    country = returnedAddress.getCountryName();
                    city = returnedAddress.getLocality();
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    private void initLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    // get address
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses != null && addresses.size() > 0) {

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("location updates>>>", location.getLatitude() + String.valueOf(location.getLongitude()));
                }
            }
        };
    }

    // Permission Check
    private boolean checkPermission() {
        int isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return isGranted == PackageManager.PERMISSION_GRANTED;
    }

    // Permission Request
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //initLocationCallback();
            if (!checkPermission())
                requestPermission();
            else {
                //initLocationCallback();
                getSplashLocation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (latLng != null) {
                            setHomeLocation(latLng);
                        }
                    }
                }, 2000);
            }
            //fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    // Set Home Location
    private void setHomeLocation(LatLng location) {
        startPoint = new Location("locationA");
        startPoint.setLatitude(location.latitude);
        startPoint.setLongitude(location.longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(location)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .snippet("You are here");
        curr_marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 8));
    }

    // Set Destination Location
    private void setDestinationLocation(LatLng location) {
        markerLatLng.add(location);
        String label = nameOfMarkers[checkMarkerNamePosition];
        if (checkMarkerNamePosition == 3) {
            checkMarkerNamePosition = 0;
        } else {
            checkMarkerNamePosition++;
        }
        MarkerOptions markerOptions = new MarkerOptions().position(location)
                .title(label)
                .snippet(label)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .draggable(true);

        if (markersList.size() == POLYGON_NUM) {
            for (Polyline line : polyline) {
                line.remove();
            }
            polyline.clear();
            clearMap();
        }

        markersList.add(mMap.addMarker(markerOptions));

        if (markersList.size() == POLYGON_NUM) {
            drawShape();
            if (markersList.size() > 3) {
                drawLine(markersList.get(0).getPosition(), markersList.get(1).getPosition());
                drawLine(markersList.get(1).getPosition(), markersList.get(2).getPosition());
                drawLine(markersList.get(2).getPosition(), markersList.get(3).getPosition());
                drawLine(markersList.get(3).getPosition(), markersList.get(0).getPosition());
            }
        }
    }

    // Clear Map
    private void clearMap() {
        centerMarker.remove();
        if (dest_marker != null) {
            dest_marker.remove();
            dest_marker = null;
        }
        for (int i = 0; i < POLYGON_NUM; i++) {
            markersList.get(i).remove();
        }
        markersList.removeAll(markersList);
        //polyline.remove();
        //polyline.remove();
        polygon.remove();
    }

    // Draw Line
    private void drawLine(LatLng home, LatLng dest) {
        //int strokeColor = polyline.getStrokeColor() ^ 0xff0000;
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(home, dest)
                .clickable(true)
                .color(0xff0000)
                .width(20)
                .visible(true);
        polyline.add(mMap.addPolyline(polylineOptions));
        //polyline.setClickable(true);
    }

    // Draw Shape
    private void drawShape() {
        PolygonOptions polygonOptions = new PolygonOptions()
                .clickable(true)
                .strokeColor(R.color.red)
                .strokeWidth(20)
                .visible(true);

        for (int i = 0; i < POLYGON_NUM; i++) {
            polygonOptions.add(markersList.get(i).getPosition());
        }

        MarkerOptions markerOptions = new MarkerOptions().position(getPolygonCenterPoint(markerLatLng))
                .icon(getMarkerFromDrwable(getResources().getDrawable(R.drawable.ic_transparent)))
                .snippet("center");
        centerMarker = mMap.addMarker(markerOptions);

        polygon = mMap.addPolygon(polygonOptions);
        int fillColor = polygon.getStrokeColor() ^ 0x00FF00;
        polygon.setFillColor(fillColor);
        int strokeColor = polygon.getStrokeColor() ^ 0xff0000;
        polygon.setStrokeColor(strokeColor);
    }

    // Get location from Splash
    public void getSplashLocation() {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        setLoc(location);
                    }
                }
            };
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(2000);
            mLocationRequest.setFastestInterval(2000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } catch (Exception e) {
            Log.d("error", e.getMessage());
            e.printStackTrace();
        }

    }

    // Set Location
    private LatLng setLoc(Location loc) {

        latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        Log.d("lat", String.valueOf(loc.getLatitude()));
        return latLng;
    }

    // Alert Dialog
    private void showAlertDialog(String message) {
        final AlertDialog alertDialog1 = new AlertDialog.Builder(
                MapsActivity.this).create();
        alertDialog1.setTitle("");
        alertDialog1.setMessage(message);
        alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog1.show();
    }

    // Get Polygon Center
    private LatLng getPolygonCenterPoint(ArrayList<LatLng> polygonPointsList) {
        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < polygonPointsList.size(); i++) {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng = bounds.getCenter();

        return centerLatLng;
    }

    private BitmapDescriptor getMarkerFromDrwable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
