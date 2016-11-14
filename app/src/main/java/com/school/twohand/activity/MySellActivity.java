package com.school.twohand.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.entity.GoodsOrderState;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.query.entity.QueryOrderBean;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MySellActivity extends AppCompatActivity {

    @InjectView(R.id.goback)
    ImageView goback;
    @InjectView(R.id.lv_sell)
    ListView lvSell;

    CommonAdapter<QueryOrderBean> queryOrderBeanCommonAdapter;
    List<QueryOrderBean> queryOrderBeen=new ArrayList<>();

    public static final int UNPAY=1;//等待买家付款
    public static final int UNSEND=2;//已付款，但未确认收货
    public static final int UNRECEIVE=3;//未发货
    public static final int ACHIEVE=4;//确认收货成功
    public static final int ACHIEVEBUY=5;//交易完成
    public static final int CANCEL=6;//取消订单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sell);
        ButterKnife.inject(this);
        getOrderData();




        lvSell.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MyBuyActivity", "onItemClick:+11 " + id);
                Intent intent = new Intent(MySellActivity.this, OrderDetailActivity2.class);
                intent.putExtra("orderId", queryOrderBeen.get(position).getOrderId());
                Log.i("MyBuyActivity", "onItemClick: +11"+queryOrderBeen.get(position).getOrderId());
                startActivity(intent);
            }
        });

    }
    public  void getOrderData(){
        String url= NetUtil.url+"QueryOrderBeanServlet";
        Integer userId=  ((MyApplication) this.getApplication()).getUser().getUserId();
        RequestParams requestParams=new RequestParams(url);
        requestParams.addQueryStringParameter("userId", userId + "");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson=new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type type=new TypeToken<List<QueryOrderBean>>(){}.getType();

                List<QueryOrderBean> newQueryOrderBean=new ArrayList<QueryOrderBean>();
                newQueryOrderBean=gson.fromJson(result,type);
                queryOrderBeen.clear();
                queryOrderBeen.addAll(newQueryOrderBean);

                if (queryOrderBeanCommonAdapter==null){
                    queryOrderBeanCommonAdapter=new CommonAdapter<QueryOrderBean>(MySellActivity.this,queryOrderBeen, R.layout.item_mysell) {
                        @Override
                        public void convert(ViewHolder viewHolder, QueryOrderBean queryOrderBean, int position) {
                            //设置item中控件的取值
                            initItemView(viewHolder,queryOrderBean,position);
                        }
                    };
                    lvSell.setAdapter(queryOrderBeanCommonAdapter);

                }else {
                    queryOrderBeanCommonAdapter.notifyDataSetChanged();
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


    public void initItemView(ViewHolder viewHolder, QueryOrderBean queryOrderBean, int position){

        ImageView a =viewHolder.getViewById(R.id.iv_sellImage);
        x.image().bind(a, NetUtil.imageUrl+queryOrderBean.getGoodsImage().getImageAddress());
        Log.i("MyBuyActivity", "initItemView: initItemView"+ NetUtil.imageUrl+queryOrderBean.getGoodsImage().getImageAddress());
        TextView b=viewHolder.getViewById(R.id.tv_sellTitle);
        b.setText(""+queryOrderBean.getGoodsDescribe());

        TextView c=viewHolder.getViewById(R.id.tv_sellPrice);
        c.setText("￥"+queryOrderBean.getOrderPrice());


        TextView e=viewHolder.getViewById(R.id.tv_time1);
        SimpleDateFormat aa=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=new Date(queryOrderBean.getOrderTime().getTime());
        Date d2=new Date(System.currentTimeMillis());
        long target=(3*1000*60*60*24);
        long diff=target-(d2.getTime()-d1.getTime());
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
        e.setText("还剩"+days+"天"+hours+"时");



        TextView btnLeft=viewHolder.getViewById(R.id.btn_pay);
        TextView btnRight=viewHolder.getViewById(R.id.btn_close);
        TextView btntime=viewHolder.getViewById(R.id.tv_time1);
        TextView btnState=viewHolder.getViewById(R.id.tv_time);
        btnShow(queryOrderBean.getGoodsOrderState().getGoodsOrderStateId(),btnLeft,btnRight,btntime,btnState);
        btnClick(queryOrderBean,position,btnLeft,btnRight);


    }





    //根据订单状态，判断按钮是否显示，按钮的文本，按钮的点击事件
    public void btnShow(int orderStateId,TextView btnLeft,TextView btnRight,TextView btntime,TextView btnState){
        switch (orderStateId){
            case UNPAY:
                //未付款
                btnLeft.setVisibility(View.GONE);
                btnRight.setVisibility(View.VISIBLE);
                btntime.setVisibility(View.VISIBLE);
                btnState.setVisibility(View.VISIBLE);


                btnRight.setText("关闭交易");
                btnState.setText("等待买家付款");

                break;

            case UNSEND:
                //已付款，未发货
                btnLeft.setVisibility(View.VISIBLE);
                btnRight.setVisibility(View.VISIBLE);
                btntime.setVisibility(View.GONE);
                btnState.setVisibility(View.VISIBLE);
                btnLeft.setText("我要发货");
                btnRight.setText("关闭交易");
                btnState.setText("未发货");
                break;


            case UNRECEIVE:
                //未确认收货
                btnLeft.setVisibility(View.VISIBLE);
                btnRight.setVisibility(View.GONE);//右边按钮消失
                btntime.setVisibility(View.GONE);
                btnState.setVisibility(View.VISIBLE);
                btnLeft.setText("提醒买家收货");
                btnState.setText("等待买家收货");
                break;
            case ACHIEVE:
                //交易完成，未评价
                btnLeft.setVisibility(View.VISIBLE);
                btnRight.setVisibility(View.GONE);
                btntime.setVisibility(View.GONE);
                btnState.setVisibility(View.VISIBLE);
                btnLeft.setText("评价");
                btnState.setText("未评价");
                break;
            case ACHIEVEBUY:
                //交易完成，已评价
                btnLeft.setVisibility(View.GONE);
                btnRight.setVisibility(View.GONE);
                btntime.setVisibility(View.GONE);
                btnState.setVisibility(View.VISIBLE);
                btnState.setText("交易完成");
                break;

            case CANCEL:
                //交易关闭
                btnLeft.setVisibility(View.GONE);//左边按钮消失
                btnRight.setVisibility(View.GONE);//右边按钮消失
                btntime.setVisibility(View.GONE);
                btnState.setVisibility(View.VISIBLE);
                btnState.setText("交易关闭");

                break;

        }

    }




    //按钮点击事件
    public void btnClick(final QueryOrderBean queryOrderBean, final int position, TextView btnLeft, TextView btnRight){
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断订单状态
                switch (queryOrderBean.getGoodsOrderState().getGoodsOrderStateId()){
                    case UNPAY:
                        break;
                    case UNSEND:
                        //发货
                        new AlertDialog.Builder(MySellActivity.this)
                                .setTitle("请先联系快递公司发货，您是否确认发货？")
                                .setPositiveButton("是",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                changeState(queryOrderBean.getOrderId(),UNRECEIVE,"",position);

                                                Toast.makeText(MySellActivity.this,"已发货",Toast.LENGTH_SHORT).show();
                                            }
                                        }).setNegativeButton("否",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }
                        ).create().show();

                        break;

                    case UNRECEIVE:
                        //提醒买家收货

                        Toast.makeText(MySellActivity.this,"提醒买家收货成功",Toast.LENGTH_SHORT).show();


                        break;
                    case ACHIEVE:
                        //评价
                        changeState(queryOrderBean.getOrderId(),ACHIEVEBUY,"已评价",position);
                        break;





                }

            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (queryOrderBean.getGoodsOrderState().getGoodsOrderStateId()){
                    case UNPAY:
                        //取消订单(更新订单状态，更新界面)
                        changeState(queryOrderBean.getOrderId(),CANCEL,"交易关闭",position);
                        Toast.makeText(MySellActivity.this,"已取消交易",Toast.LENGTH_SHORT).show();
                        break;

                    case UNSEND:
                        //关闭交易
                        changeState(queryOrderBean.getOrderId(),CANCEL,"交易关闭",position);
                        Toast.makeText(MySellActivity.this,"已取消交易",Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });

    }



    //更新订单状态，更新界面
    public void changeState(int orderId, final int newStateId, final String newStateName,final int position) {

        RequestParams requestParams = new RequestParams(NetUtil.url + "OrderUpdateServlet");
        requestParams.addBodyParameter("orderId", orderId + "");
        requestParams.addBodyParameter("orderStatatusId", newStateId + "");


        //更新订单，更新界面
        x.http().post(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.i("OrderAllFragment", "onSuccess: " + result);
                //更新界面
                queryOrderBeen.get(position).setGoodsOrderState(new GoodsOrderState(newStateId,newStateName));
                queryOrderBeanCommonAdapter.notifyDataSetChanged();//更新界面

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
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }


    @OnClick(R.id.goback)
    public void onClick() {
        finish();
    }
}
