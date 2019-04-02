package uk.ac.shef.oak.com6510.imagelocator.view;

import uk.ac.shef.oak.com6510.imagelocator.databinding.DiscriptionActivityBinding;
import uk.ac.shef.oak.com6510.imagelocator.interfaces.IDiscription;

/*
DescriptionView provides constraints on the meta data to be saved on the database
for title and description
 */
public class DiscriptionView  {
    private IDiscription iDiscription;
    public DiscriptionView(IDiscription iDiscription){
        this.iDiscription=iDiscription;
    }

    public boolean isValidFeilds(DiscriptionActivityBinding binding) {
        if (binding.etTitle.equals(null) || binding.etTitle.getText().toString().trim().equalsIgnoreCase("")) {
            iDiscription.showMessage("Title not be empty");
            return false;
        } else if (binding.etTitle.getText().toString().trim().length()<5) {
            iDiscription.showMessage("Title must have atleast 5 charaters");
            return false;
        }
        else if (binding.etDiscription.equals(null) || binding.etDiscription.getText().toString().trim().equalsIgnoreCase("")) {
            iDiscription.showMessage("Discription not be empty");
            return false;
        } else if (binding.etDiscription.getText().toString().trim().length()<5) {
            iDiscription.showMessage("Discription must have atleast 5 charaters");
            return false;
        }
        return true;
    }
}
