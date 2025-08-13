package com.gaopeng.test.cnm.xuanfu;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class FloatingBallsService extends Service {

    private WindowManager windowManager;
    private View rootView;
    private View ball1, ball2;

    private FrameLayout container;
    private boolean isVisible = false;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCreate() {
        super.onCreate();
        createFloatingView();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toggleVisibility();
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void toggleVisibility() {
        if (container == null) {
            createFloatingView();
        } else {
            isVisible = !isVisible;
            container.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void createFloatingView() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        container = new FrameLayout(this);
        WindowManager.LayoutParams containerParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, // 允许覆盖系统UI
                PixelFormat.TRANSLUCENT
        );
        containerParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        containerParams.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        windowManager.addView(container, containerParams);

        rootView = new View(this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootView.setBackgroundColor(Color.TRANSPARENT);

        ball1 = createBallView(100, 400, "A", true);  // A键，左上角圆角
        ball2 = createBallView(240, 400, "B", false); // B键，右上角圆角

        container.addView(ball1);
        container.addView(ball2);

        container.setVisibility(View.VISIBLE);
        isVisible = true;
        addCloseButton();
    }

    private View createBallView(int x, int y, String label, boolean isLeft) {
        // 创建TextView显示字母
        TextView button = new TextView(this);

        // 设置布局参数
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpToPx(60), // 宽度
                dpToPx(40)  // 高度
        );
        params.leftMargin = x;
        params.topMargin = y;
        button.setLayoutParams(params);

        // 设置肩键样式 - 带圆角的矩形
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(Color.parseColor("#CC000000")); // 深色半透明背景

        // 设置特定角的圆角
        float cornerRadius = dpToPx(15); // 圆角半径
        float[] radii;

        if (isLeft) {
            // A键：左上角圆角
            radii = new float[]{
                    cornerRadius, cornerRadius, // 左上X,Y
                    0, 0,                     // 右上
                    0, 0,                     // 右下
                    0, 0                      // 左下
            };
        } else {
            // B键：右上角圆角
            radii = new float[]{
                    0, 0,                     // 左上
                    cornerRadius, cornerRadius, // 右上X,Y
                    0, 0,                     // 右下
                    0, 0                      // 左下
            };
        }

        shape.setCornerRadii(radii);
        shape.setStroke(dpToPx(2), Color.parseColor("#FFFFFF")); // 白色边框

        button.setBackground(shape);

        // 设置文字样式
        button.setText(label);
        button.setTextColor(Color.WHITE);
        button.setTextSize(16);
        button.setTypeface(Typeface.DEFAULT_BOLD); // 粗体
        button.setGravity(Gravity.CENTER); // 居中

        // 添加阴影
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(dpToPx(4));
        }

        // 添加点击效果
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.7f); // 按下时变透明
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1.0f); // 恢复不透明
                    // 这里可以添加点击事件处理
                    return true;
            }
            return false;
        });

        // 设置拖动监听
        setupBallDragging(button);
        return button;
    }

    private void addCloseButton() {
        ImageButton closeButton = new ImageButton(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
        );
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.topMargin = dpToPx(20);
        params.rightMargin = dpToPx(20);

        closeButton.setLayoutParams(params);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeButton.setBackgroundResource(android.R.color.transparent);
        closeButton.setOnClickListener(v -> {
            container.setVisibility(View.GONE);
            isVisible = false;
            onDestroy();
        });

        container.addView(closeButton);
    }

    // 优化拖动逻辑
    private void setupBallDragging(View ball) {
        ball.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
                int screenWidth = getScreenWidth();
                int screenHeight = getScreenHeight();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.leftMargin;
                        initialY = params.topMargin;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - initialTouchX;
                        float deltaY = event.getRawY() - initialTouchY;

                        // 计算新位置（考虑状态栏高度）
                        int newX = (int) (initialX + deltaX);
                        int newY = (int) (initialY + deltaY);

                        // 边界检查
                        newX = Math.max(0, Math.min(newX, screenWidth - v.getWidth()));
                        newY = Math.max(0, Math.min(newY, screenHeight - v.getHeight()));

                        params.leftMargin = newX;
                        params.topMargin = newY;
                        v.setLayoutParams(params);
                        return true;

                    case MotionEvent.ACTION_UP:
                        return true;
                }
                return false;
            }
        });
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (rootView != null) windowManager.removeView(rootView);
            if (ball1 != null) windowManager.removeView(ball1);
            if (ball2 != null) windowManager.removeView(ball2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}