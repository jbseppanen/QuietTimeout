package com.jbseppanen.quiettimeout.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jbseppanen.quiettimeout.R;

public class TimerView extends View {

    private int height, width;
    private Paint paint;
    int level;

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
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TimerView);
            level = typedArray.getResourceId(R.styleable.TimerView_level, 0);
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = getHeight();
        width = getWidth();
        paint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawRect(0, level, width, height, paint);
    }

    public void updateLevel(float inputLevel) {
        level = (int) (inputLevel * height);
        invalidate();
    }

}
