package com.school.twohand.fragement.homeChildFragement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.DetailGoodsActivity;
import com.school.twohand.activity.InforPageActivity;
import com.school.twohand.customview.HomePageListView;
import com.school.twohand.entity.Goods;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.query.entity.QueryGoodsBean;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/25 0025.
 */
public class TwoFragment extends Fragment implements HomePageListView.OnLoadChangeListener{

    HomePageListView listViewBody;//显示商品信息的listView
    List<Goods> goodsMessage = new ArrayList<>();//服务器获取到的数据源
    //查询方式
    final int TIMEDESC = 0;//时间降序
    final int PRICEUPTODWON = 1;//价格降序
    final int PRICEDOWNTOUP = 2;//价格升序
    final int SUREPRICE = 3;//一口价
    final int NOSUREPRICE = 4;//拍卖
    private Integer pageNo = 1;
    private Integer pageSize = 6;

    SwipeRefreshLayout swipe_container;//刷新的控件
    Handler handler = new Handler();
    QueryGoodsBean queryGoodsBean;
    Gson gson = new Gson();
    CommonAdapter<Goods> co;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_child_page, container, false);

        init(view);

        //服务器获取商品详情将值赋值给listViewBody
        initListBody();
        initEvent();
        return view;
    }

    private void init(View view){
        //初始化显示商品详情的listView
        listViewBody = (HomePageListView) view.findViewById(R.id.listViewBody);

        swipe_container = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);//刷新的布局控件
        //设置刷新的动画的颜色，最多四个
        swipe_container.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);

        queryGoodsBean = new QueryGoodsBean(null,null,null,0,pageNo,pageSize);//0表示按时间降序
    }

    /**
     * 服务器获取商品详情将值赋值给listViewBody
     */
    public void initListBody(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryGoodsServlet");
        String queryGoodsBeanString = gson.toJson(queryGoodsBean);
        requestParams.addQueryStringParameter("queryGoodsBean",queryGoodsBeanString);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(final String result) {
                List<Goods> newGoodsMessage = gson.fromJson(result,new TypeToken<List<Goods>>(){}.getType());
                goodsMessage.clear();
                goodsMessage.addAll(newGoodsMessage);
                //用通用适配器将数据源显示在listView上
                if(co == null){
                    co = new CommonAdapter<Goods>(getActivity(),goodsMessage,R.layout.goods_message) {
                        @Override
                        public void convert(ViewHolder viewHolder, final Goods goods, final int position) {
                            ImageView userHeadView = viewHolder.getViewById(R.id.user_head_t);//用户头像
                            TextView userName = viewHolder.getViewById(R.id.user_name_t);//用户名
                            TextView goodsPrice = viewHolder.getViewById(R.id.goods_price_t);//商品价格
                            //ImageView goodsImage = viewHolder.getViewById(R.id.goods_image_t);//商品图片
                            TextView userSchool = viewHolder.getViewById(R.id.user_school_t);//用户学校
                            TextView amoyCircle = viewHolder.getViewById(R.id.amoy_circle_t);//淘圈名
                            TextView like = viewHolder.getViewById(R.id.like_t);//点赞
                            TextView messageBoard = viewHolder.getViewById(R.id.message_t);//留言
                            TextView goodsText = viewHolder.getViewById(R.id.goods_text_t);//商品描述
                            //从数据库获取头像
                            String userHeadUrl=NetUtil.imageUrl+goods.getGoodsUser().getUserHead();
                            ImageOptions userImageOptions=new ImageOptions.Builder()
                                    .setCircular(true)
                                    .build();
                            x.image().bind(userHeadView,userHeadUrl,userImageOptions);
                            //点击跳转到该用户的名片,前提是用户已登录
                            if(((MyApplication)getActivity().getApplication()).getUser()!=null){
                                userHeadView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity(), InforPageActivity.class);
                                        intent.putExtra("infoPageUser",goods.getGoodsUser());
                                        startActivity(intent);
                                    }
                                });
                            }
                            //从数据库获取商品图片
                            /**
                             * 修改为多图片滑动滑到最后一张的时候可以进入详情或者点击进入详情
                             */
//                        String goodsUrl = NetUtil.imageUrl+goods.getGoodsImages().get(0).getImageAddress();
//                        ImageOptions goodsImageOptions= new ImageOptions.Builder()
//                                .build();
//                        x.image().bind(goodsImage,goodsUrl,goodsImageOptions);

                            LinearLayout LL = viewHolder.getViewById(R.id.LL);
                            LL.setTag(goods);
                            LL.removeAllViews(); //加之前要先把之前的remove掉，！！！
                            for (int i = 0; i < ((Goods) LL.getTag()).getGoodsImages().size(); i++) {
                                View view = LayoutInflater.from(getActivity()).inflate(
                                        R.layout.each_taoquan_image_item, null);
                                ImageView iv_goodsImage = (ImageView) view.findViewById(R.id.iv_goods_image_item);
                                iv_goodsImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(),DetailGoodsActivity.class);
                                        intent.putExtra("goodsMessage",result);
                                        intent.putExtra("position",position+1);
                                        startActivity(intent);
                                    }
                                });
                                String url = NetUtil.imageUrl + ((Goods) LL.getTag()).getGoodsImages().get(i).getImageAddress();
                                ImageOptions imageOptions2 = new ImageOptions.Builder()
                                        .setFailureDrawableId(R.mipmap.ic_launcher)
                                        .setLoadingDrawableId(R.mipmap.ic_launcher)
                                        .setCrop(true).build();
                                x.image().bind(iv_goodsImage, url, imageOptions2);
                                LL.addView(view);
                            }

                            //给控件赋值
                            userName.setText(goods.getGoodsUser().getUserName());
                            goodsPrice.setText("￥"+goods.getGoodsPrice()+"");

                            userSchool.setVisibility(View.GONE);
                            if(goods.getGoodsUserSchoolName()!=null){
                                userSchool.setVisibility(View.VISIBLE);
                                userSchool.setText("来自 "+goods.getGoodsUserSchoolName());
                            }else{
                                if(goods.getGoodsUser().getUserSchoolName()!=null){
                                    userSchool.setVisibility(View.VISIBLE);
                                    userSchool.setText("来自 "+goods.getGoodsUser().getUserSchoolName());
                                }
                            }
                            amoyCircle.setVisibility(View.GONE);
                            if(goods.getGoodsAmoyCircle()!=null){
                                amoyCircle.setVisibility(View.VISIBLE);
                                amoyCircle.setText("淘圈丨"+goods.getGoodsAmoyCircle().getCircleName());
                            }
                            like.setText("点赞"+goods.getGoodsLikes().size());
                            messageBoard.setText("留言"+goods.getGoodsMessageBoards().size());
                            goodsText.setText(goods.getGoodsTitle()+"  "+goods.getGoodsDescribe());
                        }
                    };
                    listViewBody.setAdapter(co);
                    //点击跳转详情界面
                    listViewBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getContext(),DetailGoodsActivity.class);
                            intent.putExtra("goodsMessage",result);
                            intent.putExtra("position",position+1);

                            startActivity(intent);
                        }
                    });
                }else{
                    co.notifyDataSetChanged();
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

    public void loadMorData(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryGoodsServlet");
        String queryGoodsBeanString = gson.toJson(queryGoodsBean);
        requestParams.addQueryStringParameter("queryGoodsBean",queryGoodsBeanString);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(final String result) {
                final Gson gson = new Gson();
                List<Goods> newGoodsMessage = gson.fromJson(result,new TypeToken<List<Goods>>(){}.getType());
                if(newGoodsMessage.size()==0){
                    pageNo--;
                    queryGoodsBean.setPageNo(pageNo);
                    return;
                }
                goodsMessage.addAll(newGoodsMessage);

                co.notifyDataSetChanged();
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

    private void initEvent(){
        listViewBody.setOnLoadChangeListener(this);
        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageNo = 1;
                        queryGoodsBean.setPageNo(pageNo);
                        initListBody();
                        swipe_container.setRefreshing(false); //完成刷新
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onLoad() {
        pageNo++;
        queryGoodsBean.setPageNo(pageNo);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMorData();
                listViewBody.completeLoad();
            }
        },1000);
    }

}
