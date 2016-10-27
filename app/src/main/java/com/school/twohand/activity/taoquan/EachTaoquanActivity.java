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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.DetailGoodsActivity;
import com.school.twohand.customview.MyListView;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.entity.Goods;
import com.school.twohand.entity.GoodsImage;
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
import butterknife.OnClick;

/**
 * 单个淘圈的页面
 */
public class EachTaoquanActivity extends AppCompatActivity {

    @InjectView(R.id.iv_each_taoquan_image)
    ImageView ivEachTaoquanImage;
    @InjectView(R.id.tv_each_taoquan_name)
    TextView tvEachTaoquanName;
    @InjectView(R.id.tv_each_taoquan_goodsNum)
    TextView tvEachTaoquanGoodsNum;
    @InjectView(R.id.tv_each_taoquan_popularity)
    TextView tvEachTaoquanPopularity;
    @InjectView(R.id.ll_taoquan_bg)
    RelativeLayout llTaoquanBg;
    @InjectView(R.id.lv_each_taoquan_goods)
    MyListView lvEachTaoquanGoods;
    @InjectView(R.id.btn_bottom)
    Button btnBottom;
    @InjectView(R.id.btn_bottom_publish)
    Button btnBottomPublish;
    @InjectView(R.id.iv_each_taoquan_return)
    ImageView ivEachTaoquanReturn;
    @InjectView(R.id.iv_each_taoquan_more)
    ImageView ivEachTaoquanMore;
    @InjectView(R.id.iv_each_taoquan_search)
    ImageView ivEachTaoquanSearch;
    @InjectView(R.id.iv_each_taoquan_share)
    ImageView ivEachTaoquanShare;
    @InjectView(R.id.LL_1)
    LinearLayout LL_1;
    @InjectView(R.id.LL_2)
    LinearLayout rl2;
    @InjectView(R.id.LL_3)
    LinearLayout rl3;
    @InjectView(R.id.LL_1_time)
    LinearLayout LL_1_Time;

    private static final int ModifyTaoquanInfo = 1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_taoquan);
        ButterKnife.inject(this);

        init();
        initView();
        initData();
        Log.i("EachTaoquanActivity", "onCreate: 111");

    }

    private void init() {
        myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        amoyCircle = bundle.getParcelable("amoyCircle"); //获取到上个页面传来的AmoyCircle对象

        //判断淘圈是否存在此人，并改变isCircleMember和isCircleMaster的值
        isCircleMemberExists(user.getUserId(), amoyCircle.getCircleId());

    }

    //判断淘圈中是否存在此人,若存在，isCircleMember为true，
    private void isCircleMemberExists(int userId, int circleId) {
        RequestParams requestParams = new RequestParams(NetUtil.url + "/isCircleMemberExistsServlet");
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
                    ivEachTaoquanMore.setVisibility(View.VISIBLE);//显示退出按钮
                    if (user.getUserId() == amoyCircle.getCircleUserId()) {
                        isCircleMaster = true;  //是淘圈圈主,圈主不可退出淘圈
                        ivEachTaoquanMore.setVisibility(View.INVISIBLE);
                        ivEachTaoquanImage.setOnClickListener(new View.OnClickListener() {
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
            x.image().bind(ivEachTaoquanImage, url, imageOptions);
            //设置淘圈名
            tvEachTaoquanName.setText(amoyCircle.getCircleName());
            //设置淘圈人气
            tvEachTaoquanPopularity.setText("人气 " + (amoyCircle.getCircleNumber() + 100));
        }

    }

    private void initData() {
        queryGoodsBean = new QueryGoodsBean(null, null, null, 0, null, null, amoyCircle.getCircleId());//0表示时间顺序
        getGoodsData(queryGoodsBean);
    }

    //获取显示的商品的数据并显示
    private void getGoodsData(QueryGoodsBean queryGoodsBean) {
        String url = NetUtil.url + "/QueryGoodsServlet";
        RequestParams requestParams = new RequestParams(url);
        Gson gson = new Gson();
        String queryGoodsBeanString = gson.toJson(queryGoodsBean);
        requestParams.addQueryStringParameter("queryGoodsBean", queryGoodsBeanString);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(final String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Goods>>() {
                }.getType();
                List<Goods> newGoodsList = gson.fromJson(result, type);
                goodsList.clear();
                goodsList.addAll(newGoodsList);
                tvEachTaoquanGoodsNum.setText("发布数 " + goodsList.size());

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
                            List<GoodsImage> goodsImages = goods.getGoodsImages();
//                            Log.i("EachTaoquanActivity", "convert: goodsImages"+goodsImages);
//                            int count = goodsImages.size();
//                            GridView gv_goodsImage = viewHolder.getViewById(R.id.gv_each_taoquan_item_image);
//                            DisplayMetrics dm = new DisplayMetrics();
//                            getWindowManager().getDefaultDisplay().getMetrics(dm);
//                            int columnWidth = dm.widthPixels / 3;//由一屏幕显示的项数决定
//                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                                    count * columnWidth + count, LinearLayout.LayoutParams.WRAP_CONTENT);//
//                            gv_goodsImage.setLayoutParams(params);//设置高和宽
//                            gv_goodsImage.setColumnWidth(columnWidth);//根据你一屏显示的项数决定
//                            gv_goodsImage.setHorizontalSpacing(1);
//                            gv_goodsImage.setStretchMode(GridView.NO_STRETCH);
//                            gv_goodsImage.setNumColumns(count);//设置一行显示的总列数
//                            CommonAdapter<GoodsImage> goodsImageAdapter = new CommonAdapter<GoodsImage>(EachTaoquanActivity.this, goodsImages, R.layout.each_taoquan_image_item) {
//                                @Override
//                                public void convert(ViewHolder viewHolder, GoodsImage goodsImage, int position) {
//                                    //取出控件赋值
//                                    ImageView iv_goodsImage = viewHolder.getViewById(R.id.iv_goods_image_item);
//                                    String url = PathUrl.imageUrl + goodsImage.getImageAddress();
//                                    //设置图片样式
//                                    ImageOptions imageOptions = new ImageOptions.Builder()
//                                            /*.setCircular(true)  设为圆形*/
//                                            .setFailureDrawableId(R.mipmap.ic_launcher)
//                                            .setLoadingDrawableId(R.mipmap.ic_launcher)
//                                            .setCrop(true).build();          //是否裁剪？
//                                    x.image().bind(iv_goodsImage, url, imageOptions);
//                                }
//                            };
//                            gv_goodsImage.setAdapter(goodsImageAdapter);
                            //CustomHScrollView mHorizontalScrollView = viewHolder.getViewById(R.id.my_hs);
                            LinearLayout LL = viewHolder.getViewById(R.id.LL);
                            LL.setTag(goods);

                            addLLView(LL,result,position);
//                            Log.i("EachTaoquanActivity", "convert: LL:"+LL);
                            //动态创建View并加入到HorizontalScrollView里包裹的线性布局里
                            //bug!!! 第一行把所有的图片全显示出来了！
                           /* for (int i = 0; i < goodsImages.size(); i++) {
                                *//*if(position==1){
                                    Log.i("EachTaoquanActivity", "convert: 1111:+LL"+LL);
                                }*//*
//                                View view = LayoutInflater.from(EachTaoquanActivity.this).inflate(
//                                        R.layout.each_taoquan_image_item, LL, false);
                                View view = LayoutInflater.from(EachTaoquanActivity.this).inflate(
                                        R.layout.each_taoquan_image_item, null);
                                ImageView iv_goodsImage = (ImageView) view.findViewById(R.id.iv_goods_image_item);
                                String url2 = NetUtil.imageUrl + goodsImages.get(i).getImageAddress();
                                //设置图片样式
                                ImageOptions imageOptions2 = new ImageOptions.Builder()
                                            *//*.setCircular(true)  设为圆形*//*
                                        .setFailureDrawableId(R.mipmap.ic_launcher)
                                        .setLoadingDrawableId(R.mipmap.ic_launcher)
                                        .setCrop(true).build();          //是否裁剪？
                                LL.addView(view);
                                x.image().bind(iv_goodsImage, url2, imageOptions2);
                            }*/
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

    public void addLLView(LinearLayout LL, final String result, final int position) {
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
                    intent.putExtra("goodsMessage", result);
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


    @OnClick({R.id.ll_taoquan_bg, R.id.btn_bottom, R.id.btn_bottom_publish, R.id.iv_each_taoquan_return,
            R.id.iv_each_taoquan_more, R.id.iv_each_taoquan_search, R.id.iv_each_taoquan_share, R.id.LL_1, R.id.LL_2, R.id.LL_3,R.id.LL_1_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_taoquan_bg:
                break;
            case R.id.btn_bottom:  //加入淘圈
                joinCircle(user.getUserId(), amoyCircle.getCircleId());
                break;
            case R.id.btn_bottom_publish:
                break;
            case R.id.iv_each_taoquan_return:
                finish();
                break;
            case R.id.iv_each_taoquan_more:
                //点击弹出popupWidow：“退出淘圈”
//                View popupWindowView = LayoutInflater.from(this).inflate(R.layout.popupwindow_quit_taoquan,null);
//                final PopupWindow popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT);
//                //设置在外部触摸的时候可以消失掉
//                popupWindow.setOutsideTouchable(true);
//                popupWindow.setBackgroundDrawable(new BitmapDrawable());//据说新版本不设置该方法也可以？？
//                //设置popupWindow显示位置
//                popupWindow.showAsDropDown(view,0,50);
//                TextView tv_quit_taoquan = (TextView) popupWindowView.findViewById(R.id.tv_quit_taoquan);
//                tv_quit_taoquan.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //显示对话框
//                        new AlertDialog.Builder(EachTaoquanActivity.this).setMessage("确定退出该淘圈？")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        popupWindow.dismiss();
//                                        quitCircle(user.getUserId(),amoyCircle.getCircleId());                                    }
//                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                popupWindow.dismiss();
//                            }
//                        }).show();
//                    }
//                });
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
                break;
            case R.id.LL_1:
                LL_1.setVisibility(View.GONE);
                LL_1_Time.setVisibility(View.VISIBLE);
                pd = new ProgressDialog(EachTaoquanActivity.this);
                pd.setMessage("正在按照热度排序..");
                pd.show();
                queryGoodsBean = new QueryGoodsBean(null, null, null, 5, null, null, amoyCircle.getCircleId());//0表示时间顺序
                getGoodsData(queryGoodsBean);
                break;
            case R.id.LL_1_time:
                LL_1_Time.setVisibility(View.GONE);
                LL_1.setVisibility(View.VISIBLE);
                pd = new ProgressDialog(EachTaoquanActivity.this);
                pd.setMessage("正在按照时间排序..");
                pd.show();
                queryGoodsBean = new QueryGoodsBean(null, null, null, 0, null, null, amoyCircle.getCircleId());//0表示时间顺序
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
        RequestParams requestParams = new RequestParams(NetUtil.url + "/JoinCircleServlet");
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
                    ivEachTaoquanMore.setVisibility(View.VISIBLE);//显示退出按钮
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
        RequestParams requestParams = new RequestParams(NetUtil.url + "/QuitCircleServlet");
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
                    ivEachTaoquanMore.setVisibility(View.INVISIBLE);//显示退出按钮
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
        }
    }
}
