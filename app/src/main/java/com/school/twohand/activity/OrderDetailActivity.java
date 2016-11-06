package com.school.twohand.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.school.twohand.entity.GoodsOrderState;
import com.school.twohand.query.entity.QueryOrderDetailBean;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class OrderDetailActivity extends AppCompatActivity {
    int orderId = 1;
    @InjectView(R.id.goback)
    ImageView goback;


    public static final int UNPAY = 1;//等待买家付款
    public static final int UNSEND = 2;//已付款，但未确认收货
    public static final int UNRECEIVE = 3;//未发货
    public static final int ACHIEVE = 4;//确认收货成功
    public static final int ACHIEVEBUY=5;//交易完成
    public static final int CANCEL = 6;//取消订单
    QueryOrderDetailBean queryOrderDetailBean = new QueryOrderDetailBean();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_detail);
        ButterKnife.inject(this);
        getOrderData();
    }

    public void getOrderData() {
        Intent intent = getIntent();
        orderId = intent.getIntExtra("orderId",0);
        Log.i("OrderDetailActivity", "getOrderData: "+orderId);
        String url = NetUtil.url + "QueryOrderDetailServlet";
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("orderId", orderId + "");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        Log.i("OrderDetailActivity", "onSuccess: "+result);
                        Gson gson = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        Log.i("OrderDetailActivity", "onSuccess: +解析成功");
                        queryOrderDetailBean = gson.fromJson(result,QueryOrderDetailBean.class);
                        Log.i("OrderDetailActivity", "onSuccess: "+queryOrderDetailBean);
                        initView(queryOrderDetailBean);


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
                }

        );

    }

    public void initView(QueryOrderDetailBean queryOrderDetailBean) {

        TextView a= (TextView) findViewById(R.id.tv_rechieve);
        a.setText("卖家：" + queryOrderDetailBean.getSellUserName());

        ImageView b= (ImageView) findViewById(R.id.iv_goodImage);
        x.image().bind(b, NetUtil.imageUrl +queryOrderDetailBean.getGoodsImage().getImageAddress());

        TextView c= (TextView) findViewById(R.id.tv_text_title);
        c.setText("#"+queryOrderDetailBean.getGoodsName()+"#");

        TextView d= (TextView) findViewById(R.id.tv_text_price);
        d.setText(""+queryOrderDetailBean.getGoodsPrice());

        TextView e= (TextView) findViewById(R.id.tv_addressMen);
        e.setText(queryOrderDetailBean.getRechieveName());

        TextView f= (TextView) findViewById(R.id.tv_phoneNumber);
        f.setText(""+queryOrderDetailBean.getPhoneNumber());

        TextView g= (TextView) findViewById(R.id.tv_address);
        g.setText(queryOrderDetailBean.getReceiptDetail());

        TextView h= (TextView) findViewById(R.id.tv_sell_name);
        h.setText(queryOrderDetailBean.getSellUserName());

        TextView i= (TextView) findViewById(R.id.tv_orderId);
        i.setText(queryOrderDetailBean.getOrderNumber());

        ImageView ivbackground1= (ImageView) findViewById(R.id.iv_background1);
        ImageView ivbackground2= (ImageView) findViewById(R.id.iv_background2);
        ImageView ivbackground3= (ImageView) findViewById(R.id.iv_background3);
        ImageView ivbackground4= (ImageView) findViewById(R.id.iv_background4);
        ImageView ivbackground5= (ImageView) findViewById(R.id.iv_background5);
        TextView tvorderState= (TextView) findViewById(R.id.tv_orderState);
        TextView tvtimeState = (TextView) findViewById(R.id.tv_timeState);
        Button btn1= (Button) findViewById(R.id.btn1);
        Button btn2= (Button) findViewById(R.id.btn2);
        Button btn3= (Button) findViewById(R.id.btn3);

        Detailshow(queryOrderDetailBean.getGoodsOrderState().getGoodsOrderStateId(),ivbackground1,ivbackground2,ivbackground3,ivbackground4
                ,ivbackground5,tvorderState,tvtimeState,btn1,btn2,btn3);
        btnClick(queryOrderDetailBean,btn1,btn2,btn3);


    }


    public void Detailshow(int orderStateId,ImageView ivbackground1,ImageView ivbackground2,ImageView ivbackground3,ImageView ivbackground4
            ,ImageView ivbackground5,TextView tvorderState,TextView tvtimeState,Button btn1,Button btn2,Button btn3) {
        switch (orderStateId){
            case UNPAY:
                //未付款
                ivbackground1.setVisibility(View.VISIBLE);
                ivbackground2.setVisibility(View.GONE);
                ivbackground3.setVisibility(View.GONE);
                ivbackground4.setVisibility(View.GONE);
                ivbackground5.setVisibility(View.GONE);
                tvorderState.setText("等你付款，您需要通过支付宝支付");
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.GONE);
                btn1.setText("关闭交易");
                btn2.setText("我要付款");

                SimpleDateFormat aa = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d1 = new Date(queryOrderDetailBean.getOrderTime().getTime());
                Date d2 = new Date(System.currentTimeMillis());
                long target = (3 * 1000 * 60 * 60 * 24);
                long diff = target - (d2.getTime() - d1.getTime());
                long days = diff / (1000 * 60 * 60 * 24);
                long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
                tvtimeState.setText(days + "天" + hours + "时" + minutes + "分后，如果您未付款，订单将自动关闭");

                break;

            case UNSEND:
                //已付款
                ivbackground1.setVisibility(View.GONE);
                ivbackground2.setVisibility(View.VISIBLE);
                ivbackground3.setVisibility(View.GONE);
                ivbackground4.setVisibility(View.GONE);
                ivbackground5.setVisibility(View.GONE);
                tvorderState.setText("等待卖家发货，请提醒ta及时完成");
                tvtimeState.setVisibility(View.GONE);
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.VISIBLE);
                btn3.setText("提醒卖家发货");

                break;
            case UNRECEIVE:
                //等待卖家发货
                ivbackground1.setVisibility(View.GONE);
                ivbackground2.setVisibility(View.GONE);
                ivbackground3.setVisibility(View.VISIBLE);
                ivbackground4.setVisibility(View.GONE);
                ivbackground5.setVisibility(View.GONE);
                tvorderState.setText("等待买卖双方见面交易，请仔细验货确认");
                tvtimeState.setVisibility(View.GONE);
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.VISIBLE);
                btn3.setText("确认收货");

                break;
            case ACHIEVE:
                //未确认收货
                ivbackground1.setVisibility(View.GONE);
                ivbackground2.setVisibility(View.GONE);
                ivbackground3.setVisibility(View.GONE);
                ivbackground4.setVisibility(View.VISIBLE);
                ivbackground5.setVisibility(View.GONE);
                tvorderState.setText("交易成功，等您评价，对这次交易说点啥");
                tvtimeState.setVisibility(View.GONE);
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.VISIBLE);
                btn3.setText("评价");
                break;
            case ACHIEVEBUY:
                //交易完成
                ivbackground1.setVisibility(View.GONE);
                ivbackground2.setVisibility(View.GONE);
                ivbackground3.setVisibility(View.GONE);
                ivbackground4.setVisibility(View.GONE);
                ivbackground5.setVisibility(View.VISIBLE);
                tvorderState.setText("交易成功，买家已评价，等待卖家的评价");
                tvtimeState.setVisibility(View.GONE);
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.VISIBLE);
                btn3.setText("查看评价");
                break;
            case CANCEL:
                //交易关闭
                ivbackground1.setVisibility(View.GONE);
                ivbackground2.setVisibility(View.GONE);
                ivbackground3.setVisibility(View.GONE);
                ivbackground4.setVisibility(View.GONE);
                ivbackground5.setVisibility(View.GONE);
                tvorderState.setText("买家关闭了这笔交易");
                tvtimeState.setVisibility(View.GONE);
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.GONE);
                break;



        }


    }

    public void btnClick(final QueryOrderDetailBean queryOrderDetailBean, Button btn1, Button btn2, Button btn3){
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断订单状态
                switch (queryOrderDetailBean.getGoodsOrderState().getGoodsOrderStateId()){
                    case UNPAY:
                        changeState(queryOrderDetailBean.getOrderId(),CANCEL);
                        Toast.makeText(OrderDetailActivity.this,"已取消交易",Toast.LENGTH_SHORT).show();

                        break;




                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (queryOrderDetailBean.getGoodsOrderState().getGoodsOrderStateId()){
                    case UNPAY:
                        new AlertDialog.Builder(OrderDetailActivity.this)
                                .setTitle("您确认付款吗？")
                                .setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                changeState(queryOrderDetailBean.getOrderId(), UNSEND);

                                                Toast.makeText(OrderDetailActivity.this,"付款成功",Toast.LENGTH_SHORT).show();
                                            }
                                        }).setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }
                        ).create().show();
                        break;

                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (queryOrderDetailBean.getGoodsOrderState().getGoodsOrderStateId()){
                    case UNSEND:
                        changeState(queryOrderDetailBean.getOrderId(),UNRECEIVE);
                        Toast.makeText(OrderDetailActivity.this,"已提醒卖家发货",Toast.LENGTH_SHORT).show();
                        break;
                    case UNRECEIVE:
                        new AlertDialog.Builder(OrderDetailActivity.this)
                                .setTitle("请仔细验货确认，您是否确认收货？")
                                .setPositiveButton("是",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                changeState(queryOrderDetailBean.getOrderId(), ACHIEVE);

                                                Toast.makeText(OrderDetailActivity.this,"已确认收货",Toast.LENGTH_SHORT).show();
                                            }
                                        }).setNegativeButton("否",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }
                        ).create().show();


                        break;
                    case ACHIEVE:
                        changeState(queryOrderDetailBean.getOrderId(),ACHIEVEBUY);
                        break;
                }
            }
        });

    }



    //更新订单状态，更新界面
    public void changeState( int orderId, final int newStateId) {

        RequestParams requestParams = new RequestParams(NetUtil.url + "OrderUpdateServlet");
        requestParams.addBodyParameter("orderId", orderId + "");
        requestParams.addBodyParameter("orderStatatusId", newStateId + "");


        //更新订单，更新界面
        x.http().post(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.i("OrderAllFragment", "onSuccess: " + result);
                //更新界面
                queryOrderDetailBean.setGoodsOrderState(new GoodsOrderState(newStateId,result));

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
}
