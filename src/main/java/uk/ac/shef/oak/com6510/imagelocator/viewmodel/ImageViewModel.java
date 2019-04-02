
package uk.ac.shef.oak.com6510.imagelocator.viewmodel;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;


import java.io.ByteArrayOutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


import uk.ac.shef.oak.com6510.imagelocator.database.ImageDao;
import uk.ac.shef.oak.com6510.imagelocator.database.ImageDatabase;
import uk.ac.shef.oak.com6510.imagelocator.model.Image;
import uk.ac.shef.oak.com6510.imagelocator.utilities.Constants;
import uk.ac.shef.oak.com6510.imagelocator.utilities.ToastUtilities;
import uk.ac.shef.oak.com6510.imagelocator.view.ImageActivity;
import uk.ac.shef.oak.com6510.imagelocator.view.ImageMapActivity;

//it observes for the camera and map button clicks

public class ImageViewModel extends Observable {

  public ObservableInt imageProgress;
  public ObservableInt textSearch;
  public ObservableInt imageRecycler;
  public ObservableInt imageLabel;
  public ObservableField<String> messageLabel;

  private LiveData<List<Image>> imageList;
  private Context context;
  private ImageDao imageDao;

  private LiveData<List<Image>> imageLiveData;
    android.arch.lifecycle.Observer<List<Image>> temp;
  private ImageActivity imageActivity;
  public ImageViewModel(@NonNull Context context, ImageActivity imageActivity) {

    this.context = context;
    this.imageActivity = imageActivity;
    imageProgress = new ObservableInt(View.GONE);
    imageRecycler = new ObservableInt(View.GONE);
    imageLabel = new ObservableInt(View.VISIBLE);
    textSearch=new ObservableInt(View.GONE);
  }
    public void onClickFabLoad(View view) {

    //Intent to camera module start here with permission request
      if (isCameraPermissionGranted()) {

        Intent camera = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        imageActivity.startActivityForResult(camera, Constants.CAMERA_REQUEST);

      } else {
        ToastUtilities.toast(context,"Provide Amera request");
      }
    }

    //check for the permission status
  private boolean isCameraPermissionGranted() {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
      return  true;
    }
    return false;
  }
  public void onClickFabLoadMap(View view) {

    //Intent to map module start here
    Intent i=new Intent(imageActivity,ImageMapActivity.class);

    context.startActivity(i);
    }

  //It is "public" to show an example of test
  public void initializeViews() {
    textSearch.set(View.GONE);
    imageLabel.set(View.GONE);
    imageRecycler.set(View.GONE);
    imageProgress.set(View.VISIBLE);
    imageDao = ImageDatabase.getDatabase(context, imageActivity).imageDao();
    this.imageList = imageDao.getAllImage(24,0);
    getAllShownImagesPath();

  }

  //notifies observer for the image Dataset Changes
  private void changeImageDataSet(LiveData<List<Image>>  images) {
   imageList = images;
    setChanged();
    notifyObservers();
  }

  public LiveData<List<Image>> getImageList() {
    return imageList;
  }

  private void unSubscribeFromObservable() {

  }

  public void reset() {
    unSubscribeFromObservable();

    context = null;
  }

  public void getAllShownImagesPath() {


    changeImageDataSet(this.imageList);
    imageProgress.set(View.GONE);
    imageLabel.set(View.GONE);
    imageRecycler.set(View.VISIBLE);
    textSearch.set(View.VISIBLE);
  }

  public void filter(final String text){
      final List<Image> temp=new ArrayList<>();
   final   MediatorLiveData<List<Image>> usersLiveData =
              new MediatorLiveData<>();

   //observe for the change in the image activity

    imageList.observe(imageActivity, new android.arch.lifecycle.Observer<List<Image>>() {
      @Override
      public void onChanged(@Nullable List<Image> images) {
        for(Image i: images){
          //or use .equal(text) with you want equal match
          //use .toLowerCase() for better matches
          if(i.getTitle().equalsIgnoreCase(text) || i.getDescription().equalsIgnoreCase(text))
            temp.add(i);

        }

        if(temp.size()>0 && text.length()>0){
          imageList.getValue().clear();
          imageList.getValue().addAll(temp);
          setChanged();
          notifyObservers();
        }
        else {
        imageList = imageDao.getAllImage();
        }
      }
    });

    //update recyclerview

  }

  public boolean isPermissionGranted() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
              || ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
              || ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
              || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
              || ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
        return false;
      }

    }
    return  true;
  }

  public boolean isStoragePermissionGranted() {
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
      return  true;
    }
    return false;
  }

  public Uri getImageUri(Bitmap photo) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), photo, "Title", null);
    return Uri.parse(path);
  }

  private byte[] convertBitmaptoByte(Bitmap bitmap) {
    int size     = bitmap.getRowBytes() * bitmap.getHeight();
    ByteBuffer b = ByteBuffer.allocate(size);
    bitmap.copyPixelsToBuffer(b);
    byte[] bytes = new byte[size];
    try {
      b.get(bytes, 0, bytes.length);
    } catch (BufferUnderflowException e) {
      // always happens
    }
    return bytes;
  }

  //save data to the database via room

  public void saveDataOnRoom(final Bitmap photo, Location location, String title, String discription, String filePathString) {
    Image image =new Image();
    image.setImage(convertBitmaptoByte(photo));
    image.setTitle(title);
    image.setDescription(discription);
    image.setImagePath(filePathString);
    image.setLongitude(String.valueOf(location.getLatitude()));
    image.setLatitude(String.valueOf(location.getLatitude()));
    Long value=imageDao.insertSingleImageOnDB(image);
    ToastUtilities.toastSuccess(context,"Id is:"+value);
  }
  public void runOutsideOnscroll(int limit,int offset){
    this.imageList=imageDao.getAllImage(limit,offset);
    setChanged();
    notifyObservers();
  }
  public void getFilterImages(String search){
 new SearchDbAsync(search,imageDao).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

  }

  //AsyncTask for searching the images in the database using keywords
  private  class SearchDbAsync extends AsyncTask<Void, Void,  LiveData<List<Image>>> {
    private final String search;
    private final ImageDao imageDao;


    public SearchDbAsync(String search,ImageDao imageDao) {
      this.search =search;
      this.imageDao =imageDao;

    }

    @Override
    protected LiveData<List<Image>> doInBackground(Void... voids) {

      imageList=imageDao.getFilterImages(search);




      return imageList;
    }

    //notifies the observers for changes in the image list after search
    @Override
    protected void onPostExecute(LiveData<List<Image>> images) {
      super.onPostExecute(images);
      imageList=images;
      setChanged();
      notifyObservers();
    }
  }
}
