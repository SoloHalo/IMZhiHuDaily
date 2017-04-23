package com.example.cql.imzhihudaily.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;


/**
 * Created by home on 2016/7/31.
 */
public class RateTransformation extends BitmapTransformation {

    public RateTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {

        int orginWidth = toTransform.getWidth();
        float scaleWidth = (float) outWidth/orginWidth;
        Bitmap bitmap = pool.get(outWidth, outHeight, toTransform.getConfig());
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(outWidth, outHeight, toTransform.getConfig());
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth,scaleWidth);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(toTransform, matrix, paint);
        return bitmap;
    }

    @Override
    public String getId() {
        return "RateTransformation";
    }

}