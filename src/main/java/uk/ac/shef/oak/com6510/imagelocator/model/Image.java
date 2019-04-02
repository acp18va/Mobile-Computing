

package uk.ac.shef.oak.com6510.imagelocator.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
The attributes of Image are defined here.
The corresponding Getter/ Setter methods are implemented
 */
@Entity(tableName = "Image",

        indices = {@Index("title"), @Index("description")})
public class Image implements Serializable {

  @SerializedName("email") public String mail;

  @SerializedName("picture") public String picture;

  public String fullName;


  public boolean hasEmail() {
    return mail != null && !mail.isEmpty();
  }

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "pid")
  private int id;

  @ColumnInfo(name = "title")
  @NonNull
  private String title;

  @ColumnInfo(name = "description")
  private String description;

  @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
  public byte[] image;

  @ColumnInfo(name = "latitude")
  private String latitude;

  @ColumnInfo(name = "longitude")
  private String longitude;

  @ColumnInfo(name = "imagePath")
  private String imagePath;

  @ColumnInfo(name = "location")
  public String location;

  /*
  Getter/Setter methods for each attribute
   */

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @NonNull
  public String getTitle() {
    return title;
  }

  public void setTitle(@NonNull String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }
  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getImagePath() {
    return picture;
  }

  public void setImagePath(String imagePath) {
    this.picture = imagePath;
  }

  /*
  Image constructor is defined here
   */
  public Image(){}
  public Image(@NonNull String title, String description) {
    this.description = description;
    this.picture = description;
    this.mail = description;
    this.title = title;
  }
  @Ignore
  public Image(String title,String description,String location){
    this.mail=title;

    this.picture=description;
    this.title=title;

  }
}
