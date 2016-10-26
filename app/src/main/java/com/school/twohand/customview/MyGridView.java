package com.school.twohand.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/** 自定义GridView,用于解决在多个ScrollView型的控件相互嵌套的时候只显示一行的问题
 * Created by yang on 2016/10/4 0004.
 */
public class MyGridView extends GridView{

    public MyGridView(Context context) {
        this(context,null);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
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
