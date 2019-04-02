package uk.ac.shef.oak.com6510.imagelocator.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import uk.ac.shef.oak.com6510.imagelocator.R;
import uk.ac.shef.oak.com6510.imagelocator.databinding.DiscriptionActivityBinding;
import uk.ac.shef.oak.com6510.imagelocator.interfaces.IDiscription;
import uk.ac.shef.oak.com6510.imagelocator.utilities.Constants;
import uk.ac.shef.oak.com6510.imagelocator.utilities.ToastUtilities;
import uk.ac.shef.oak.com6510.imagelocator.viewmodel.ImageDescriptionViewModel;

/*
It extends BaseActivity and implements IDiscription and its methods
it binds the data to ImageDescriptionViewModel
uses an instance of the descriptionView class
 */
public class DiscriptionActivity extends BaseActivity<DiscriptionActivityBinding> implements IDiscription {
    private ImageDescriptionViewModel imageDescriptionViewModel;
    private Context mContext;
    private IDiscription iDiscription=this;
    private DiscriptionView discriptionView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        imageDescriptionViewModel = ViewModelProviders.of(this).get(ImageDescriptionViewModel.class);
        setView();
        getBinding().setImageDescriptionViewModel(imageDescriptionViewModel); //Binding the data
        discriptionView=new DiscriptionView(iDiscription);
        getBinding().btSave.setOnClickListener(v -> iDiscription.onClickListner(v)); //calling onClickListener for save button
        imageDescriptionViewModel.imageUrl.setValue(getIntent().getStringExtra(Constants.IMAGE_STRING));
    }

    @Override
    protected int getLayoutID() {
        return R.layout.discription_activity;
    }

    /*
    Displaying the toast msg using toastUtilities
     */
    @Override
    public void showMessage(String msg) {
        ToastUtilities.toast(mContext,msg);
    }
    /*
    Displaying the toastSuccess/toastFailure msg using toastUtilities
     */
    @Override
    public void showMessageWithIcon(String msg, boolean isSuccess) {
        if (isSuccess)ToastUtilities.toastSuccess(mContext,msg);
        else ToastUtilities.toastfailure(mContext,msg);
    }

    /*
    Defining the onClickListener function of the IDescription Interface
     */
    @Override
    public void onClickListner(View view) {
        switch (view.getId()) {
            case R.id.btSave:
                if (discriptionView.isValidFeilds(getBinding())) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.TITLE,getBinding().etTitle.getText().toString()); //Saving the title
                    returnIntent.putExtra(Constants.DISCRIPTION,getBinding().etDiscription.getText().toString()); //Saving the description
                    setResult(RESULT_OK,returnIntent);
                    finish();
                }
                break;
            default: iDiscription.showMessage(getString(R.string.nothing_click));
        }
    }
}
