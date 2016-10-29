package com.school.twohand.activity.taoquan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.DetailGoodsActivity;
import com.school.twohand.customview.EachTaoquanListView;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.entity.Goods;
import com.school.twohand.entity.User;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 单个淘圈的页面
 */
public class EachTaoquanActivity extends AppCompatActivity implements EachTaoquanListView.OnLoadChangeListener,View.OnClickListener{

//    @InjectView(R.id.iv_each_taoquan_image)
//    ImageView ivEachTaoquanImage;
//    @InjectView(R.id.tv_each_taoquan_name)
//    TextView tvEachTaoquanName;
//    @InjectView(R.id.tv_each_taoquan_goodsNum)
//    TextView tvEachTaoquanGoodsNum;
//    @InjectView(R.id.tv_each_taoquan_popularity)
//    TextView tvEachTaoquanPopularity;
//    @InjectView(R.id.ll_taoquan_bg)
//    RelativeLayout llTaoquanBg;
    @InjectView(R.id.lv_each_taoquan_goods)
    EachTaoquanListView lvEachTaoquanGoods;
    @InjectView(R.id.btn_bottom)
    Button btnBottom;
    @InjectView(R.id.btn_bottom_publish)
    Button btnBottomPublish;
//    @InjectView(R.id.iv_each_taoquan_return)
//    ImageView ivEachTaoquanReturn;
//    @InjectView(R.id.iv_each_taoquan_more)
//    ImageView ivEachTaoquanMore;
//    @InjectView(R.id.iv_each_taoquan_search)
//    ImageView ivEachTaoquanSearch;
//    @InjectView(R.id.iv_each_taoquan_share)
//    ImageView ivEachTaoquanShare;
//    @InjectView(R.id.LL_1)
//    LinearLayout LL_1;
//    @InjectView(R.id.LL_2)
//    LinearLayout rl2;
//    @InjectView(R.id.LL_3)
//    LinearLayout rl3;
//    @InjectView(R.id.LL_1_time)
//    LinearLayout LL_1_Time;

    private static final int ModifyTaoquanInfo = 1;
    private static final int PublishGoods = 2;

    MyApplication myApplication;
    User user;
    AmoyCircle amoyCircle;    //所在的淘圈对象
    boolean isCircleMember = false;  //是否是淘圈成员
    boolean isCircleMaster = false;  //是否是淘圈圈主

    CommonAdapter<Goods> goodsAdapter;
    List<Goods> goodsList = new ArrayList<>();
    QueryGoodsBean queryGoodsBean;
    ProgressDialog pd;   //进度条，圆形
    Handler handler = new Handler();
    int orderFlag = 0;          //0表示时间顺序
    private int pageNo = 1 ;    //页号
    private int pageSize = 5;  //每页数量
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_taoquan);
        ButterKnife.inject(this);

        init();
        initView();
        initData();
        initEvent();

    }

    private void init() {
        myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        amoyCircle = bundle.getParcelable("amoyCircle"); //获取到上个页面传来的AmoyCircle对象
        orderFlag = 0;
        pageNo = 1;
        //判断淘圈是否存在此人，并改变isCircleMember和isCircleMaster的值
        isCircleMemberExists(user.getUserId(), amoyCircle.getCircleId());

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
                    lvEachTaoquanGoods.iv_exit.setVisibility(View.VISIBLE);//显示退出按钮
                    if (user.getUserId() == amoyCircle.getCircleUserId()) {
                        isCircleMaster = true;  //是淘圈圈主,圈主不可退出淘圈
                        lvEachTaoquanGoods.iv_exit.setVisibility(View.INVISIBLE);
                        lvEachTaoquanGoods.iv_taoquan_head.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(EachTaoquanActivity.this, ModifyTaoquanInfoActivity.class);
                                intent.putExtra("circleId", amoyCircle.getCircleId());
                                intent.putExtra("circleImageUrl", amoyCircle.getCircleImageUrl());
                                startActivityForResult(intent, ModifyTaoquanInfo);
                            }
                        });
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

    private void initEvent(){
        btnBottom.setOnClickListener(this);
        btnBottomPublish.setOnClickListener(this);
        lvEachTaoquanGoods.iv_return.setOnClickListener(this);
        lvEachTaoquanGoods.iv_exit.setOnClickListener(this);
        lvEachTaoquanGoods.iv_search.setOnClickListener(this);
        lvEachTaoquanGoods.iv_share.setOnClickListener(this);
        lvEachTaoquanGoods.LL_order_by_heat.setOnClickListener(this);
        lvEachTaoquanGoods.LL_order_by_time.setOnClickListener(this);
        lvEachTaoquanGoods.LL_chat_room.setOnClickListener(this);
        lvEachTaoquanGoods.LL_taoquan_dynamic.setOnClickListener(this);

        //实现自定义ListView里的OnRefreshUploadChangeListener接口
        lvEachTaoquanGoods.setOnLoadChangeListener(this);

        lvEachTaoquanGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position<=goodsList.size()){
                    Log.i("EachTaoquanActivity", "1111onClick: "+position);
                    Intent intent = new Intent(EachTaoquanActivity.this, DetailGoodsActivity.class);
                    intent.putExtra("goodsMessage", gson.toJson(goodsList));
                    intent.putExtra("position", position ); //  position+ 1,头部也算位置,区别于346行的position？？为什么不一样
                    startActivity(intent);
                }
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
                if(newGoodsList.size()<=3){ //商品数量小于等于3，则移除底部布局
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
                            if(goods.getGoodsUser().getUserId()==amoyCircle.getCircleUserId()){
                                tv_isCircleMaster.setVisibility(View.VISIBLE);
                            }
                            //设置价格
                            TextView tv_price = viewHolder.getViewById(R.id.each_taoquan_item_price);
                            tv_price.setText("￥ " + goods.getGoodsPrice());
                            //设置商品描述
                            TextView tv_describe = viewHolder.getViewById(R.id.each_taoquan_item_describe);
                            tv_describe.setText(goods.getGoodsDescribe());
                            //设置商品图片
                            LinearLayout LL = viewHolder.getViewById(R.id.LL);
                            LL.setTag(goods);
                            addLLView(LL,position);
                            //设置商品所属用户的学校
                            TextView tv_goods_user_school = viewHolder.getViewById(R.id.tv_goods_user_school);
                            String goodsUserSchool = goods.getGoodsUser().getUserSchool().getSchoolName();
                            if(goodsUserSchool!=null){
                                tv_goods_user_school.setText("来自 "+goodsUserSchool);
                            }
                            //设置点赞量和浏览量
                            TextView tv_likes_pageview = viewHolder.getViewById(R.id.tv_likes_pageview);
                            tv_likes_pageview.setText("点赞 "+goods.getGoodsLikes().size()+" · 浏览 "+goods.getGoodsPV());
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
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(pd!=null){
                            pd.cancel();
                        }
                    }
                },500);
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
                    Log.i("EachTaoquanActivity", "2222onClick: "+position);
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
    private void loadMoreGoodsData(){
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
                if(newGoodsList.size()==0){ //服务器没有返回新的数据
                    pageNo--; //下一次继续加载这一页
                    lvEachTaoquanGoods.removeFootViewIfNeed();
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
                            if(goods.getGoodsUser().getUserId()==amoyCircle.getCircleUserId()){
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
                            tv_describe.setText(goods.getGoodsDescribe());
                            //设置商品图片
                            LinearLayout LL = viewHolder.getViewById(R.id.LL);
                            LL.setTag(goods);
                            addLLView(LL,position);

                            //设置商品所属用户的学校
                            TextView tv_goods_user_school = viewHolder.getViewById(R.id.tv_goods_user_school);
                            String goodsUserSchool = goods.getGoodsUser().getUserSchool().getSchoolName();
                            if(goodsUserSchool!=null){
                                tv_goods_user_school.setText("来自 "+goodsUserSchool);
                            }
                            //设置点赞量和浏览量
                            TextView tv_likes_pageview = viewHolder.getViewById(R.id.tv_likes_pageview);
                            tv_likes_pageview.setText("点赞 "+goods.getGoodsLikes().size()+" · 浏览 "+goods.getGoodsPV());
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
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(pd!=null){
                            pd.cancel();
                        }
                    }
                },500);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bottom:  //加入淘圈
                joinCircle(user.getUserId(), amoyCircle.getCircleId());
                break;
            case R.id.btn_bottom_publish: //发布
                Intent intent = new Intent(this,TaoquanPublishActivity.class);
                intent.putExtra("circleId",amoyCircle.getCircleId());
                intent.putExtra("circleName",amoyCircle.getCircleName());
                startActivityForResult(intent,PublishGoods);
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
                                        quitCircle(user.getUserId(), amoyCircle.getCircleId());
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
            case R.id.LL_1:
                lvEachTaoquanGoods.LL_order_by_heat.setVisibility(View.GONE);
                lvEachTaoquanGoods.LL_order_by_time.setVisibility(View.VISIBLE);
                pd = new ProgressDialog(EachTaoquanActivity.this);
                pd.setMessage("正在按照热度排序..");
                pd.show();
                orderFlag = 5;                 //5表示按照热度顺序
                pageNo = 1;
                queryGoodsBean = new QueryGoodsBean(null, null, null, orderFlag, pageNo, pageSize, amoyCircle.getCircleId());
                goodsAdapter = null;
                getGoodsData(queryGoodsBean);
                break;
            case R.id.LL_1_time:
                lvEachTaoquanGoods.LL_order_by_time.setVisibility(View.GONE);
                lvEachTaoquanGoods.LL_order_by_heat.setVisibility(View.VISIBLE);
                pd = new ProgressDialog(EachTaoquanActivity.this);
                pd.setMessage("正在按照时间排序..");
                pd.show();
                orderFlag = 0;              //0表示时间顺序
                pageNo = 1;
                queryGoodsBean = new QueryGoodsBean(null, null, null, orderFlag, pageNo, pageSize, amoyCircle.getCircleId());
                goodsAdapter = null;
                getGoodsData(queryGoodsBean);
                break;
            case R.id.LL_2:
                break;
            case R.id.LL_3: //跳转到动态页面
                Intent intent2 = new Intent(this, TaoquanDynamicActivity.class);
                intent2.putExtra("circleId", amoyCircle.getCircleId());
                intent2.putExtra("circleName", amoyCircle.getCircleName());
                startActivity(intent2);
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
                    Toast.makeText(EachTaoquanActivity.this, "加入淘圈成功~", Toast.LENGTH_SHORT).show();
                    //将“加入淘圈隐藏”，显示“发布”
                    btnBottom.setVisibility(View.GONE);
                    btnBottomPublish.setVisibility(View.VISIBLE);
                    lvEachTaoquanGoods.iv_exit.setVisibility(View.VISIBLE);//显示退出按钮
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
                    Toast.makeText(EachTaoquanActivity.this, "已退出淘圈", Toast.LENGTH_SHORT).show();
                    //将“加入淘圈隐藏”，显示“发布”
                    btnBottomPublish.setVisibility(View.GONE);
                    btnBottom.setVisibility(View.VISIBLE);
                    lvEachTaoquanGoods.iv_exit.setVisibility(View.INVISIBLE);//不显示退出按钮
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
            initView();
        }else if(requestCode == PublishGoods&&resultCode==TaoquanPublishActivity.ResultCode){
            //是在发布页面发布成功后返回的
            lvEachTaoquanGoods.LL_order_by_time.setVisibility(View.GONE);//还是按照时间排序
            lvEachTaoquanGoods.LL_order_by_heat.setVisibility(View.VISIBLE);
            orderFlag = 0;              //0表示时间顺序
            pageNo = 1;
            queryGoodsBean = new QueryGoodsBean(null, null, null, orderFlag, pageNo, pageSize, amoyCircle.getCircleId());
            goodsAdapter = null;
            getGoodsData(queryGoodsBean);
        }
    }

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
        oks.setText("我在淘圈“"+amoyCircle.getCircleName()+"”\n快下载校园二手圈app一起来玩吧");
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
        pageNo++;
        //原来数据基础上增加
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                queryGoodsBean = new QueryGoodsBean(null, null, null, orderFlag, pageNo, pageSize, amoyCircle.getCircleId());
                loadMoreGoodsData();
                lvEachTaoquanGoods.completeLoad();  //没获取到数据也要改变界面
            }
        },1000);
    }


}
