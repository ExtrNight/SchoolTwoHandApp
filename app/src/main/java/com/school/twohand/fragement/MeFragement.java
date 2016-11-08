package com.school.twohand.fragement;


import android.content.Context;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
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
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class MeFragement extends Fragment implements MyScrollView.OnScrollListener {

    RelativeLayout release;
    RelativeLayout sell;
    RelativeLayout buy;
    RelativeLayout priase;
    //RelativeLayout auction;
    FrameLayout numberPriase;
    FrameLayout numberAttention;
    FrameLayout numberFans;
    LinearLayout myInfor;
    TextView tv_praise1;  //被赞数
    TextView tv_care1;    //关注数
    TextView tv_fans1;    //粉丝数
    TextView tv_nameTop; //用户名
    ImageView headImg;   //用户头像

    User user;

    private MyScrollView myScrollView;
    private LinearLayout mNameLayout;
    private WindowManager mWindowManager;
    /**
     * 手机屏幕宽度
     */
    private int screenWidth;
    /**
     * 悬浮框View
     */
    private static View suspendView;
    /**
     * 悬浮框的参数
     */
    private static LayoutParams suspendLayoutParams;
    /**
     * 购买布局的高度
     */
    private int nameLayoutHeight;
    /**
     * myScrollView与其父类布局的顶部距离
     */
    private int myScrollViewTop;

    /**
     * 购买布局与其父类布局的顶部距离
     */
    private int nameLayoutTop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initView(view);
        release = (RelativeLayout) view.findViewById(R.id.rl_release);
        sell = (RelativeLayout) view.findViewById(R.id.rl_sell);
        buy = (RelativeLayout) view.findViewById(R.id.rl_buy);
        priase = (RelativeLayout) view.findViewById(R.id.rl_praise);
        //auction = (RelativeLayout) view.findViewById(R.id.rl_auction);
        numberPriase = (FrameLayout) view.findViewById(R.id.fl_sum1);
        numberAttention = (FrameLayout) view.findViewById(R.id.fl_sum2);
        numberFans = (FrameLayout) view.findViewById(R.id.fl_sum3);
        myInfor = (LinearLayout) view.findViewById(R.id.li_nameTop);
        tv_praise1 = (TextView) view.findViewById(R.id.tv_praise1);
        tv_care1 = (TextView) view.findViewById(R.id.tv_care1);
        tv_fans1 = (TextView) view.findViewById(R.id.tv_fans1);
        tv_nameTop = (TextView) view.findViewById(R.id.tv_nameTop);
        headImg = (ImageView) view.findViewById(R.id.headImg);

        init();          //初始化，获取user对象
        initUserData();  //初始化用户数据，展示用户名、头像、点赞关注量等数据
        initEvent();     //初始化事件

        return view;
    }


    public void initView(View view){

        myScrollView = (MyScrollView) view.findViewById(R.id.scrollView);
        mNameLayout = (LinearLayout) view.findViewById(R.id.name);

        myScrollView.setOnScrollListener(this);
        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();

        ViewTreeObserver vto = mNameLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                nameLayoutHeight = mNameLayout.getHeight();
                nameLayoutTop = mNameLayout.getTop();
                mNameLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        ViewTreeObserver vto1 = myScrollView.getViewTreeObserver();
        vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                myScrollViewTop = myScrollView.getTop();
                myScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

    }

    private void init(){
        user = ((MyApplication)getActivity().getApplication()).getUser();
    }

    private void initUserData(){
        if(user==null){
            return;
        }
        tv_nameTop.setText(user.getUserName());
        String userImageUrl = NetUtil.imageUrl+user.getUserHead();
        ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(headImg,userImageUrl,imageOptions);

        initPraiseAndConcernData();
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

    private void initEvent(){
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

//        auction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent4 = new Intent(getActivity(), MyAuctionActivity.class);
//                startActivity(intent4);
//            }
//        });

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

    /**
     * 窗口有焦点的时候，即所有的布局绘制完毕的时候，我们来获取购买布局的高度和myScrollView距离父类布局的顶部位置
     */


    /**
     * 滚动的回调方法，当滚动的Y距离大于或者等于 购买布局距离父类布局顶部的位置，就显示购买的悬浮框
     * 当滚动的Y的距离小于 购买布局距离父类布局顶部的位置加上购买布局的高度就移除购买的悬浮框
     */
    @Override
    public void onScroll(int scrollY) {
        if (scrollY >= nameLayoutTop) {
            if (suspendView == null) {
                showSuspend();
            }
        } else if (scrollY <= nameLayoutTop + nameLayoutHeight) {
            if (suspendView != null) {
                removeSuspend();
            }
        }
    }

    /**
     * 显示购买的悬浮框
     */
    private void showSuspend() {
        if (suspendView == null) {
            suspendView = LayoutInflater.from(getActivity()).inflate(R.layout.name_layout, null);
            if (suspendLayoutParams == null) {
                suspendLayoutParams = new LayoutParams();
                suspendLayoutParams.type = LayoutParams.TYPE_PHONE;
                suspendLayoutParams.format = PixelFormat.RGBA_8888;
                suspendLayoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE;
                suspendLayoutParams.gravity = Gravity.TOP;
                suspendLayoutParams.width = screenWidth;
                suspendLayoutParams.height = nameLayoutHeight;
                suspendLayoutParams.x = 0;
                suspendLayoutParams.y = myScrollViewTop;
            }
        }

        mWindowManager.addView(suspendView, suspendLayoutParams);
    }


    /**
     * 移除购买的悬浮框
     */
    private void removeSuspend() {
        if (suspendView != null) {
            mWindowManager.removeView(suspendView);
            suspendView = null;
        }
    }



}