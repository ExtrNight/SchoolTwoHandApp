package com.school.twohand.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    @InjectView(R.id.btn_write)
    Button btnWrite;
    RelativeLayout rl_join1;
    RelativeLayout rl_end1;

    int fraindex;
    List<Fragment> fragmentList = new ArrayList<Fragment>();
    List<RelativeLayout> RelativeLayoutList = new ArrayList<RelativeLayout>();
    TopicFragment topicFragment = new TopicFragment();
    CircleFragment circleFragment = new CircleFragment();

    MyApplication myApplication;
    User infoPageUser;    //名片页面的User对象
    boolean isMyConcern = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_page);
        ButterKnife.inject(this);

        init();
        getInfoData();

        rl_join1 = (RelativeLayout) findViewById(R.id.rl_join1);
        rl_end1 = (RelativeLayout) findViewById(R.id.rl_end1);

        RelativeLayoutList.add(rl_join1);
        RelativeLayoutList.add(rl_end1);

        fragmentList.add(topicFragment);
        fragmentList.add(circleFragment);

        rl_join1.setSelected(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, fragmentList.get(0));
        fragmentTransaction.commit();
    }

    private void init(){
        myApplication = (MyApplication) getApplication();
        Intent intent = getIntent();
        infoPageUser = intent.getParcelableExtra("infoPageUser");

        if(myApplication.getUser().getUserId()!=infoPageUser.getUserId()){
            //说明进入的不是自己的名片
            btnWrite.setText("关注Ta");
            checkIsMyConcern();
        }

    }

    //判断是否是我的关注
    private void checkIsMyConcern(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"CheckIsMyConcernServlet");
        requestParams.addQueryStringParameter("myUserId",myApplication.getUser().getUserId()+"");
        requestParams.addQueryStringParameter("checkedUserId",infoPageUser.getUserId()+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result.equals("y")){
                    isMyConcern = true;
                    btnWrite.setText("已关注");
                }
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

    int currentindext = 0;

    @OnClick({R.id.rl_join1, R.id.rl_end1,  R.id.goback1, R.id.btn_write})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_join1:
                currentindext = 0;
                break;
            case R.id.rl_end1:
                currentindext = 1;
                break;
            case R.id.goback1:
                finish();
                return;
            case R.id.btn_write:
                if(myApplication.getUser().getUserId()==infoPageUser.getUserId()){
                    //说明进入的是自己的名片,点击跳转到编辑信息页面
                    Intent intent=new Intent(InforPageActivity.this,MyInforActivity.class);
                    startActivity(intent);
                }else{
                    //说明进入的是别人的名片
                    if(isMyConcern){ //已经是我的关注，点击取消关注
                        new AlertDialog.Builder(InforPageActivity.this).setMessage("是否取消关注")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        insertOrDeleteConcern(2);
                                        Toast.makeText(InforPageActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    }else{
                        insertOrDeleteConcern(1);
                        Toast.makeText(InforPageActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
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
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("userId", infoPageUser.getUserId() + "");

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

                SimpleDateFormat aa=new SimpleDateFormat("yyyy-MM-dd");
                Date d1=new Date(user.getUserBirthday().getTime());
                Date d2=new Date(System.currentTimeMillis());
                long diff=(d2.getTime()-d1.getTime())/(1000 * 60 * 60 * 24);
                long year = diff/365 ;

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

    //增加或删除一条关注记录,1表示增加一条关注记录，2表示删除一条关注记录
    private void insertOrDeleteConcern(final int flag){
        RequestParams requestParams = new RequestParams(NetUtil.url+"InsertOrDeleteConcernServlet");
        requestParams.addQueryStringParameter("myUserId",myApplication.getUser().getUserId()+"");
        requestParams.addQueryStringParameter("concernedUserId",infoPageUser.getUserId()+"");
        requestParams.addQueryStringParameter("flag",flag+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(flag==1){  //关注成功
                    isMyConcern = true;
                    btnWrite.setText("取消关注");
                }else if(flag==2){      //取消关注成功
                    isMyConcern = false;
                    btnWrite.setText("关注Ta");
                }
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




}

