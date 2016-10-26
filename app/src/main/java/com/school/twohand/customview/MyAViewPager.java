package com.school.twohand.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/** 自定义的ViewPager,在这里可以设置是否滑动
 * Created by yang on 2016/10/19 0019.
 */
public class MyAViewPager extends ViewPager {
    private boolean noScroll = false;//true代表整个ViewPager页面不能滑动，false代表能滑动

    public MyAViewPager(Context context) {
        super(context);
    }

    public MyAViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setNoScroll(boolean noScroll) {  //修改noScroll的值
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(noScroll) {
            return false;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        //    参数设置false表示：
        //调用该方法设置ViewPager显示项，在切换的时候，将不需要切换时间，即不会有滑动效果
        super.setCurrentItem(item,false);
    }

}
