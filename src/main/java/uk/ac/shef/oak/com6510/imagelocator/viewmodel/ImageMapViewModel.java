package uk.ac.shef.oak.com6510.imagelocator.viewmodel;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;
import java.util.Observable;

import uk.ac.shef.oak.com6510.imagelocator.database.ImageDao;

import uk.ac.shef.oak.com6510.imagelocator.database.ImageDatabase;
import uk.ac.shef.oak.com6510.imagelocator.model.Image;

import uk.ac.shef.oak.com6510.imagelocator.view.ImageMapActivity;

//observes for changes in the imageMapActivity and updates the database
public class ImageMapViewModel extends Observable {

    public ObservableInt imageRecycler;

    private Context context;
    private ImageMapActivity imageMapActivity;
    private LiveData<List<Image>>  imageList;
    private ImageDao imageDao;
    public ImageMapViewModel(@NonNull Context context, ImageMapActivity imageMapActivity) {

        this.context = context;
        this.imageMapActivity=imageMapActivity;
        imageRecycler = new ObservableInt(View.GONE);

    }

    public void initializeViews() {

        imageRecycler.set(View.GONE);

        imageDao = ImageDatabase.getDatabase(context,imageMapActivity).imageDao();
        this.imageList = imageDao.getAllImage();
        getAllShownImagesPath();
    }
    //notify the view for changes in the image dataset
    private void changePeopleDataSet(LiveData<List<Image>> images) {
        imageList = images;
        setChanged();
        notifyObservers();
    }

    //notify the observer for changes in the scroll bar
    public void runOutsideOnscroll(){
        setChanged();
        notifyObservers();
    }
    public LiveData<List<Image>>  getImageList() {
        return imageList;
    }




    public void getAllShownImagesPath() {

        changePeopleDataSet(this.imageList);

        imageRecycler.set(View.VISIBLE);
    }
    public void reset() {

        context = null;
    }
}
