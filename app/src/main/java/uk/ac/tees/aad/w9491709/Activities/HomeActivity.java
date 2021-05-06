package uk.ac.tees.aad.w9491709.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.tees.aad.w9491709.MainActivity;
import uk.ac.tees.aad.w9491709.R;
import uk.ac.tees.aad.w9491709.Utills.mLocation;
import uk.ac.tees.aad.w9491709.directionhelpers.FetchURL;
import uk.ac.tees.aad.w9491709.directionhelpers.TaskLoadedCallback;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        LocationListener, NavigationView.OnNavigationItemSelectedListener, TaskLoadedCallback {


    //create varibale
    Polyline currentPolyline;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    View mapView;
    LocationManager locationManager;
    public static final int GPS = 101;
    TextView distanceTV;
    LatLng latLngCurrentLocation, latLngTargetLocation;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef, mUserRef;
    ImageView addLocationBtn;
    MaterialSearchBar searchBar;
    Dialog dialog;
    List<mLocation> listSavedLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    PlacesClient placesClient;
    List<AutocompletePrediction> perdictionList;
    LocationCallback locationCallback;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CircleImageView profileImage;
    TextView username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Location");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        addLocationBtn = findViewById(R.id.addLocationBtn);
        searchBar = findViewById(R.id.searchBar);
        distanceTV = findViewById(R.id.distance);
        dialog = new Dialog(this);

        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        View view = navigationView.inflateHeaderView(R.layout.drawer_layout);
        username = view.findViewById(R.id.username);
        profileImage = view.findViewById(R.id.setup_profile_image1);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        mapView = mapFrag.getView();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomeActivity.this);
        Places.initialize(HomeActivity.this, "AIzaSyBMaQW3gV074y-Y2Gz4WRj81a6Gj-c1Zbo");
        placesClient = Places.createClient(HomeActivity.this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        //load my profile from firebase
        LoadMyProfile();


        //set click event listner on searchbar
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    searchBar.closeSearch();
                }
            }
        });

        //when user type anything .its will perform finding suggestions
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    searchBar.clearSuggestions();
                }

                //get suggestion using places APIs
                FindAutocompletePredictionsRequest perdictionRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("uk").setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(charSequence.toString())
                        .build();
                placesClient.findAutocompletePredictions(perdictionRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse perdictionRespose = task.getResult();
                            if (perdictionRequest != null) {
                                perdictionList = perdictionRespose.getAutocompletePredictions();
                                List<String> suggestionList = new ArrayList<>();

                                //assign places search bar
                                for (int i = 0; i < perdictionList.size(); i++) {
                                    AutocompletePrediction perdiction = perdictionList.get(i);
                                    suggestionList.add(perdiction.getFullText(null).toString());
                                }
                                searchBar.updateLastSuggestions(suggestionList);
                                if (!searchBar.isSuggestionsVisible()) {
                                    searchBar.showSuggestionsList();
                                }
                            }

                        } else {
                            Toast.makeText(HomeActivity.this, " " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //set clcik event listner on ad button clcik
        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latLngCurrentLocation != null) {

                    //open new activity and send latitude and longitude
                    Intent intent = new Intent(HomeActivity.this, AddLocationActivity.class);
                    intent.putExtra("latitude", latLngCurrentLocation.latitude);
                    intent.putExtra("longitude", latLngCurrentLocation.longitude);
                    startActivity(intent);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        //set click liner on when user lcick on any suggestion
        searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= perdictionList.size()) {
                    return;
                } else {

                    //add clicked suggestion in search bar and remove other suggestion
                    AutocompletePrediction clickedPrediction = perdictionList.get(position);
                    String suggestion = searchBar.getLastSuggestions().get(position).toString();
                    searchBar.setText(suggestion);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchBar.clearSuggestions();
                        }
                    }, 1000);

                    hideKeyboard();

                    String placeID = clickedPrediction.getPlaceId();
                    List<Place.Field> placeField = Arrays.asList(Place.Field.LAT_LNG);
                    FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeID, placeField).build();
                    placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @Override
                        public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {

                            //get the location of selected sugegstion
                            Place place = fetchPlaceResponse.getPlace();
                            LatLng latLng = place.getLatLng();
                            if (latLng != null) {
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
            }
        });
    }


    //load my profile data from firebase database
    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    //assign data to Navugation drawer after loaded data from firebase
                    username.setText(snapshot.child("username").getValue().toString());
                    Picasso.get().load(snapshot.child("profileImageUrl").getValue().toString()).into(profileImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    //hide keyboard method when user click on any suggestion
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

    }

    //read all location and assign on googlr map
    private void LoadSavedLocation() {
        mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSavedLocation = new ArrayList<>();
                listSavedLocation.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    mLocation post = postSnapshot.getValue(mLocation.class);
                    listSavedLocation.add(post);

                    //add marker accroding to saved location
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().
                            position(new LatLng(post.getLatitude(), post.getLongitude())).
                            title(post.getMarkerName()));

                    marker.setTag(postSnapshot.getRef().getKey());
                    marker.showInfoWindow();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    //when clocse app gps will close
    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    //Check Gps is enable or not
    private void getLocationUpdate() {
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, HomeActivity.this);
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, HomeActivity.this);
            } else {
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(5000);
                mLocationRequest.setFastestInterval(2000);


                //request to show dialog if gps is not enable
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(mLocationRequest);
                builder.setAlwaysShow(true);
                Task<LocationSettingsResponse> requestTask = LocationServices.getSettingsClient(getApplicationContext())
                        .checkLocationSettings(builder.build());
                requestTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            LocationSettingsResponse result = task.getResult(ApiException.class);
                        } catch (ApiException e) {
//                            e.printStackTrace();
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            try {
                                resolvableApiException.startResolutionForResult(HomeActivity.this, GPS);
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            }
                        }
                    }
                });


            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LoadSavedLocation();
        //getLocationPermisiion
        getLocationPermisiion();
        ChangeLocationOfGpsButton();


        //set click listner when click on myLOcation Button
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                getLocationUpdate();
                searchBar.clearSuggestions();
                searchBar.closeSearch();
                return false;
            }
        });


        //set click listner to select target location
        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                latLngTargetLocation = latLng;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                alertDialog.setTitle("Do you want to draw Route b/w Current Location and Target Location");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (latLngCurrentLocation != null && latLngTargetLocation != null) {
                            new FetchURL(HomeActivity.this).execute(getUrl(latLngCurrentLocation, latLngTargetLocation), "driving");
                            //Distance calculation
                            double distance = SphericalUtil.computeDistanceBetween(latLngCurrentLocation, latLngTargetLocation);
//                            distance.setVisibility(View.VISIBLE);
                            if (distance > 1000) {
                                distanceTV.setText("Distance: " + (int) distance / 1000 + "KM");
                            } else {
                                distanceTV.setText("Distance: " + (int) distance + "M");
                            }
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alertDialog.show();
            }
        });


        //Set Click event listner on Marker images
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = (marker.getPosition());
//                Intent intent = new Intent(HomeActivity.this, ViewActivity.class);
//                intent.putExtra("key", marker.getTag().toString());
//                startActivity(intent);

                return false;
            }
        });
    }
    //change the loscation of my Current lcoation button
    private void ChangeLocationOfGpsButton() {
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();

            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 50, 30);

        }
    }

    //check permisision of current lcoation
    private void getLocationPermisiion() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    //prepare the Url request to show route on google mpa
    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + "walking";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyBMaQW3gV074y-Y2Gz4WRj81a6Gj-c1Zbo";
    }

    //on Connect map help to check gps enable
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    //when location change of device autometically change this method
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        latLngCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    //permission respose when use allow or deny permission
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HomeActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    //    permission checking
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        getLocationUpdate();

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //check gps enable response
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS) {
            switch (GPS) {
                case Activity.RESULT_OK:
                    Toast.makeText(this, "GPS is ON", Toast.LENGTH_SHORT).show();

                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "GPS is required to use feture of this app", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

    //draw line on map
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mGoogleMap.addPolyline((PolylineOptions) values[0]);
    }


    //set click event listner on navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();

            //open new Activity when click on logout
            Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.myMemory) {
            Intent intent = new Intent(HomeActivity.this, MemoryActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.discustion) {
            Intent intent = new Intent(HomeActivity.this, UsersActivity.class);
            startActivity(intent);
        }

        else if (item.getItemId() == R.id.alarm) {
            Intent intent = new Intent(HomeActivity.this, AlarmActivity.class);
            startActivity(intent);
        }
        return false;
    }
}


