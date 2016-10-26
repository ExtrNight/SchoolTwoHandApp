package com.school.twohand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.school.twohand.entity.User;

import com.school.twohand.fragement.HomeFragement;
import com.school.twohand.fragement.MeFragement;
import com.school.twohand.fragement.MessageFragement;
import com.school.twohand.fragement.TaoquanPageFragment;
import com.school.twohand.schooltwohandapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ShowActivity extends AppCompatActivity {
    List<Fragment> fragmentLists = new ArrayList<>();

    List<Button> buttonLists = new ArrayList<>();

    HomeFragement homeFragement;
    TaoquanPageFragment groupFragement;
    MessageFragement messageFragement;
    MeFragement meFragement;
    Button home_button;
    Button group_button;
    Button message_button;
    Button me_button;

    int preIndex;
    @InjectView(R.id.button_home)
    Button buttonHome;
    @InjectView(R.id.button_container_home)
    RelativeLayout buttonContainerHome;
    @InjectView(R.id.button_group)
    Button buttonGroup;
    @InjectView(R.id.button_container_group)
    RelativeLayout buttonContainerGroup;
    @InjectView(R.id.button_public)
    Button buttonPublic;
    @InjectView(R.id.button_container_public)
    RelativeLayout buttonContainerPublic;
    @InjectView(R.id.button_message)
    Button buttonMessage;
    @InjectView(R.id.button_container_message)
    RelativeLayout buttonContainerMessage;
    @InjectView(R.id.button_me)
    Button buttonMe;
    @InjectView(R.id.button_container_me)
    RelativeLayout buttonContainerMe;
    @InjectView(R.id.bottom_navigation)
    LinearLayout bottomNavigation;
    TextView goods_price;
    User user = new User();

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        ButterKnife.inject(this);
        //创建fragment存到list中
        homeFragement = new HomeFragement();


        groupFragement = new TaoquanPageFragment();
        messageFragement = new MessageFragement();
        meFragement = new MeFragement();
        fragmentLists.add(homeFragement);//主页是1
        fragmentLists.add(groupFragement);
        fragmentLists.add(messageFragement);
        fragmentLists.add(meFragement);
        //底部按钮放在list中
        home_button = (Button) findViewById(R.id.button_home);
        group_button = (Button) findViewById(R.id.button_group);
        message_button = (Button) findViewById(R.id.button_message);
        me_button = (Button) findViewById(R.id.button_me);
        buttonLists.add(home_button);
        buttonLists.add(group_button);
        buttonLists.add(message_button);
        buttonLists.add(me_button);

        //初始化界面按钮选中首页
        buttonLists.get(0).setSelected(true);
        //初始化界面fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContent,fragmentLists.get(0)).commit();

    }

    @OnClick({R.id.button_home, R.id.button_group, R.id.button_public, R.id.button_message, R.id.button_me})
    public void onClick(View view) {
        int currentindex = 0;
        switch (view.getId()) {
            case R.id.button_home:
                currentindex = 0;
                break;
            case R.id.button_group:
                currentindex = 1;
                break;
            case R.id.button_public:
                currentindex = preIndex;
                Intent intent = new Intent(this,PublicActivity.class);
                startActivity(intent);
                break;
            case R.id.button_message:
                currentindex = 2;
                break;
            case R.id.button_me:
                currentindex = 3;
                break;
        }
        if (currentindex != preIndex){
            /*
            当前按钮选中，之前按钮取消选中
             */
            buttonLists.get(currentindex).setSelected(true);
            buttonLists.get(preIndex).setSelected(false);
            selectFragment(fragmentLists.get(preIndex),fragmentLists.get(currentindex));
        }

        preIndex = currentindex;
    }

    public void selectFragment(Fragment preFragment, Fragment currentFragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        if (currentFragment.isAdded()){
            fragmentTransaction.hide(preFragment);
            fragmentTransaction.show(currentFragment);
        }else {
            fragmentTransaction.hide(preFragment);
            fragmentTransaction.add(R.id.fragmentContent,currentFragment);
        }
        fragmentTransaction.commit();

    }

}
