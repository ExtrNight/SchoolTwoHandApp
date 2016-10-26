package com.school.twohand.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.school.twohand.fragement.homeChildFragement.BabyFragment;
import com.school.twohand.fragement.homeChildFragement.TopicFragment;
import com.school.twohand.schooltwohandapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MyReleaseActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.rl_baby)
    RelativeLayout rlBaby;
    @InjectView(R.id.rl_topic)
    RelativeLayout rlTopic;

    RelativeLayout rl_baby;
    RelativeLayout rl_topic;
    int fraindex;
    List<Fragment> fragmentList = new ArrayList<Fragment>();
    List<RelativeLayout> RelativeLayoutList = new ArrayList<RelativeLayout>();
    BabyFragment babyFragment = new BabyFragment();
    TopicFragment topicFragment = new TopicFragment();
    @InjectView(R.id.goback)
    ImageView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_release);
        ButterKnife.inject(this);


        rl_baby = (RelativeLayout) findViewById(R.id.rl_baby);
        rl_topic = (RelativeLayout) findViewById(R.id.rl_topic);

        RelativeLayoutList.add(rl_baby);
        RelativeLayoutList.add(rl_topic);


        fragmentList.add(babyFragment);
        fragmentList.add(topicFragment);

        rl_baby.setOnClickListener(this);
        rl_topic.setOnClickListener(this);

        rl_baby.setSelected(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, fragmentList.get(0));
        fragmentTransaction.commit();
    }

    int currentindext = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_baby:
                currentindext = 0;
                break;
            case R.id.rl_topic:
                currentindext = 1;
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
