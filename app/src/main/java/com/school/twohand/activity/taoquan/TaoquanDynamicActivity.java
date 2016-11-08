package com.school.twohand.activity.taoquan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.school.twohand.activity.login.LoginActivity;
import com.school.twohand.fragement.taoquan.TaoquanBaseFragment;
import com.school.twohand.fragement.taoquan.TaoquanDynamicAllFragment;
import com.school.twohand.fragement.taoquan.TaoquanDynamicForwardFragment;
import com.school.twohand.fragement.taoquan.TaoquanDynamicMyFragment;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 淘圈动态页面
 */
public class TaoquanDynamicActivity extends AppCompatActivity {

    @InjectView(R.id.finish)
    ImageView finish;
    @InjectView(R.id.tv_publish_dynamic)
    TextView tvPublishDynamic;
    @InjectView(R.id.tv_taoquan_dynamic_all)
    TextView tvTaoquanDynamicAll;
    @InjectView(R.id.tv_taoquan_dynamic_my)
    TextView tvTaoquanDynamicMy;
    @InjectView(R.id.tv_taoquan_dynamic_forward)
    TextView tvTaoquanDynamicForward;
    @InjectView(R.id.vp_taoquan_dynamic)
    ViewPager vpTaoquanDynamic;

    MyApplication myApplication;

    List<TaoquanBaseFragment> fragmentList=new ArrayList<>();
    TaoquanDynamicAllFragment taoquanDynamicAllFragment;
    TaoquanDynamicMyFragment taoquanDynamicMyFragment;
    TaoquanDynamicForwardFragment taoquanDynamicForwardFragment;
    //上面“所有动态，我的动态，我转发的”3个按钮的数组
    TextView[] tvs;
    int oldIndex = 0; //记录上一个ViewPager的页面的位置,用来把上一个位置的按钮颜色变为灰色

    private int circleId;
    private String circleName;
    public static final int RequestCode = 10;
    private static final int LoginRequestCode = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taoquan_dynamic);
        ButterKnife.inject(this);

        init();
        initView();
    }

    private void init(){
        Intent intent = getIntent();
        if(intent!=null){
            circleId = intent.getIntExtra("circleId",0);
            circleName = intent.getStringExtra("circleName");
        }

        myApplication = (MyApplication) getApplication();

    }

    //初始化界面,设置初始显示的fragment
    void initView(){
        tvs = new TextView[]{tvTaoquanDynamicAll, tvTaoquanDynamicMy, tvTaoquanDynamicForward};
        tvs[0].setTextColor(Color.RED); //初始的第一个页面的按钮设为红色

        Bundle bundle = new Bundle(); //Activity给Fragment传值
        bundle.putInt("circleId",circleId);
        bundle.putString("circleName",circleName);

        taoquanDynamicAllFragment = new TaoquanDynamicAllFragment();
        taoquanDynamicAllFragment.setArguments(bundle);
        fragmentList.add(taoquanDynamicAllFragment);

        taoquanDynamicMyFragment = new TaoquanDynamicMyFragment();
        taoquanDynamicMyFragment.setArguments(bundle);
        fragmentList.add(taoquanDynamicMyFragment);

        taoquanDynamicForwardFragment = new TaoquanDynamicForwardFragment();
        taoquanDynamicForwardFragment.setArguments(bundle);
        fragmentList.add(taoquanDynamicForwardFragment);

        //设置viewpager显示内容
        vpTaoquanDynamic.setOffscreenPageLimit(2); //设置预加载个数，设为0无效
        vpTaoquanDynamic.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        //设置页面改变监听事件
        vpTaoquanDynamic.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                oldIndex=position; //滑动完后使oldIndex变为当前位置
                if(position!=0){  //设置相邻的左边按钮颜色为灰色
                    tvs[position-1].setTextColor(Color.GRAY);
                }
                if(position!=2){ //设置相邻的右边按钮颜色为灰色
                    tvs[position+1].setTextColor(Color.GRAY);
                }
                tvs[position].setTextColor(Color.RED);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick({R.id.finish, R.id.tv_publish_dynamic, R.id.tv_taoquan_dynamic_all, R.id.tv_taoquan_dynamic_my, R.id.tv_taoquan_dynamic_forward})
    public void onClick(View view) {
        if(view.getId()==R.id.finish){
            finish();  //返回主页面
            return;
        }
        if(view.getId()==R.id.tv_publish_dynamic){ //发布动态
            if(myApplication.getUser()==null){
                //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                Intent intent = new Intent(TaoquanDynamicActivity.this, LoginActivity.class);
                startActivityForResult(intent,LoginRequestCode);
            }else{
                Intent intent = new Intent(this,CreateTaoquanDynamicActivity.class);
                intent.putExtra("circleId",circleId);
                startActivityForResult(intent,RequestCode);
            }
            return;
        }
        int currentIndex = 0;  //viewPager显示项的位置
        switch (view.getId()) {
            case R.id.tv_taoquan_dynamic_all:
                currentIndex = 0;
                break;
            case R.id.tv_taoquan_dynamic_my:
                currentIndex = 1;
                break;
            case R.id.tv_taoquan_dynamic_forward:
                currentIndex = 2;
                break;
        }
        tvs[oldIndex].setTextColor(Color.GRAY);//将上一个索引位置的按钮颜色变为灰色
        oldIndex = currentIndex;     //将当前索引数设为oldIndex
        vpTaoquanDynamic.setCurrentItem(currentIndex);
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        taoquanDynamicAllFragment.getData();//全部动态的Fragment重新加载
//        taoquanDynamicMyFragment.getData(); //我的动态的Fragment重新加载
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RequestCode&&resultCode==CreateTaoquanDynamicActivity.ResultCode){
            //是从创建淘圈页面返回的
            taoquanDynamicAllFragment.setPageNo(1); //设置页数为第一页
            taoquanDynamicAllFragment.mLv_dynamic_all.setSelectionAfterHeaderView();//设置第一项展示出来
            taoquanDynamicAllFragment.getData();//全部动态的Fragment重新加载
            taoquanDynamicMyFragment.setPageNo(1);
            taoquanDynamicMyFragment.mLv_dynamic_all.setSelectionAfterHeaderView();
            taoquanDynamicMyFragment.getData(); //我的动态的Fragment重新加载
            taoquanDynamicForwardFragment.setPageNo(1);//设置页数为第一页
            taoquanDynamicForwardFragment.mLv_dynamic_all.setSelectionAfterHeaderView();
            taoquanDynamicForwardFragment.getData();//我评论的Fragment重新加载
        }else if(requestCode==TaoquanDynamicForwardFragment.RequestCode&&resultCode==LoginActivity.ResultCode){
            //"我评论的"和"我发布的"fragment登录成功的回调
            taoquanDynamicForwardFragment.initData();
            taoquanDynamicMyFragment.initData();
        }
    }



}
