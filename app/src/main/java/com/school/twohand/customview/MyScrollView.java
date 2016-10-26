package com.school.twohand.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/** 自定义ScrollView
 * 能够兼容ViewPager的ScrollView
 * 解决了ViewPager在ScrollView中的滑动反弹问题
 * Created by yang on 2016/10/15 0015.
 */
public class MyScrollView extends ScrollView{

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    public MyScrollView(Context context) {
        this(context,null);
    }
    public MyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if(xDistance > yDistance){
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }


}
