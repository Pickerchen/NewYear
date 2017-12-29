package com.sen5.secure.launcher.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class NoScrollGridView extends GridView {

    public NoScrollGridView(Context context) {
        super(context);
    }

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = getFocusedChild();

       if (childFocuseListener!=null &&view!=null){
           childFocuseListener.onFocuseChild(view);
       }


        super.onScrollChanged(l, t, oldl, oldt);
    }

    



    public interface childFocuse{
        void onFocuseChild(View view);
    }

    public void setChildFocuseListener(childFocuse childFocuseListener) {
        this.childFocuseListener = childFocuseListener;
    }

    private childFocuse childFocuseListener;

}
