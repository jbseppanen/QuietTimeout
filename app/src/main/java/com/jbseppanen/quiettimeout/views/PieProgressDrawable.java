package com.jbseppanen.quiettimeout.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class PieProgressDrawable extends Drawable {

    private Paint mPaint;
    private RectF mBoundsF;
    private RectF mInnerBoundsF;
    private final float START_ANGLE = 0.f;
    private float mDrawTo;

    public PieProgressDrawable() {
        super();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setBorderWidth(float widthDp, DisplayMetrics dm) {
        float borderWidth = widthDp * dm.density;
        mPaint.setStrokeWidth(borderWidth);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        // Rotate the canvas around the center of the pie by 90 degrees
        // counter clockwise so the pie stars at 12 o'clock.
        canvas.rotate(-90f, getBounds().centerX(), getBounds().centerY());
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawOval(mBoundsF, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(mInnerBoundsF, START_ANGLE, mDrawTo, true, mPaint);

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mBoundsF = mInnerBoundsF = new RectF(bounds);
        final int halfBorder = (int) (mPaint.getStrokeWidth()/2f + 0.5f);
        mInnerBoundsF.inset(halfBorder, halfBorder);
    }

    @Override
    protected boolean onLevelChange(int level) {
        final float drawTo = START_ANGLE + ((float)360*level)/100f;
        boolean update = drawTo != mDrawTo;
        mDrawTo = drawTo;
        return update;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return mPaint.getAlpha();
    }
}
