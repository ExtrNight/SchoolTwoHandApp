package com.school.twohand.fragement;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.MyAuctionActivity;
import com.school.twohand.activity.MyBuyActivity;
import com.school.twohand.activity.MyInforActivity;
import com.school.twohand.activity.MyPriaseActivity;
import com.school.twohand.activity.MyReleaseActivity;
import com.school.twohand.activity.MyScrollView;
import com.school.twohand.activity.MySellActivity;
import com.school.twohand.activity.NumberAttentionActivity;
import com.school.twohand.activity.NumberFansActivity;
import com.school.twohand.activity.NumberPriaseActivity;
import com.school.twohand.activity.login.LoginActivity;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Type;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class MeFragement extends Fragment  {

    RelativeLayout release;
    RelativeLayout sell;
    RelativeLayout buy;
    RelativeLayout priase;
    FrameLayout numberPriase;
    FrameLayout numberAttention;
    FrameLayout numberFans;
    LinearLayout myInfor;
    Button login;
    TextView exit;

    TextView tv_praise1;  //被赞数
    TextView tv_care1;    //关注数
    TextView tv_fans1;    //粉丝数
    TextView tv_nameTop; //用户名
    ImageView headImg;   //用户头像

    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        release = (RelativeLayout) view.findViewById(R.id.rl_release);
        sell = (RelativeLayout) view.findViewById(R.id.rl_sell);
        buy = (RelativeLayout) view.findViewById(R.id.rl_buy);
        priase = (RelativeLayout) view.findViewById(R.id.rl_praise);
        numberPriase = (FrameLayout) view.findViewById(R.id.fl_sum1);
        numberAttention = (FrameLayout) view.findViewById(R.id.fl_sum2);
        numberFans = (FrameLayout) view.findViewById(R.id.fl_sum3);
        myInfor = (LinearLayout) view.findViewById(R.id.li_nameTop);
        tv_praise1 = (TextView) view.findViewById(R.id.tv_praise1);
        tv_care1 = (TextView) view.findViewById(R.id.tv_care1);
        tv_fans1 = (TextView) view.findViewById(R.id.tv_fans1);
        tv_nameTop = (TextView) view.findViewById(R.id.tv_nameTop);
        headImg = (ImageView) view.findViewById(R.id.headImg);

        /*//登录按钮点击
        login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivityForResult(intent,0);
            }
        });*/

        //退出按钮点击
        exit = (TextView)view.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空本地存储的User对象
                SharedPreferences sp = getActivity().getSharedPreferences("USER",getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                MyApplication myApplication = (MyApplication) getActivity().getApplication();
                myApplication.setUser(null);
                JMessageClient.logout();
                Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
            }
        });

        init();          //初始化，获取user对象
        initUserData();  //初始化用户数据，展示用户名、头像、点赞关注量等数据
        initEvent();     //初始化事件



        return view;
    }

    private void init(){
        user = ((MyApplication)getActivity().getApplication()).getUser();
    }

    private void initUserData(){
        if(user==null){
            return;
        }
        if(user.getUserName()!=null){
            tv_nameTop.setText(user.getUserName());
        }
        //最新的user
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryInfoServlet");
        requestParams.addQueryStringParameter("userId",user.getUserId()+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {



                Gson gson=new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                User user = gson.fromJson(result,User.class);
                String userImageUrl = NetUtil.imageUrl+user.getUserHead();
                ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true).build();
                x.image().bind(headImg,userImageUrl,imageOptions);


                initPraiseAndConcernData();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    //初始化“被赞数”，“关注数”，“粉丝数”等数据
    private void initPraiseAndConcernData(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryPraiseAndConcernNumServlet");
        requestParams.addQueryStringParameter("userId",user.getUserId()+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("MeFragement", "onSuccess: "+result);
                Gson gson = new Gson();
                Type type = new TypeToken<int[]>(){}.getType();
                int[] numbers = gson.fromJson(result,type);
                tv_praise1.setText(numbers[0]+"");
                tv_care1.setText(numbers[1]+"");
                tv_fans1.setText(numbers[2]+"");
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("MeFragement", "onError: "+ex);
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }

    void initEvent(){
        release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyReleaseActivity.class);
                startActivity(intent);
            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), MySellActivity.class);
                startActivity(intent1);
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), MyBuyActivity.class);
                startActivity(intent2);
            }
        });

        priase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getActivity(), MyPriaseActivity.class);
                startActivity(intent3);
            }
        });

        numberPriase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(getActivity(), NumberPriaseActivity.class);
                startActivity(intent5);
            }
        });

        numberAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 = new Intent(getActivity(), NumberAttentionActivity.class);
                startActivity(intent6);
            }
        });

        numberFans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent7 = new Intent(getActivity(), NumberFansActivity.class);
                startActivity(intent7);
            }
        });

        myInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent8 = new Intent(getActivity(), MyInforActivity.class);
                startActivity(intent8);
            }
        });
    }



}