package com.school.twohand.fragement;


import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.school.twohand.schooltwohandapp.R;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class MeFragement extends Fragment implements MyScrollView.OnScrollListener {

    RelativeLayout release;
    RelativeLayout sell;
    RelativeLayout buy;
    RelativeLayout priase;
    RelativeLayout auction;
    FrameLayout numberPriase;
    FrameLayout numberAttention;
    FrameLayout numberFans;
    LinearLayout myInfor;

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
        auction = (RelativeLayout) view.findViewById(R.id.rl_auction);
        numberPriase = (FrameLayout) view.findViewById(R.id.fl_sum1);
        numberAttention = (FrameLayout) view.findViewById(R.id.fl_sum2);
        numberFans = (FrameLayout) view.findViewById(R.id.fl_sum3);
        myInfor = (LinearLayout) view.findViewById(R.id.li_nameTop);

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

        auction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getActivity(), MyAuctionActivity.class);
                startActivity(intent4);
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