package uk.ac.shef.oak.com6510.imagelocator.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import uk.ac.shef.oak.com6510.imagelocator.utilities.Constants;


    /*
    Created this class for DataBinding for the activities
    It extends the AppCompatActivity class
     */

public abstract class BaseActivity  <DB extends ViewDataBinding> extends AppCompatActivity {
    private DB binding;
    private  String[] PERMISSION  ;
    protected void setView(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        binding = DataBindingUtil.setContentView(this, getLayoutID());
        binding.setLifecycleOwner(this);
    }

    /*
    Requesting access to storage
     */
    public void onRequestPermission(String [] permissionRequest, final int requestCode) {
        this.PERMISSION=permissionRequest;
        ActivityCompat.requestPermissions(this,permissionRequest,requestCode);
    }

    /*
    DataBinding for the activity
     */
    protected abstract int getLayoutID();
    public DB getBinding() {
        return binding;
    }

    /*
    Overriding the onRequestPermissionsResult function for storage request
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.permissionRequest:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,PERMISSION,Constants.permissionRequest);
                }
                return;
            default: break;
        }
    }

    /*
    Overriding the onActivityResult to get the result from the activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

