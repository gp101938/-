package com.gaopeng.test.cnm;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

public class DragContainerView extends FrameLayout {

    private DraggableView view1, view2;
    private DraggableView.OnPositionChangeListener positionListener;

    public DragContainerView(Context context) {
        super(context);
        init();
    }

    public DragContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragContainerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 创建两个可拖动视图
        view1 = new DraggableView(getContext(), 1, ContextCompat.getColor(getContext(), R.color.draggable_color1));
        view2 = new DraggableView(getContext(), 2, ContextCompat.getColor(getContext(), R.color.draggable_color2));

        // 添加到容器
        addView(view1);
        addView(view2);

        // 设置位置监听器
        positionListener = (id, x, y) -> {
            if (onPositionChangeListener != null) {
                onPositionChangeListener.onPositionChanged(id, x, y);
            }
        };
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            int width = getWidth() - getPaddingLeft() - getPaddingRight();
            int height = getHeight() - getPaddingTop() - getPaddingBottom();

            // 设置容器尺寸
            view1.setContainerSize(width, height);
            view2.setContainerSize(width, height);

            // 设置初始位置
            view1.setX(width * 0.2f);
            view1.setY(height * 0.3f);
            view2.setX(width * 0.6f);
            view2.setY(height * 0.5f);

            // 设置监听器
            view1.setOnPositionChangeListener(positionListener);
            view2.setOnPositionChangeListener(positionListener);
        }
    }

    public void setViewPositions(int id, int x, int y) {
        Log.d("shabi","(x,y)-    "+x+","+y + "  view1" + view1 + "," + view2);
        if (id == 1) {
            view1.setX(x);
            view1.setY(y);
        } else if (id == 2) {
            view2.setX(x);
            view2.setY(y);
        }
    }

    public Point getViewPosition(int id) {
        if (id == 1) {
            return new Point((int) view1.getX(), (int) view1.getY());
        } else if (id == 2) {
            return new Point((int) view2.getX(), (int) view2.getY());
        }
        return new Point(0, 0);
    }

    public void resetPositions() {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();

        view1.animate().x(width * 0.2f).y(height * 0.3f).setDuration(300).start();
        view2.animate().x(width * 0.6f).y(height * 0.5f).setDuration(300).start();
    }

    private OnPositionChangeListener onPositionChangeListener;

    public interface OnPositionChangeListener {
        void onPositionChanged(int id, int x, int y);
    }

    public void setOnPositionChangeListener(OnPositionChangeListener listener) {
        this.onPositionChangeListener = listener;
    }
}