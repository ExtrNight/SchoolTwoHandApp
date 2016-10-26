package com.school.twohand.fragement.taoquan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/** 自定义Fragment
 * Created by yang on 2016/10/19 0019.
 */
public abstract class TaoquanBaseFragment extends Fragment{

    //找控件
    //界面数据初始化
    //设置事件

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initData();
        initEvent();
    }

    public abstract void initView(); //找控件
    public abstract void initData();//设置界面初始值
    public abstract void initEvent();//设置控件的事件

}
