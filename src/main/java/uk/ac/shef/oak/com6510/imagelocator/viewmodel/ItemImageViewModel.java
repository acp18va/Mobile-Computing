
package uk.ac.shef.oak.com6510.imagelocator.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;


import uk.ac.shef.oak.com6510.imagelocator.model.Image;
import uk.ac.shef.oak.com6510.imagelocator.view.ImageDetailActivity;


//created to set the image and handle click events
public class ItemImageViewModel extends BaseObservable {

  private Image image;
  private Context context;

  public ItemImageViewModel(Image image, Context context) {
    this.image = image;
    this.context = context;
  }

  public String getPictureProfile() {
    return image.picture;
  }


  @BindingAdapter("imageUrl") public static void setImageUrl(ImageView imageView, String url) {
    try{
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inSampleSize = 8;
    imageView.setImageBitmap(BitmapFactory.decodeFile(url));}
    catch (Exception e){
      e.printStackTrace();
    }
  }

  public void onItemClick(View view) {
    context.startActivity(ImageDetailActivity.launchDetail(view.getContext(), image));
  }

  public void setImage(Image image) {
    this.image = image;
    notifyChange();
  }
}
