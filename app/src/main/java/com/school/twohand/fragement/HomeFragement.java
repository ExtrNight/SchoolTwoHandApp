package com.school.twohand.fragement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;


import com.school.twohand.activity.GoodsClassActivity;
import com.school.twohand.activity.GoodsClassHActivity;
import com.school.twohand.activity.NearBySchoolActivity;
import com.school.twohand.activity.SousuoActivity;
import com.school.twohand.fragement.homeChildFragement.TwoFragment;
import com.school.twohand.fragement.homeChildFragement.OneFragment;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class HomeFragement extends Fragment {
    List<Fragment> homeFragmentLists = new ArrayList<>();
    ViewPager vp ;
    TwoFragment twoFragment;
    OneFragment oneFragment;

    Button  fenlei;
    Button sousuo;
    ImageView iv_location_school;    //定位到学校

    /*
        首页标题滑动的动画效果所需要的变量
    */
    TextView firstTile;//第一页标题
    TextView secondTitle;//第二页标题
    int currentIndex = 0;//当前页
    ImageView line;//字下面的线
    private int screenWidth;//屏幕的宽度
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_home,container , false);

        //当是首页的时候可以viewPage切换推荐页面和最新页面
        vp = (ViewPager) view.findViewById(R.id.home_changePage);
        firstTile = (TextView) view.findViewById(R.id.firstTitle);//第一页
        secondTitle = (TextView) view.findViewById(R.id.secondTitle);//第二页
        line = (ImageView) view.findViewById(R.id.line);//首页切换时的下划线

        fenlei = (Button) view.findViewById(R.id.fenlei);
        sousuo = (Button) view.findViewById(R.id.sousuo);
        iv_location_school = (ImageView) view.findViewById(R.id.iv_location_school);

        oneFragment = new OneFragment();
        twoFragment = new TwoFragment();
        //初始化下划线长为屏幕的二分之一
        initTabLineWidth();
        //将推荐页面的Fragment和最新页面的Fragment加入FragmentList
        homeFragmentLists.add(oneFragment);
        homeFragmentLists.add(twoFragment);

        //初始化界面中的点击事件
        init();
        initEvent();
        return view;

    }

    /**
     * 初始化界面
     */
    public void init(){
        vp.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(),homeFragmentLists));
        //左右页面切换
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /*
                滑动首页，动态改变标题下划线位置的核心代码
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) line
                        .getLayoutParams();
                float width = (float)screenWidth / 2;   //屏幕一半的长度
                float headWidth = (float)screenWidth / 8;//初始下划线距离左边边距
                float offsets = positionOffset * width;//滑动的比率*屏幕长度
                //动态计算得出距离左边的长度
                lp.leftMargin = (int) (headWidth+(currentIndex * width + (position == currentIndex ? offsets: - (width - offsets))));
                line.setLayoutParams(lp);
            }
            /*
                滑动首页，动态改变标题颜色代码
             */
            @Override
            public void onPageSelected(int position) {
                resetTextView();
                if (position == 0){
                    firstTile.setTextColor(getActivity().getResources().getColor(R.color.text_color_after));
                }
                if (position == 1){
                    secondTitle.setTextColor(getActivity().getResources().getColor(R.color.text_color_after));
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 界面的点击事件
     */
    public void initEvent(){

        //点击标题改变到对应页面
        firstTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vp.setCurrentItem(0);
            }
        });
        secondTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(1);
            }
        });

        //跳转到分类查询界面
        fenlei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoodsClassHActivity.class);
                startActivity(intent);

            }
        });

        //点击跳转到定位附近学校的页面
        iv_location_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NearBySchoolActivity.class);
                startActivity(intent);
            }
        });

        //进入搜索页面
        sousuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SousuoActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * 重置首页标题颜色，标题改变颜色前初始化的代码
     */
    private void resetTextView() {
        firstTile.setTextColor(this.getResources().getColor(R.color.text_color_before));
        secondTitle.setTextColor(this.getResources().getColor(R.color.text_color_before));

    }

    /**
     * 设置滑动条的宽度为屏幕的1/4(根据Tab的个数而定)
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) line
                .getLayoutParams();
        lp.width = screenWidth / 4;
        line.setLayoutParams(lp);
    }
}
