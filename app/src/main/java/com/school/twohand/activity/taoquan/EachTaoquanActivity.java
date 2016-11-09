package com.school.twohand.activity.taoquan;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.DetailGoodsActivity;
import com.school.twohand.activity.login.LoginActivity;
import com.school.twohand.customview.EachTaoquanListView;
import com.school.twohand.customview.loadingview.ShapeLoadingDialog;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.entity.Goods;
import com.school.twohand.entity.Group;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.query.entity.QueryGoodsBean;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.BlurBitmap;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.ListViewItemUtils;
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
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 单个淘圈的页面
 */
public class EachTaoquanActivity extends AppCompatActivity implements EachTaoquanListView.OnLoadChangeListener, View.OnClickListener, View.OnTouchListener {

    @InjectView(R.id.lv_each_taoquan_goods)
    EachTaoquanListView lvEachTaoquanGoods;
    @InjectView(R.id.btn_bottom)
    LinearLayout btnBottom;
    @InjectView(R.id.btn_bottom_joinCircle)
    Button btnBottomJoinCircle;
    @InjectView(R.id.btn_bottom_publish)
    ImageView btnBottomPublish;
    //群id
    Long groupId = 0L;
    RelativeLayout RL_top;         //上面的布局
    private ImageView iv_return;  //返回键
    private ImageView iv_share;   //分享
    private ImageView iv_search;  //搜索
    private ImageView iv_exit;     //退出淘圈
    private ImageView iv_setting; //淘圈设置

    SwipeRefreshLayout swipe_container;     //刷新的布局控件

    private static final int ModifyTaoquanInfo = 1;
    private static final int PublishGoods = 2;
    private static final int RequestCode = 3;

    MyApplication myApplication;

    AmoyCircle amoyCircle;    //所在的淘圈对象
    boolean isCircleMember = false;  //是否是淘圈成员
    boolean isCircleMaster = false;  //是否是淘圈圈主

    CommonAdapter<Goods> goodsAdapter;
    List<Goods> goodsList = new ArrayList<>();
    QueryGoodsBean queryGoodsBean;
//    ProgressDialog pd;   //进度条，圆形
    private ShapeLoadingDialog shapeLoadingDialog; //带有动画效果的加载
    Handler handler = new Handler();
    int orderFlag = 0;          //0表示时间顺序
    private int pageNo = 1;    //页号
    private int pageSize = 5;  //每页数量
    Gson gson = new Gson();
    AnimationSet animationSetSmallToBig;  //组合动画，由小到大，旋转
    AnimationSet animationSetBigToSmall;  //组合动画，由大到小，旋转
    AlphaAnimation alphaAnimationTo1;      //透明度变化，由全透明到不透明
    AlphaAnimation alphaAnimationTo0;      //透明度变化，由不透明到全透明
    AnimationSet animationSetPublish;  //组合动画，点击发布开始动画

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_taoquan);
        ButterKnife.inject(this);

        init();
        initView();
        myApplication = (MyApplication) getApplication();
        if(myApplication.getUser()==null){
            //将“发布”隐藏，显示“加入淘圈”
            btnBottom.setVisibility(View.VISIBLE);
            btnBottomPublish.setVisibility(View.GONE);
        }else{
            //判断淘圈是否存在此人，并改变isCircleMember和isCircleMaster的值
            isCircleMemberExists(myApplication.getUser().getUserId(), amoyCircle.getCircleId());
        }
        initData();
        initAnimation();
        //获取群id，初始化
        groupNumber(amoyCircle.getCircleUserId());
        initEvent();

    }

    private void init() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        amoyCircle = bundle.getParcelable("amoyCircle"); //获取到上个页面传来的AmoyCircle对象
        orderFlag = 0;
        pageNo = 1;

        //初始化头部控件
        RL_top = (RelativeLayout) findViewById(R.id.RL_top);
        iv_return = (ImageView) findViewById(R.id.iv_each_taoquan_return);
        iv_share = (ImageView) findViewById(R.id.iv_each_taoquan_share);
        iv_search = (ImageView) findViewById(R.id.iv_each_taoquan_search);
        iv_exit = (ImageView) findViewById(R.id.iv_each_taoquan_more);
        iv_setting = (ImageView) findViewById(R.id.iv_each_taoquan_setting);

        swipe_container = (SwipeRefreshLayout) findViewById(R.id.swipe_container);//刷新的布局控件
        //设置刷新的动画的颜色，最多四个
        swipe_container.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);

        RL_top.getBackground().setAlpha(0);

        //lvEachTaoquanGoods.iv_taoquan_bg.setImageResource(R.drawable.taoquan_bg_15);
        lvEachTaoquanGoods.setOnTouchListener(this);   //注册OnTouch监听

        shapeLoadingDialog = new ShapeLoadingDialog(this);//shapeLoadingDialog对象
    }

    //判断淘圈中是否存在此人,若存在，isCircleMember为true，
    private void isCircleMemberExists(int userId, int circleId) {
        RequestParams requestParams = new RequestParams(NetUtil.url + "isCircleMemberExistsServlet");
        requestParams.addQueryStringParameter("userId", userId + "");
        requestParams.addQueryStringParameter("circleId", circleId + "");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result.equals("1")) {
                    isCircleMember = true;
                    //如果是淘圈成员,将“加入淘圈隐藏”，显示“发布”
                    btnBottom.setVisibility(View.GONE);
                    btnBottomPublish.setVisibility(View.VISIBLE);
                    iv_exit.setVisibility(View.VISIBLE);//显示退出按钮
                    if (myApplication.getUser().getUserId() == amoyCircle.getCircleUserId()) {
                        isCircleMaster = true;  //是淘圈圈主,圈主不可退出淘圈
                        iv_exit.setVisibility(View.INVISIBLE);
                        iv_setting.setVisibility(View.VISIBLE);
                    }
                } else {
                    //不是淘圈成员,将“发布”隐藏，显示“加入淘圈”
                    btnBottom.setVisibility(View.VISIBLE);
                    btnBottomPublish.setVisibility(View.GONE);
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

    private void initView() {
        if (amoyCircle != null) {
            //初始化淘圈头布局背景
            String circleBackgroundUrl = amoyCircle.getCircleBackgroundUrl();
            if(circleBackgroundUrl.substring(0,1).equals("s")){
                int systemCircleBackgroundId = Integer.parseInt(circleBackgroundUrl.substring(1));
                setHeadBackground(systemCircleBackgroundId,lvEachTaoquanGoods.iv_taoquan_bg);
            }else{
                String url = NetUtil.imageUrl + amoyCircle.getCircleBackgroundUrl();
                ImageOptions imageOptions = new ImageOptions.Builder().setCrop(true).build();
                x.image().bind(lvEachTaoquanGoods.iv_taoquan_bg, url, imageOptions);
            }

            //设置淘圈头像
            String url = NetUtil.imageUrl + amoyCircle.getCircleImageUrl();
            ImageOptions imageOptions = new ImageOptions.Builder()
                                    /*.setCircular(true)  设为圆形*/
                    .setFailureDrawableId(R.mipmap.upload_circle_image)
                    .setLoadingDrawableId(R.mipmap.upload_circle_image)
                    .setCrop(true).build();          //是否裁剪？
            x.image().bind(lvEachTaoquanGoods.iv_taoquan_head, url, imageOptions);
            //设置淘圈名
            lvEachTaoquanGoods.tv_taoquan_name.setText(amoyCircle.getCircleName());
            //设置淘圈人气
            lvEachTaoquanGoods.tv_taoquan_popularity.setText("人气 " + (amoyCircle.getCircleNumber() + 100));

        }
    }

    private void initData() {
        queryGoodsBean = new QueryGoodsBean(null, null, null, orderFlag, pageNo, pageSize, amoyCircle.getCircleId());//0表示时间顺序
        getGoodsData(queryGoodsBean);
    }

    //初始化动画
    private void initAnimation(){
        //由小变大的动画
        ScaleAnimation scaleAnimationSmallToBig = new ScaleAnimation(0,1,0,1, Animation.RELATIVE_TO_SELF,
                        0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimationSmallToBig.setDuration(1000);
        scaleAnimationSmallToBig.setInterpolator(new AccelerateInterpolator());//加速动画

        //由大到小的动画
        ScaleAnimation scaleAnimationBigToSmall = new ScaleAnimation(1,0,1,0, Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimationBigToSmall.setDuration(1000);
        scaleAnimationBigToSmall.setInterpolator(new AccelerateInterpolator());//加速动画

        //旋转动画
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new AccelerateInterpolator());//加速动画

        //透明度变化，由全透明变为不透明
        alphaAnimationTo1 = new AlphaAnimation(0, 1);
        alphaAnimationTo1.setDuration(500);
        alphaAnimationTo1.setInterpolator(new AccelerateInterpolator()); //加速动画

        //透明度变化，由不透明变为全透明
        alphaAnimationTo0 = new AlphaAnimation(1, 0);
        alphaAnimationTo0.setDuration(500);
        alphaAnimationTo0.setInterpolator(new AccelerateInterpolator()); //加速动画

        //组合动画,由小变大，旋转,点击加入淘圈后开始动画
        animationSetSmallToBig = new AnimationSet(true);
        animationSetSmallToBig.addAnimation(scaleAnimationSmallToBig);
        animationSetSmallToBig.addAnimation(rotateAnimation);

        //组合动画,由大变小，旋转，点击退出淘圈后开始动画
        animationSetBigToSmall = new AnimationSet(true);
        animationSetBigToSmall.addAnimation(scaleAnimationBigToSmall);
        animationSetBigToSmall.addAnimation(rotateAnimation);

        //旋转动画,点击发布开始动画
        RotateAnimation rotateAnimationPublish = new RotateAnimation(0, 250, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, -1f);//圆心：X向右0.5f，Y向上1f
        rotateAnimationPublish.setDuration(500);
        rotateAnimationPublish.setInterpolator(new AccelerateInterpolator());
        //由小变大的动画,点击发布开始动画
        ScaleAnimation scaleAnimationPublish = new ScaleAnimation(1,2,1,2, Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimationPublish.setDuration(500);
        scaleAnimationPublish.setInterpolator(new AccelerateInterpolator());
        //组合动画，点击发布开始动画
        animationSetPublish = new AnimationSet(true);
        animationSetPublish.addAnimation(rotateAnimationPublish);
        animationSetPublish.addAnimation(scaleAnimationPublish);
        animationSetPublish.addAnimation(alphaAnimationTo0);
        animationSetPublish.addAnimation(rotateAnimation);      //自身旋转
    }

    private void initEvent() {
        btnBottomJoinCircle.setOnClickListener(this);
        btnBottomPublish.setOnClickListener(this);
        iv_return.setOnClickListener(this);
        iv_exit.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        lvEachTaoquanGoods.LL_order_by_heat.setOnClickListener(this);
        lvEachTaoquanGoods.LL_order_by_time.setOnClickListener(this);
        lvEachTaoquanGoods.LL_chat_room.setOnClickListener(this);
        lvEachTaoquanGoods.LL_taoquan_dynamic.setOnClickListener(this);

        //实现自定义ListView里的OnRefreshUploadChangeListener接口
        lvEachTaoquanGoods.setOnLoadChangeListener(this);

        lvEachTaoquanGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position <= goodsList.size()) {
                    Log.i("EachTaoquanActivity", "1111onClick: " + position);
                    Intent intent = new Intent(EachTaoquanActivity.this, DetailGoodsActivity.class);
                    intent.putExtra("goodsMessage", gson.toJson(goodsList));
                    intent.putExtra("position", position); //  position+ 1,头部也算位置,区别于346行的position？？为什么不一样
                    startActivity(intent);
                }
            }
        });

        //刷新的布局控件的刷新监听
        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageNo = 1;
                        initData();
                        swipe_container.setRefreshing(false); //完成刷新
                    }
                }, 1000);
            }
        });

    }

    //获取显示的商品的数据并显示
    private void getGoodsData(QueryGoodsBean queryGoodsBean) {
        String url = NetUtil.url + "QueryGoodsServlet";
        RequestParams requestParams = new RequestParams(url);
        Gson gson = new Gson();
        String queryGoodsBeanString = gson.toJson(queryGoodsBean);
        requestParams.addQueryStringParameter("queryGoodsBean", queryGoodsBeanString);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(final String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Goods>>() {}.getType();
                List<Goods> newGoodsList = gson.fromJson(result, type);
                if (newGoodsList.size() < 5) { //商品数量小于5，则移除底部布局,即不需要加载
                    lvEachTaoquanGoods.removeFootViewIfNeed();
                }
                goodsList.clear();
                goodsList.addAll(newGoodsList);
                lvEachTaoquanGoods.tv_goods_number.setText("发布数 " + goodsList.size());
                //设置ListView的数据源
                if (goodsAdapter == null) {
                    goodsAdapter = new CommonAdapter<Goods>(EachTaoquanActivity.this, goodsList, R.layout.each_taoquan_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, Goods goods, final int position) {
                            //显示用户头像
                            ImageView iv_userImage = viewHolder.getViewById(R.id.each_taoquan_item_userImage);
                            String url = NetUtil.imageUrl + goods.getGoodsUser().getUserHead();
                            //设置图片样式
                            ImageOptions imageOptions = new ImageOptions.Builder()
                                    .setCircular(true)  /*设为圆形*/
                                    .setFailureDrawableId(R.mipmap.ic_launcher)
                                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                                    .setCrop(true).build();          //是否裁剪？
                            x.image().bind(iv_userImage, url, imageOptions);
                            //设置用户名
                            TextView tv_userName = viewHolder.getViewById(R.id.each_taoquan_item_userName);
                            tv_userName.setText(goods.getGoodsUser().getUserName());
                            //设置用户是否是圈主
                            TextView tv_isCircleMaster = viewHolder.getViewById(R.id.tv_isCircleMaster);
                            tv_isCircleMaster.setVisibility(View.GONE);
                            if (goods.getGoodsUser().getUserId() == amoyCircle.getCircleUserId()) {
                                tv_isCircleMaster.setVisibility(View.VISIBLE);
                            }
                            //设置价格
                            TextView tv_price = viewHolder.getViewById(R.id.each_taoquan_item_price);
                            tv_price.setText("￥ " + goods.getGoodsPrice());
                            //设置商品描述
                            TextView tv_describe = viewHolder.getViewById(R.id.each_taoquan_item_describe);
                            if (goods.getGoodsTitle() != null) {
                                tv_describe.setText(goods.getGoodsTitle() + " " + goods.getGoodsDescribe());
                            }
                            //设置商品图片
                            LinearLayout LL = viewHolder.getViewById(R.id.LL);
                            LL.setTag(goods);
                            addLLView(LL, position);
                            //设置商品所属用户的学校
                            TextView tv_goods_user_school = viewHolder.getViewById(R.id.tv_goods_user_school);
                            String goodsUserSchool = goods.getGoodsUser().getUserSchoolName();
                            if (goodsUserSchool != null) {
                                tv_goods_user_school.setText("来自 " + goodsUserSchool);
                            }
                            //设置点赞量和浏览量
                            TextView tv_likes_pageview = viewHolder.getViewById(R.id.tv_likes_pageview);
                            tv_likes_pageview.setText("点赞 " + goods.getGoodsLikes().size() + " · 浏览 " + goods.getGoodsPV());
                            //点击跳转详情界面
                            LinearLayout LL_click_to_details = viewHolder.getViewById(R.id.LL_click_to_details);
//                            LL_click_to_details.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Log.i("EachTaoquanActivity", "onClick: "+position);
//                                    Intent intent = new Intent(EachTaoquanActivity.this, DetailGoodsActivity.class);
//                                    intent.putExtra("goodsMessage", result);
//                                    intent.putExtra("position", position + 1);
//                                    startActivity(intent);
//                                }
//                            });
                        }
                    };
                    lvEachTaoquanGoods.setAdapter(goodsAdapter);
//                    ListViewItemUtils.setListViewHeightBasedOnChildren(lvEachTaoquanGoods);
                } else {
                    goodsAdapter.notifyDataSetChanged();
//                    ListViewItemUtils.setListViewHeightBasedOnChildren(lvEachTaoquanGoods);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(shapeLoadingDialog!=null){
                            shapeLoadingDialog.dismiss();
                        }
                    }
                }, 3000);
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

    public void addLLView(LinearLayout LL, final int position) {
        LL.removeAllViews(); //加之前要先把之前的remove掉，！！！
//        Log.i("EachTaoquanActivity", "addLLView: ((EachCircleItem)LL.getTag()).getGoodsImages():"+((EachCircleItem)LL.getTag()).getGoodsImages());
        for (int i = 0; i < ((Goods) LL.getTag()).getGoodsImages().size(); i++) {
//                                View view = LayoutInflater.from(EachTaoquanActivity.this).inflate(
//                                        R.layout.each_taoquan_image_item, LL, false);
            View view = LayoutInflater.from(EachTaoquanActivity.this).inflate(
                    R.layout.each_taoquan_image_item, null);
            ImageView iv_goodsImage = (ImageView) view.findViewById(R.id.iv_goods_image_item);
            iv_goodsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EachTaoquanActivity.this, DetailGoodsActivity.class);
                    intent.putExtra("goodsMessage", gson.toJson(goodsList));
                    Log.i("EachTaoquanActivity", "2222onClick: " + position);
                    intent.putExtra("position", position + 1);
                    startActivity(intent);
                }
            });
            String url2 = NetUtil.imageUrl + ((Goods) LL.getTag()).getGoodsImages().get(i).getImageAddress();
            ImageOptions imageOptions2 = new ImageOptions.Builder()
                    .setFailureDrawableId(R.mipmap.ic_launcher)
                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                    .setCrop(true).build();
            x.image().bind(iv_goodsImage, url2, imageOptions2);
            LL.addView(view);
        }
    }

    //上拉加载更多数据
    private void loadMoreGoodsData() {
        String url = NetUtil.url + "QueryGoodsServlet";
        RequestParams requestParams = new RequestParams(url);
        Gson gson = new Gson();
        String queryGoodsBeanString = gson.toJson(queryGoodsBean);
        requestParams.addQueryStringParameter("queryGoodsBean", queryGoodsBeanString);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(final String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Goods>>() {}.getType();
                List<Goods> newGoodsList = gson.fromJson(result, type);
                if (newGoodsList.size() == 0) { //服务器没有返回新的数据
                    pageNo--; //下一次继续加载这一页
                    //lvEachTaoquanGoods.removeFootViewIfNeed();
                    Toast.makeText(EachTaoquanActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                    return;
                }
                //goodsList.clear();
                goodsList.addAll(newGoodsList);
                lvEachTaoquanGoods.tv_goods_number.setText("发布数 " + goodsList.size());
                //设置ListView的数据源
                if (goodsAdapter == null) {
                    goodsAdapter = new CommonAdapter<Goods>(EachTaoquanActivity.this, goodsList, R.layout.each_taoquan_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, Goods goods, final int position) {
                            //显示用户头像
                            ImageView iv_userImage = viewHolder.getViewById(R.id.each_taoquan_item_userImage);
                            String url = NetUtil.imageUrl + goods.getGoodsUser().getUserHead();
                            //设置图片样式
                            ImageOptions imageOptions = new ImageOptions.Builder()
                                    .setCircular(true)  /*设为圆形*/
                                    .setFailureDrawableId(R.mipmap.ic_launcher)
                                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                                    .setCrop(true).build();          //是否裁剪？
                            x.image().bind(iv_userImage, url, imageOptions);
                            //设置用户名
                            TextView tv_userName = viewHolder.getViewById(R.id.each_taoquan_item_userName);
                            tv_userName.setText(goods.getGoodsUser().getUserName());
                            //设置用户是否是圈主
                            TextView tv_isCircleMaster = viewHolder.getViewById(R.id.tv_isCircleMaster);
                            tv_isCircleMaster.setVisibility(View.GONE);
                            if (goods.getGoodsUser().getUserId() == amoyCircle.getCircleUserId()) {
                                tv_isCircleMaster.setVisibility(View.VISIBLE);
                            }
                            //设置价格
                            TextView tv_price = viewHolder.getViewById(R.id.each_taoquan_item_price);
                            tv_price.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(EachTaoquanActivity.this, DetailGoodsActivity.class);
                                    intent.putExtra("goodsMessage", result);
                                    intent.putExtra("position", position + 1);
                                    startActivity(intent);
                                }
                            });
                            tv_price.setText("￥ " + goods.getGoodsPrice());
                            //设置商品描述
                            TextView tv_describe = viewHolder.getViewById(R.id.each_taoquan_item_describe);
                            if (goods.getGoodsTitle() != null) {
                                tv_describe.setText(goods.getGoodsTitle() + " " + goods.getGoodsDescribe());
                            }

                            //设置商品图片
                            LinearLayout LL = viewHolder.getViewById(R.id.LL);
                            LL.setTag(goods);
                            addLLView(LL, position);

                            //设置商品所属用户的学校
                            TextView tv_goods_user_school = viewHolder.getViewById(R.id.tv_goods_user_school);
                            String goodsUserSchool = goods.getGoodsUser().getUserSchoolName();
                            if (goodsUserSchool != null) {
                                tv_goods_user_school.setText("来自 " + goodsUserSchool);
                            }
                            //设置点赞量和浏览量
                            TextView tv_likes_pageview = viewHolder.getViewById(R.id.tv_likes_pageview);
                            tv_likes_pageview.setText("点赞 " + goods.getGoodsLikes().size() + " · 浏览 " + goods.getGoodsPV());
                            //点击跳转详情界面
                            LinearLayout LL_click_to_details = viewHolder.getViewById(R.id.LL_click_to_details);
                            LL_click_to_details.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(EachTaoquanActivity.this, DetailGoodsActivity.class);
                                    intent.putExtra("goodsMessage", result);
                                    intent.putExtra("position", position + 1);
                                    startActivity(intent);
                                }
                            });
                        }
                    };
                    lvEachTaoquanGoods.setAdapter(goodsAdapter);
                } else {
                    goodsAdapter.notifyDataSetChanged();
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

    private void groupNumber(Integer userId){
        //根据userId 查询q对应群号集合，根据群号查询群名，根据群名是否跟当前群名相同判断返回的群号
        RequestParams requestParams = new RequestParams(NetUtil.url+"QuestGroupServlet");
        requestParams.addQueryStringParameter("groupMainUserId",userId+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("EachTaoquanActivity", "onSuccess: "+result);
                Gson gson = new Gson();
                List<Group> groups = gson.fromJson(result,new TypeToken<List<Group>>(){}.getType());
                for (int i = 0 ;i < groups.size(); i++){
                    String groupNumber = groups.get(i).getGroupNumber();
                    JMessageClient.getGroupInfo(Long.parseLong(groupNumber), new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, GroupInfo groupInfo) {
                            if (i == 0){
                                if (groupInfo.getGroupName().equals(amoyCircle.getCircleName())) {
                                    groupId = groupInfo.getGroupID();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("EachTaoquanActivity", "onError: "+ex);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bottom_joinCircle:  //加入淘圈
                if(myApplication.getUser()==null){
                    Toast.makeText(EachTaoquanActivity.this, "请先登录哦~", Toast.LENGTH_SHORT).show();
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(EachTaoquanActivity.this, LoginActivity.class);
                    startActivityForResult(intent,RequestCode);
                }else{
                    JMessageClient.logout();//用户先退出
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 写子线程中的操作
                            try {
                                Thread.sleep(500);
                                joinCircle(myApplication.getUser().getUserId(), amoyCircle.getCircleId());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
            case R.id.btn_bottom_publish: //发布
                btnBottomPublish.startAnimation(animationSetPublish);//点击发布后开始动画，然后跳转到发布页面
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(EachTaoquanActivity.this, TaoquanPublishActivity.class);
                        intent.putExtra("circleId", amoyCircle.getCircleId());
                        intent.putExtra("circleName", amoyCircle.getCircleName());
                        startActivityForResult(intent, PublishGoods);
                    }
                },400);
                break;
            case R.id.iv_each_taoquan_return:
                finish();
                break;
            case R.id.iv_each_taoquan_more:
                //弹出“退出淘圈”弹框
                String[] items = {"退出淘圈"};
                new AlertDialog.Builder(EachTaoquanActivity.this).setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(EachTaoquanActivity.this).setMessage("确定退出该淘圈？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        JMessageClient.logout();//用户先退出
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 写子线程中的操作
                                                try {
                                                    Thread.sleep(500);
                                                    quitCircle(myApplication.getUser().getUserId(), amoyCircle.getCircleId());
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    }
                }).show();
                break;
            case R.id.iv_each_taoquan_search:
                Intent intent1 = new Intent(this, SearchActivity.class);
                startActivity(intent1);
                break;
            case R.id.iv_each_taoquan_share:
                showShare();
                break;
            case R.id.iv_each_taoquan_setting:
                Intent intent2 = new Intent(EachTaoquanActivity.this, ModifyTaoquanInfoActivity.class);
                intent2.putExtra("circleId", amoyCircle.getCircleId());
                intent2.putExtra("circleName",amoyCircle.getCircleName());
                intent2.putExtra("circleBackgroundUrl",amoyCircle.getCircleBackgroundUrl());
                intent2.putExtra("circleImageUrl", amoyCircle.getCircleImageUrl());
                startActivityForResult(intent2, ModifyTaoquanInfo);
                break;
            case R.id.LL_1:
                lvEachTaoquanGoods.LL_order_by_heat.setVisibility(View.GONE);
                lvEachTaoquanGoods.LL_order_by_time.setVisibility(View.VISIBLE);
                shapeLoadingDialog.setLoadingText("正在按照热度排序..");
                shapeLoadingDialog.show();
                orderFlag = 5;                 //5表示按照热度顺序
                pageNo = 1;
                queryGoodsBean = new QueryGoodsBean(null, null, null, orderFlag, pageNo, pageSize, amoyCircle.getCircleId());
                goodsAdapter = null;
                getGoodsData(queryGoodsBean);
                break;
            case R.id.LL_1_time:
                lvEachTaoquanGoods.LL_order_by_time.setVisibility(View.GONE);
                lvEachTaoquanGoods.LL_order_by_heat.setVisibility(View.VISIBLE);
                shapeLoadingDialog.setLoadingText("正在按照时间排序..");
                shapeLoadingDialog.show();
                orderFlag = 0;              //0表示时间顺序
                pageNo = 1;
                queryGoodsBean = new QueryGoodsBean(null, null, null, orderFlag, pageNo, pageSize, amoyCircle.getCircleId());
                goodsAdapter = null;
                getGoodsData(queryGoodsBean);
                break;
            case R.id.LL_2:
                if(myApplication.getUser()==null){
                    Toast.makeText(EachTaoquanActivity.this, "请先登录哦~", Toast.LENGTH_SHORT).show();
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(EachTaoquanActivity.this, LoginActivity.class);
                    startActivityForResult(intent,RequestCode);
                }else{
                    Log.i("groupId", "onClick: "+groupId);
                    if(isCircleMember){
                        //传入群号？？？
                        if (groupId!=0) {
                            Intent intent4 = new Intent(this,QunLiaoActivity.class);
                            intent4.putExtra("groupId",groupId+"");
                            JMessageClient.enterGroupConversation(groupId);
                            startActivity(intent4);
                        }
                    }
                }
                break;
            case R.id.LL_3: //跳转到动态页面
                Intent intent3 = new Intent(this, TaoquanDynamicActivity.class);
                intent3.putExtra("circleId", amoyCircle.getCircleId());
                intent3.putExtra("circleName", amoyCircle.getCircleName());
                if(isCircleMember){
                    intent3.putExtra("isCircleMemberFlag",1);
                }else{
                    intent3.putExtra("isCircleMemberFlag",0);
                }
                startActivity(intent3);
                break;
        }
    }

    //加入淘圈，需要参数：用户Id，所加入淘圈Id
    private void joinCircle(int userId, int circleId) {
        RequestParams requestParams = new RequestParams(NetUtil.url + "JoinCircleServlet");
        requestParams.addQueryStringParameter("userId", userId + "");
        requestParams.addQueryStringParameter("circleId", circleId + "");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("1".equals(result)) {
                    isCircleMember = true;
//                    Toast.makeText(EachTaoquanActivity.this, "加入淘圈成功~", Toast.LENGTH_SHORT).show();
                    //将“加入淘圈”隐藏，显示“发布”
                    btnBottom.startAnimation(alphaAnimationTo0); //动画，透明度由1到0
                    btnBottom.setVisibility(View.GONE);
                    btnBottomPublish.setVisibility(View.VISIBLE);
                    btnBottomPublish.startAnimation(animationSetSmallToBig);//动画,由小到大，旋转
                    iv_exit.setVisibility(View.VISIBLE);//显示退出按钮
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

    //退出淘圈，需要参数：用户Id，所加入淘圈Id
    private void quitCircle(int userId, int circleId) {
        RequestParams requestParams = new RequestParams(NetUtil.url + "QuitCircleServlet");
        requestParams.addQueryStringParameter("userId", userId + "");
        requestParams.addQueryStringParameter("circleId", circleId + "");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("1".equals(result)) {
                    isCircleMember = false;
//                    Toast.makeText(EachTaoquanActivity.this, "已退出淘圈", Toast.LENGTH_SHORT).show();
                    //将“发布”隐藏，显示“加入淘圈”
                    btnBottomPublish.startAnimation(animationSetBigToSmall);//旋转和缩放组合动画
                    btnBottomPublish.setVisibility(View.GONE);
                    btnBottom.startAnimation(alphaAnimationTo1);//透明度变换动画,由0到1
                    btnBottom.setVisibility(View.VISIBLE);
                    iv_exit.setVisibility(View.INVISIBLE);//不显示退出按钮
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ModifyTaoquanInfo && resultCode == ModifyTaoquanInfoActivity.ResultCode) {
            //是在修改信息页面点击确认后返回的
            String modifyCircleImageUrl = data.getStringExtra("modifyCircleImageUrl");
            if(modifyCircleImageUrl!=null){
                //重新设置淘圈头像
                String url = NetUtil.imageUrl + modifyCircleImageUrl;
                ImageOptions imageOptions = new ImageOptions.Builder()
                        .setFailureDrawableId(R.mipmap.upload_circle_image)
                        .setLoadingDrawableId(R.mipmap.upload_circle_image)
                        .setCrop(true).build();          //是否裁剪？
                x.image().bind(lvEachTaoquanGoods.iv_taoquan_head, url, imageOptions);
            }
            String modifyCircleBackgroundUrl = data.getStringExtra("modifyCircleBackgroundUrl");
            if(modifyCircleBackgroundUrl!=null){
                //重新设置淘圈背景
                if(modifyCircleBackgroundUrl.substring(0,1).equals("s")){
                    int systemCircleBackgroundId = Integer.parseInt(modifyCircleBackgroundUrl.substring(1));
                    setHeadBackground(systemCircleBackgroundId,lvEachTaoquanGoods.iv_taoquan_bg);
                }else{
                    String url = NetUtil.imageUrl + modifyCircleBackgroundUrl;
                    ImageOptions imageOptions = new ImageOptions.Builder().setCrop(true).build();
                    x.image().bind(lvEachTaoquanGoods.iv_taoquan_bg, url, imageOptions);
                }
            }
        } else if (requestCode == PublishGoods && resultCode == TaoquanPublishActivity.ResultCode) {
            //是在发布页面发布成功后返回的
            lvEachTaoquanGoods.LL_order_by_time.setVisibility(View.GONE);//还是按照时间排序
            lvEachTaoquanGoods.LL_order_by_heat.setVisibility(View.VISIBLE);
            orderFlag = 0;              //0表示时间顺序
            pageNo = 1;
            queryGoodsBean = new QueryGoodsBean(null, null, null, orderFlag, pageNo, pageSize, amoyCircle.getCircleId());
            goodsAdapter = null;
            getGoodsData(queryGoodsBean);
        } else if(requestCode == RequestCode && resultCode == LoginActivity.ResultCode){
            //登录成功返回
            myApplication = (MyApplication) getApplication();
            if(myApplication.getUser()==null){
                //将“发布”隐藏，显示“加入淘圈”
                btnBottom.setVisibility(View.VISIBLE);
                btnBottomPublish.setVisibility(View.GONE);
            }else{
                //判断淘圈是否存在此人，并改变isCircleMember和isCircleMaster的值
                isCircleMemberExists(myApplication.getUser().getUserId(), amoyCircle.getCircleId());
            }
        }
    }

    //分享
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("来自app:校园二手圈的分享");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://com.baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我在淘圈“" + amoyCircle.getCircleName() + "”\n快下载校园二手圈app一起来玩吧");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://pic.4j4j.cn/upload/pic/20130815/31e652fe2d.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.baidu.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("这款软件不错哦");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.baidu.com");
        // 启动分享GUI
        oks.show(this);
    }

    //加载
    @Override
    public void onLoad() {
        if (goodsList.size() < 5) {
            return;
        }
        pageNo++;
        //原来数据基础上增加
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                queryGoodsBean = new QueryGoodsBean(null, null, null, orderFlag, pageNo, pageSize, amoyCircle.getCircleId());
                loadMoreGoodsData();
                lvEachTaoquanGoods.completeLoad();  //没获取到数据也要改变界面
            }
        }, 1000);
    }

    //使头部恢复为初始状态，即全透明
    @Override
    public void onRestoreTop() {
        RL_top.getBackground().setAlpha(0);
        iv_return.setImageResource(R.mipmap.return_ffffff);
        iv_share.setImageResource(R.mipmap.share_ffffff);
        iv_search.setImageResource(R.mipmap.search_ffffff);
        iv_exit.setImageResource(R.mipmap.exit_ffffff_64);
        iv_setting.setImageResource(R.mipmap.setting_ffffff_64);
    }

    //使头部变为全不透明状态
    @Override
    public void onCompleteTop() {
        RL_top.getBackground().setAlpha(255);
        iv_return.setImageResource(R.mipmap.return_515151_64);
        iv_share.setImageResource(R.mipmap.share_515151_64);
        iv_search.setImageResource(R.mipmap.search_515151_64);
        iv_exit.setImageResource(R.mipmap.exit_515151_64);
        iv_setting.setImageResource(R.mipmap.setting_515151_64);
    }

    //private float downY;     //按下去的时候，Y轴的坐标
//    float topPosition = 0;   //头部布局在整个ListView中的位置，从头部布局的顶端算起，初始值为0，到400表示头部的布局完全不透明
//    boolean isFirstMove = true;    //是否是第一次移动
//    float firstMoveY;       //第一次移动是Y轴位置

    float firstItemToTop; //第一个Item(这里即头布局)离父控件顶部(这里即屏幕顶端)的距离,需要int->float
    float rate;    //透明度的比率

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(lvEachTaoquanGoods.getChildAt(0)==null){  //头布局还没加载出来滑动的时候就不执行
            return false;
        }
        if (lvEachTaoquanGoods.getCount() <= 2) { //总Item数量小于3，则不执行
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:   //手按下去的时候
                //记录初始值
                //downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:  //手在屏幕上滑动的时候
                //最新版本：调用getChildAt(0).getTop()，第一个Item(这里即头布局)离父控件顶部(这里即屏幕顶端)的距离,需要int->float
                if (lvEachTaoquanGoods.getFirstVisiblePosition() == 0) {
                    firstItemToTop = -lvEachTaoquanGoods.getChildAt(0).getTop(); //getTop获得的而是int类型，转换为float
                    if (firstItemToTop > 0 && firstItemToTop < 500) {
//                        Log.i("EachTaoquanActivity", "onTouch: @@@@@@@@@"+lvEachTaoquanGoods.getChildAt(0).getTop());
                        rate = firstItemToTop / 500;
                        RL_top.getBackground().setAlpha(Math.round(255 * rate));
                    }
//                    else if (firstItemToTop > 500) {
//                        RL_top.getBackground().setAlpha(255);
//                        iv_return.setImageResource(R.mipmap.return_515151_64);
//                        iv_share.setImageResource(R.mipmap.share_515151_64);
//                        iv_search.setImageResource(R.mipmap.search_515151_64);
//                        iv_exit.setImageResource(R.mipmap.exit_515151_64);
//                        iv_setting.setImageResource(R.mipmap.setting_515151_64);
//                    }
                }

                //版本2，不监听ACTION_DOWN，记录ACTION_MOVE第一个值为初始值,即downY,可以实现功能，有一些小问题
//                if(lvEachTaoquanGoods.getFirstVisiblePosition()==0){
//                    if(isFirstMove){//第一次移动，前提是第一个Item可见
//                        firstMoveY = event.getY();
//                        isFirstMove = false;
//                    }
//                    float moveY; //移动的时候，Y轴的坐标
//                    moveY = event.getY();  //滑动点Y轴的坐标
//                    if(moveY<=firstMoveY){ //移动的Y轴值小于初始的Y轴值，即用户在上拉,(Y值起点在上面，越往上越小)
//                        if((firstMoveY-moveY)>0&&(firstMoveY-moveY)<=400){ //上拉的距离小于等于400
//                            //改变顶部布局的背景的透明度
//                            float rate = (firstMoveY-moveY)/400;
//                            RL_top.getBackground().setAlpha(Math.round(255*rate));
//                            topPosition = firstMoveY-moveY;//topPosition初始为0，当用户上拉的时候变为上滑的距离
//                        }else if((firstMoveY-moveY)>400){
//                            RL_top.getBackground().setAlpha(255);
//                            iv_return.setImageResource(R.mipmap.return_515151_64);
//                            iv_share.setImageResource(R.mipmap.share_515151_64);
//                            iv_search.setImageResource(R.mipmap.search_515151_64);
//                            iv_exit.setImageResource(R.mipmap.exit_515151_64);
//                        }
//
//                    }else if(moveY>firstMoveY){//移动的Y轴值大于初始的Y轴值，即用户在下拉,(Y值起点在上面，越往上越小)
////                        Log.i("EachTaoquanActivity", "onTouch: !!!!!!!!!"+(moveY-firstMoveY));
//                        if((moveY-firstMoveY)<=400){
//                            float rate = (moveY-firstMoveY)/400;
//                            RL_top.getBackground().setAlpha(255-Math.round(255*rate));
//                        }else if((moveY-firstMoveY)>400){
//                            iv_return.setImageResource(R.mipmap.return_ffffff);
//                            iv_share.setImageResource(R.mipmap.share_ffffff);
//                            iv_search.setImageResource(R.mipmap.search_ffffff);
//                            iv_exit.setImageResource(R.mipmap.exit_ffffff_64);
//                        }
//                    }
//
//                }else if(lvEachTaoquanGoods.getFirstVisiblePosition()==1){//第一个可见的Item是第二个时
//                    RL_top.getBackground().setAlpha(255);
//                    iv_return.setImageResource(R.mipmap.return_515151_64);
//                    iv_share.setImageResource(R.mipmap.share_515151_64);
//                    iv_search.setImageResource(R.mipmap.search_515151_64);
//                    iv_exit.setImageResource(R.mipmap.exit_515151_64);
//                }

                //版本1：有很多bug：监听ACTION_DOWN时记录初始值，但后来发现点击在HorizontalScrollView上时ACTION_DOWN事件被拦截，因此无法得到downY
//                if(lvEachTaoquanGoods.firstVisibleItem==0){ //第一个Item可见，这里即头布局可见
//                    if(moveY<=downY){ //移动的Y轴值小于初始的Y轴值，即用户在上拉,(Y值起点在上面，越往上越小)
//                        if((downY-moveY)>0&&(downY-moveY)<=400){ //上拉的距离小于等于400
//                            //改变顶部布局的背景的透明度
//                            float rate = (downY-moveY)/400;
//                            RL_top.getBackground().setAlpha(Math.round(255*rate));
//                            topPosition = downY-moveY;//topPosition初始为0，当用户上拉的时候变为上滑的距离
//                        }else if((downY-moveY)==0){
////                            Log.i("EachTaoquanActivity", "onTouch: 666");
//                        }else if((downY-moveY)>400){
////                            Log.i("EachTaoquanActivity", "onTouch: 777");
//                            RL_top.getBackground().setAlpha(255);
//                            topState = finishSliding;
//                            iv_return.setImageResource(R.mipmap.return_515151_64);
//                            iv_share.setImageResource(R.mipmap.share_515151_64);
//                            iv_search.setImageResource(R.mipmap.search_515151_64);
//                            iv_exit.setImageResource(R.mipmap.exit_515151_64);
//                        }
//
//                    }else if(moveY>downY){//移动的Y轴值大于初始的Y轴值，即用户在下拉,(Y值起点在上面，越往上越小)
//                        if(topPosition<=400){//头部布局在整个ListView中的位置<=400,此时也要动态改变头部布局的透明度
//                            float rate = (moveY-downY)/400;
//                            RL_top.getBackground().setAlpha(255-Math.round(255*rate));
//                            Log.i("EachTaoquanActivity", "onTouch: ##"+topPosition+"--"+(moveY-downY));
////                            topPosition =topPosition-(moveY-downY);
//                            if(topPosition<5){
//                                RL_top.getBackground().setAlpha(0);
//                                Log.i("EachTaoquanActivity", "onTouch: !!!!");
//                                iv_return.setImageResource(R.mipmap.return_ffffff);
//                                iv_share.setImageResource(R.mipmap.share_ffffff);
//                                iv_search.setImageResource(R.mipmap.search_ffffff);
//                                iv_exit.setImageResource(R.mipmap.exit_ffffff_64);
//                            }
//                        }
//                    }
//
//                }
                break;
            case MotionEvent.ACTION_UP: //触摸弹起
//                isFirstMove = true;     //手弹起的时候使isFirstMove变为true，这样下次依旧是第一次点击
                break;
        }
        return false;
    }

    //设置头布局背景图片
    private void setHeadBackground(int systemCircleBackgroundId,ImageView imageView){
        switch (systemCircleBackgroundId){
            case 1:
                imageView.setImageResource(R.drawable.taoquan_bg_1);
                setHeadTextBlack();
                break;
            case 2:
                imageView.setImageResource(R.drawable.taoquan_bg_2);
                setHeadTextWhite();
                break;
            case 3:
                imageView.setImageResource(R.drawable.taoquan_bg_3);
                setHeadTextWhite();
                break;
            case 4:
                imageView.setImageResource(R.drawable.taoquan_bg_4);
                setHeadTextWhite();
                break;
            case 5:
                imageView.setImageResource(R.drawable.taoquan_bg_5);
                setHeadTextWhite();
                break;
            case 6:
                imageView.setImageResource(R.drawable.taoquan_bg_6);
                setHeadTextBlack();
                break;
            case 7:
                imageView.setImageResource(R.drawable.taoquan_bg_7);
                setHeadTextWhite();
                break;
            case 8:
                imageView.setImageResource(R.drawable.taoquan_bg_8);
                setHeadTextWhite();
                break;
            case 9:
                imageView.setImageResource(R.drawable.taoquan_bg_9);
                setHeadTextBlack();
                break;
            case 10:
                imageView.setImageResource(R.drawable.taoquan_bg_10);
                setHeadTextBlack();
                break;
            case 11:
                imageView.setImageResource(R.drawable.taoquan_bg_11);
                setHeadTextBlack();
                break;
            case 12:
                imageView.setImageResource(R.drawable.taoquan_bg_12);
                setHeadTextWhite();
                break;
            case 13:
                imageView.setImageResource(R.drawable.taoquan_bg_13);
                setHeadTextWhite();
                break;
            case 14:
                imageView.setImageResource(R.drawable.taoquan_bg_14);
                setHeadTextWhite();
                break;
            case 15:
                imageView.setImageResource(R.drawable.taoquan_bg_15);
                setHeadTextBlack();
                break;
        }
    }

    //设置头布局字体为黑色
    private void setHeadTextBlack() {
        lvEachTaoquanGoods.tv_taoquan_name.setTextColor(Color.BLACK);
        lvEachTaoquanGoods.tv_goods_number.setTextColor(Color.BLACK);
        lvEachTaoquanGoods.tv_taoquan_popularity.setTextColor(Color.BLACK);
    }
    //设置头布局字体为白色
    private void setHeadTextWhite() {
        lvEachTaoquanGoods.tv_taoquan_name.setTextColor(Color.WHITE);
        lvEachTaoquanGoods.tv_goods_number.setTextColor(Color.WHITE);
        lvEachTaoquanGoods.tv_taoquan_popularity.setTextColor(Color.WHITE);
    }

}
