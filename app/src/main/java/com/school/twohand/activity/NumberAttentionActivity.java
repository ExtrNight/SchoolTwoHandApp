package com.school.twohand.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.ultra.CustomUltraRefreshHeader;
import com.school.twohand.ultra.UltraRefreshListView;
import com.school.twohand.ultra.UltraRefreshListener;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * “我关注的人”的activity
 */
public class NumberAttentionActivity extends AppCompatActivity implements UltraRefreshListener {

    @InjectView(R.id.goback)
    ImageView goback;
    @InjectView(R.id.LL_no_concern)
    LinearLayout LL_no_concern;
    @InjectView(R.id.ptrCFL)
    PtrClassicFrameLayout ptrCFL;
    @InjectView(R.id.ultra_lv)
    UltraRefreshListView ultra_lv;

    User user;
    List<User> userList = new ArrayList<>();
    CommonAdapter<User> userAdapter;
    Integer pageNo = 1;
    Integer pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_attention);
        ButterKnife.inject(this);

        init();
        initView();
        initData();

    }

    private void init(){
        user = ((MyApplication)getApplication()).getUser();
    }

    private void initView(){
        CustomUltraRefreshHeader header = new CustomUltraRefreshHeader(this);
        //设置头部视图
        ptrCFL.setHeaderView(header);
        //设置视图修改的回调，因为我们的CustomUltraRefreshHeader实现了PtrUIHandler
        ptrCFL.addPtrUIHandler(header);
        //设置数据刷新的会回调，因为UltraRefreshListView实现了PtrHandler
        ptrCFL.setPtrHandler(ultra_lv);
        //设置数据刷新回调接口
        ultra_lv.setUltraRefreshListener(this);
    }

    private void initData(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryUserByFollowFlagServlet");
        requestParams.addQueryStringParameter("userId",user.getUserId()+"");
        requestParams.addQueryStringParameter("flag","1");//flag==1表示查出我关注的用户
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",pageSize+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<User>>(){}.getType();
                List<User> newUserList = gson.fromJson(result,type);
                if(newUserList.size()==0){
                    LL_no_concern.setVisibility(View.VISIBLE);
                    ptrCFL.setVisibility(View.GONE);
                }else{
                    LL_no_concern.setVisibility(View.GONE);
                    ptrCFL.setVisibility(View.VISIBLE);
                }
                if(newUserList.size()<=10){
                    ultra_lv.removeFootIfNeed();
                }
                userList.clear();
                userList.addAll(newUserList);
                if(userAdapter==null){
                    userAdapter = new CommonAdapter<User>(NumberAttentionActivity.this,userList,R.layout.my_concern_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, User user, int position) {
                            //用户头像
                            ImageView iv_userImage = viewHolder.getViewById(R.id.iv_userImage);
                            String userImageUrl = NetUtil.imageUrl+user.getUserHead();
                            ImageOptions imageOptions = new ImageOptions.Builder()
                                    .setCircular(true).setCrop(true).build();
                            x.image().bind(iv_userImage,userImageUrl,imageOptions);

                            //用户名
                            TextView tv_userName = viewHolder.getViewById(R.id.tv_userName);
                            tv_userName.setText(user.getUserName());

                            //用户描述
                            TextView tv_user_describe = viewHolder.getViewById(R.id.tv_user_describe);
                            if(user.getUserPersonalProfile()!=null&&!user.getUserPersonalProfile().equals("")){
                                tv_user_describe.setText(user.getUserPersonalProfile());
                            }

                        }
                    };
                    ultra_lv.setAdapter(userAdapter);
                }else{
                    userAdapter.notifyDataSetChanged();
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

    private void loadMoreData(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryUserByFollowFlagServlet");
        requestParams.addQueryStringParameter("userId",user.getUserId()+"");
        requestParams.addQueryStringParameter("flag","1");//flag==1表示查出我关注的用户
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",pageSize+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<User>>(){}.getType();
                List<User> newUserList = gson.fromJson(result,type);
                if(newUserList.size()==0){
                    pageNo--;
                    return;
                }
                userList.addAll(newUserList);
                if(userAdapter==null){
                    userAdapter = new CommonAdapter<User>(NumberAttentionActivity.this,userList,R.layout.my_concern_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, User user, int position) {
                            //用户头像
                            ImageView iv_userImage = viewHolder.getViewById(R.id.iv_userImage);
                            String userImageUrl = NetUtil.imageUrl+user.getUserHead();
                            ImageOptions imageOptions = new ImageOptions.Builder()
                                    .setCircular(true).setCrop(true).build();
                            x.image().bind(iv_userImage,userImageUrl,imageOptions);

                            //用户名
                            TextView tv_userName = viewHolder.getViewById(R.id.tv_userName);
                            tv_userName.setText(user.getUserName());

                            //用户描述
                            TextView tv_user_describe = viewHolder.getViewById(R.id.tv_user_describe);
                            if(user.getUserPersonalProfile()!=null&&!user.getUserPersonalProfile().equals("")){
                                tv_user_describe.setText(user.getUserPersonalProfile());
                            }
                        }
                    };
                    ultra_lv.setAdapter(userAdapter);
                }else{
                    userAdapter.notifyDataSetChanged();
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

    @OnClick(R.id.goback)
    public void onClick() {
        finish();
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        ptrCFL.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
                ultra_lv.refreshComplete();
            }
        },1000);
    }

    @Override
    public void addMore() {
        pageNo++;
        ptrCFL.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreData();
                ultra_lv.refreshComplete();
            }
        },1000);
    }

}
