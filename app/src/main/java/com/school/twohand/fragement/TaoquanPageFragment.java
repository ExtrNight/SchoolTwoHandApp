package com.school.twohand.fragement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;


import com.google.gson.Gson;
import com.school.twohand.activity.taoquan.SearchActivity;
import com.school.twohand.entity.User;
import com.school.twohand.fragement.taoquan.TaoquanDiscoveryFragment;
import com.school.twohand.fragement.taoquan.TaoquanMineFragment;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/** 淘圈页面的fragment
 * Created by yang on 2016/9/19 0019.
 */
public class TaoquanPageFragment extends Fragment {

    RelativeLayout ll_top_taoquan;
    ViewPager vp; //控件：ViewPager：“发现”页面和“我的”页面,这里使用了自定义的ViewPager
    RadioGroup rg;  //控件：RadioGroup：“发现”和“我的”按钮
    ImageButton ib;  //控件：ImageButton 搜索按钮
    ImageView iv_underline; //文本下面的下划线
    List<Fragment> fragmentList = new ArrayList<Fragment>();

    TranslateAnimation translateToRight; //向右平移的动画
    TranslateAnimation translateToLeft;  //向左平移的动画
    MyApplication exampleApplication;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.taoquan_page_fragment,null);
        ll_top_taoquan = (RelativeLayout) v.findViewById(R.id.ll_top_taoquan);
        vp = (ViewPager) v.findViewById(R.id.vp);
        rg = (RadioGroup) v.findViewById(R.id.rg);
        ib = (ImageButton) v.findViewById(R.id.ib_search);
        iv_underline = (ImageView) v.findViewById(R.id.underline);
        //“发现”页面的fragment对象和“我的”页面的fragment对象
        TaoquanDiscoveryFragment taoquanDiscoveryFragment = new TaoquanDiscoveryFragment();
        TaoquanMineFragment taoquanMineFragment = new TaoquanMineFragment();
        //将fragment添加到fragmentList里面
        fragmentList.add(taoquanDiscoveryFragment);
        fragmentList.add(taoquanMineFragment);

        vp.setAdapter(new MyFragmentPagerAdapter(getFragmentManager()));//为什么不是getSupportFragmentManager()？？
        initAnimation(); //初始化动画
        //设置viewPager的页面切换事件（改变radioButton按钮选中状态）
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //在滑动过程会一值执行 positionOffset：滚动的百分比  positionOffsetPixels：滚动的像素
            }

            @Override
            public void onPageSelected(int position) {//position：滚动到的页面的位置
                //改变选中的radioButton的选中状态,getChildAt()方法：根据位置找到radioButton的子元素
                RadioButton rb = (RadioButton) rg.getChildAt(position);
                rb.setChecked(true);
                if(position==0){  //滑到左边
                    iv_underline.startAnimation(translateToRight);
                }else{  //滑到右边
                    iv_underline.startAnimation(translateToLeft);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //替换页面的时候会执行3次（1、空闲状态 2、滚动状态、3、滚动完成状态）
            }
        });

        //解决滑动冲突？
        vp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //radioGroup选中项改变的事件
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int currentIndex = 0;  //viewPager显示项的位置
                switch (checkedId){
                    case R.id.rb_discovery:
                        currentIndex=0;
                        break;
                    case R.id.rb_mine:
                        currentIndex=1;
                        break;
                }
                vp.setCurrentItem(currentIndex);//设置viewPager哪一项显示
            }
        });

        ib.setOnClickListener(new View.OnClickListener() {//设置ImageButton按钮点击事件：跳转到搜索页面
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    //初始化动画
    void initAnimation(){
        //4个数字参数的含义：起始的X轴位置，结束的X轴位置，起始的Y轴位置，结束的Y轴位置（都是相对于自身RELATIVE_TO_SELF）
        translateToLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.6f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        translateToLeft.setDuration(300);
        translateToLeft.setFillAfter(true);

        translateToRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.6f,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        translateToRight.setDuration(300);
        translateToRight.setFillAfter(true);
    }

    //继承FragmentPagerAdapter，不是PagerAdapter
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);//父类没有无参构造方法，因此显式调用父类有参构造方法
        }

        //返回position位置的fragment
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        //返回ViewPager中显示Fragment的个数
        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

}


/*
exampleApplication = (MyApplication) getActivity().getApplication();
        //从本地取用户信息
        */
/*
        同样根据Context对象获取SharedPreference对象；
        直接使用SharedPreference的getXXX(key)方法获取数据。
         *//*

        SharedPreferences sp = getActivity().getSharedPreferences("USER",getActivity().MODE_PRIVATE);
        String userString = sp.getString("user",null);
        Log.i("MyApplication", "onCreate: "+userString);
        if (userString!=null){
        Gson gson = new Gson();
        User user = gson.fromJson(userString,User.class);
        exampleApplication.setUser(user);
        if (JMessageClient.getMyInfo()==null) {
        JMessageClient.login(user.getUserAccount(), user.getUserPassword(), new BasicCallback() {
@Override
public void gotResult(int i, String s) {
        if (i == 0) {

        }
        }
        });
        }else{

        }
        }*/
