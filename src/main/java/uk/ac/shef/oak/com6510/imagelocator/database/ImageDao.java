package uk.ac.shef.oak.com6510.imagelocator.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import uk.ac.shef.oak.com6510.imagelocator.model.Image;

/*
These are the queries which are required to build the local database
 */
@Dao
public interface ImageDao {
    @Query("SELECT * FROM Image WHERE title = :title LIMIT 1")
    Image findImageByTitle(String title);

    /*
    Insert Query
    */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Image image);

    /*
    Update Query
    */

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(Image image);

    /*
    Delete Query
    */

    @Query("DELETE FROM Image")
    void deleteAll();


    @Query("SELECT * FROM Image ORDER BY title ASC")
    LiveData<List<Image>> getAllImage();

    @Query("SELECT * FROM Image LIMIT :limit OFFSET :offset")
    LiveData<List<Image>> getAllImage(int limit,int offset);

    @Query("SELECT * FROM Image WHERE title LIKE :searchTitle  OR description LIKE :searchTitle LIMIT 24")
    LiveData<List<Image>> getFilterImages(String searchTitle);

    @Insert
    Long insertSingleImageOnDB (Image image);
}

