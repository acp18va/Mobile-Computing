

package uk.ac.shef.oak.com6510.imagelocator.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
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

import java.io.IOException;

import uk.ac.shef.oak.com6510.imagelocator.R;
import uk.ac.shef.oak.com6510.imagelocator.databinding.ImageDetailActivityBinding;
import uk.ac.shef.oak.com6510.imagelocator.database.ImageDao;
import uk.ac.shef.oak.com6510.imagelocator.database.ImageDatabase;
import uk.ac.shef.oak.com6510.imagelocator.interfaces.IDiscription;
import uk.ac.shef.oak.com6510.imagelocator.model.Image;
import uk.ac.shef.oak.com6510.imagelocator.viewmodel.ImageDetailViewModel;

/*
ImageDetailActivity extends the super class AppCompatActivity and implements IDiscription ,OnMapReadyCallback,
GoogleMap.OnMarkerClickListener to view the metadata of the image
 */

public class ImageDetailActivity extends AppCompatActivity implements IDiscription ,OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

  private static final String EXTRA_IMAGE = "EXTRA_IMAGE";
  private LinearLayout linearLayout;

  private MapFragment mapFragment; //Using map fragment to show image location on the side bar
  private GoogleMap mGoogleMap;
  private boolean mMapReady = false;
   private boolean windoInfo;
   private String title;
  private IDiscription iDiscription=this;
  private ImageDetailActivityBinding imageDetailActivityBinding;

  /*
  Data Binding to ImageDetailViewModel
   */

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    imageDetailActivityBinding =
        DataBindingUtil.setContentView(this, R.layout.image_detail_activity);
    mapFragment= (MapFragment) getFragmentManager().findFragmentById(R.id.map_location);
    mapFragment.getMapAsync(this);
    setSupportActionBar(imageDetailActivityBinding.toolbar);
    setUpLayout(imageDetailActivityBinding.linearMain);

    displayHomeAsUpEnabled();

    imageDetailActivityBinding.btSave.setOnClickListener(v -> iDiscription.onClickListner(v));

  }

  //Using intent to show the image
  public static Intent launchDetail(Context context, Image image) {
    Intent intent = new Intent(context, ImageDetailActivity.class);
    intent.putExtra(EXTRA_IMAGE, image);
    return intent;
  }

  private void displayHomeAsUpEnabled() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  /*
  Set the layout as linerLayout and parameters as MATCH_PARENT when the info button is clicked
   */
  private void setUpLayout(LinearLayout linearLayout) {
   this.linearLayout=linearLayout;
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.MATCH_PARENT);
    params.weight = 3.0f;
   this.linearLayout.setLayoutParams(params);
  }

  /*
  Get Image Information from the intent
   */
  private void getExtrasFromIntent() {
     Image image = (Image) getIntent().getSerializableExtra(EXTRA_IMAGE);
    ImageDetailViewModel imageDetailViewModel = new ImageDetailViewModel(image,ImageDetailActivity.this);
    imageDetailActivityBinding.setImageDetailViewModel(imageDetailViewModel);
    imageDetailActivityBinding.etTitle.setText(image.getTitle());
    imageDetailActivityBinding.etDescription.setText(image.getDescription());
    title=image.getTitle();
    try {
      Double latitude = Double.valueOf(image.getLatitude());  //Image location Details
      Double longitude = Double.valueOf(image.getLongitude());
      LatLng currentLatLng = new LatLng(latitude, longitude);
      CameraPosition cameraPosition = CameraPosition.builder()
              .target(currentLatLng)
              .zoom(15)
              .bearing(0)
              .tilt(0)
              .build();
      mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
              1500, null);
      mGoogleMap.addMarker(new MarkerOptions()
              .position(currentLatLng)
              .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_map)));
    }catch (Exception e){
      e.printStackTrace();
      Toast.makeText(getApplicationContext(),"You have location issue",Toast.LENGTH_LONG).show();
    }
  }

  //Apply Layout changes made above
  public void changeLayout() {
   if(!windoInfo) {
     LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
             0, LinearLayout.LayoutParams.MATCH_PARENT);
     params.weight = 2.0f;
     this.linearLayout.setLayoutParams(params);
     windoInfo=true;
   }
   else {
     LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
             0, LinearLayout.LayoutParams.MATCH_PARENT);
     params.weight = 3.0f;
     this.linearLayout.setLayoutParams(params);
     windoInfo=false;
   }
  }

  @Override
  public void showMessage(String msg) {

  }

  @Override
  public void showMessageWithIcon(String msg, boolean isSuccess) {

  }
//Overriding onClickListner function of IDescription on save button
  @Override
  public void onClickListner(View view) {
    switch (view.getId()) {
      case R.id.btSave:

          Intent returnIntent = new Intent();
          updateImage(imageDetailActivityBinding.etTitle.getText().toString(),imageDetailActivityBinding.etDescription.getText().toString());

          setResult(RESULT_OK,returnIntent);
          finish();

        break;
      default: iDiscription.showMessage(getString(R.string.nothing_click));
    }
  }

  //Update Image metadata throughout the database
  private void updateImage(String title,String description) {
    if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) {
      return;
    }

    ImageDao imageDao = ImageDatabase.getDatabase(this,ImageDetailActivity.this).imageDao();

    if (title != null && description != null) {
      // clicked on item row -> update
      Image imageToUpdate = imageDao.findImageByTitle(title);
      if (imageToUpdate != null) {
        if (!imageToUpdate.getTitle().equals(title) || !imageToUpdate.getDescription().equals(description)) {
          imageToUpdate.setTitle(title);
          imageToUpdate.setDescription(description);
          imageDao.update(imageToUpdate);
          try {
            ExifInterface exif =new ExifInterface(imageToUpdate.getImagePath());


          exif.setAttribute(ExifInterface.TAG_USER_COMMENT, description);
          exif.saveAttributes();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    return false;
  }

  //Google Map Access functionality
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mGoogleMap = googleMap;
    mMapReady = true;
    getExtrasFromIntent();
  }
}
