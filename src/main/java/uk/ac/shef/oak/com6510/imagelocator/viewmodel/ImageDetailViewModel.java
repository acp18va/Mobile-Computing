

package uk.ac.shef.oak.com6510.imagelocator.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;



import uk.ac.shef.oak.com6510.imagelocator.model.Image;
import uk.ac.shef.oak.com6510.imagelocator.view.ImageDetailActivity;
import uk.ac.shef.oak.com6510.imagelocator.view.ImageMapActivity;

public class ImageDetailViewModel {

  private Image image;
  private ImageDetailActivity context;

  public ImageDetailViewModel(Image image,ImageDetailActivity context) { //Class constructor
    this.image = image;
    this.context=context;
  }

  //full image name
  public String getFullUserName() {

    return image.fullName;
  }



  public String getEmail() {
    return image.mail;
  }

  public int getEmailVisibility() {
    return image.hasEmail() ? View.VISIBLE : View.GONE;
  }


//gets the image for imageDetailActivity
  public String getPictureProfile() {
    return image.picture;
  }

  public String getAddress() {
    return image.location
        + " "
        + image.location
       ;
  }


// Binding images to the adapter
  @BindingAdapter({"imageUrl"})
  public static void loadImage(ImageView view, String imageUrl) {
    view.setImageBitmap(BitmapFactory.decodeFile(imageUrl));
  }

  public void onClickFabLoadInfo(View view) {
    //Intent to camera Your module start here
   context.changeLayout();

  }
}
