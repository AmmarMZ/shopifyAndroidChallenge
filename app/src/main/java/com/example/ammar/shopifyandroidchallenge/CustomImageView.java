package com.example.ammar.shopifyandroidchallenge;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Ammar on 2018-01-05.
 */

public class CustomImageView extends android.support.v7.widget.AppCompatImageView
{

        public CustomImageView(Context context)
        {
            super(context);
        }

        public CustomImageView(Context context, AttributeSet attrs)
        {
            super(context, attrs);
        }

        public CustomImageView(Context context, AttributeSet attrs, int defStyle)
        {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        }

}
