package com.school.twohand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.entity.LikeTbl;
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
 * “收到的赞”的Activity
 */
public class NumberPriaseActivity extends AppCompatActivity implements UltraRefreshListener {

    @InjectView(R.id.goback)
    ImageView goback;
    @InjectView(R.id.LL_no_likes)
    LinearLayout LL_no_likes;
    @InjectView(R.id.ultra_lv)
    UltraRefreshListView ultra_lv;
    @InjectView(R.id.ptrCFL)
    PtrClassicFrameLayout ptrCFL;

    User user;
    List<LikeTbl> likeTblList = new ArrayList<>();
    CommonAdapter<LikeTbl> likeTblAdapter;
    Integer pageNo = 1;
    Integer pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_priase);
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
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryReceivedPraiseServlet");
        requestParams.addQueryStringParameter("userId",user.getUserId()+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",pageSize+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<LikeTbl>>(){}.getType();
                List<LikeTbl> newLikeTblList = gson.fromJson(result,type);
                if(newLikeTblList.size()==0){
                    LL_no_likes.setVisibility(View.VISIBLE);
                    ptrCFL.setVisibility(View.GONE);
                }else{
                    LL_no_likes.setVisibility(View.GONE);
                    ptrCFL.setVisibility(View.VISIBLE);
                }
                if(newLikeTblList.size()<=10){
                    ultra_lv.removeFootIfNeed();
                }
                likeTblList.clear();
                likeTblList.addAll(newLikeTblList);
                if(likeTblAdapter==null){
                    likeTblAdapter = new CommonAdapter<LikeTbl>(NumberPriaseActivity.this,likeTblList,R.layout.my_received_praise_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, final LikeTbl likeTbl, int position) {
                            //给我点赞的用户的头像
                            ImageView iv_userImage = viewHolder.getViewById(R.id.iv_userImage);
                            String userImageUrl = NetUtil.imageUrl+likeTbl.getLikeUserMe().getUserHead();
                            ImageOptions imageOptions = new ImageOptions.Builder()
                                    .setCircular(true).setCrop(true).build();
                            x.image().bind(iv_userImage,userImageUrl,imageOptions);
                            //给我点赞的用户的用户名
                            TextView tv_userName = viewHolder.getViewById(R.id.tv_userName);
                            tv_userName.setText(likeTbl.getLikeUserMe().getUserName());

                            //我的被点赞的商品的第一张图片
                            ImageView iv_goods_image = viewHolder.getViewById(R.id.iv_goods_image);
                            if(likeTbl.getLikeGoods()!=null){
                                String goodsImageUrl = NetUtil.imageUrl+likeTbl.getLikeGoods().getGoodsImages().get(0).getImageAddress();
                                ImageOptions imageOptions1 = new ImageOptions.Builder().setCrop(true).build();
                                x.image().bind(iv_goods_image,goodsImageUrl,imageOptions1);

                                iv_goods_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Gson gson = new Gson();
                                        Intent intent = new Intent(NumberPriaseActivity.this,DetailGoodsActivity.class);
                                        intent.putExtra("goodsJson",gson.toJson(likeTbl.getLikeGoods()));
                                        startActivity(intent);
                                    }
                                });
                            }

                        }
                    };
                    ultra_lv.setAdapter(likeTblAdapter);
                }else{
                    likeTblAdapter.notifyDataSetChanged();
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
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryReceivedPraiseServlet");
        requestParams.addQueryStringParameter("userId",user.getUserId()+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",pageSize+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<LikeTbl>>(){}.getType();
                List<LikeTbl> newLikeTblList = gson.fromJson(result,type);
                if(newLikeTblList.size()==0){
                    pageNo--;
                    return;
                }
                likeTblList.addAll(newLikeTblList);
                likeTblAdapter.notifyDataSetChanged();
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
