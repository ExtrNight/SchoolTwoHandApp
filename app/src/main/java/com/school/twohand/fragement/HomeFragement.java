package com.school.twohand.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;


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
    RadioButton recommend;
    RadioButton newest;
    Button  fenlei;
    Button sousuo;
    ImageView iv_location_school;    //定位到学校
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_home,container , false);

        //当是首页的时候可以viewPage切换推荐页面和最新页面
        vp = (ViewPager) view.findViewById(R.id.home_changePage);
        recommend = (RadioButton) view.findViewById(R.id.radio1);
        newest = (RadioButton) view.findViewById(R.id.radio2);
        fenlei = (Button) view.findViewById(R.id.fenlei);
        sousuo = (Button) view.findViewById(R.id.sousuo);
        iv_location_school = (ImageView) view.findViewById(R.id.iv_location_school);
        oneFragment = new OneFragment();
        twoFragment = new TwoFragment();
        //将推荐页面的Fragment和最新页面的Fragment加入FragmentList
        homeFragmentLists.add(oneFragment);
        homeFragmentLists.add(twoFragment);
        //初始化页面的radiobutton选中
        recommend.setChecked(true);

        //进入搜索页面
        sousuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SousuoActivity.class);
                startActivity(intent);
            }
        });

        vp.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(),homeFragmentLists));
        //左右页面切换
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    recommend.setChecked(true);
                }
                if (position == 1){
                    newest.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //按钮点击radio切换页面
        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vp.setCurrentItem(0);
            }
        });

        newest.setOnClickListener(new View.OnClickListener() {
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
        return view;


    }
}
