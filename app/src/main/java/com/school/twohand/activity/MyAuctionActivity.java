package com.school.twohand.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.school.twohand.fragement.homeChildFragement.EndFragment;
import com.school.twohand.fragement.homeChildFragement.JoinFragment;
import com.school.twohand.fragement.homeChildFragement.OrderFragment;
import com.school.twohand.schooltwohandapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MyAuctionActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.rl_join)
    RelativeLayout rlJoin;
    @InjectView(R.id.rl_end)
    RelativeLayout rlEnd;
    @InjectView(R.id.rl_order)
    RelativeLayout rlOrder;

    RelativeLayout rl_join;
    RelativeLayout rl_end;
    RelativeLayout rl_order;
    int fraindex;
    List<Fragment> fragmentList = new ArrayList<Fragment>();
    List<RelativeLayout> RelativeLayoutList = new ArrayList<RelativeLayout>();
    JoinFragment joinFragment = new JoinFragment();
    EndFragment endFragment = new EndFragment();
    OrderFragment orderFragment = new OrderFragment();
    @InjectView(R.id.goback)
    ImageView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_auction);
        ButterKnife.inject(this);
        rl_join = (RelativeLayout) findViewById(R.id.rl_join);
        rl_end = (RelativeLayout) findViewById(R.id.rl_end);
        rl_order = (RelativeLayout) findViewById(R.id.rl_order);

        RelativeLayoutList.add(rl_join);
        RelativeLayoutList.add(rl_end);
        RelativeLayoutList.add(rl_order);


        fragmentList.add(joinFragment);
        fragmentList.add(endFragment);
        fragmentList.add(orderFragment);

        rl_join.setOnClickListener(this);
        rl_end.setOnClickListener(this);
        rl_order.setOnClickListener(this);


        rl_join.setSelected(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, fragmentList.get(0));
        fragmentTransaction.commit();
    }

    int currentindext = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_join:
                currentindext = 0;
                break;
            case R.id.rl_end:
                currentindext = 1;
                break;
            case R.id.rl_order:
                currentindext = 2;
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

    @OnClick(R.id.goback)
    public void onClick() {
    finish();
    }
}