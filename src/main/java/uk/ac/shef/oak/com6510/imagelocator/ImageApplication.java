

package uk.ac.shef.oak.com6510.imagelocator;

import android.content.Context;
import android.support.multidex.MultiDexApplication;



public class ImageApplication extends MultiDexApplication {




  private static ImageApplication get(Context context) {
    return (ImageApplication) context.getApplicationContext();
  }

  public static ImageApplication create(Context context) {
    return ImageApplication.get(context);
  }





}
