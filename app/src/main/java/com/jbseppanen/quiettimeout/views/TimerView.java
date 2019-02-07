package com.jbseppanen.quiettimeout.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jbseppanen.quiettimeout.R;

public class TimerView extends View {

    private float height, width, level;
    private Paint fillPaint, linePaint;

    public TimerView(Context context) {
        super(context);
        init(null);

    }

    public TimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public TimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void init(AttributeSet attrs) {
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        height = -1;
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TimerView);
            level = typedArray.getResourceId(R.styleable.TimerView_level, 0);
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (height == -1) {
            height = getHeight();
            width = getWidth();
            level = height;
        }

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(10); //TODO figure out why this doesn't work to make fill line wider.
        canvas.drawLine(0, 0, width, 0, fillPaint);
        fillPaint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawRect(0, level, width, height, fillPaint);
    }

    public void updateLevel(float inputLevel) {
        level = inputLevel * height;
        invalidate();
    }

}
