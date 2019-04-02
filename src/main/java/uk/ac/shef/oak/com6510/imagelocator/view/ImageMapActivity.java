package uk.ac.shef.oak.com6510.imagelocator.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import uk.ac.shef.oak.com6510.imagelocator.R;
import uk.ac.shef.oak.com6510.imagelocator.databinding.ImageMapActivityBinding;
import uk.ac.shef.oak.com6510.imagelocator.model.Image;

import uk.ac.shef.oak.com6510.imagelocator.viewmodel.ImageMapViewModel;

/*
ImageMapActivity extends AppCompatActivity and implements Observer, OnMapReadyCallback,
GoogleMap.OnMarkerClickListener to view the map activity
 */
public class ImageMapActivity extends AppCompatActivity implements Observer, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private ImageMapActivityBinding imageMapActivityBinding;
    private ImageMapViewModel imageMapViewModel;
    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;
    private LatLng myLocation;


/*
Overriding onCreate method and binds the data to ImageMapViewModel
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataBinding();
        setSupportActionBar(imageMapActivityBinding.toolbar);
        setupListPeopleView(imageMapActivityBinding.listImage);
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        imageMapActivityBinding.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences sp = getSharedPreferences("location", Activity.MODE_PRIVATE);
                    double latitude = Double.parseDouble(sp.getString("latitude", ""));
                    double longitude = Double.parseDouble(sp.getString("longitude", "")); //Location Detail
                    myLocation = new LatLng(latitude, longitude);
                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(myLocation)
                            .zoom(15)
                            .bearing(0)
                            .tilt(0)
                            .build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                            1500, null);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"unable to get Your Location",Toast.LENGTH_LONG);
                }
            }
        });
        setupObserver(imageMapViewModel);


        imageMapViewModel.initializeViews();

        //Using Async Function to access map fragment
        mMapFragment.getMapAsync(this);

    }


    private void initDataBinding() {
        imageMapActivityBinding = DataBindingUtil.setContentView(this, R.layout.image_map_activity);

        imageMapViewModel = new ImageMapViewModel(this, ImageMapActivity.this);

        imageMapActivityBinding.setImageMapViewModel(imageMapViewModel);


    }

    /*
    Using RecyclerView, the image list has been displayed at the bottom of the screen using ImageAdapter
     */
    private void setupListPeopleView(RecyclerView listImages) {
        ImageAdapter adapter = new ImageAdapter();
        listImages.setAdapter(adapter);

        listImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

    }

    private void setupMapView(MapFragment mapFragment) {

    }

    public void setupObserver(Observable observable) {
        observable.addObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageMapViewModel.reset();
    }

    /*
    Observes the change in the image list and updates the adapter accordingly
     */
    @Override
    public void update(Observable observable, Object o) {

        if (observable instanceof ImageMapViewModel) {
            final ImageAdapter peopleAdapter = (ImageAdapter) imageMapActivityBinding.listImage.getAdapter();
            ImageMapViewModel imageMapViewModel = (ImageMapViewModel) observable;
            imageMapViewModel.getImageList().observe(this, new android.arch.lifecycle.Observer<List<Image>>() {
                @Override
                public void onChanged(@Nullable List<Image> images) {

                    peopleAdapter.setImageList(images);

                    for (int i = 0; i < images.size(); i++) {
                        try {
                            Image image = images.get(i);
                            Double latitude1 = Double.valueOf(image.getLatitude());
                            Double longitude1 = Double.valueOf(image.getLongitude());
                            LatLng currentLatLng = new LatLng(latitude1, longitude1);
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(currentLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_map)));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    try {
                        double latitude = Double.parseDouble(images.get(0).getLatitude());
                        double longitude = Double.parseDouble(images.get(0).getLongitude());
                        myLocation = new LatLng(latitude, longitude);
                        CameraPosition cameraPosition = CameraPosition.builder()
                                .target(myLocation)
                                .zoom(15)
                                .bearing(0)
                                .tilt(0)
                                .build();
                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                                1500, null);

                    }catch (Exception e){

                    }
                }
            });

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    //Create the custom marker to scatter images on the map
    public static Bitmap createCustomMarker(Context context, String url) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        ImageView markerImage = (ImageView) marker.findViewById(R.id.image_dp);
        markerImage.setImageBitmap(BitmapFactory.decodeFile(url));



        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint color = new Paint();
        color.setTextSize(35);
        color.setColor(Color.BLACK);
        canvas.drawBitmap(BitmapFactory.decodeFile(url), 0,0, color);
        return bitmap;

    }

    /*
    populate the image list
     */
    private  class PopulateMapAsync extends AsyncTask<Void, Void, Void> {

        private List<Image> images;
        public PopulateMapAsync(List<Image> images) {
        this.images=images;

        }
        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }
}
