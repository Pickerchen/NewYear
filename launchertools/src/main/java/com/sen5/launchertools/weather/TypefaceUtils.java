/**
 *TypefaceUtils.java[V 1.0.0]
 *classes : com.sen5.wifidvbt.page.TypefaceUtils
 * Xlee Create at 19 Nov 2015 10:28:19
 */
package com.sen5.launchertools.weather;

import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

/**
 * com.sen5.wifidvbt.page.TypefaceUtils
 * 
 * @author Xlee <br/>
 *         create at 19 Nov 2015 10:28:19
 */
public enum TypefaceUtils {
    ROBOTO_LIGHT(Path.PATH_TYPEFACE_ROBOTO_LIGHT);
    private static final String TAG = TypefaceUtils.class.getSimpleName();

    private interface Path {
        String PATH_TYPEFACE_ROBOTO_LIGHT = "fonts/Roboto-Light.ttf";
    }

    private String mTypfacePath;
    private Typeface mTypeface;

    private TypefaceUtils(String path) {
        mTypfacePath = path;
    }

    public void setTypeface(TextView view) {
        if (null == view) {
            throw new IllegalArgumentException("view is null");
        }
        if (null == mTypeface) {
            mTypeface = Typeface.createFromAsset(view.getContext().getApplicationContext().getAssets(), mTypfacePath);
        }
        if (null != mTypeface) {
            view.setTypeface(mTypeface);
        } else {
            Log.w(TAG, "null ==  typeface");
        }
    }
}
