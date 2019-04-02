package uk.ac.shef.oak.com6510.imagelocator.utilities;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import uk.ac.shef.oak.com6510.imagelocator.R;

/*
When an image is clicked and saved
this dialog will display the id for the image
 */
public class CustomDialog extends Dialog {
    private  Dialog dialog=this;
    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); //Drawing the dialog
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_dialog,null);
        dialog.setContentView(layout);
    }
    public void showDialog() {
        dialog.show(); //Making it visible
    }
    public void dismissDialog() {
        try {

            if (dialog.isShowing())  //It disappears within milliseconds
                dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
