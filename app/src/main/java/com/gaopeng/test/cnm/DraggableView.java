package com.gaopeng.test.cnm;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class DraggableView extends androidx.appcompat.widget.AppCompatImageView {

    private float dX, dY;
    private int containerWidth, containerHeight;
    private OnPositionChangeListener listener;
    private int id;

    public interface OnPositionChangeListener {
        void onPositionChanged(int id, int x, int y);
    }

    public DraggableView(Context context, int id, int color) {
        super(context);
        this.id = id;
        init(color);
    }

    private void init(int color) {
        // 设置视图外观
        setBackgroundColor(color);
        setAlpha(0.8f);
        setScaleType(ScaleType.CENTER_INSIDE);

        // 设置视图大小
        int size = dpToPx(80);
        setLayoutParams(new FrameLayout.LayoutParams(size, size));

        // 添加图标
        setImageResource(R.drawable.ic_drag_handle);
        setColorFilter(Color.WHITE);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 使用 ShapeDrawable 包装 OvalShape
        ShapeDrawable ovalBackground = new ShapeDrawable(new OvalShape());
        ovalBackground.setIntrinsicWidth(w);
        ovalBackground.setIntrinsicHeight(h);
        ovalBackground.getPaint().setColor(getResources().getColor(R.color.draggable_color));
        setBackground(ovalBackground);
    }


    public void setContainerSize(int width, int height) {
        containerWidth = width;
        containerHeight = height;
    }

    public void setOnPositionChangeListener(OnPositionChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = x - getX();
                dY = y - getY();
                bringToFront();
                animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                break;

            case MotionEvent.ACTION_MOVE:
                float newX = x - dX;
                float newY = y - dY;

                // 边界检查
                if (newX < 0) newX = 0;
                if (newY < 0) newY = 0;
                if (newX > containerWidth - getWidth()) newX = containerWidth - getWidth();
                if (newY > containerHeight - getHeight()) newY = containerHeight - getHeight();

                setX(newX);
                setY(newY);

                if (listener != null) {
                    listener.onPositionChanged(id, (int) newX, (int) newY);
                }
                break;

            case MotionEvent.ACTION_UP:
                animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                break;
        }
        return true;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
