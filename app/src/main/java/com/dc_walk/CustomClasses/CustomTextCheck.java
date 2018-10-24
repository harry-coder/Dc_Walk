package com.dc_walk.CustomClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by goutams on 26/05/17.
 */

public class CustomTextCheck extends CheckBox {

    public CustomTextCheck(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public CustomTextCheck(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public CustomTextCheck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context){
        Typeface customFont = FontCache.getTypeface(context, "SystemSanFranciscoDisplayThin.ttf");

        setTypeface(customFont);
    }
}
