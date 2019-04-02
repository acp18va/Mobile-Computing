package uk.ac.shef.oak.com6510.imagelocator.utilities;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import uk.ac.shef.oak.com6510.imagelocator.R;
/*
It contains design and msgs for toastSuccess, toastfailure, toast
 */

public class ToastUtilities {
    public static void toastSuccess(Context context,String msg){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast,null);

        ImageView image = layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_success);
        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);
        image.setVisibility(View.VISIBLE);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    public static void toastfailure(Context context,String msg){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast,null);

        ImageView image = layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_error);
        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);
        image.setVisibility(View.VISIBLE);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    public static void toast(Context context,String msg){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast,null);
        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
