package uk.ac.shef.oak.com6510.imagelocator.interfaces;

import android.view.View;
/*
IDiscription Interface contains these undefined functions which can be implemented by
any other class implementing this Interface
 */
public interface IDiscription {
    void showMessage(String msg);
    void showMessageWithIcon(String msg, boolean isSuccess);
    void onClickListner(View view);
}
