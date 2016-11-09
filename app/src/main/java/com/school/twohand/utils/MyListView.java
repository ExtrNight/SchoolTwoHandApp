package com.school.twohand.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/** 自定义ListView，解决在ScrollView嵌套ListView时只显示一行的问题
 * Created by yang on 2016/10/6 0006.
 */
public class MyListView extends ListView{

    public MyListView(Context context) {
        this(context,null);
    }

    public MyListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //重写onMeasure方法
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec,expandSpec);
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }



}
