package uk.ac.shef.oak.com6510.imagelocator.viewmodel;

import android.arch.lifecycle.MutableLiveData;

//gets the mutable live data of the images
public class ImageDescriptionViewModel extends BaseViewModel {
    public MutableLiveData<String> imageUrl= new MutableLiveData<>();
    public MutableLiveData<String> imageDescription= new MutableLiveData<>();
    public MutableLiveData<String> imageTitle= new MutableLiveData<>();
}
