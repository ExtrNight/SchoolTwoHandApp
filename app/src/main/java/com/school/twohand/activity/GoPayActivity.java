package com.school.twohand.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.customview.loadingview.ShapeLoadingDialog;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.entity.Goods;
import com.school.twohand.entity.OrderTbl;
import com.school.twohand.entity.Receipt;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import c.b.BP;
import c.b.PListener;

/**
 * Created by dliu on 2016/10/8.
 */
public class GoPayActivity extends AppCompatActivity {

    Goods goods;
    LinearLayout gopayOrderInfo;
    @InjectView(R.id.gopay_weixinpay)
    ImageView gopayWeixinpay;
    @InjectView(R.id.gopay_select_weixin)
    ImageView gopaySelectWeixin;
    @InjectView(R.id.gopay_rl_weixinpay)
    RelativeLayout gopayRlWeixinpay;
    @InjectView(R.id.gopay_pay)
    Button gopayPay;
    //商品图片
    @InjectView(R.id.goods_image)
    ImageView goodsImage;
    //商品标题
    @InjectView(R.id.title)
    TextView title;
    //商品价格
    @InjectView(R.id.goodsPrice)
    TextView goodsPrice;
    //商品价格
    @InjectView(R.id.goodsPrice2)
    TextView goodsPrice2;

    //收货地址
    @InjectView(R.id.shouhuodizhi)
    Spinner shouHuoDiZhi;

    List<Receipt> receipts;

//    ProgressDialog dialog;
    private ShapeLoadingDialog shapeLoadingDialog;
    String AppId  = "42dab6855427c532053895c9fc3930a7";
    MyApplication myApplication;
    //商品名
    String goodsN;
    String goodsP;

    //订单相关属性
    /*1，已拍下
    2，代付款
    3，待发货
    4，确认收货
    5，待评价*/
    private final static int YIPAI = 1;
    private final static int DAIFU = 2;
    private final static int DAIFA = 3;
    private final static int QUEREN = 4;
    private final static int DAIPIN = 5;
    String orderIdM;
    int receiptPosition;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_pay);
        ButterKnife.inject(this);
        myApplication = (MyApplication) getApplication();
        BP.init(this,AppId);
        Intent intent = getIntent();
        String goodsMessage = intent.getStringExtra("goodsMessage");
        String position = intent.getStringExtra("position");
        Log.i("position", "onCreate: "+position);
        Gson gson = new Gson();
        List<Goods> goodsMessages = gson.fromJson(goodsMessage, new TypeToken<List<Goods>>() {}.getType());
        goods = goodsMessages.get(Integer.parseInt(position));
        //给控件赋值
        String goodsUrl = NetUtil.imageUrl + goods.getGoodsImages().get(0).getImageAddress();
        Log.i("imageUrl", "onCreate: "+goods.getGoodsImages().get(0).getImageAddress());
        ImageOptions goodsImageOptions = new ImageOptions.Builder()
                .build();
        x.image().bind(goodsImage, goodsUrl, goodsImageOptions);
        goodsN = goods.getGoodsTitle();
        title.setText(goodsN);
        goodsP = goods.getGoodsPrice()+"";
        goodsPrice.setText("￥ "+goods.getGoodsPrice());
        goodsPrice2.setText("￥ "+goods.getGoodsPrice());

        //查询收货地址
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryReceiptServlet");
        requestParams.addQueryStringParameter("userId",myApplication.getUser().getUserId()+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("QueryReceiptServlet", "onSuccess: "+result);
                Gson gson = new Gson();

                receipts = gson.fromJson(result, new TypeToken<List<Receipt>>(){}.getType());
                CommonAdapter<Receipt> commonAdapter = new CommonAdapter<Receipt>(GoPayActivity.this,receipts,R.layout.address_item) {
                    @Override
                    public void convert(ViewHolder viewHolder, Receipt receipt, int position) {
                        TextView address = viewHolder.getViewById(R.id.address);
                        TextView phone = viewHolder.getViewById(R.id.phoneNumber);
                        address.setText(receipt.getReceiptDetailed());
                        phone.setText(receipt.getReceiptContactNumber());
                    }
                };
                shouHuoDiZhi.setAdapter(commonAdapter);
                shouHuoDiZhi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        receiptPosition = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
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


    @OnClick({ R.id.gopay_pay,R.id.btn_return})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_return:
                finish();
                break;
            case R.id.gopay_pay:
                //立即支付
                pay(false);

                break;
        }
    }

    /**
     * 调用支付
     *
     * @param alipayOrWechatPay
     *            支付类型，true为支付宝支付,false为微信支付
     */
    void pay(final boolean alipayOrWechatPay) {
        showDialog("正在获取订单...");
        final String name = getName();

        BP.pay(name, getBody(), getPrice(), alipayOrWechatPay, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                /*Toast.makeText(GoPayActivity.this, "支付结果未知,请稍后手动查询"+name+"'s pay status is unknow", Toast.LENGTH_SHORT)
                        .show();*/

                hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                /*Toast.makeText(GoPayActivity.this, "支付成功!"+name + "+'s pay status is success", Toast.LENGTH_SHORT).show();
*/
                //将订单号,买家id（用户本身），（商品id，当前价格）商品类，订单状态id(yizhifu daifahuo)，收货地址id ，订单时间null

                OrderTbl orderTbl = new OrderTbl(null,orderIdM,myApplication.getUser(),goods,DAIFA,receipts.get(receiptPosition),null,goods.getGoodsPrice());
                //订单信息插入数据库
                RequestParams requestParams = new RequestParams(NetUtil.url+"InsertOrderServlet");
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                String orderTblString = gson.toJson(orderTbl);

                requestParams.addQueryStringParameter("orderTbl",orderTblString);
                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        //Toast.makeText(GoPayActivity.this, result, Toast.LENGTH_SHORT).show();
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
                hideDialog();
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                // Toast.makeText(GoPayActivity.this,name + "'s orderid is " + orderId , Toast.LENGTH_SHORT).show();
                //showDialog("获取订单成功!请等待跳转到支付页面~");

                orderIdM = orderId;
            }
            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因





            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(
                            GoPayActivity.this,
                            "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(GoPayActivity.this, "支付中断!", Toast.LENGTH_SHORT)
                            .show();
                    //将订单号,买家id（用户本身），（商品id，当前价格，卖家id）商品类，订单状态id(weizhifu daizhifu)，收货地址id ，订单时间null
                    Toast.makeText(GoPayActivity.this, receiptPosition+"---"+receipts.get(receiptPosition).getReceiptId(), Toast.LENGTH_SHORT).show();
                    OrderTbl orderTbl = new OrderTbl(null,orderIdM,myApplication.getUser(),goods,DAIFU,receipts.get(receiptPosition),null,goods.getGoodsPrice());
                    //订单信息插入数据库
                    RequestParams requestParams = new RequestParams(NetUtil.url+"InsertOrderServlet");
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    String orderTblString = gson.toJson(orderTbl);
                    requestParams.addQueryStringParameter("orderTbl",orderTblString);
                    x.http().get(requestParams, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            //Toast.makeText(GoPayActivity.this, result, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(GoPayActivity.this,MyBuyActivity.class);
                            startActivity(intent);

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
                /*Toast.makeText(GoPayActivity.this, name + "'s pay status is fail, error code is "
                        + code + " ,reason is " + reason , Toast.LENGTH_SHORT).show();*/

                hideDialog();
            }
        });
    }
    void showDialog(String message) {
//        try {
//            if (dialog == null) {
//                dialog = new ProgressDialog(this);
//                dialog.setCancelable(true);
//            }
//            dialog.setMessage(message);
//            dialog.show();
//        } catch (Exception e) {
//            // 在其他线程调用dialog会报错
//        }
        try {
            if (shapeLoadingDialog == null) {
                shapeLoadingDialog = new ShapeLoadingDialog(this);
            }
            shapeLoadingDialog.setLoadingText(message);
            shapeLoadingDialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    void hideDialog() {
//        if (dialog != null && dialog.isShowing()){
//            try {
//                dialog.dismiss();
//            } catch (Exception e) {
//
//            }
//        }
        if (shapeLoadingDialog != null ){
            try {
                shapeLoadingDialog.dismiss();
            } catch (Exception e) {

            }
        }

    }

    // 默认为0.02
    double getPrice() {
        double price = Double.parseDouble(goodsP) ;

        return price;
    }
    // 商品详情(可不填)
    String getName() {
        return goodsN;
    }

    // 商品详情(可不填)
    String getBody() {
        return "dddddd";
    }

    // 支付订单号(查询时必填)
    String getOrder() {
        return "dddddd";
    }

}
