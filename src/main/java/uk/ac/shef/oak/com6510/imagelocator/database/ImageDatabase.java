package uk.ac.shef.oak.com6510.imagelocator.database;

import android.app.Activity;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import uk.ac.shef.oak.com6510.imagelocator.model.Image;

/*
ImageDatabase extends the RoomDatabase and implements queries of ImageDao
Defining the name of our Database
 */

@Database(entities = {Image.class}, version = 1)
public abstract class ImageDatabase extends RoomDatabase {

    private static ImageDatabase INSTANCE;
    private static final String DB_NAME = "images.db";
    private  static Context imageActivityInstance;

    /*
    Using Synchronized block to prevent overloading UI thread
    Building our Database
     */

    public static ImageDatabase getDatabase(final Context context, Context imageActivity) {
        imageActivityInstance = imageActivity;
        if (INSTANCE == null) {
            synchronized (ImageDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ImageDatabase.class, DB_NAME)
                            .allowMainThreadQueries()
                            .addCallback(new Callback() {

                                /*
                                Overriding the onCreate function
                                Using SQLite database
                                Populating the database with our data(images) using AsyncTask
                                 */

                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d("ImageDatabase", "populating with data...");
                                 new PopulateDbAsync(INSTANCE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                                }
                            })
                            .build();
                }
            }
        }

        return INSTANCE;
    }
    public void clearDb() {
        if (INSTANCE != null) {
           new PopulateDbAsync(INSTANCE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
        }
    }

    /*
    ImageDao Abstract Function
    defining PopulateDbAsync class which extends AsyncTask
     */
    public abstract ImageDao imageDao();
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final ImageDao imageDao;

        /*
        populate database by using imageDao and database Instance
         */
        public PopulateDbAsync(ImageDatabase instance) {
            imageDao = instance.imageDao();

        }

        /*
        Overriding the doInBackground function
         */
        @Override
        protected Void doInBackground(Void... voids) {
           imageDao.deleteAll();

            Uri uri;
            Cursor cursor;
            String latitude="0",longitude="";
            int column_index_data, column_index_folder_name;
            ArrayList<Image> listOfAllImages = new ArrayList<Image>();      //Array list to save the list of all images
            String absolutePathOfImage = null;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; //Using EXTERNAL_CONTENT_URI of android.provider.MediaStore.Images.Media class


            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

            cursor = imageActivityInstance.getContentResolver().query(uri, projection, null,
                    null, null);

            /*
            Storing the metadata of the images in the database
             */
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int i=0;
            SharedPreferences sp = imageActivityInstance.getSharedPreferences("location", Activity.MODE_PRIVATE);
            SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                StringBuilder builder = new StringBuilder();
                try {
                    final ExifInterface exifInterface = new ExifInterface(absolutePathOfImage);


                    builder.append(" " + getExifTag(exifInterface,ExifInterface.TAG_USER_COMMENT) + "");


                    float[] latLong = new float[2];
                    if (exifInterface.getLatLong(latLong)) {
                        // Do stuff with lat / long...
                        latitude=latLong[0]+"";
                        longitude=latLong[1]+"";
                        Log.i("getexif0",latitude+","+longitude+","+getExifTag(exifInterface,ExifInterface.TAG_DATETIME));
                    }
                    else {
                        latitude=sp.getString("latitude","");
                        longitude=sp.getString("longitude","");;
                    }

                /*
                calling Setter methods for setting the values of the following attributes
                 */
                Image image =new Image();
                image.setImage(null);
                image.setTitle(getExifTag(exifInterface,ExifInterface.TAG_DATETIME));
                image.setDescription(builder.toString());
                image.setImagePath(absolutePathOfImage);
                image.setLongitude(String.valueOf(longitude));
                image.setLatitude(String.valueOf(latitude));
                imageDao.insertSingleImageOnDB(image);

                } catch (IOException e) {
                    latitude=sp.getString("latitude","");
                    longitude=sp.getString("longitude","");;
                    e.printStackTrace();
                }


            }

            return null;
        }
    }

    //Getter function for ExifTag
    private static String getExifTag(ExifInterface exif,String tag){
        String attribute = exif.getAttribute(tag);

        return (null != attribute ? attribute : "");
    }

    //Setting Default value to the title of the images
    private static String setTitle(String filepath) {
        int pos = filepath.lastIndexOf("/");
        String title = filepath;

        if (-1 != pos) {
            title = filepath.substring(pos + 1);
        }
        return title;
    }
    /*
    Converting the bitmap of the images to the bytes
     */
    private static byte[] convertBitmaptoByte(Bitmap bitmap) {
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

}
