package com.school.twohand.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.school.twohand.entity.User;

import com.school.twohand.fragement.homeChildFragement.CircleFragment;
import com.school.twohand.fragement.homeChildFragement.TopicFragment;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class InforPageActivity extends AppCompatActivity{






    MyApplication myApplication;
    User user;

    @InjectView(R.id.rl_join)
    RelativeLayout rlJoin;
    @InjectView(R.id.rl_end)
    RelativeLayout rlEnd;
    @InjectView(R.id.rl_order)
    RelativeLayout rlOrder;
    @InjectView(R.id.goback)
    ImageView goback;
    @InjectView(R.id.btn_write)
    Button btnWrite;
    RelativeLayout rl_join;
    RelativeLayout rl_end;
    RelativeLayout rl_order;
    int fraindex;
    List<Fragment> fragmentList = new ArrayList<Fragment>();
    List<RelativeLayout> RelativeLayoutList = new ArrayList<RelativeLayout>();
    TopicFragment topicFragment = new TopicFragment();
    CircleFragment circleFragment = new CircleFragment();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_page);
        ButterKnife.inject(this);
        getInfoData();



        rl_join = (RelativeLayout) findViewById(R.id.rl_join);
        rl_end = (RelativeLayout) findViewById(R.id.rl_end);


        RelativeLayoutList.add(rl_join);
        RelativeLayoutList.add(rl_end);



        fragmentList.add(topicFragment);
        fragmentList.add(circleFragment);



        rl_join.setSelected(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, fragmentList.get(0));
        fragmentTransaction.commit();
    }




    int currentindext = 0;

    @OnClick({R.id.rl_join, R.id.rl_end, R.id.rl_order, R.id.goback, R.id.btn_write})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_join:
                currentindext = 0;
                break;
            case R.id.rl_end:
                currentindext = 1;
                break;
            case R.id.rl_order:
                currentindext = 2;
                break;
            case R.id.goback:
                finish();
                break;
            case R.id.btn_write:
                Intent intent=new Intent(InforPageActivity.this,MyInforActivity.class);
                startActivity(intent);

                break;
        }


        if (currentindext != fraindex) {
            RelativeLayoutList.get(fraindex).setSelected(false);//前一个按钮取消选中
            RelativeLayoutList.get(currentindext).setSelected(true);
            toggleFragment(fragmentList.get(fraindex), fragmentList.get(currentindext));
            // textView.setText(title[currentindext]);
        }
        fraindex = currentindext;
    }

    //控制fragment的显式和隐藏：
    public void toggleFragment(Fragment hideFragment, Fragment showFragment) {

        //如果两次显式的是同一个fragment
        if (hideFragment != showFragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.hide(hideFragment);

            if (!showFragment.isAdded()) {

                fragmentTransaction.add(R.id.fragment_container, showFragment);//第一次显式，先add
            }

            fragmentTransaction.show(showFragment);

            fragmentTransaction.commit();
        }

    }



    public void getInfoData() {
        String url = NetUtil.url + "QueryInfoPageServlet";
        Integer userId=  ((MyApplication) this.getApplication()).getUser().getUserId();
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("userId", userId + "");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                User user = gson.fromJson(result, User.class);

                String url = NetUtil.imageUrl + user.getUserHead();
                ImageOptions imageOptions = new ImageOptions.Builder()
                        .setCircular(true)
                        .build();
                ImageView imageVIew2 = (ImageView) findViewById(R.id.iv_headimg);
                x.image().bind(imageVIew2, url, imageOptions);

                TextView tv2 = (TextView) findViewById(R.id.tv_nickname);
                tv2.setText(user.getUserName());

                TextView tv3 = (TextView) findViewById(R.id.tv_sumInfor);

                SimpleDateFormat aa=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d1=new Date(user.getUserBirthday().getTime());
                Date d2=new Date(System.currentTimeMillis());
                long diff=d2.getTime()-d1.getTime();
                long year = diff / (365*1000 * 60 * 60 * 24);
                tv3.setText("现居" + user.getUserAddress()+","+year+"岁"+user.getUserSex()+"生");



                TextView tv4 = (TextView) findViewById(R.id.tv_person);
                tv4.setText(user.getUserPersonalProfile());


            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.getStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }





}

