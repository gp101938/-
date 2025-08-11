package com.gaopeng.test.sbshujuku;

import android.graphics.Point;

public class KeyRecord {
    private String pkg; // 唯一标识
    private Point singleA; // 单键-A坐标
    private Point singleB; // 单键-B坐标
    private Point doubleADown; // 双键-A down坐标
    private Point doubleAUp;   // 双键-A up坐标
    private Point doubleBDown; // 双键-B down坐标
    private Point doubleBUp;   // 双键-B up坐标

    // 构造函数
    public KeyRecord(String pkg, Point singleA, Point singleB,
                     Point doubleADown, Point doubleAUp,
                     Point doubleBDown, Point doubleBUp) {
        this.pkg = pkg;
        this.singleA = singleA;
        this.singleB = singleB;
        this.doubleADown = doubleADown;
        this.doubleAUp = doubleAUp;
        this.doubleBDown = doubleBDown;
        this.doubleBUp = doubleBUp;
    }

    // Getters
    public String getPkg() { return pkg; }
    public Point getSingleA() { return singleA; }
    public Point getSingleB() { return singleB; }
    public Point getDoubleADown() { return doubleADown; }
    public Point getDoubleAUp() { return doubleAUp; }
    public Point getDoubleBDown() { return doubleBDown; }
    public Point getDoubleBUp() { return doubleBUp; }
}
