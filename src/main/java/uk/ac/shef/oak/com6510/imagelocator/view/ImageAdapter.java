

package uk.ac.shef.oak.com6510.imagelocator.view;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.shef.oak.com6510.imagelocator.R;
import uk.ac.shef.oak.com6510.imagelocator.databinding.ItemImageBinding;
import uk.ac.shef.oak.com6510.imagelocator.model.Image;
import uk.ac.shef.oak.com6510.imagelocator.viewmodel.ItemImageViewModel;
/*
ImageAdapter extends RecyclerView.Adapter and implements Filterable to access the special functionalities such as ImageAdapterViewHolder
 */


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageAdapterViewHolder>  implements Filterable {

  private List<Image> imageList;
  private List<Image> mFilteredImageEntries = new ArrayList<>();
  ImageAdapter() {
    this.imageList = Collections.emptyList();
  }

  /*
  Overriding onCreateViewHolder function and binding the image to the view using ItemImageViewModel
   */
  @NonNull
  @Override public ImageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemImageBinding itemImageBinding =
        DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_image,
            parent, false);
    return new ImageAdapterViewHolder(itemImageBinding);
  }

  @Override public void onBindViewHolder(ImageAdapterViewHolder holder, int position) {
    holder.bindImage(imageList.get(position));
  }

  @Override public int getItemCount() {
    return imageList.size(); //calculating the number of images
  }

  void setImageList(List<Image> imageList) {
    this.imageList = imageList;
    notifyDataSetChanged(); //notifies the ViewModel about the change in ImageList
  }

  static class ImageAdapterViewHolder extends RecyclerView.ViewHolder {
    ItemImageBinding mItemImageBinding;

    ImageAdapterViewHolder(ItemImageBinding itemImageBinding) {
      super(itemImageBinding.itemImage);
      this.mItemImageBinding = itemImageBinding;
    }

    //Binds the image to the ItemImageViewModel
    void bindImage(Image image) {
      if (mItemImageBinding.getImageViewModel() == null) {
        mItemImageBinding.setImageViewModel(
            new ItemImageViewModel(image, itemView.getContext()));
      } else {
        mItemImageBinding.getImageViewModel().setImage(image);
      }
    }
  }

  //Get  the filtered images after search functionality
  @Override
  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence charSequence) {
        String charString = charSequence.toString();
        List<Image> filteredList = new ArrayList<>();
        if (charString.isEmpty()) {
          filteredList = imageList;
        } else {
          for (Image row : imageList) {

            if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
              filteredList.add(row);
            }
          }

        }

        FilterResults filterResults = new FilterResults();
        filterResults.values = filteredList;
        return filterResults;
      }

      //Displaying the results
      @Override
      protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        mFilteredImageEntries = (ArrayList<Image>) filterResults.values;
        notifyDataSetChanged();
      }
    };
  }
}
