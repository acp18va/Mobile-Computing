


package uk.ac.shef.oak.com6510.imagelocator.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import uk.ac.shef.oak.com6510.imagelocator.R;
import uk.ac.shef.oak.com6510.imagelocator.databinding.ImageActivityBinding;

import uk.ac.shef.oak.com6510.imagelocator.database.ImageDatabase;
import uk.ac.shef.oak.com6510.imagelocator.interfaces.ILocation;
import uk.ac.shef.oak.com6510.imagelocator.model.Image;
import uk.ac.shef.oak.com6510.imagelocator.utilities.Constants;
import uk.ac.shef.oak.com6510.imagelocator.utilities.FileUtilities;
import uk.ac.shef.oak.com6510.imagelocator.utilities.LocationClass;
import uk.ac.shef.oak.com6510.imagelocator.viewmodel.ImageViewModel;

/*
ImageActivity extends BaseActivity and implements Observer and ILocation Interfaces
It is the main activity of our application
 */
public class ImageActivity extends BaseActivity<ImageActivityBinding> implements Observer,ILocation {


  int pageNumber=0, y=0;

  //Setting Manifest permissions
  private ImageAdapter imageAdapter;
  private ImageViewModel imageViewModel;
  private static final String[] LOCATION_AND_STORAGE = {
          Manifest.permission.ACCESS_FINE_LOCATION
          , Manifest.permission.ACCESS_COARSE_LOCATION
          , Manifest.permission.READ_EXTERNAL_STORAGE
          , Manifest.permission.WRITE_EXTERNAL_STORAGE
          , Manifest.permission.CAMERA
  };

  //Accessing sharedpreferances for location action permission
  SharedPreferences sharedpreferences ;
  public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
  private ILocation iLocation=this;
  private LocationClass locationClass;
  public Location location;

  //Overriding the onCreate Function
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setView();
    //Requesting and Checking Permissions
    checkAndRequestPermissions();

    if(isPermissionGranted()) {
      sharedpreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
      initDataBinding();
      imageViewModel = new ImageViewModel(this, ImageActivity.this);

    //Data Binding
      getBinding().setMainViewModel(imageViewModel);
      setSupportActionBar(getBinding().toolbar);
      setUpSearchText(getBinding().searchText);
      setupListImageView(getBinding().listImage);
      setupObserver(imageViewModel);
    //Set up the observer

      imageViewModel.initializeViews();
    }

  }

  private void initDataBinding() {
    reCreateDatabase();
    locationClass=new LocationClass();
    locationClass.getLocation(getApplicationContext(),iLocation);
  }

//Checking if the permission ia granted
  public boolean isPermissionGranted() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
              || ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
              || ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
              || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
              || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
        return false;
      }

    }
    return  true;
  }

  //Accessing the storage if permission granted
  private void checkPermission() {
    if (imageViewModel.isPermissionGranted()) {
      super.onRequestPermission(LOCATION_AND_STORAGE, Constants.permissionRequest);
    }
  }
  //Setting up list view in the recyclerView
  private void setupListImageView(RecyclerView listImage) {
    ImageAdapter adapter = new ImageAdapter();
    listImage.setAdapter(adapter);
    listImage.setLayoutManager(new GridLayoutManager(ImageActivity.this,3));
    listImage.setNestedScrollingEnabled(false);
    listImage.addOnScrollListener(new RecyclerView.OnScrollListener() {

      /*
      Overriding the onScrolled function
      calling the super function
       */

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        y=dy;

      }

      /*
      Setting the onScroll setting such that 24 images are shown on each scroll
      or on 1 page
       */

      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        try {
          if (listImage.SCROLL_STATE_DRAGGING == newState) {

          }

          if (listImage.SCROLL_STATE_IDLE == newState) {

            if (y <= 0 && pageNumber >= 0) {

              pageNumber-=24;
              imageViewModel.runOutsideOnscroll(24,pageNumber);

            } else {

              if (!recyclerView.canScrollVertically(1)) {


                y = 0;
                //     Toast.makeText(getActivity(), "up", Toast.LENGTH_LONG).show();
               pageNumber+=24;
                imageViewModel.runOutsideOnscroll(24,pageNumber);




              }
            }
          }
        }catch (Exception e){

        }
      }

    });
  }

  /*Search bar on top to search for the words in title and description through the database and
  displaying the valid results
   */

  private void setUpSearchText(EditText searchText) {
    getBinding().imSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        imageViewModel.getFilterImages("%"+searchText.getText().toString()+"%");
      }
    });

  }

  //Setting up observer
  public void setupObserver(Observable observable) {
    observable.addObserver(this);
  }
  //onDestroy function
  @Override protected void onDestroy() {
    super.onDestroy();
    imageViewModel.reset();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    return super.onOptionsItemSelected(item);
  }



  @Override public void update(Observable observable, Object data) {

    //Updating adapter on notifychanged when ever image list is updated

    if (observable instanceof ImageViewModel) {
       imageAdapter = (ImageAdapter) getBinding().listImage.getAdapter();

      ImageViewModel imageViewModel = (ImageViewModel) observable;
      imageViewModel.getImageList().observe(this, new android.arch.lifecycle.Observer<List<Image>>() {
        @Override
        public void onChanged(@Nullable List<Image> images) {
     try {
       if (images.size() > 0) {
         imageAdapter.setImageList(images);
       }
     }catch (Exception e)
     {
       e.printStackTrace();
     }
        }
      });

    }
  }

  private void reCreateDatabase() {

    try {
         //Calling asynchronous task
        ImageDatabase.getDatabase(this,ImageActivity.this).clearDb();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  String filePathString=null; Bitmap photo=null;


  @Override
  public void onLocationUpdate(Location location) {
    //saving the current location in shared preference
    SharedPreferences.Editor editor = sharedpreferences.edit();
    editor.putString("latitude", location.getLatitude()+"");
    editor.putString("longitude", location.getLongitude()+"");
    editor.commit();
    Log.e("location",location.getLatitude() +"-"+ location.getLongitude());
    this.location=location;
  }

  @Override
  protected int getLayoutID() {
    return R.layout.image_activity;
  }

  @SuppressLint("WrongConstant")
  public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

//handling the camera intent
    switch(requestCode) {
      case Constants.CAMERA_REQUEST:
        if(resultCode == RESULT_OK) {
          photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
          // GET URI FROM BITMAP
          Uri tempUri = imageViewModel.getImageUri(photo);
          FileUtilities fileUtilities=new FileUtilities(this);
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            filePathString= fileUtilities.getFilePath(tempUri);
            startActivityForSaveOtherInformation(filePathString);
          }
        }
        break;
      case Constants.START_ACTIVITY:
        if(resultCode == RESULT_OK) {
          String title=imageReturnedIntent.getStringExtra(Constants.TITLE);
          String description=imageReturnedIntent.getStringExtra(Constants.DISCRIPTION);
          if(filePathString != null && photo != null) {
            imageViewModel.saveDataOnRoom(photo, location, title, description, filePathString);
          try {
            ExifInterface exif = new ExifInterface(filePathString);


            exif.setAttribute(ExifInterface.TAG_USER_COMMENT, description);
            exif.setAttribute(ExifInterface.TAG_DATETIME,title);
            exif.saveAttributes();
          }catch (IOException e){
            e.printStackTrace();
          }
          }
        }
        break;
    }
  }

  private void startActivityForSaveOtherInformation(String filePathString) {
    //starting the image description activity after capturing image
    Intent intent = new Intent(this, DiscriptionActivity.class);
    intent.putExtra(Constants.IMAGE_STRING,filePathString);
    startActivityFromChild(this,intent,Constants.START_ACTIVITY);
  }

  private  boolean checkAndRequestPermissions() {
    //checking the permission
    int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
    int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
    int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
    int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
    List<String> listPermissionsNeeded = new ArrayList<>();

    if (camera != PackageManager.PERMISSION_GRANTED) {
      listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
    }
    if (storage != PackageManager.PERMISSION_GRANTED) {
      listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    if (loc2 != PackageManager.PERMISSION_GRANTED) {
      listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
    }
    if (loc != PackageManager.PERMISSION_GRANTED) {
      listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
    }
    final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
      buildAlertMessageNoGps();
    }


    if (!listPermissionsNeeded.isEmpty())
    {
      ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
              (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
      return false;
    }

    return true;
  }
  private void buildAlertMessageNoGps() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
              }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
              public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
              }
            });
    final AlertDialog alert = builder.create();
    alert.show();
  }
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {

      case 1: {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

         // initializing function after permission is granted
          sharedpreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
          initDataBinding();
          imageViewModel = new ImageViewModel(this, ImageActivity.this);


          getBinding().setMainViewModel(imageViewModel);
          setSupportActionBar(getBinding().toolbar);
          setUpSearchText(getBinding().searchText);
          setupListImageView(getBinding().listImage);
          setupObserver(imageViewModel);


          imageViewModel.initializeViews();


        } else {
          Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
        return;
      }

      // other 'case' lines to check for other
      // permissions this app might request
    }
  }
}
