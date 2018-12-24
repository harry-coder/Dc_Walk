package com.dc_walk.CustomClasses;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Window;

import java.util.Objects;

public class DialogBox {

    Context context;
    public DialogBox(Context context){

        this.context=context;

    }

    Dialog dialog;
    public Dialog setRequestedDialog(boolean setCancelable, int id) {

        dialog = new Dialog (context);
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(setCancelable);
        dialog.setCancelable(true);

        dialog.setContentView(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable ( Color.TRANSPARENT));
        }
        return dialog;


    }

}
