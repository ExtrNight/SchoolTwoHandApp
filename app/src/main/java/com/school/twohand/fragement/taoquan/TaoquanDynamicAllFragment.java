package com.school.twohand.fragement.taoquan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.taoquan.TaoquanDynamicActivity;
import com.school.twohand.activity.taoquan.TaoquanDynamicDetailsActivity;
import com.school.twohand.entity.AmoyCircleDynamic;
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
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * "所有动态"页面的Fragment
 * Created by yang on 2016/10/19 0019.
 */
public class TaoquanDynamicAllFragment extends TaoquanBaseFragment implements UltraRefreshListener {

    @InjectView(R.id.ultra_lv_dynamic)
    public UltraRefreshListView mLv_dynamic_all;
    @InjectView(R.id.ultra_ptr_dynamic)
    PtrClassicFrameLayout mPtrFrame_dynamic_all;

    private int pageNo = 1;
    public void setPageNo(int pageNo) {this.pageNo = pageNo;}
    private int pageSize = 10;
    private int circleId;
    private String circleName;
    List<AmoyCircleDynamic> dynamicList = new ArrayList<>();
    CommonAdapter<AmoyCircleDynamic> dynamicAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.taoquan_dynamic_fragment, null);
        ButterKnife.inject(this, view);
//        mLv_dynamic_all.removeFootIfNeed(); //去掉底部布局
        //创建我们的自定义头部视图
        CustomUltraRefreshHeader header = new CustomUltraRefreshHeader(getActivity());
        //设置头部视图
        mPtrFrame_dynamic_all.setHeaderView(header);
        //设置视图修改的回调，因为我们的CustomUltraRefreshHeader实现了PtrUIHandler
        mPtrFrame_dynamic_all.addPtrUIHandler(header);
        //设置数据刷新的会回调，因为UltraRefreshListView实现了PtrHandler
        mPtrFrame_dynamic_all.setPtrHandler(mLv_dynamic_all);
        //设置数据刷新回调接口
        mLv_dynamic_all.setUltraRefreshListener(this);


        return view;
    }

    @Override
    public void initView() {
        Bundle bundle = getArguments();
        if(bundle!=null){
            circleId = bundle.getInt("circleId");
            circleName = bundle.getString("circleName");
        }

    }

    @Override
    public void initData() {
        getData();
    }

    //获取该淘圈的所有动态
    public void getData(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryCircleDynamicServlet");
        requestParams.addQueryStringParameter("circleId",circleId+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",pageSize+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type type = new TypeToken<List<AmoyCircleDynamic>>(){}.getType();
                List<AmoyCircleDynamic> newDynamicList = gson.fromJson(result,type);
                if(newDynamicList.size()<=4){
                    mLv_dynamic_all.removeFootIfNeed();//这一页如果只有4条数据，就把底部布局去掉
                }
                dynamicList.clear();
                dynamicList.addAll(newDynamicList);

                if(dynamicAdapter==null){
                    dynamicAdapter = new CommonAdapter<AmoyCircleDynamic>(getActivity(),dynamicList,R.layout.taoquan_dynamic_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, final AmoyCircleDynamic amoyCircleDynamic, int position) {
                            //设置用户头像
                            ImageView iv_user_image = viewHolder.getViewById(R.id.iv_taoquan_dynamic_item_userImage);
                            String user_image_url = NetUtil.imageUrl+amoyCircleDynamic.getUser().getUserHead();
                            ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true)
                                    .setFailureDrawableId(R.mipmap.ic_launcher)
                                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                                    .setCrop(true).build();
                            x.image().bind(iv_user_image,user_image_url,imageOptions);
                            //设置用户名
                            TextView tv_user_name = viewHolder.getViewById(R.id.tv_taoquan_dynamic_item_userName);
                            tv_user_name.setText(amoyCircleDynamic.getUser().getUserName());
                            //设置动态的图片，只显示第一张图片
                            ImageView iv_dynamic_image = viewHolder.getViewById(R.id.iv_taoquan_dynamic_item_image);
                            iv_dynamic_image.setVisibility(View.VISIBLE);//每次让ImageView显示，然后再判断是否去掉ImageView
                            if(amoyCircleDynamic.getImageList().size()!=0){  //如果有图片的话，显示第一张
                                String real_dynamic_image_url = NetUtil.imageUrl+amoyCircleDynamic.getImageList().get(0).getCircleDynamicImageUrl();
                                ImageOptions imageOptions1 = new ImageOptions.Builder().setCrop(true).build();
                                x.image().bind(iv_dynamic_image,real_dynamic_image_url,imageOptions1);
                            }else{ //若没有图片，就将ImageView去掉
                                iv_dynamic_image.setVisibility(View.GONE);
                            }
                            //设置动态的文字内容
                            TextView tv_dynamic_content = viewHolder.getViewById(R.id.tv_taoquan_dynamic_content);
                            tv_dynamic_content.setText(amoyCircleDynamic.getAmoyCircleDynamicTitle()+"  "+amoyCircleDynamic.getAmoyCircleDynamicContent());
                            //设置淘圈名
                            TextView tv_dynamic_circle_name = viewHolder.getViewById(R.id.tv_taoquan_dynamic_item_circleName);
                            tv_dynamic_circle_name.setText("淘圈 | "+circleName);
                            //设置点击事件：跳转到动态详情页面
                            LinearLayout LL_dynamic_content = viewHolder.getViewById(R.id.LL_dynamic_content);
                            LL_dynamic_content.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), TaoquanDynamicDetailsActivity.class);
                                    intent.putExtra("amoyCircleDynamic",amoyCircleDynamic);
                                    intent.putExtra("circleName",circleName);
                                    getActivity().startActivityForResult(intent, TaoquanDynamicActivity.RequestCode);
                                }
                            });
                            //设置点赞
                            TextView tv_likes = viewHolder.getViewById(R.id.tv_likes);
                            tv_likes.setText(amoyCircleDynamic.getLikesList().size()+"");
                            //设置浏览量
                            TextView tv_pv = viewHolder.getViewById(R.id.tv_pv);
                            tv_pv.setText(amoyCircleDynamic.getAmoyCircleDynamicPageviews()+"");

                        }
                    };
                    mLv_dynamic_all.setAdapter(dynamicAdapter);
                }else{
                    dynamicAdapter.notifyDataSetChanged();
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
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryCircleDynamicServlet");
        requestParams.addQueryStringParameter("circleId",circleId+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",pageSize+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result==null){
                    return;
                }
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type type = new TypeToken<List<AmoyCircleDynamic>>(){}.getType();
                List<AmoyCircleDynamic> newDynamicList = gson.fromJson(result,type);
                if(newDynamicList.size()==0){//服务器没有返回新的数据
                    pageNo--; //下一次继续加载这一页
                    //mLv.completeLoad();//没获取到数据也要改变界面
                    return;
                }
                dynamicList.addAll(newDynamicList);

                if(dynamicAdapter==null){
                    dynamicAdapter = new CommonAdapter<AmoyCircleDynamic>(getActivity(),dynamicList,R.layout.taoquan_dynamic_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, final AmoyCircleDynamic amoyCircleDynamic, int position) {

                            //设置用户头像
                            ImageView iv_user_image = viewHolder.getViewById(R.id.iv_taoquan_dynamic_item_userImage);
                            String user_image_url = NetUtil.imageUrl+amoyCircleDynamic.getUser().getUserHead();
                            ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true)
                                    .setFailureDrawableId(R.mipmap.ic_launcher)
                                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                                    .setCrop(true).build();
                            x.image().bind(iv_user_image,user_image_url,imageOptions);
                            //设置用户名
                            TextView tv_user_name = viewHolder.getViewById(R.id.tv_taoquan_dynamic_item_userName);
                            tv_user_name.setText(amoyCircleDynamic.getUser().getUserName());
                            //设置动态的图片，只显示第一张图片
                            ImageView iv_dynamic_image = viewHolder.getViewById(R.id.iv_taoquan_dynamic_item_image);
                            iv_dynamic_image.setVisibility(View.VISIBLE);
                            if(amoyCircleDynamic.getImageList().size()!=0){  //如果有图片的话，显示第一张

                                String real_dynamic_image_url = NetUtil.imageUrl+amoyCircleDynamic.getImageList().get(0).getCircleDynamicImageUrl();
                                ImageOptions imageOptions1 = new ImageOptions.Builder().setCrop(true).build();
                                x.image().bind(iv_dynamic_image,real_dynamic_image_url,imageOptions1);
                            }else{ //若没有图片，就将ImageView去掉
                                iv_dynamic_image.setVisibility(View.GONE);

                            }
                            //设置动态的文字内容
                            TextView tv_dynamic_content = viewHolder.getViewById(R.id.tv_taoquan_dynamic_content);
                            tv_dynamic_content.setText(amoyCircleDynamic.getAmoyCircleDynamicTitle()+"  "+amoyCircleDynamic.getAmoyCircleDynamicContent());
                            //设置淘圈名
                            TextView tv_dynamic_circle_name = viewHolder.getViewById(R.id.tv_taoquan_dynamic_item_circleName);
                            tv_dynamic_circle_name.setText("淘圈 | "+circleName);
                            //设置点击事件：跳转到动态详情页面
                            LinearLayout LL_dynamic_content = viewHolder.getViewById(R.id.LL_dynamic_content);
                            LL_dynamic_content.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), TaoquanDynamicDetailsActivity.class);
                                    intent.putExtra("amoyCircleDynamic",amoyCircleDynamic);
                                    intent.putExtra("circleName",circleName);
                                    getActivity().startActivityForResult(intent, TaoquanDynamicActivity.RequestCode);
                                }
                            });
                            //设置点赞
                            TextView tv_likes = viewHolder.getViewById(R.id.tv_likes);
                            tv_likes.setText(amoyCircleDynamic.getLikesList().size()+"");
                            //设置浏览量
                            TextView tv_pv = viewHolder.getViewById(R.id.tv_pv);
                            tv_pv.setText(amoyCircleDynamic.getAmoyCircleDynamicPageviews()+"");
                        }
                    };
                    mLv_dynamic_all.setAdapter(dynamicAdapter);
                }else{
                    dynamicAdapter.notifyDataSetChanged();
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

    @Override
    public void initEvent() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onRefresh() {
        pageNo = 1; //每次刷新，让pageNo变成初始值1
        mPtrFrame_dynamic_all.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLv_dynamic_all.refreshComplete();
                initData(); //在再次获取数据并刷新ListView
            }
        },1000);
    }

    @Override
    public void addMore() {
        pageNo++;
        mPtrFrame_dynamic_all.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreData();
                mLv_dynamic_all.refreshComplete();
            }
        },1000);
    }

    //    //Fragment对于用户是否可见
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser){
//            Log.i("Taoquan", "setUserVisibleHint: 3333");
//        }else{
//            Log.i("Taoquan", "setUserVisibleHint: 4444");
//        }
//    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if(hidden){
//            Log.i("Taoquan", "onHiddenChanged: 55555");
//        }else{
//            Log.i("Taoquan", "onHiddenChanged: 6666");
//        }
//    }



}
