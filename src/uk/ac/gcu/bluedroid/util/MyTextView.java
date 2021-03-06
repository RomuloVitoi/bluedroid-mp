package uk.ac.gcu.bluedroid.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

public class MyTextView extends Button {

    Context context;
    String ttfName;

    String TAG = getClass().getName();

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            Log.i(TAG, attrs.getAttributeName(i));
            /*
             * Read value of custom attributes
             */

            this.ttfName = attrs.getAttributeValue(
                    "http://schemas.android.com/apk/res/com.lht", "ttf_name");
            //Log.i(TAG, "firstText " + firstText);
            // Log.i(TAG, "lastText "+ lastText);

            init();
        }

    }

    private void init() {
        Typeface font = Typeface.createFromAsset(context.getAssets(), ttfName);
        setTypeface(font);
    }

    @Override
    public void setTypeface(Typeface tf) {

        // TODO Auto-generated method stub
        super.setTypeface(tf);
    }

}
